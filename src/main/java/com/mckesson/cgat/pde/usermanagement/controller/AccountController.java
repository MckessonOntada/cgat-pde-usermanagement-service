package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Accounts", description = "Controller for Manages account-related operations and data.")
@RequestMapping(path = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountController {

	private final AccountService accountService;

	private static final String SORT_ORDER_ASC = "asc";

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping
	@Operation(summary = "Create new  account")
	public ResponseEntity<Object> createAccount(@RequestBody Account account) throws BadRequestException {
		return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.OK);

	}

	@PutMapping("/{accountId}")
	@Operation(summary = "Update existing account")
	public void updateAccount(@PathVariable Long accountId, @RequestBody Account account) throws ResourceNotFoundException {
		accountService.updateAccount(accountId ,account);

	}

	@GetMapping("/{accountId}")
	@Operation(summary = "Getting all account details and account details by accountId ")
	public ResponseEntity<Object> accounts(@PathVariable Long accountId) throws ResourceNotFoundException {
		List<Account> accounts = accountService.accounts(accountId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);

	}

	@DeleteMapping("/{accountId}")
	@Operation(summary = "Delete account details")
	public ResponseEntity<Object> deleteAccounts(@PathVariable Long accountId) throws ResourceNotFoundException {
		String account = accountService.deleteAccounts(accountId);
		return new ResponseEntity<>(account + " has been deleted", HttpStatus.OK);

	}

	@GetMapping
	@Operation(summary = "Fetch all Accounts based on filter provided with pagination capabilities.")
	Page<Account> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "displayName") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status);
	}

}
