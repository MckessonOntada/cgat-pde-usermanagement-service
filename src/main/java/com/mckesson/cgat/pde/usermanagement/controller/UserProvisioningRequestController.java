package com.mckesson.cgat.pde.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users Provisioning", description = "Controller for Managing user-provisioning related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users-provisioning", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)

@Slf4j
public class UserProvisioningRequestController {
	
	@Autowired
	private UserProvisioningRequestService userProvisioningRequestService;
	
	
	@PostMapping
	@Operation(summary = "Create user provisioning ")
	public ResponseEntity<UserProvisioningRequest> createUserProvisioning(@Valid @RequestBody UserProvisioningRequest request,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType)
			throws UnauthorizedAccessException {
		return new ResponseEntity<>(userProvisioningRequestService.createUserProvisioning(request), HttpStatus.OK);
	}
	

}
