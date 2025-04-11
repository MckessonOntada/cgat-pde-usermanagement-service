package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.dto.AccountsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Users", description = "Controller for Managing user-related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

	private final UserService userService;

	private final AccountService accountService;

	private final AccountRepository accountRepository;

	private static final String SORT_ORDER_ASC = "asc";

	public UserController(UserService userModelService, AccountService accountService,
			AccountRepository accountRepository) {
		this.userService = userModelService;
		this.accountService = accountService;
		this.accountRepository = accountRepository;
	}

	@RequirePermissions({ Permission.ADD_USERS })
	@PostMapping
	@Operation(summary = "Create new user")
	public ResponseEntity<User> createUser(@Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.ADD_USERS })
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@Operation(summary = "Upload bulk users")
	public ResponseEntity<byte[]> uploadUsers(@RequestPart("userData") MultipartFile userData,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@NotNull(message = "Practice Id is Required") @PathVariable Long practiceId)
			throws FileNotValidException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Boolean uploadStatus = userService.uploadUsers(userData, userType, practiceId, outputStream);
		HttpStatus status = uploadStatus ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;

		return ResponseEntity.status(status)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FailedUsers.csv")
			.contentType(MediaType.parseMediaType("text/csv"))
			.body(outputStream.toByteArray());
	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PostMapping("/{userId}/retry-okta-provisioning")
	@Operation(summary = "Retry Okta provisioning for an existing user")
	public ResponseEntity<User> retryOktaProvisioning(@PathVariable Long userId) throws ResourceNotFoundException {
		User user = userService.retryOktaProvisioning(userId);
		return ResponseEntity.ok(user);
	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}")
	@Operation(summary = "Update existing user details byId")
	public User updateUserDetails(@PathVariable Long userId, @Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.updateUser(userId, user);
	}

	@RequirePermissions({ Permission.VIEW_USERS })
	@GetMapping("/{userId}")
	@Operation(summary = "Get user details byId ")
	public ResponseEntity<User> getUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete user details byId")
	public void deleteUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.deleteUser(userId);

	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@PatchMapping("/{userId}/activate")
	@Operation(summary = "Activate user by Id")
	public void activateUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.activateUser(userId);

	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@DeleteMapping("/{userId}/accounts/{accountId}")
	@Operation(summary = "Delink user from account")
	public ResponseEntity<Void> delinkUserFromAccount(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@NotNull(message = "Account can not be empty") @PathVariable Long accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.delinkUserFromAccount(userId, accountId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_USERS_SCREEN })
	@GetMapping
	@Operation(summary = "Fetch all Users based on filter provided with pagination capabilities.")
	Page<User> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<Long> roleIds,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@Parameter(description = "Date format : " + CustomUtils.API_DATE_FORMAT) @RequestParam(
					required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate startDate,
			@Parameter(description = "Date format : " + CustomUtils.API_DATE_FORMAT) @RequestParam(
					required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate endDate)
			throws BadRequestException, UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		if (!((null == startDate && null == endDate) || (null != startDate && null != endDate)))
			throw new BadRequestException("Either enter both dates or none for the filter as its a part of date range");

		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.findAll(paging, query, status, OrganizationContext.getCurrentOrgId(), startDate, endDate,
				roleIds);

	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}/accounts")
	@Operation(summary = "Link User with multiple accounts.")
	public User linkUserToAccounts(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotEmpty(message = "Accounts can not be empty") @RequestBody List<Long> accountIds,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.linkUserToAccounts(userId, accountIds);

	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}/roles-permissions")
	@Operation(summary = "Link User with role and permissions")
	public ResponseEntity<User> linkUserToRole(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@RequestBody RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		UserRequestAuthorizationValidator.canLoggedInUserManageRoleAndPermissions(userId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.linkUserToRoleAndPermissions(userId, userType, rolePermissions));
	}

	@GetMapping("/{userId}/accounts")
	@Operation(summary = "Fetch all Acccounts for User based on filter provided with pagination capabilities.")
	Page<Account> findAllAccounts(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name_1") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable(required = false) String practiceId, @PathVariable(required = false) Long userId,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> distributionChannel) throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status, userId, List.of(OrganizationContext.getCurrentOrgId()),
				AccountPartnerFunction.SOLD_TO, salesOrg, distributionChannel, ChannelType.ALL);

	}

	@PatchMapping("/current/preference/defaultpayto/{payToAccount}")
	@Operation(summary = "Set default PayTo Account to User")
	public User setDefaultPayToACcount(
			@NotNull(message = "Account Id can not be null") @PathVariable String payToAccount)
			throws ResourceNotFoundException {
		return userService.setDefaultPayToAccount(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
				payToAccount);
	}

	@GetMapping("/current/accounts/ids")
	@Operation(summary = "Fetch all AcccountIds for User")
	ResponseEntity<List<Long>> findAllAccountIds() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(accountRepository.findAccountIdsByUser(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
					OrganizationContext.getCurrentOrgId(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType()));

	}

	@Operation(summary = "Find all payto accounts that are linked to all soldto accounts that the user has access")
	@GetMapping("/current/paytoaccounts")
	public Page<AccountsResponseDTO> findAllPayToAccounts(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return accountService.findAllPayToAccounts(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query,
				paging);
	}

	@PostMapping("/current/reset-password")
	@Operation(summary = "Reset Okta password by email")
	public ResponseEntity<Void> resetPasswordByEmail() throws CGATHttpException {
		String email = Objects.requireNonNull(UserSecurityUtils.getLoggedInUser()).getUserName();
		userService.resetPasswordByEmail(email);
		return ResponseEntity.ok().build();
	}

}
