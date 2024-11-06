package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Permissions", description = "Controller for getting all permissions.")
@RequestMapping(path = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalPermissionController {

	private final RoleService roleService;

	public GlobalPermissionController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	@Operation(summary = "Fetch AllPermissions")
	public ResponseEntity<List<Permission>> getPermissions() {
		return new ResponseEntity<>(roleService.getAllPermissions(), HttpStatus.OK);
	}

}
