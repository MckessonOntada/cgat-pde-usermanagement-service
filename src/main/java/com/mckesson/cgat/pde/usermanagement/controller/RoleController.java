package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "roles", description = "Controller for Manages role/permission related operations and data.")
@RequestMapping(path = "/practice/{practiceId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

	@Autowired
	RoleService roleService;

	@PostMapping("/role")
	@Operation(summary = "Create Role")
	public ResponseEntity<Role> createRole(@Validated @RequestBody Role role, @PathVariable String practiceId)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@PutMapping("/role/{roleId}")
	@Operation(summary = "Update Role")
	public ResponseEntity<Role> updateRole(@Validated @RequestBody Role role, @PathVariable String practiceId)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@GetMapping
	@Operation(summary = "Fetch Role Details")
	public ResponseEntity<List<Role>> getRolesByPracticeId(@PathVariable String practiceId)
			throws ResourceNotFoundException {
		return new ResponseEntity<>(roleService.getRolesByPracticeId(practiceId), HttpStatus.OK);
	}

	@GetMapping("/permission")
	@Operation(summary = "Fetch AllPermissions Details")
	public ResponseEntity<List<Permission>> getPermissions() {
		return new ResponseEntity<>(roleService.getAllPermissions(), HttpStatus.OK);
	}

}
