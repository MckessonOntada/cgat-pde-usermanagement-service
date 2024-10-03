package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.AccountRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface AccountService {

	Account createAccount(String accountId, AccountRequestDto dto) throws BadRequestException, ResourceNotFoundException;

	List<Account> accounts(String accountId) throws ResourceNotFoundException;

	String deleteAccounts(String accountId) throws ResourceNotFoundException;

	Page<Account> findAll(Pageable paging, String query, List<String> status);

	Account createAccount(Account account) throws BadRequestException;

	void updateAccount(String accountId) throws ResourceNotFoundException;

	

}
