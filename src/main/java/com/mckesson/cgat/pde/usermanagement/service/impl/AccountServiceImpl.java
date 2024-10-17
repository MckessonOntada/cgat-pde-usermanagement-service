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

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final PracticeRepository practiceRepository;

	public AccountServiceImpl(AccountRepository accountRepository, PracticeRepository practiceRepository) {
		this.accountRepository = accountRepository;
		this.practiceRepository = practiceRepository;
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
	public Page<Account> findAll(Pageable paging, String query, List<String> status, String practiceId) {
		query = StringUtils.toRootLowerCase(query);
		Page<Account> response;
		String statusActive = "ALL";
		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));

			if (hasActive)
				statusActive = "ACTIVE";

			if (hasInactive)
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
		}
		response = accountRepository.findAllByQueryText(query, statusActive, paging, Long.valueOf(practiceId));
		return response;
	}

	@Override
	public Boolean linkAccountsToPractice(Long practiceId, Set<Long> acccountIds) throws ResourceNotFoundException {
		Optional<Practice> practice = practiceRepository.findById(practiceId);
		practice.orElseThrow(() -> new ResourceNotFoundException(practiceId.toString()));
		List<Account> allAccounts = accountRepository.findAllById(acccountIds);
		Optional<Account> anyAccount = allAccounts.stream().filter(a -> a.getPracticeId() != null).findAny();
		if (anyAccount.isPresent())
			return false;
		allAccounts.forEach(account -> account.setPracticeId(practiceId));
		accountRepository.saveAll(allAccounts);
		return true;
	}

}
