package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.Role;
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

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users", description = "Controller for Managing user-related operations and data.")
@RequestMapping(path = "/practice/{practiceId}/users", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

	private final UserService userService;

	private static final String SORT_ORDER_ASC = "asc";

	public UserController(UserService userModelService) {
		this.userService = userModelService;
	}

	@PostMapping
	@Operation(summary = "Create new user")
	public ResponseEntity<User> createUser(@RequestBody User model) {
		return new ResponseEntity<>(userService.createUser(model), HttpStatus.OK);

	}

	@PutMapping("/{userId}")
	@Operation(summary = "Update existing user details byId")
	public User updateUser(@PathVariable Long userId, @RequestBody User model) throws ResourceNotFoundException {
		return userService.updateUser(userId, model);

	}

	@GetMapping("/{userId}")
	@Operation(summary = "Get user details byId ")
	public ResponseEntity<User> getUser(@PathVariable Long userId) throws ResourceNotFoundException {
		return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete user details byId")
	public void deleteUser(@PathVariable Long userId) throws ResourceNotFoundException {
		userService.deleteUser(userId);

	}

	@GetMapping
	@Operation(summary = "Fetch all Users based on filter provided with pagination capabilities.")
	Page<User> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable(required = false) String practiceId, @RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return userService.findAll(paging, query, status, practiceId);

	}

	@PatchMapping("/{userId}/accounts")
	@Operation(
			summary = "Link/Delink User with accounts.(This API maps the accounts being passed in requst with the user "
					+ "such as the requested mapping is the current state of user to account association")
	public User linkUserToAccounts(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@RequestParam List<Long> accountIds) throws ResourceNotFoundException {
		return userService.linkUserToAccounts(userId, accountIds);

	}

	@PatchMapping("/{userId}/roles-permissions")
	@Operation(summary = "Link User with role and permissions")
	public ResponseEntity<User> linkUserToRole(@PathVariable @NotNull(message = "User ID can not be null") Long userId,
			@RequestParam @NotNull(message = "Role Id can not be null") Long roleId,
			@RequestBody @NotEmpty(message = "Permissions can not be empty") @NotNull(
					message = "Permissions can not be null") Set<Permission> permissions)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.linkUserToRoleAndPermissions(userId, roleId, permissions));
	}

}
