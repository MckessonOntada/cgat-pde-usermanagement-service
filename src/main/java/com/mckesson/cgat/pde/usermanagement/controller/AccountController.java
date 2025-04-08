package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Set;

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

import com.mckesson.cgat.pde.usermanagement.dto.AccountDTO;
import com.mckesson.cgat.pde.usermanagement.dto.AccountDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Accounts", description = "Controller for Manages account-related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/accounts", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountController {

	private final AccountService accountService;

	private static final String SORT_ORDER_ASC = "asc";

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@RequirePermissions({ Permission.ADD_ORGANIZATION, Permission.ADD_SP_ACCOUNT })
	@PostMapping(consumes = "application/json")
	@Operation(summary = "Create new SP Account")
	public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDTO accountDto)
			throws UnauthorizedAccessException {
		return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.OK);

	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION, Permission.ADD_ORGANIZATION, Permission.ADD_SP_ACCOUNT })
	@PutMapping("/{accountId}")
	@Operation(summary = "Update existing SP Account")
	public Account updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountDTO accountDto)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return accountService.updateAccount(accountId, accountDto);

	}

	@RequirePermissions({ Permission.VIEW_ORGANIZATION, Permission.VIEW_USERS, Permission.ADD_ORDERS,
			Permission.EDIT_ORDERS, Permission.MANAGE_SP_ACCOUNT })
	@GetMapping("/{accountId}")
	@Operation(summary = "Get account details by accountId ")
	public ResponseEntity<Account> getAccount(@PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		Account account = accountService.getAccount(accountId);
		return new ResponseEntity<>(account, HttpStatus.OK);

	}

	@RequirePermissions({ Permission.VIEW_ORGANIZATION, Permission.VIEW_USERS })
	@GetMapping("/configuration/{accountId}")
	@Operation(summary = "Get account details with configurations by accountId ")
	public ResponseEntity<AccountDetailsResponseDTO> getAccountDetails(@PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		AccountDetailsResponseDTO account = accountService.getAccountDetails(accountId);
		return new ResponseEntity<>(account, HttpStatus.OK);

	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION })
	@DeleteMapping("/{accountId}")
	@Operation(summary = "Delink account from practice")
	public ResponseEntity<String> delinkAccountFromPractice(@PathVariable Long practiceId, @PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		accountService.delinkAccountFromPractice(practiceId, accountId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequirePermissions({ Permission.MANAGE_ORGANIZATION_SCREEN, Permission.MANAGE_USERS_SCREEN,
			Permission.MANAGE_SP_ACCOUNT })
	@GetMapping
	@Operation(summary = "Fetch all Accounts based on filter provided with pagination capabilities.")
	Page<Account> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "sap_partner") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable String practiceId, @RequestParam(required = false) List<String> status,
			@RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> distChannels,
			@RequestParam AccountPartnerFunction partnerFunction,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status, null, Long.valueOf(practiceId), partnerFunction, salesOrg,
				distChannels, channelType);
	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION, Permission.ADD_ORGANIZATION })
	@PatchMapping
	@Operation(summary = "Link multiple accounts to a Practice.")
	public ResponseEntity<Void> linkAccountsToPractice(
			@PathVariable @NotNull(message = "Practice Id can not be null") Long practiceId,
			@RequestBody @NotEmpty(message = "Accounts can not be empty") Set<Long> accountIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		accountService.linkAccountsToPractice(practiceId, accountIds);
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
