package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mckesson.cgat.pde.usermanagement.dto.ResendNumberVerificationDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.FileValidationUtils;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import jakarta.validation.constraints.NotBlank;
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
@Tag(name = "Practice Users",
		description = "Manages users and their relationships with roles and accounts within a specific practice.")
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
	@Operation(summary = "Create a new user", description = "Creates a new user within the specified practice.")
	public ResponseEntity<User> createUser(@Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.ADD_USERS })
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@Operation(summary = "Upload and process a CSV file of users",
			description = "Allows for bulk uploading of users from a CSV file. If any records fail validation, a CSV file with the failed records is returned.")
	public ResponseEntity<byte[]> uploadUsers(@RequestPart("userData") MultipartFile userData,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@NotNull(message = "Practice Id is Required") @PathVariable Long practiceId)
			throws FileNotValidException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);

		FileValidationUtils.validateMultiPartFile(userData);

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
	@Operation(summary = "Retry Okta provisioning for an existing user",
			description = "Triggers a retry of the Okta provisioning process for an existing user, in case the initial attempt failed.")
	public ResponseEntity<User> retryOktaProvisioning(@PathVariable Long userId) throws ResourceNotFoundException {
		User user = userService.retryOktaProvisioning(userId);
		return ResponseEntity.ok(user);
	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}")
	@Operation(summary = "Update an existing user's details by ID",
			description = "Updates the details of an existing user, identified by their ID.")
	public User updateUserDetails(@PathVariable Long userId, @Validated(BasicInfo.class) @RequestBody User user,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.updateUser(userId, user);
	}

	@RequirePermissions({ Permission.VIEW_USERS, Permission.SUBMIT_ORDER, Permission.EDIT_USERS })
	@GetMapping("/{userId}")
	@Operation(summary = "Get a user's details by ID",
			description = "Retrieves the full details for a single user by their unique ID.")
	public ResponseEntity<User> getUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete a user by ID", description = "Deletes a specified user from the practice.")
	public void deleteUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.deleteUser(userId);

	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@PatchMapping("/{userId}/activate")
	@Operation(summary = "Activate a user by ID", description = "Marks a specified user as active.")
	public void activateUser(@PathVariable Long userId,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		userService.activateUser(userId);

	}

	@RequirePermissions({ Permission.EDIT_USERS })
	@DeleteMapping("/{userId}/accounts/{accountId}")
	@Operation(summary = "Delink a user from an account",
			description = "Removes the association between a specified user and an account.")
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
	@Operation(summary = "Find and filter all users with pagination",
			description = "Retrieves a paginated and filtered list of all users for the specified practice.")
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
	@Operation(summary = "Link a user to multiple accounts",
			description = "Creates associations between a specified user and a list of accounts.")
	public User linkUserToAccounts(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotEmpty(message = "Accounts can not be empty") @RequestBody List<Long> accountIds,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.linkUserToAccounts(userId, accountIds);

	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}/roles-permissions")
	@Operation(summary = "Link a user to a role and assign specific permissions",
			description = "Links a user to a role and assigns a specific set of permissions to that user.")
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
	@Operation(summary = "Find and filter all accounts linked to a specific user",
			description = "Retrieves a paginated and filtered list of all accounts that are linked to a specific user.")
	Page<Account> findAllAccounts(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name_1") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable(required = false) String practiceId, @PathVariable(required = false) Long userId,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> salesOrg,
			@RequestParam(required = false) List<String> distributionChannel) throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return accountService.findAll(paging, query, status, userId, OrganizationContext.getCurrentOrgId(),
				AccountPartnerFunction.SOLD_TO, salesOrg, distributionChannel, ChannelType.ALL, false);

	}

	@PatchMapping("/current/preference/defaultpayto/{payToAccount}")
	@Operation(summary = "Set the default PayTo account for the current user",
			description = "Sets the default PayTo account for the currently authenticated user.")
	public User setDefaultPayToACcount(
			@NotNull(message = "Account Id can not be null") @PathVariable String payToAccount)
			throws ResourceNotFoundException {
		return userService.setDefaultPayToAccount(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
				payToAccount);
	}

	@GetMapping("/current/accounts/ids")
	@Operation(summary = "Get all account IDs for the current user",
			description = "Retrieves a list of all account IDs for the currently authenticated user.")
	ResponseEntity<List<Long>> findAllAccountIds() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(accountRepository.findAccountIdsByUser(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
					OrganizationContext.getCurrentOrgId(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType()));

	}

	@GetMapping("/current/accounts")
	@Operation(summary = "Get all account details for the current user",
			description = "Retrieves a list of all account details for the currently authenticated user.")
	ResponseEntity<List<Account>> findAllAccount() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(accountRepository.findAccountsByUser(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
					OrganizationContext.getCurrentOrgId(), AccountPartnerFunction.SOLD_TO.getPartnerFunctionType()));

	}

	@GetMapping("/current/accounts-excluded-therapies")
	@Operation(summary = "Get all account details and unavailable therapies for the current user",
			description = "Retrieves a list of all account details and unavailable therapies for the currently authenticated user.")
	ResponseEntity<UserAccountsTherapiesResponse> getUserAccessibleSoldToAccountAndExcludedTherapies()
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.OK)
			.body(accountService.getUserAccessibleSoldToAccountAndExcludedTherapies(
					Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), OrganizationContext.getCurrentOrgId(),
					AccountPartnerFunction.SOLD_TO.getPartnerFunctionType()));

	}

	@Operation(summary = "Find all PayTo accounts available to the current user",
			description = "Retrieves a paginated list of all PayTo accounts that the currently authenticated user has access to.")
	@GetMapping("/current/paytoaccounts")
	public Page<AccountsResponseDTO> findAllPayToAccounts(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return accountService.findAllPayToAccounts(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()), query,
				paging);
	}

	@PostMapping("/current/reset-password")
	@Operation(summary = "Trigger a password reset for the current user",
			description = "Triggers a password reset for the currently authenticated user. An email will be sent to the user with instructions on how to reset their password.")
	public ResponseEntity<Void> resetPasswordByEmail() throws CGATHttpException {
		String email = Objects.requireNonNull(UserSecurityUtils.getLoggedInUser()).getUserName();
		userService.resetPasswordByEmail(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resend-activation/{email}")
	@Operation(summary = "Resend the activation email for a user",
			description = "Resends the activation email to a user who has not yet activated their account.")
	public ResponseEntity<Void> resendActivationEmail(
			@NotBlank(message = "Email must not be blank") @PathVariable String email)
			throws CGATHttpException, JsonProcessingException {
		userService.resendActivationEmail(email);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/current/communication-preference")
	@Operation(summary = "Updating the communication preferences for the current user",
			description = "Updating the communication preferences for the current user")
	public User updateCommunicationPreferences(@RequestParam(required = false) Long userId,
			@RequestBody UserCommunicationPreferences communicationPreferences) throws ResourceNotFoundException {
		if (userId != null) {
			return userService.updateCommunicationPreferences(userId, communicationPreferences);
		}
		return userService.updateCommunicationPreferences(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId()),
				communicationPreferences);
	}

	@GetMapping("/current/communication-preference")
	@Operation(summary = "Get  the communication preferences for the current user",
			description = "Retrieves  the communication preferences for the currently authenticated user.")
	ResponseEntity<UserCommunicationPreferencesDTO> fetchCommunicationPreferences(
			@RequestParam(required = false) Long userId) throws ResourceNotFoundException {
		if (userId != null) {
			return ResponseEntity.status(HttpStatus.OK).body(userService.fetchCommunicationPreferences(userId));
		}
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.fetchCommunicationPreferences(Long.valueOf(UserSecurityUtils.getLoggedInUser().getId())));
	}

	@PatchMapping("/{userId}/number")
	@Operation(summary = "Update an existing user's phone number by ID",
			description = "Updates the details of an existing user, identified by their ID.")
	public User updateUserNumberById(@PathVariable Long userId,
			@NotBlank(message = "Number must not be blank") @RequestParam String number,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		UserRequestAuthorizationValidator.canLoggedInUserManageUserType(userType);
		return userService.updateUserNumberById(userId, number);
	}

	@GetMapping("/{userId}/resend-number-verification-sms")
	@Operation(summary = "Resend number verification sms for a user",
			description = "Resends number verification sms to a user who has not give consent for sms service.")
	public OffsetDateTime resendNumberVerificationSms(@PathVariable Long userId)
			throws CGATHttpException, ResourceNotFoundException {
		return userService.resendNumberVerificationSms(userId);
	}

	@GetMapping("/{userId}/number-verification-status")
	@Operation(summary = "Resend number verification details",
			description = "Resend number verification details of a user who has not give consent for sms service.")
	public ResendNumberVerificationDTO resendNumberVerificationDetails(@PathVariable Long userId)
			throws CGATHttpException, ResourceNotFoundException {
		return userService.resendNumberVerificationDetails(userId);
	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@PatchMapping("/{userId}/restrict-materials")
	@Operation(summary = "Restrict a user to multiple materials",
			description = "Creates associations between a specified user and a list of materials.")
	public User restrictUserToMaterial(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotEmpty(message = "Materials can not be empty") @RequestBody List<Long> materialIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return userService.restrictUserToMaterial(userId, materialIds);

	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@DeleteMapping("/{userId}/allow-materials")
	@Operation(summary = "Restrict a user to multiple materials",
			description = "Creates associations between a specified user and a list of materials.")
	public User deleteUserMaterialRestriction(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@NotEmpty(message = "Materials can not be empty") @RequestBody List<Long> materialIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return userService.deleteUserMaterialRestriction(userId, materialIds);

	}

	@RequirePermissions({ Permission.ADD_USERS, Permission.EDIT_USERS })
	@GetMapping("/{userId}/restricted-materials")
	@Operation(summary = "List restricted materials by user id",
			description = "Give all restricted materials for a user.")
	public List<Therapy> getExcludedMaterialsForUser(
			@PathVariable @NotNull(message = "User ID can not be null") Long userId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return userService.getExcludedMaterialsForUser(userId);

	}

}
