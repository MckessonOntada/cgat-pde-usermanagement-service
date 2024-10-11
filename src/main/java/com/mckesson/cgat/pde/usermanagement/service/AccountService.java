package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface AccountService {

	List<Account> accounts(Long accountId) throws ResourceNotFoundException;

	String deleteAccounts(Long accountId) throws ResourceNotFoundException;

	Page<Account> findAll(Pageable paging, String query, List<String> status);

	Account createAccount(Account account) throws BadRequestException;

	void updateAccount(Long accountId, Account account) throws ResourceNotFoundException;

}
