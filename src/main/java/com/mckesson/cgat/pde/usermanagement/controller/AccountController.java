package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Accounts", description = "Controller for Manages account-related operations and data.")
@RequestMapping(path = "/practice/{practiceId}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountController {

	private final AccountService accountService;

	private final PracticeService practiceService;

	private static final String SORT_ORDER_ASC = "asc";

	public AccountController(AccountService accountService, PracticeService practiceService) {
		this.accountService = accountService;
		this.practiceService = practiceService;
	}

	@PostMapping
	@Operation(summary = "Create new account")
	public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) throws BadRequestException {
		return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.OK);

	}

	@PutMapping("/{accountId}")
	@Operation(summary = "Update existing account")
	public Account updateAccount(@PathVariable Long accountId, @RequestBody Account account)
			throws ResourceNotFoundException {
		return accountService.updateAccount(accountId, account);

	}

	@GetMapping("/{accountId}")
	@Operation(summary = "Get account details by accountId ")
	public ResponseEntity<Account> getAccount(@PathVariable Long accountId) throws ResourceNotFoundException {
		Account account = accountService.getAccount(accountId);
		return new ResponseEntity<>(account, HttpStatus.OK);

	}

	@DeleteMapping("/{accountId}")
	@Operation(summary = "Delete account details byId")
	public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) throws ResourceNotFoundException {
		accountService.deleteAccount(accountId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@GetMapping
	@Operation(summary = "Fetch all Accounts based on filter provided with pagination capabilities.")
	Page<Account> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable(required = false) String practiceId, @RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status, practiceId);
	}

	@PatchMapping
	@Operation(summary = "Link multiple accounts to a Practice.")
	public ResponseEntity<List<Account>> linkAccountsToPractice(@PathVariable @NotNull Long practiceId,
			@RequestBody @NotEmpty Set<Long> accountIds) throws ResourceNotFoundException {
		if (Boolean.TRUE.equals(accountService.linkAccountsToPractice(practiceId, accountIds)))
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

}
