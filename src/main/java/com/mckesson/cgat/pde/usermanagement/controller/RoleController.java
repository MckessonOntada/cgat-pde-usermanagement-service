package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.RoleDto;
import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission2;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "roles", description = "Controller for Manages role/permission related operations and data.")
@RequestMapping(path = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

	@Autowired
	RoleService roleService;

	@PostMapping
	@Operation(summary = "Create or update Role")
	public ResponseEntity<String> createRole(@RequestBody RoleDto dto) throws ResourceNotFoundException {
		String status = roleService.createOrUpdateRole(dto);
		if (status.equals(CustomUtils.operation_type.CREATE.name()))
			return ResponseEntity.status(HttpStatus.CREATED).body("Role created successfully");
		else
			return ResponseEntity.status(HttpStatus.OK).body("Role updated successfully");
	}

	@GetMapping
	@Operation(summary = "Fetch Roles Details")
	public ResponseEntity<List<RoleResponseDto>> getRoles() {
		List<RoleResponseDto> allRoles = roleService.getAllRoles();
		return new ResponseEntity<>(allRoles, HttpStatus.OK);

	}

	@PostMapping("/permission")
	@Operation(summary = "Create permission")
	public ResponseEntity<String> createPermission(@RequestParam String name, @RequestParam String label) {
		roleService.createPermission(name, label);
		return ResponseEntity.status(HttpStatus.CREATED).body("Permission created successfully with name : " + name);
	}

	@DeleteMapping("/permission/{permissionId}")
	@Operation(summary = "Delete permission Details")
	public ResponseEntity<String> deletePermission(@PathVariable String permissionId) throws ResourceNotFoundException {
		String deletedPermission = roleService.deletePermission(permissionId);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Permission deleted successfully with name : " + deletedPermission);

	}

	@GetMapping("/permission")
	@Operation(summary = "Fetch permissions Details")
	public ResponseEntity<List<Permission2>> getAllPermissions() {

		List<Permission2> response = new ArrayList<>();
		response = roleService.getAllPermissions();

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
