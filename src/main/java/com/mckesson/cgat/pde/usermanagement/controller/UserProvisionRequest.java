package com.mckesson.cgat.pde.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users Provisioning Request",
		description = "Controller for Managing user-provisioning request related operations and data.")
@RequestMapping(path = "/users-provisioning", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserProvisionRequest {

	@Autowired
	private UserProvisioningRequestService userProvisioningRequestService;

	@PostMapping
	@Operation(summary = "Create user provisioning ")
	public ResponseEntity<UserProvisioningRequest> createUserProvisioning(
			@Valid @RequestBody UserProvisioningRequest request) {
		return new ResponseEntity<>(userProvisioningRequestService.createUserProvisioning(request), HttpStatus.OK);
	}

}
