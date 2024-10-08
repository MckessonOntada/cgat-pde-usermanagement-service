package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;

@RestController
@Tag(name = "roles", description = "Controller for Manages role/permission related operations and data.")
@RequestMapping(path = "/roles", produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

	@Autowired
	RoleService roleService;

	@PostMapping
	@Operation(summary = "Create Role")
	public ResponseEntity<Role> createRole( @Validated @RequestBody Role role){
		return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createOrUpdateRole(role));
	}

	@PutMapping(path = "/{roleId}")
	@Operation(summary = "Update Role")
	public ResponseEntity<Role> updateRole(@Validated @RequestBody Role role){
		return ResponseEntity.status(HttpStatus.OK).body(roleService.createOrUpdateRole(role));
	}
	
	@GetMapping(path = "/{roleId}")
	@Operation(summary = "Fetch Role Details")
	public ResponseEntity<Role> getRoles(@PathVariable("roleId") String id) throws ResourceNotFoundException {
		return new ResponseEntity<>(roleService.getRole(id), HttpStatus.OK);
	}

}
