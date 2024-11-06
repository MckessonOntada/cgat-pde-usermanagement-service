package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	private final PracticeRepository practiceRepository;

	private final UserRepository userRepository;

	public AccountServiceImpl(AccountRepository accountRepository, PracticeRepository practiceRepository,
			UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.practiceRepository = practiceRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Account createAccount(Account account) throws BadRequestException {
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
	public void deleteAccount(Long accountId) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ResourceNotFoundException(accountId.toString()));
		if (Boolean.FALSE.equals(account.getIsActive()))
			throw new ResourceNotFoundException(accountId.toString());
		account.setIsActive(false);
		accountRepository.save(account);
	}

	@Override
	public Page<Account> findAll(Pageable paging, String query, List<String> status, Long userId, String practiceId)
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
			return accountRepository.findAccountsByUser(userId, statusActive, paging, Long.valueOf(practiceId));
		}
		return accountRepository.findAllByQueryText(query, statusActive, paging, Long.valueOf(practiceId));
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

		Set<Account> existingAccounts = accountRepository.findByPracticeId(practiceId);
		existingAccounts.addAll(allAccounts);

		existingAccounts.forEach(a -> {
			if (accountIds.contains(a.getId()))
				a.setPracticeId(practiceId);
			else
				a.setPracticeId(null);

		});
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

}
