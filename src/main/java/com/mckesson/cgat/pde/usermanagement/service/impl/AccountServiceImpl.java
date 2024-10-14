package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public Account createAccount(Account account) throws BadRequestException {
		Account createdAccount = accountRepository.save(account);
		if (createdAccount == null)
			throw new BadRequestException("Bad request..!!");
		return createdAccount;
	}

	@Override
	public Account updateAccount(Long accountId, Account obj) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with given userId: " + accountId));
		if (!Boolean.FALSE.equals(account.getIsActive()))
			throw new ResourceNotFoundException("account not found with given accountId = " + accountId);
		obj.setId(accountId);
		return accountRepository.save(obj);
	}

	@Override
	public Account getAccount(Long accountId) throws ResourceNotFoundException {
		Optional<Account> account = Optional.of(accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with given Id = " + accountId)));
		return account.get();
	}

	@Override
	public void deleteAccounts(Long accountId) throws ResourceNotFoundException {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with given Id = " + accountId));
		if (Boolean.FALSE.equals(account.getIsActive()))
			throw new ResourceNotFoundException("Account not found with given Id = " + accountId);
		account.setIsActive(false);
		accountRepository.save(account);
	}

	@Override
	public Page<Account> findAll(Pageable paging, String query, List<String> status) {
		query = StringUtils.toRootLowerCase(query);
		Page<Account> response;
		String statusActive = "Active";
		if (!CollectionUtils.isEmpty(status)) {
			boolean hasActive = status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"));
			boolean hasInactive = status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"));

			if (hasActive)
				statusActive = "ACTIVE";

			if (hasInactive)
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
		}
		response = accountRepository.findAllByQueryText(query, statusActive, paging);
		return response;
	}

}
