package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Global Permissions", description = "Provides endpoints for retrieving available system permissions.")
@RequestMapping(path = "/global/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalPermissionController {

	private final RoleService roleService;

	public GlobalPermissionController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	@Operation(summary = "Get all permissions available in the system",
			description = "Retrieves a complete list of all possible permissions defined within the application.")
	public ResponseEntity<List<Permission>> getPermissions() {
		return new ResponseEntity<>(Arrays.asList(Permission.values()), HttpStatus.OK);
	}

	@GetMapping("/{userType}")
	@Operation(summary = "Get all permissions available for a specific user type",
			description = "Retrieves a set of permissions that are applicable to a specific user type (e.g., PRACTICE_USER, INTERNAL_USER).")
	public ResponseEntity<Set<Permission>> getPermissions(@PathVariable UserType userType)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return new ResponseEntity<>(Permission.getPermissions(userType), HttpStatus.OK);
	}

}
