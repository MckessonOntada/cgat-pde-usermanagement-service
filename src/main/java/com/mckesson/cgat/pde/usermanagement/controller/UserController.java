package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;

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

import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users", description = "Controller for Manages user-related operations and data.")
@RequestMapping(path = "/practice/{practiceId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

	private final UserService userService;

	private static final String SORT_ORDER_ASC = "asc";

	public UserController(UserService userModelService) {
		this.userService = userModelService;
	}

	@PostMapping
	@Operation(summary = "Create new user")
	public ResponseEntity<User> createUser(@Valid @RequestBody User model) throws BadRequestException {
		return new ResponseEntity<>(userService.createUser(model), HttpStatus.OK);

	}

	@PutMapping("/{userId}")
	@Operation(summary = "Update existing user details byId")
	public User updateUser(@PathVariable Long userId, @RequestBody User model) throws ResourceNotFoundException {
		return userService.updateUser(userId, model);

	}

	@GetMapping
	@Operation(summary = "Get All user details with paginated result ")
	public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return userService.getAllUsers(pageable);

	}

	@GetMapping("/{userId}")
	@Operation(summary = "Get user details byId ")
	public ResponseEntity<User> getUser(@PathVariable Long userId) throws ResourceNotFoundException {
		return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete user details")
	public void deleteUsers(@PathVariable Long userId) throws ResourceNotFoundException {
		userService.deleteUsers(userId);

	}

	@GetMapping("/filter")
	@Operation(summary = "Fetch all Users based on filter provided with pagination capabilities.")
	Page<User> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) String query,
			@PathVariable(required = false) String practiceId,
			@RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return userService.findAll(paging, query, status,practiceId);

	}
	
	@PatchMapping("/{userId}")
	@Operation(summary = "Link user to account")
	public User linkUserToAccount(@PathVariable Long userId, @RequestBody Map<String,Object> user) throws ResourceNotFoundException {
		return userService.linkUserToAccount(userId, user);

	}

}
