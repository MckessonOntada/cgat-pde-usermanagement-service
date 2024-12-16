package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.ForbiddenActionException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
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

	public AccountServiceImpl(AccountRepository accountRepository, PracticeRepository practiceRepository,
			UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
		this.accountRepository = accountRepository;
		this.practiceRepository = practiceRepository;
		this.userRepository = userRepository;
		this.userPreferenceRepository = userPreferenceRepository;
	}

	@Override
	public Account createAccount(Account account) {
		return accountRepository.save(account);
	}

	@Override
	public Account updateAccount(Long accountId, Account obj) throws ResourceNotFoundException {
		Account account = getAccount(accountId);
		if (account != null)
			obj.setId(accountId);
		return accountRepository.save(obj);
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
			AccountPartnerFunction partnerFunction, List<String> salesOrg, List<String> distChannels)
			throws ResourceNotFoundException {
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
					partnerFunction.getPartnerFunctionType(), salesOrg, distChannels);
		}
		return accountRepository.findAllByQueryText(query, statusActive, paging, practiceId,
				partnerFunction.getPartnerFunctionType(), salesOrg, distChannels);
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

		List<Object[]> allAccounts = new ArrayList<>();
		Page<Object[]> allPayToAccounts = accountRepository.findAllPayToAccounts(userId, query, paging,
				AccountPartnerFunction.PAYER.getPartnerFunctionType());
		allAccounts.addAll(allPayToAccounts.getContent());

		List<Object[]> userPreference = userPreferenceRepository.findByUserIdAndType(userId,
				UserPreferenceType.DEFAULT_PAY_TO_ACCOUNT.name());
		if (!userPreference.isEmpty())
			allAccounts.add(userPreference.get(0));
		Set<AccountsResponseDTO> payToAccounts = new HashSet<>();
		for (Object[] obj : allAccounts) {
			AccountsResponseDTO account = new AccountsResponseDTO(String.valueOf(obj[0]), String.valueOf(obj[1]),
					String.valueOf(obj[2]), String.valueOf(obj[3]), String.valueOf(obj[4]), String.valueOf(obj[5]),
					String.valueOf(obj[6]), String.valueOf(obj[7]), String.valueOf(obj[8]), String.valueOf(obj[9]),
					String.valueOf(obj[10]), String.valueOf(obj[11]), String.valueOf(obj[12]), false,
					String.valueOf(obj[13]), String.valueOf(obj[14]));
			if (!userPreference.isEmpty() && account.getAccountNumber().equals(userPreference.get(0)[1]))
				account.setIsDefault(true);

			payToAccounts.add(account);
		}
		return new PageImpl<>(new ArrayList<AccountsResponseDTO>(payToAccounts), paging, payToAccounts.size());
	}

}
