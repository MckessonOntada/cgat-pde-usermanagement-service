package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "roles", description = "Controller for Manages role/permission related operations and data.")
@RequestMapping(path = "/practice/{practiceId}/role", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

	private static final String SORT_ORDER_ASC = "asc";

	@Autowired
	RoleService roleService;

	@PostMapping
	@Operation(summary = "Create Role")
	public ResponseEntity<Role> createRole(@Validated @RequestBody Role role, @PathVariable String practiceId)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@PutMapping("/{roleId}")
	@Operation(summary = "Update Role / Add permissions to Role")
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

	@GetMapping("/filterRoles")
	@Operation(summary = "Fetch all Roles based on filter provided with pagination capabilities.")
	Page<Role> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "displayName") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return roleService.findAll(paging, query, status);
	}

}
