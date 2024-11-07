package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface AccountService {

	Account getAccount(Long accountId) throws ResourceNotFoundException;

	void deleteAccount(Long accountId) throws ResourceNotFoundException;

	Page<Account> findAll(Pageable paging, String query, List<String> status, Long userId, String practiceId)
			throws ResourceNotFoundException;

	Account createAccount(Account account);

	Account updateAccount(Long accountId, Account account) throws ResourceNotFoundException;

	void linkAccountsToPractice(Long practiceId, Set<Long> accountIds)
			throws ResourceNotFoundException, UnauthorizedAccessException;

}
