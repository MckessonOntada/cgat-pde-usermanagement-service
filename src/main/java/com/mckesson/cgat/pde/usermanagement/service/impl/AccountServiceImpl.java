package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.dto.AccountRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.utils.AccountUtil;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	private final AccountUtil accountUtil;

	public AccountServiceImpl(AccountRepository accountRepository, AccountUtil accountUtil) {
		this.accountRepository = accountRepository;
		this.accountUtil = accountUtil;
	}

	@Override
	public Account createAccount(Account account) throws BadRequestException {
		Account createdAccount = accountRepository.save(account);
		if (createdAccount == null)
			throw new BadRequestException("Bad request..!!");
		return createdAccount;
	}

	@Override
	public void updateAccount(String accountId, Account obj) throws ResourceNotFoundException {
		Optional<Account> account = accountRepository.findById(accountId);
		if (!account.isPresent())
			throw new ResourceNotFoundException("account not found with given accountId = " + accountId);
		obj.setId(accountId);
		accountRepository.save(obj);
	}

	@Override
	public Account createAccount(String accountId, AccountRequestDto dto)
			throws BadRequestException, ResourceNotFoundException {
		if (!accountId.isEmpty()) {
			Optional<Account> account = accountRepository.findById(accountId);
			if (!account.isPresent())
				throw new ResourceNotFoundException("account not found with given accountId = " + accountId);
			else {
				account.get().setId(accountId);
				accountRepository.save(account.get());
				return account.get();
			}
		}
		Account createdAccount = accountRepository.save(accountUtil.mapDtoTOEntity(dto));
		if (createdAccount == null)
			throw new BadRequestException("Bad request..!!");
		return createdAccount;
	}

	@Override
	public List<Account> accounts(String accountId) throws ResourceNotFoundException {
		List<Account> accountList = new ArrayList<>();
		if (accountId == null || accountId.isEmpty())
			accountList = accountRepository.findAll();
		else {
			Optional<Account> account = accountRepository.findById(accountId);
			if (account.isPresent())
				accountList.add(account.get());
			else
				throw new ResourceNotFoundException("account not found with given Id = " + accountId);

			return accountList;
		}
		return accountList;
	}

	@Override
	public String deleteAccounts(String accountId) throws ResourceNotFoundException {
		Optional<Account> account = accountRepository.findById(accountId);
		if (!account.isPresent())
			throw new ResourceNotFoundException("Account not found with given Id = " + accountId);
		account.get().setIsActive(false);
		accountRepository.save(account.get());
		return account.get().getName();
	}

	@Override
	public Page<Account> findAll(Pageable paging, String query, List<String> status) {
		query = StringUtils.toRootLowerCase(query);
		Page<Account> response;
		String statusActive = "Active";
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("Active"))) {
				statusActive = "Active";
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"))) {
				statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
			}
		}
		response = accountRepository.findAllByQueryText(query, statusActive, paging);
		return response;
	}

}
