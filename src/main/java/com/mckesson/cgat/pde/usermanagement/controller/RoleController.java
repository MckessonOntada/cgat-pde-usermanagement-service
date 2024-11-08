package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "roles", description = "Controller for Manages role/permission related operations and data.")
@RequestMapping(path = "/practice/{practiceId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)

public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PostMapping
	@Operation(summary = "Create Role")
	public ResponseEntity<Role> createRole(@Validated @RequestBody Role role, @PathVariable String practiceId)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@PutMapping("/{roleId}")
	@Operation(summary = "Update Role / Add permissions to Role")
	public ResponseEntity<Role> updateRole(@Validated @RequestBody Role role, @PathVariable String practiceId,
			@PathVariable String roleId) throws ResourceNotFoundException {
		Role roleObj = roleService.getRole(roleId);
		if (roleObj == null)
			throw new ResourceNotFoundException(roleId);
		role.setId(Long.valueOf(roleId));
		return ResponseEntity.status(HttpStatus.OK).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@PatchMapping("/{roleId}/permissions")
	@Operation(summary = "Link/Unlink Roles to permission. Associated the permission in current request with the role.")
	public ResponseEntity<Role> linkPermissionToRole(
			@PathVariable @NotNull(message = "Role Id can not be null") Long roleId,
			@RequestBody @NotNull(message = "Permissions can not be null") @NotEmpty(
					message = "Permissions can not be Empty") Set<Permission> permissions)
			throws ResourceNotFoundException {
		return ResponseEntity.status(HttpStatus.OK).body(roleService.linkPermissionToRole(roleId, permissions));
	}

	@GetMapping("/{roleId}")
	@Operation(summary = "Get Role by Id")
	public ResponseEntity<Role> getRole(@PathVariable String roleId) throws ResourceNotFoundException {
		return new ResponseEntity<>(roleService.getRole(roleId), HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Fetch all Roles based on filter provided with pagination capabilities.")
	Page<Role> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(required = false) List<String> status, @RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) String query, @PathVariable String practiceId) {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return roleService.findAll(paging, query, practiceId, status);
	}

}
