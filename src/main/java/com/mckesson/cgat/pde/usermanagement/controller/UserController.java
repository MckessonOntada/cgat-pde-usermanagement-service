package com.mckesson.cgat.pde.usermanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.AccountPartnerFunction;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users", description = "Controller for Managing user-related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

	private final UserService userService;

	private final AccountService accountService;

	private static final String SORT_ORDER_ASC = "asc";

	public UserController(UserService userModelService, AccountService accountService) {
		this.userService = userModelService;
		this.accountService = accountService;
	}

	@PostMapping
	@Operation(summary = "Create new user")
	public ResponseEntity<User> createUser(@Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
	}

	@PatchMapping("/{userId}")
	@Operation(summary = "Update existing user details byId")
	public User updateUserDetails(@PathVariable Long userId, @Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.updateUser(userId, user);
	}

	@GetMapping("/{userId}")
	@Operation(summary = "Get user details byId ")
	public ResponseEntity<User> getUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete user details byId")
	public void deleteUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.deleteUser(userId);

	}

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

	@PatchMapping("/{userId}/accounts")
	@Operation(summary = "Link User with multiple accounts.")
	public User linkUserToAccounts(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotEmpty(message = "Accounts can not be empty") @RequestBody List<Long> accountIds,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.linkUserToAccounts(userId, accountIds);

	}

	@PatchMapping("/{userId}/roles-permissions")
	@Operation(summary = "Link User with role and permissions")
	public ResponseEntity<User> linkUserToRole(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@RequestBody RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
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
			@RequestParam(required = false) List<String> distChannels) throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status, userId, Long.valueOf(practiceId),
				AccountPartnerFunction.SOLD_TO, salesOrg, distChannels);

	}

}
