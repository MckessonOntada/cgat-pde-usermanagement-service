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

import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.cgat.pde.usermanagement.service.UserModelService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Users", description = "Controller for Manages user-related operations and data.")
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserModelController {

	private final UserModelService userModelService;

	private static final String SORT_ORDER_ASC = "asc";

	public UserModelController(UserModelService userModelService) {
		this.userModelService = userModelService;
	}

	@PostMapping
	@Operation(summary = "Create new user")
	public ResponseEntity<Object> createUser(@RequestBody UserModel model) throws BadRequestException {
		return new ResponseEntity<>(userModelService.createUser(model), HttpStatus.OK);

	}

	@PutMapping("/{userId}")
	@Operation(summary = "Update existing user details")
	public ResponseEntity<HttpStatus> updateUser(@PathVariable String userId, @RequestBody UserModel model)
			throws ResourceNotFoundException {
		userModelService.updateUser(userId, model);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@GetMapping("/{userId}")
	@Operation(summary = "Get user details byId ")
	public ResponseEntity<Object> users(@PathVariable String userId) throws ResourceNotFoundException {
		List<UserModel> users = userModelService.users(userId);
		return new ResponseEntity<>(users, HttpStatus.OK);

	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "Delete user details")
	public ResponseEntity<Object> deleteUsers(@PathVariable String userId) throws ResourceNotFoundException {
		String user = userModelService.deleteUsers(userId);
		return new ResponseEntity<>(user + " has been deleted", HttpStatus.OK);

	}

	@GetMapping
	@Operation(summary = "Fetch all Users based on filter provided with pagination capabilities.")
	Page<UserModel> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "displayName") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return userModelService.findAll(paging, query, status);

	}

}
