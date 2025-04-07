package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.AccountDTO;
import com.mckesson.cgat.pde.usermanagement.dto.AccountDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.ForbiddenActionException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.mapper.AccountMapper;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserPreferenceType;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserPreferenceRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	private final PracticeRepository practiceRepository;

	private final UserPreferenceRepository userPreferenceRepository;

	private final UserRepository userRepository;

	private final QuestionnaireService questionnaireService;

	private final AccountMapper accountMapper;

	public AccountServiceImpl(AccountRepository accountRepository, PracticeRepository practiceRepository,
			UserRepository userRepository, UserPreferenceRepository userPreferenceRepository,
			QuestionnaireService questionnaireService, AccountMapper accountMapper) {
		this.accountRepository = accountRepository;
		this.practiceRepository = practiceRepository;
		this.userRepository = userRepository;
		this.userPreferenceRepository = userPreferenceRepository;
		this.questionnaireService = questionnaireService;
		this.accountMapper = accountMapper;
	}

	@Override
	public Account createAccount(AccountDTO accountDto) {
		Account account = accountMapper.mapToModel(accountDto);
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
		account.setName(accountDto.getName());
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
	public void delinkAccountFromPractice(Long practiceId, Long accountId) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException("Account not found with it: " + accountId.toString()));
		if (!practiceId.equals(account.getPracticeId()))
			throw new ForbiddenActionException(CustomUtils.UNAUTHORIZED_MESSAGE);
		account.setPracticeId(null);
		accountRepository.save(account);
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
		if (userId != null) {
			Optional<User> user = userRepository.findById(userId);
			if (!user.isPresent())
				throw new ResourceNotFoundException("User not found with id: " + userId);
			return accountRepository.findAccountsByUser(userId, query, statusActive, paging, practiceId,
					partnerFunction.getPartnerFunctionType(), salesOrg, distChannels, channelType.name());
		}
		return accountRepository.findAllByQueryText(query, statusActive, paging, practiceId,
				partnerFunction.getPartnerFunctionType(), salesOrg, distChannels, channelType.name());
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

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Account, "", account.getAccountNumber(), "", "",
				account.getChannelType());

		AccountDetailsResponseDTO detailsResponseDTO = AccountDetailsResponseDTO.builder()
			.account(account)
			.questionnaireList(questionnaires)
			.build();

		return detailsResponseDTO;
	}

}
