package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
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

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final AccountRepository accountRepository;

	private final RoleRepository roleRepository;

	private final OktaUserManagementService oktaUserManagementService;

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
			log.error("Error occurred while provisioning user in okta, e: {}", e.getMessage());

		}
		model.setProvisionedInOktaStatus(oktaStatus);
		return userRepository.save(model);

	}

	@Override
	public User updateUser(Long userId, User model) throws ResourceNotFoundException {
		User user = getUser(userId);
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

		if (userType.equals(UserType.ADMIN)) {
			Role systemAdminRole = roleRepository.findByName(UserRequestAuthorizationValidator.SYSTEM_ADMIN_ROLE);
			user.getPermissions().clear();
			user.setRole(systemAdminRole);
			user.setPermissions(new HashSet<>(systemAdminRole.getPermissions()));
		}
		else {
			Role role = roleRepository.findByIdAndIsActiveTrue(rolePermissions.getRoleId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Role not found with this Id :" + rolePermissions.getRoleId()));

			user.setRole(role);
			user.getPermissions().clear();
			user.setPermissions(rolePermissions.getPermissions());
		}
		return userRepository.save(user);
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByContactEmail(email);
	}

	@Transactional
	@Override
	public User setDefaultPayToAccount(Long userId, Long payToAccountId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId).get();

		if (accountRepository.findByAccountNumber(payToAccountId.toString()) == null)
			throw new ResourceNotFoundException("Account not found with id:" + payToAccountId);

		UserPreference userPreference = UserPreference.builder()
			.type(UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT)
			.userId(userId)
			.value(payToAccountId.toString())
			.build();
		userRepository.resetDefaultPayToAccount(userId, UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT.name());
		user.getPreferences().add(userPreference);
		return userRepository.save(user);
	}

}
