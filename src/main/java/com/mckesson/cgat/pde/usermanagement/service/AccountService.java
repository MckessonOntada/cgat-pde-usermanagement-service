package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.LinkAccountToUserDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface AccountService {

	Account getAccount(Long accountId) throws ResourceNotFoundException;

	void deleteAccount(Long accountId) throws ResourceNotFoundException;

	Page<Account> findAll(Pageable paging, String query, List<String> status, String practiceId);

	Account createAccount(Account account) throws BadRequestException;

	Account updateAccount(Long accountId, Account account) throws ResourceNotFoundException;

	Account linkAccountsToUser(LinkAccountToUserDto dto);

}
