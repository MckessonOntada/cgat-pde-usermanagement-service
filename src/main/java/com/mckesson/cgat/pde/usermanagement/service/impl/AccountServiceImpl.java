package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mckesson.cgat.pde.usermanagement.dto.SpecialtyPharmaAccountDto;
import com.mckesson.cgat.pde.usermanagement.dto.SpecialtyPharmaAccountProjection;
import com.mckesson.cgat.pde.usermanagement.model.Provider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CSVGenerationException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CsvFileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.repository.ProviderRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.SalesOrgUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.ForbiddenActionException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.mapper.AccountMapper;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserPreferenceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.mckesson.cgat.pde.usermanagement.utils.CsvFileValidationUtils.validateCSVFile;
import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.*;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	private final PracticeRepository practiceRepository;

	private final UserPreferenceRepository userPreferenceRepository;

	private final UserRepository userRepository;

	private final QuestionnaireService questionnaireService;

	private final AccountMapper accountMapper;

	private final ProviderRepository providerRepository;

	private final Validator validator;

	private final UserService userService;

	public AccountServiceImpl(AccountRepository accountRepository, PracticeRepository practiceRepository,
			UserRepository userRepository, UserPreferenceRepository userPreferenceRepository,
			QuestionnaireService questionnaireService, AccountMapper accountMapper,
			ProviderRepository providerRepository, Validator validator, UserService userService) {
		this.accountRepository = accountRepository;
		this.practiceRepository = practiceRepository;
		this.userRepository = userRepository;
		this.userPreferenceRepository = userPreferenceRepository;
		this.questionnaireService = questionnaireService;
		this.accountMapper = accountMapper;
		this.providerRepository = providerRepository;
		this.validator = validator;
		this.userService = userService;
	}

	@Override
	public Account createAccount(AccountDTO accountDto) {
		Account account = accountMapper.mapToModel(accountDto);
		account.setPracticeId(OrganizationContext.getCurrentOrgId());
		account.setChannelType(ChannelType.SPECIALITY_PHARMA);
		populateAddress(account, accountDto);
		return accountRepository.save(account);
	}

	private void populateAddress(Account account, AccountDTO accountDto) {
		account.getAddress().setEmail(accountDto.getContact().getEmail());
		account.getAddress().setTelephone(accountDto.getContact().getPhone());
		account.getAddress().setFax(accountDto.getContact().getFax());
	}

	@Override
	public Account updateAccount(Long accountId, AccountDTO accountDto) throws ResourceNotFoundException {
		Account account = getAccount(accountId);
		if (account == null)
			throw new ResourceNotFoundException("Account not found with id: " + accountId);
		account.setAccountName(accountDto.getAccountName());
		account.getName().setFirstName(accountDto.getFirstName());
		account.getName().setMiddleName(accountDto.getMiddleName());
		account.getName().setLastName(accountDto.getLastName());
		account.getAddress().setStreet(accountDto.getAddress().getAddress1());
		account.getAddress().setStreet2(accountDto.getAddress().getAddress2());
		account.getAddress().setCity(accountDto.getAddress().getCity());
		account.getAddress().setState(accountDto.getAddress().getState());
		account.getAddress().setZipCode(accountDto.getAddress().getZip());
		account.getAddress().setEmail(accountDto.getContact().getEmail());
		account.getAddress().setTelephone(accountDto.getContact().getPhone());
		account.getAddress().setFax(accountDto.getContact().getFax());

		return accountRepository.save(account);
	}

	@Override
	public Account getAccount(Long accountId) throws ResourceNotFoundException {
		return accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException(accountId.toString()));
	}

	@Override
	@Transactional
	public void delinkAccountFromPractice(Long practiceId, Long accountId) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException("Account not found with it: " + accountId));
		if (!practiceId.equals(account.getPracticeId()))
			throw new ForbiddenActionException(CustomUtils.UNAUTHORIZED_MESSAGE);
		account.setPracticeId(null);
		accountRepository.save(account);
		accountRepository.delinkUsersFromAccount(accountId);
	}

	@Override
	public Page<Account> findAll(Pageable paging, String query, List<String> status, Long userId, Long practiceId,
			AccountPartnerFunction partnerFunction, List<String> salesOrg, List<String> distChannels,
			ChannelType channelType) throws ResourceNotFoundException {
		query = StringUtils.toRootLowerCase(query);
		String statusActive = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream()
				.anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.ACTIVE.name()));
			boolean hasInactive = status.stream()
				.anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.INACTIVE.name()));

			if (hasActive)
				statusActive = CustomUtils.object_status.ACTIVE.name();

			if (hasInactive)
				statusActive = CustomUtils.object_status.ACTIVE.name().equals(statusActive)
						? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
		}

		List<String> salesOrgForQuery = null;
		boolean include3PLPattern = false;

		if (!CollectionUtils.isEmpty(salesOrg)) {
			salesOrgForQuery = SalesOrgUtils.convertNamesToQueryCodes(salesOrg);
			include3PLPattern = salesOrg.contains("3PL");
		}

		if (userId != null) {
			Optional<User> user = userRepository.findById(userId);
			if (!user.isPresent())
				throw new ResourceNotFoundException("User not found with id: " + userId);
			return accountRepository
				.findAccountsByUser(userId, query, statusActive, paging, practiceId,
						partnerFunction.getPartnerFunctionType(), salesOrgForQuery, distChannels, channelType.name(),
						include3PLPattern)
				.map(account -> {
					return convertingSalesOrgAndCGATIdentifierFrom340T0340B(account);
				});
		}
		return accountRepository
			.findAllByQueryText(query, statusActive, paging, practiceId, partnerFunction.getPartnerFunctionType(),
					salesOrgForQuery, distChannels, channelType.name(), include3PLPattern)
			.map(account -> {
				return convertingSalesOrgAndCGATIdentifierFrom340T0340B(account);
			});
	}

	private static Account convertingSalesOrgAndCGATIdentifierFrom340T0340B(Account account) {
		if (StringUtils.isNotEmpty(account.getCgatIdentifier()) && "340".equals(account.getCgatIdentifier())) {
			account.setCgatIdentifier(account.getCgatIdentifier().concat("B"));
		}

		account.setSalesOrg(SalesOrgUtils.getNameByCode(account.getSalesOrg()));
		return account;
	}

	@Transactional
	@Override
	public void linkAccountsToPractice(Long practiceId, Set<Long> accountIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		// To-do: Assuming this piece of Practice validation will go away when tenant
		// filter is implemented
		Optional<Practice> practice = practiceRepository.findById(practiceId);
		practice.orElseThrow(() -> new ResourceNotFoundException("Practice not found : " + practiceId.toString()));

		List<Account> allAccounts = accountRepository.findAllById(accountIds);
		isAnyAccountLinkedToAnyOtherPractice(practiceId, allAccounts);

		allAccounts.forEach(a -> a.setPracticeId(practiceId));
		accountRepository.saveAll(allAccounts);
	}

	private void isAnyAccountLinkedToAnyOtherPractice(Long practiceId, List<Account> allAccounts)
			throws UnauthorizedAccessException {
		Optional<Account> accountLinkedToOtherPractice = allAccounts.stream()
			.filter(a -> a.getPracticeId() != null && !a.getPracticeId().equals(practiceId))
			.findAny();
		if (accountLinkedToOtherPractice.isPresent())
			throw new UnauthorizedAccessException(
					"Account update not authorized.Some accounts that has to be linked to current practice are linked to other practices");
	}

	@Override
	public Page<AccountsResponseDTO> findAllPayToAccounts(Long userId, String query, Pageable paging) {

		List<AccountsResponseDTO> allAccounts = new ArrayList<>();
		Page<AccountsResponseDTO> allPayToAccounts = accountRepository.findAllPayToAccounts(userId,
				StringUtils.toRootLowerCase(query), paging, AccountPartnerFunction.PAYER.getPartnerFunctionType());
		allAccounts.addAll(allPayToAccounts.getContent());

		if (StringUtils.isEmpty(query)) {
			AccountsResponseDTO preferencedPaytoAccount = userPreferenceRepository.findByUserIdAndType(userId,
					UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT.name(),
					AccountPartnerFunction.PAYER.getPartnerFunctionType());
			if (preferencedPaytoAccount != null) {
				allAccounts.removeIf(a -> a.getAccountNumber().equals(preferencedPaytoAccount.getAccountNumber()));
				allAccounts.add(preferencedPaytoAccount);
			}
		}
		return new PageImpl<>(allAccounts, paging, allAccounts.size());
	}

	@Override
	public AccountDetailsResponseDTO getAccountDetails(Long accountId) throws ResourceNotFoundException {
		Account account = getAccount(accountId);
		convertingSalesOrgAndCGATIdentifierFrom340T0340B(account);

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Account, "", account.getAccountNumber(), "", "",
				account.getChannelType(), "");

		return AccountDetailsResponseDTO.builder().account(account).questionnaireList(questionnaires).build();
	}

	@Transactional
	@Override
	public Account linkAccountToProviders(Long accountId, List<Long> providerIds) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

		List<Provider> providers = providerRepository.findAllById(providerIds);

		if (providers.size() != providerIds.size()) {
			throw new ResourceNotFoundException("Some of the provided provider IDs do not exist.");
		}

		providers.stream().forEach(provider -> {
			try {
				userService.linkPhysicianToAccounts(provider.getEmail(), List.of(accountId));
			}
			catch (ResourceNotFoundException e) {
				log.error("Failed to link provider accounts with Physician user", e);
				throw new RuntimeException(e);
			}
		});

		account.getProviders().addAll(providers);
		return accountRepository.save(account);
	}

	@Transactional
	@Override
	public void delinkAccountFromProvider(Long accountId, List<Long> providerIds) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));

		List<Provider> providers = providerRepository.findAllById(providerIds);

		if (providers.size() != providerIds.size()) {
			throw new ResourceNotFoundException("Some of the provided provider IDs do not exist.");
		}

		providers.stream().forEach(provider -> {
			try {
				userService.deLinkPhysicianFromAccounts(provider.getEmail(), accountId);
			}
			catch (ResourceNotFoundException e) {
				log.error("Failed to un-link provider accounts with Physician user", e);
				throw new RuntimeException(e);
			}
		});

		account.getProviders().removeAll(providers);
		accountRepository.save(account);
	}

	@Override
	public Page<Provider> findNonLinkedProvidersByAccountId(String query, Long accountId, Pageable paging,
			Long practiceId) throws ResourceNotFoundException {
		log.debug("Fetching non-linked accounts for Account ID: {}", accountId);
		if (!accountRepository.existsById(accountId)) {
			throw new ResourceNotFoundException("Account not found with ID: " + accountId);
		}
		query = StringUtils.toRootLowerCase(query);
		return providerRepository.findNonLinkedProvidersByAccountId(query, accountId, paging, practiceId);
	}

	@Override
	public List<Account> getSPAccounts() {
		return accountRepository.findAllByChannelTypeAndIsActive(ChannelType.SPECIALITY_PHARMA, true);
	}

	@Override
	public Page<Account> findAllByOrganization(Pageable paging, String query, List<String> status,
			List<Long> practiceIdList, AccountPartnerFunction partnerFunction, List<String> salesOrg,
			List<String> distChannels, ChannelType channelType, boolean allAccounts) {
		query = StringUtils.toRootLowerCase(query);
		boolean include3PLPattern = false;

		List<String> salesOrgForQuery = null;
		if (!CollectionUtils.isEmpty(salesOrg)) {
			salesOrgForQuery = SalesOrgUtils.convertNamesToQueryCodes(salesOrg);
			include3PLPattern = salesOrg.contains("3PL");
		}
		return accountRepository.findAllByOrganization(query, CustomUtils.getConsolidatedStatus(status), paging,
				practiceIdList, partnerFunction.getPartnerFunctionType(), salesOrgForQuery, distChannels,
				channelType.name(), allAccounts, include3PLPattern);
	}

	@Override
	public Page<SpecialtyPharmaAccountDto> getSpecialtyPharmaAccountsBySapMaterial(String sapMaterial,
			Pageable pageable, String query, String salesOrgs) {

		Page<SpecialtyPharmaAccountProjection> projectionPage = accountRepository
			.findSpecialtyPharmaAccountsBySapMaterial(sapMaterial, query, salesOrgs, pageable);

		List<SpecialtyPharmaAccountDto> dtoList = projectionPage.getContent().stream().map(projection -> {
			SpecialtyPharmaAccountDto dto = new SpecialtyPharmaAccountDto();
			dto.setAccountNumber(projection.getAccountNumber());
			dto.setAccountName(projection.getAccountName());
			dto.setSalesOrg(projection.getSalesOrg());
			dto.setHouseNumber(projection.getHouseNumber());
			dto.setStreet(projection.getStreet());
			dto.setStreet2(projection.getStreet2());
			dto.setStreet3(projection.getStreet3());
			dto.setCity(projection.getCity());
			dto.setDistrict(projection.getDistrict());
			dto.setState(projection.getState());
			dto.setZipCode(projection.getZipCode());
			dto.setCountry(projection.getCountry());
			dto.setTelephone(projection.getTelephone());
			dto.setEmail(projection.getEmail());
			dto.setFax(projection.getFax());
			return dto;
		}).collect(Collectors.toList());

		return new PageImpl<>(dtoList, pageable, projectionPage.getTotalElements());
	}

	@Override
	@Transactional
	public void unlinkSpecialtyPharmaAccount(String sapMaterial, String accountNumber)
			throws ResourceNotFoundException {
		int rowsAffected = accountRepository.unlinkSpecialtyPharmaAccount(sapMaterial, accountNumber);
		if (rowsAffected == 0) {
			throw new ResourceNotFoundException(String.format(
					"No Specialty Pharma account found with number: %s for material: %s", accountNumber, sapMaterial));
		}
	}

	public Page<Account> findAccountsByProviderId(String query, Long practiceId, Long providerId, Pageable paging)
			throws ResourceNotFoundException {
		query = StringUtils.toRootLowerCase(query);

		providerRepository.findById(providerId)
			.orElseThrow(() -> new ResourceNotFoundException("Provider not found with id:" + providerId.toString()));
		return accountRepository.findAccountsByProviderId(query, practiceId, providerId,
				ChannelType.SPECIALITY_PHARMA.name(), paging);
	}

	public Page<Account> findUnlinkedAccountsOfProviderIds(String query, Long practiceId, List<Long> providerIds,
			Pageable paging) throws ResourceNotFoundException {
		query = StringUtils.toRootLowerCase(query);

		return accountRepository.findUnlinkedAccountsOfProviderIds(query, practiceId, providerIds,
				ChannelType.SPECIALITY_PHARMA.name(), paging, providerIds.size());
	}

	@Override
	public CSVProcessingResult uploadSPAccounts(MultipartFile accountsData, int practiceId) {
		validateCSVFile(accountsData);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(accountsData.getInputStream()));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
			validateCSVFileHeaders(csvParser);

			List<String[]> errorRecords = new ArrayList<>();
			Set<String> accountNames = new HashSet<>();
			Set<AccountDTO> accountDataList = parseAndValidateCSVData(csvParser, errorRecords, accountNames);

			Map<String, Account> accountMap = getAccountMap(accountNames);

			List<Account> accountsToUpdate = new ArrayList<>();
			processAccountActions(accountDataList, accountMap, accountsToUpdate, practiceId, errorRecords);

			if (CollectionsUtils.hasItems(accountsToUpdate)) {
				accountRepository.saveAll(accountsToUpdate);
			}
			log.info("Parsed records: {} | Errors: {} | Records update/saved: {}", accountDataList.size(),
					errorRecords.size(), accountsToUpdate.size());

			CSVProcessingResult result = new CSVProcessingResult();
			result.setErrors(errorRecords);
			result.setErrorFileContent(generateErrorCSV(errorRecords));
			return result;
		}
		catch (IOException | IllegalArgumentException e) {
			log.error("Error while reading/parsing CSV file: {}", e.getMessage(), e);
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.missing"));
		}
	}

	@Override
	public List<PermissionsRoleUserDto> getPermissionsAndAssociatedRolesForPractice(String practiceId,
			List<Permission> permissions) {
		List<String> permissionStrings = permissions.stream()
			.map(Permission::name) // converts each enum to its name (as String)
			.collect(Collectors.toList());

		List<Object[]> permissionWithRoleNamesAndUser = userRepository
			.findPermissionWithRoleNamesAndUser(Long.valueOf(practiceId), permissionStrings);

		Map<String, Map<String, List<String>>> permissionMap = new HashMap<>();

		for (Object[] row : permissionWithRoleNamesAndUser) {
			String permission = (String) row[0];
			String roleName = (String) row[1];
			String fullName = (String) row[2];

			permissionMap.putIfAbsent(permission, new HashMap<>());

			permissionMap.get(permission).putIfAbsent(roleName, new ArrayList<>());

			permissionMap.get(permission).get(roleName).add(fullName);
		}

		List<PermissionsRoleUserDto> permissionsRoleUserDTOs = new ArrayList<>();

		for (Map.Entry<String, Map<String, List<String>>> entry : permissionMap.entrySet()) {
			PermissionsRoleUserDto dto = new PermissionsRoleUserDto();
			dto.setPermission(Permission.valueOf(entry.getKey()));
			dto.setRoleAndUsersList(entry.getValue());
			permissionsRoleUserDTOs.add(dto);
		}

		return permissionsRoleUserDTOs;
	}

	private void processAccountActions(Set<AccountDTO> accountDataList, Map<String, Account> accountMap,
			List<Account> accountsToUpdate, int practiceId, List<String[]> errorRecords) {

		for (AccountDTO dto : accountDataList) {
			String accountName = dto.getAccountName();
			Address addressDTO = dto.getAddress();
			Contact contactDTO = dto.getContact();

			Account account = accountMap.get(accountName);

			if (account == null && !accountMap.containsKey(accountName)) {
				Account newAccount = accountMapper.mapToModel(dto);
				newAccount.setPracticeId((long) practiceId);
				newAccount.setChannelType(ChannelType.SPECIALITY_PHARMA);
				populateAddress(newAccount, dto);
				accountsToUpdate.add(newAccount);

			}
			else if (account == null) {
				errorRecords.add(new String[] { accountName, addressDTO.getAddress1(), addressDTO.getAddress2(),
						addressDTO.getZip(), addressDTO.getCity(), addressDTO.getState(), contactDTO.getEmail(),
						contactDTO.getPhone(), contactDTO.getFax(),
						"Duplicate account found with same name. Unable to process." });

			}
			else if (account.getPracticeId() != null && !account.getPracticeId().equals((long) practiceId)) {
				errorRecords.add(new String[] { accountName, addressDTO.getAddress1(), addressDTO.getAddress2(),
						addressDTO.getZip(), addressDTO.getCity(), addressDTO.getState(), contactDTO.getEmail(),
						contactDTO.getPhone(), contactDTO.getFax(),
						"Account is already linked to another organization. Unable to process." });
			}
			else {
				AccountAddress accAddress = account.getAddress();
				accAddress.setStreet(addressDTO.getAddress1());
				accAddress.setStreet2(addressDTO.getAddress2());
				accAddress.setZipCode(addressDTO.getZip());
				accAddress.setCity(addressDTO.getCity());
				accAddress.setState(addressDTO.getState());
				accAddress.setEmail(contactDTO.getEmail());
				accAddress.setTelephone(contactDTO.getPhone());
				accAddress.setFax(contactDTO.getFax());
				account.setIsActive(true);
				account.setPracticeId((long) practiceId);
				accountsToUpdate.add(account);
			}
		}
	}

	private void validateCSVFileHeaders(CSVParser csvParser) {
		Map<String, Integer> normalizedHeaderMap = csvParser.getHeaderMap()
			.entrySet()
			.stream()
			.collect(Collectors.toMap(entry -> entry.getKey().trim(), Map.Entry::getValue));

		if (!normalizedHeaderMap.keySet().containsAll(SP_ACCOUNT_REQUIRED_HEADERS)) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.missing"));
		}

		boolean isValidHeaderStructure = Stream
			.of(Map.entry(HEADER_ACCOUNT_NAME, 0), Map.entry(HEADER_ADDRESS_1, 1), Map.entry(HEADER_ADDRESS_2, 2),
					Map.entry(HEADER_ZIP, 3), Map.entry(HEADER_CITY, 4), Map.entry(HEADER_STATE, 5),
					Map.entry(HEADER_EMAIL, 6), Map.entry(HEADER_PHONE, 7), Map.entry(HEADER_FAX, 8))
			.allMatch(entry -> {
				Integer actualIndex = normalizedHeaderMap.get(entry.getKey());
				return entry.getValue().equals(actualIndex);
			});

		if (!isValidHeaderStructure) {
			throw new CsvFileNotValidException(List.of("invalid.csv.file.headers.unstructured"));
		}
	}

	private Set<AccountDTO> parseAndValidateCSVData(CSVParser csvParser, List<String[]> errorRecords,
			Set<String> accountNames) {
		Set<AccountDTO> accountsDataList = new HashSet<>();
		boolean isEmpty = true;
		for (CSVRecord accountRecord : csvParser) {

			isEmpty = false;
			if (ObjectUtils.isEmpty(accountRecord) || accountRecord.stream().allMatch(String::isBlank))
				continue;

			String accountName = accountRecord.get(0).trim();
			String address1 = accountRecord.get(1).trim();
			String address2 = accountRecord.get(2).trim();
			String zip = accountRecord.get(3).trim();
			String city = accountRecord.get(4).trim();
			String state = accountRecord.get(5).trim();
			String email = accountRecord.get(6).trim();
			String phone = accountRecord.get(7).trim();
			String fax = accountRecord.get(8).trim();

			AccountDTO account = AccountDTO.builder()
				.accountName(accountName)
				.firstName("NA")
				.lastName("NA")
				.contact(Contact.builder().phone(phone).fax(fax).email(email).build())
				.address(Address.builder()
					.address1(address1)
					.address2(address2)
					.city(city)
					.state(state)
					.zip(zip)
					.build())
				.build();
			Set<ConstraintViolation<AccountDTO>> violations = validator.validate(account, Default.class,
					BasicInfo.class);

			if (CollectionsUtils.hasItems(violations)) {
				String errorMsg = violations.stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.joining("; "));
				errorRecords.add(new String[] { accountName, address1, address2, zip, city, state, email, phone, fax,
						errorMsg });
			}
			else {
				accountNames.add(accountName);
				accountsDataList.add(account);
			}
		}
		if (isEmpty) {
			log.error("The uploaded file is empty or contains no data.");
			throw new CsvFileNotValidException(List.of("invalid.csv.file.empty"));
		}

		return accountsDataList;
	}

	private Map<String, Account> getAccountMap(Set<String> accountNames) {
		List<Account> accounts = accountRepository.findByAccountNameInAndChannelType(accountNames,
				ChannelType.SPECIALITY_PHARMA);

		Map<String, List<Account>> grouped = accounts.stream().collect(Collectors.groupingBy(Account::getAccountName));

		Map<String, Account> result = new HashMap<>();

		for (Map.Entry<String, List<Account>> entry : grouped.entrySet()) {
			if (entry.getValue().size() == 1) {
				result.put(entry.getKey(), entry.getValue().get(0));
			}
			else {
				result.put(entry.getKey(), null);
			}
		}

		return result;
	}

	private byte[] generateErrorCSV(List<String[]> errorRecords) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.builder()
							.setHeader(HEADER_ACCOUNT_NAME, HEADER_ADDRESS_1, HEADER_ADDRESS_2, HEADER_ZIP, HEADER_CITY,
									HEADER_STATE, HEADER_EMAIL, HEADER_PHONE, HEADER_FAX, HEADER_ERROR_MESSAGE)
							.build())) {

			for (String[] errors : errorRecords) {
				csvPrinter.printRecord((Object[]) errors);
			}

			csvPrinter.flush();
			return out.toByteArray();

		}
		catch (IOException e) {
			throw new CSVGenerationException("Failed to generate error CSV", e);
		}
	}

}
