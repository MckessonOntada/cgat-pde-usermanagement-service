package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.Provider;
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
@Tag(name = "Practice Accounts", description = "Manages account-related operations within a specific practice.")
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
	@Operation(summary = "Create a new Specialty Pharma (SP) account",
			description = "Creates a new Specialty Pharma (SP) account within the specified practice.")
	public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDTO accountDto)
			throws UnauthorizedAccessException {
		return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.OK);

	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION, Permission.ADD_ORGANIZATION, Permission.ADD_SP_ACCOUNT })
	@PutMapping("/{accountId}")
	@Operation(summary = "Update an existing Specialty Pharma (SP) account",
			description = "Updates the details of an existing Specialty Pharma (SP) account, identified by its ID.")
	public Account updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountDTO accountDto)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return accountService.updateAccount(accountId, accountDto);

	}

	@RequirePermissions({ Permission.VIEW_ORGANIZATION, Permission.VIEW_USERS, Permission.MANAGE_SP_ACCOUNT,
			Permission.ACCOUNT_SELECTION })
	@GetMapping("/{accountId}")
	@Operation(summary = "Get account details by ID",
			description = "Retrieves the full details for a single account by its unique ID.")
	public ResponseEntity<Account> getAccount(@PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		Account account = accountService.getAccount(accountId);
		return new ResponseEntity<>(account, HttpStatus.OK);

	}

	@RequirePermissions({ Permission.VIEW_ORGANIZATION, Permission.VIEW_USERS })
	@GetMapping("/configuration/{accountId}")
	@Operation(summary = "Get account details with configurations by ID",
			description = "Retrieves account details along with their configurations by the account's unique ID.")
	public ResponseEntity<AccountDetailsResponseDTO> getAccountDetails(@PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		AccountDetailsResponseDTO account = accountService.getAccountDetails(accountId);
		return new ResponseEntity<>(account, HttpStatus.OK);

	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION })
	@DeleteMapping("/{accountId}")
	@Operation(summary = "Delink an account from a practice",
			description = "Removes the association between a specified account and a practice.")
	public ResponseEntity<String> delinkAccountFromPractice(@PathVariable Long practiceId, @PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		accountService.delinkAccountFromPractice(practiceId, accountId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequirePermissions({ Permission.MANAGE_ORGANIZATION_SCREEN, Permission.MANAGE_USERS_SCREEN,
			Permission.MANAGE_SP_ACCOUNT })
	@GetMapping
	@Operation(summary = "Find all accounts for a practice with filtering and pagination",
			description = "Retrieves a paginated list of all accounts associated with a given practice, with options for filtering and sorting.")
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
	@Operation(summary = "Link multiple accounts to a practice",
			description = "Creates an association between a practice and a list of specified accounts.")
	public ResponseEntity<Void> linkAccountsToPractice(
			@PathVariable @NotNull(message = "Practice Id can not be null") Long practiceId,
			@RequestBody @NotEmpty(message = "Accounts can not be empty") Set<Long> accountIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		accountService.linkAccountsToPractice(practiceId, accountIds);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_LINKED_ACCOUNTS })
	@PatchMapping("/{accountId}/providers")
	@Operation(summary = "Link an account to multiple providers",
			description = "Creates an association between a specified account and a list of providers.")
	public Account linkAccountToProviders(@PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
			@NotEmpty(message = "Provider IDs cannot be empty") @RequestBody List<Long> providerIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return accountService.linkAccountToProviders(accountId, providerIds);
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_PROVIDERS_SCREEN })
	@DeleteMapping("/{accountId}/providers")
	@Operation(summary = "Delink an account from multiple providers",
			description = "Removes the association between a specified account and a list of providers.")
	public ResponseEntity<Void> delinkAccountFromProvider(
			@PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
			@NotEmpty(message = "Provider IDs cannot be empty") @RequestBody List<Long> providerIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		accountService.delinkAccountFromProvider(accountId, providerIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_LINKED_ACCOUNTS })
	@GetMapping("/{accountId}/providers")
	@Operation(summary = "Find all providers not linked to an account",
			description = "Retrieves a paginated list of providers within the practice that are not yet linked to the specified account.")
	public Page<Provider> findNonLinkedAccountsByProviderId(
			@PathVariable @NotNull(message = "Account ID cannot be null") Long accountId,
			@PathVariable @NotNull(message = "Practice ID cannot be null") Long practiceId,
			@RequestParam(defaultValue = "true") Boolean unlink, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query)
			throws ResourceNotFoundException {

		Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return accountService.findNonLinkedProvidersByAccountId(query, accountId, paging, practiceId);
	}

	@GetMapping("/permissions/roles")
	@Operation(summary = "Get permissions vs list of role names of users by practice ID and permissions")
	public ResponseEntity<Map<String, String>> getPermissionsAndAssociatedRolesForPractice(
			@PathVariable String practiceId, @RequestParam List<Permission> permissions)
			throws UnauthorizedAccessException {
		Map<String, String> permissionsMap = accountService.getPermissionsAndAssociatedRolesForPractice(practiceId,
				permissions);
		return new ResponseEntity<>(permissionsMap, HttpStatus.OK);

	}

}
