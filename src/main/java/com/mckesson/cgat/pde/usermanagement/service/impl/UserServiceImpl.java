package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.io.*;

import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserPreference;
import com.mckesson.cgat.pde.usermanagement.model.UserPreferenceType;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisionedInOktaStatus;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.OktaUserManagementService;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final AccountRepository accountRepository;

	private final RoleRepository roleRepository;

	private final OktaUserManagementService oktaUserManagementService;

	@Autowired
	Validator validator;

	public UserServiceImpl(UserRepository userRepository, AccountRepository repository, RoleRepository roleRepository,
			OktaUserManagementService oktaUserManagementService) {
		this.userRepository = userRepository;
		this.accountRepository = repository;
		this.roleRepository = roleRepository;
		this.oktaUserManagementService = oktaUserManagementService;
	}

	@Override
	public User createUser(User model) {
		UserProvisionedInOktaStatus oktaStatus = UserProvisionedInOktaStatus.PENDING;
		try {
			oktaUserManagementService.updateOrCreateOktaUser(model);
			oktaStatus = UserProvisionedInOktaStatus.PROVISIONED;
		}
		catch (CGATHttpException e) {
			log.error("Error occurred while provisioning user in Okta, userId: {}, error: {}", model.getId(),
					e.getMessage(), e);
		}
		model.setProvisionedInOktaStatus(oktaStatus);
		return userRepository.save(model);

	}

	@Override
	public User retryOktaProvisioning(Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

		if (UserProvisionedInOktaStatus.PROVISIONED.equals(user.getProvisionedInOktaStatus())) {
			return user;
		}

		try {
			oktaUserManagementService.updateOrCreateOktaUser(user);
			user.setProvisionedInOktaStatus(UserProvisionedInOktaStatus.PROVISIONED);
			userRepository.save(user);
		}
		catch (Exception e) {
			log.error("Failed to provision user in Okta, userId: {}", userId, e);
			throw e;
		}

		return user;
	}

	@Override
	public Boolean uploadUsers(MultipartFile userDataFile, UserType userType, Long practiceId,
			OutputStream outputStream) throws FileNotValidException {
		List<User> users = new ArrayList<>();
		// Map to store failed user
		Map<User, String> failedUser = new LinkedHashMap<>();

		// Parse CSV and Store User Data
		parseUserDataCSV(userDataFile, userType, users, practiceId, failedUser);

		for (User user : users) {
			try {
				// Validate User
				validateUser(user);
				// Save User
				createUser(user);
			}
			catch (IllegalArgumentException e) {
				// User Validation Failed
				log.error("Validation failed for User {}", user.getName().getFirstName());
				log.error(e.getMessage());
				failedUser.put(user, e.getMessage());
			}
			catch (DataIntegrityViolationException e) {
				// Failed to save user
				log.error("Data Integrity violation for User {}", user.getName().getFirstName());
				log.error(e.getMessage());
				failedUser.put(user, e.getCause().getCause().getMessage());
			}
		}
		// Create output csv
		createOutputCSV(outputStream, failedUser);

		return failedUser.isEmpty();
	}

	@Override
	public User updateUser(Long userId, User model) throws ResourceNotFoundException {
		User user = getUser(userId);
		model.getContact().setEmail(user.getContact().getEmail());
		user.setAddress(model.getAddress());
		user.setContact(model.getContact());
		user.setName(model.getName());
		return userRepository.save(user);
	}

	@Override
	public User getUser(Long userId) throws ResourceNotFoundException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId.toString()));
	}

	@Override
	@Transactional
	public void delinkUserFromAccount(Long userId, Long accountId) throws ResourceNotFoundException {
		userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId.toString()));
		accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException("Account not found with id:" + accountId));
		userRepository.delinkUserFromAccount(userId, accountId);
	}

	@Override
	public void deleteUser(Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		user.setIsActive(false);
		userRepository.save(user);
	}

	@Override
	public void activateUser(Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
		user.setIsActive(true);
		userRepository.save(user);
	}

	@Override
	public Page<User> findAll(Pageable paging, String query, List<String> status, Long praticeId, LocalDate startDate,
			LocalDate endDate, List<Long> roleIds) {
		query = StringUtils.toRootLowerCase(query);
		String statusActive = "ALL";

		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));

			if (hasActive)
				statusActive = "ACTIVE";

			if (hasInactive)
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
		}
		return userRepository.findAllByQueryText(statusActive, query, paging, praticeId, startDate, endDate, roleIds);
	}

	@Override
	@Transactional
	public User linkUserToAccounts(Long userId, List<Long> accountIds) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with this Id :" + userId));

		List<Account> accounts = accountRepository.findAllById(accountIds);

		if (accounts.size() != accountIds.size())
			throw new ResourceNotFoundException("Some of the provided account IDs does not exist.");

		user.getAccounts().addAll(accounts);
		return userRepository.save(user);

	}

	@Transactional
	@Override
	public User linkUserToRoleAndPermissions(Long userId, UserType userType, RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with this Id :" + userId));

		Role role = roleRepository.findByIdAndIsActiveTrue(rolePermissions.getRoleId())
			.orElseThrow(
					() -> new ResourceNotFoundException("Role not found with this Id :" + rolePermissions.getRoleId()));

		if (userType.equals(UserType.ADMIN)) {
			user.setRole(role);
			user.getPermissions().clear();
			user.getPermissions().addAll(role.getPermissions());
		}
		else {
			user.setRole(role);
			user.getPermissions().clear();
			user.setPermissions(rolePermissions.getPermissions());
		}
		return userRepository.save(user);
	}

	@Override
	public User getUserByEmail(String email) throws ResourceNotFoundException {
		return userRepository.findByContactEmailAndIsActiveTrue(email)
			.orElseThrow(() -> new ResourceNotFoundException("User not found for the given user email"));
	}

	@Transactional
	@Override
	public User setDefaultPayToAccount(Long userId, String payToAccount) throws ResourceNotFoundException {
		User user = userRepository.findById(userId).get();

		if (accountRepository.findByAccountNumber(payToAccount, AccountPartnerFunction.PAYER) == null)
			throw new ResourceNotFoundException("Account not found with Number: " + payToAccount);

		UserPreference userPreference = UserPreference.builder()
			.type(UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT)
			.userId(userId)
			.value(payToAccount)
			.build();

		userRepository.resetDefaultPayToAccount(userId, UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT.name());
		user.getPreferences().add(userPreference);
		return userRepository.save(user);
	}

	// =======================================================
	// Private methods (Bulk User Upload)
	// =======================================================

	private void parseUserDataCSV(MultipartFile userDataFile, UserType userType, List<User> users, Long practiceId,
			Map<User, String> failedUser) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(userDataFile.getInputStream()));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			for (CSVRecord record : csvParser) {

				if (record.stream().allMatch(String::isBlank)) {
					break;
				}

				List<String> userData = record.toList();
				User user = createUserFromCSVRecord(userData, practiceId);
				try {
					attachUserRole(user, userType, userData.get(12).trim(), practiceId);
					attachAccounts(user, userData.get(13).trim());
					users.add(user);
				}
				catch (ResourceNotFoundException e) {
					failedUser.put(user, e.getMessage());
				}
				catch (IllegalArgumentException e) {
					failedUser.put(user, e.getMessage());
				}

			}
		}
		catch (IOException e) {
			log.error("Unable to read User Data CSV");
			throw new FileNotValidException("Unable to read User Data CSV");
		}
		catch (IndexOutOfBoundsException e) {
			log.error("Invalid User Data file, some columns are missing");
			throw new FileNotValidException("Invalid User Data file, some columns are missing");
		}
	}

	private User createUserFromCSVRecord(List<String> userData, Long practiceId) {
		Name userName = Name.builder()
			.firstName(userData.get(0))
			.middleName(userData.get(1))
			.lastName(userData.get(2))
			.build();

		Address userAddress = Address.builder()
			.address1(userData.get(3))
			.address2(userData.get(4))
			.zip(userData.get(5))
			.city(userData.get(6))
			.state(userData.get(7))
			.build();

		Contact userContact = Contact.builder()
			.email(userData.get(8))
			.countryCode(userData.get(9))
			.phone(userData.get(10))
			.fax(userData.get(11))
			.build();

		return User.builder()
			.practiceId(practiceId)
			.name(userName)
			.contact(userContact)
			.address(userAddress)
			.isActive(true)
			.build();
	}

	private void attachUserRole(User user, UserType userType, String roleName, Long practiceId)
			throws ResourceNotFoundException {
		if (roleName.isEmpty()) {
			throw new IllegalArgumentException("Role name can not be null");
		}
		Role role;
		if (userType.equals(UserType.ADMIN)) {
			// Assuming 1L practiceId for Admin Roles
			role = roleRepository.findByNameAndPracticeId_IdAndIsActiveTrue(roleName, 1L)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Admin Role not found with this Name: %s", roleName)));

		}
		else {

			role = roleRepository.findByNameAndPracticeId_IdAndIsActiveTrue(roleName, practiceId)
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format("Role not found with this Name: %s and PracticeId: %s", roleName, practiceId)));

		}
		user.setRole(role);
		user.setPermissions(new HashSet<>(role.getPermissions()));
	}

	private void attachAccounts(User user, String accountNumbers) throws ResourceNotFoundException {
		if (accountNumbers.isEmpty())
			return;

		List<String> accountNumberList = Arrays.stream(accountNumbers.split(","))
			.map(String::trim)
			.collect(Collectors.toList());

		List<Account> accounts = accountRepository.findAllByAccountNumberInAndPartnerFunction(accountNumberList,
				AccountPartnerFunction.SOLD_TO);

		if (accounts.size() != accountNumberList.size()) {
			log.error("Some of the provided Account Numbers does not exist {}", accountNumbers);
			throw new ResourceNotFoundException(
					"Some of the provided Account Numbers does not exist: " + accountNumbers);
		}
		user.setAccounts(new HashSet<>(accounts));

	}

	private void validateUser(User user) {
		// Trigger validation with the BasicInfo group
		Set<ConstraintViolation<User>> violations = validator.validate(user, BasicInfo.class);

		if (!violations.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Validation errors: ");
			for (ConstraintViolation<User> violation : violations) {
				errorMessage.append(violation.getPropertyPath())
					.append(" ")
					.append(violation.getMessage())
					.append("; ");
			}
			throw new IllegalArgumentException(errorMessage.toString());
		}
	}

	private void createOutputCSV(OutputStream outputStream, Map<User, String> failedUser) {
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("First Name", "Phone", "Email", "Message"))) {

			for (Map.Entry<User, String> e : failedUser.entrySet()) {
				User user = e.getKey();
				csvPrinter.printRecord(user.getName().getFirstName(), user.getContact().getPhone(),
						user.getContact().getEmail(), e.getValue());
			}
		}
		catch (IOException e) {
			log.error("Unable to process output csv file, error message: {}", e.getMessage());
			throw new RuntimeException("Failed to generate output csv file");
		}
	}

}
