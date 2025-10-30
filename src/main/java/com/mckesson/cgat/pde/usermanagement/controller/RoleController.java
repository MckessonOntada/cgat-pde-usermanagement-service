package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.CommunicationPreferences;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Practice Roles & Permissions",
		description = "Manages roles and their associated permissions within a specific practice.")
@RequestMapping(path = "/practices/{practiceId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)

public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@RequirePermissions({ Permission.ADD_ROLES })
	@PostMapping
	@Operation(summary = "Create a new role", description = "Creates a new role within the specified practice.")
	public ResponseEntity<Role> createRole(@Validated @RequestBody Role role, @PathVariable String practiceId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createOrUpdateRole(role, practiceId));
	}

	@RequirePermissions({ Permission.ADD_ROLES, Permission.EDIT_ROLES })
	@PatchMapping("/{roleId}")
	@Operation(summary = "Update a role's basic details (name and description)",
			description = "Updates the name and description of an existing role, identified by its ID.")
	public ResponseEntity<Role> updateRole(@PathVariable @NotNull(message = "Role Id can not be null") Long roleId,
			@Validated(BasicInfo.class) @RequestBody Role updatedRole)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return ResponseEntity.status(HttpStatus.OK).body(roleService.updateRole(roleId, updatedRole));
	}

	@RequirePermissions({ Permission.ADD_ROLES, Permission.EDIT_ROLES })
	@PatchMapping("/{roleId}/permissions")
	@Operation(summary = "Update the permissions assigned to a role",
			description = "Updates the set of permissions that are assigned to a specific role.")
	public ResponseEntity<Role> updatePermissionsForRole(
			@PathVariable @NotNull(message = "Role Id can not be null") Long roleId,
			@Valid @RequestBody Role updatedRole) throws ResourceNotFoundException, UnauthorizedAccessException {
		return ResponseEntity.status(HttpStatus.OK).body(roleService.updatePermissionForRole(roleId, updatedRole));
	}

	@RequirePermissions({ Permission.VIEW_ROLES })
	@GetMapping("/{roleId}")
	@Operation(summary = "Get a role by its ID",
			description = "Retrieves the full details for a single role by its unique ID.")
	public ResponseEntity<Role> getRole(@PathVariable Long roleId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return new ResponseEntity<>(roleService.getRole(roleId), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.EDIT_ROLES })
	@DeleteMapping("/{roleId}")
	@Operation(summary = "Delete a role by its ID", description = "Deletes a specified role from the practice.")
	public ResponseEntity<Object> deleteRole(@PathVariable Long roleId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		roleService.deleteRole(roleId);
		return new ResponseEntity<>(roleId, HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_ROLES_SCREEN })
	@GetMapping
	@Operation(summary = "Find and filter all roles with pagination",
			description = "Retrieves a paginated and filtered list of all roles for the specified practice.")
	Page<Role> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(required = false) List<String> status, @RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) String query, @PathVariable String practiceId,
			@Parameter(description = "Date format : " + CustomUtils.API_DATE_FORMAT) @RequestParam(
					required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate startDate,
			@Parameter(description = "Date format : " + CustomUtils.API_DATE_FORMAT) @RequestParam(
					required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate endDate)
			throws BadRequestException, UnauthorizedAccessException {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		if (!((null == startDate && null == endDate) || (null != startDate && null != endDate)))
			throw new BadRequestException("Either enter both dates or none for the filter as its a part of date range");

		return roleService.findAll(paging, query, practiceId, status, startDate, endDate);
	}

	@RequirePermissions({ Permission.ADD_ROLES, Permission.EDIT_ROLES })
	@PatchMapping("/{roleId}/communication-preferences")
	public ResponseEntity<Role> updateCommunicationPreferences(@PathVariable Long roleId,
			@RequestBody CommunicationPreferences communicationPreferences) {
		Role updatedRole = roleService.updateCommunicationPreferences(roleId, communicationPreferences);
		return ResponseEntity.ok(updatedRole);
	}

}
