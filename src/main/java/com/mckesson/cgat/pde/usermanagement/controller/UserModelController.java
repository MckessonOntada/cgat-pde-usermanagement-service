package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mckesson.cgat.pde.usermanagement.dto.UserModelRequestDto;
import com.mckesson.cgat.pde.usermanagement.model.UserModel;
import com.mckesson.cgat.pde.usermanagement.service.UserModelService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Users", description = "Controller for Manages user-related operations and data.")
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserModelController {

	private final UserModelService userModelService;

	public UserModelController(UserModelService userModelService) {
		this.userModelService = userModelService;
	}

	@PostMapping("/create-user/{userId}")
	@Operation(summary = "Create new user")
	public ResponseEntity<Object> createUser(@PathVariable String userId, @RequestBody UserModelRequestDto dto) throws Exception {
		userModelService.createUser(userId,dto);
		return new ResponseEntity<>("Success", HttpStatus.OK);

	}

	@GetMapping("/users/{userId}")
	@Operation(summary = "Getting all user details")
	public ResponseEntity<Object> users(@PathVariable String userId) throws ResourceNotFoundException {
		List<UserModel> users = userModelService.users(userId);
		return new ResponseEntity<>(users, HttpStatus.OK);

	}

	@GetMapping("/delete-user/{userId}")
	@Operation(summary = "Delete user details")
	public ResponseEntity<Object> deleteUsers(@PathVariable String userId) throws ResourceNotFoundException {
		String user = userModelService.deleteUsers(userId);
		return new ResponseEntity<>(user + " has been deleted", HttpStatus.OK);

	}

}
