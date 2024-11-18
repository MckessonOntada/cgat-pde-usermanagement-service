package com.mckesson.cgat.pde.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Users Provisioning",
		description = "Controller for Managing user-provisioning related operations and data.")
@RequestMapping(path = "/practices/{practiceId}/users-provisioning", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserProvisioningRequestController {

	@Autowired
	private UserProvisioningRequestService userProvisioningRequestService;

	private static final String SORT_ORDER_ASC = "asc";

	@PostMapping
	@Operation(summary = "Create user provisioning ")
	public ResponseEntity<UserProvisioningRequest> createUserProvisioning(
			@Valid @RequestBody UserProvisioningRequest request,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType) {
		return new ResponseEntity<>(userProvisioningRequestService.createUserProvisioning(request), HttpStatus.OK);
	}

	@PatchMapping("/{userId}")
	@Operation(summary = "Update user provisioning status")
	public ResponseEntity<Object> updateUserProvisioningStatus(
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@PathVariable Long userId) throws ResourceNotFoundException {
		return new ResponseEntity<>(userProvisioningRequestService.updateUserProvisioningStatus(userId), HttpStatus.OK);
	}

	@GetMapping
	@Operation(
			summary = "Fetch all user provisioning request List based on filter provided with pagination capabilities.")
	Page<UserProvisioningRequest> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@NotNull(message = "User Type should not be null") @RequestParam UserType userType,
			@PathVariable Long practiceId) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return userProvisioningRequestService.findAll(paging, query, practiceId);

	}

}
