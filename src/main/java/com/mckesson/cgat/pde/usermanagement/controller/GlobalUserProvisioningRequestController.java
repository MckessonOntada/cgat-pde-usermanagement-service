package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@Tag(name = "Users Provisioning Request",
		description = "Controller for Managing user-provisioning request related operations and data.")
@RequestMapping(path = "/users-provisioning-requests", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalUserProvisioningRequestController {

	@Autowired
	private PracticeService practiceService;

	@Autowired
	private UserProvisioningRequestService userProvisioningRequestService;

	@PostMapping
	@Operation(summary = "Create user provisioning request")
	public ResponseEntity<UserProvisioningRequest> createUserProvisioning(
			@Valid @RequestBody UserProvisioningRequest request) {
		return new ResponseEntity<>(userProvisioningRequestService.createUserProvisioning(request), HttpStatus.CREATED);
	}

	@GetMapping("/filterPractices")
	@Operation(summary = "Fetch all the available Practices based on filter provided with pagination capabilities.")
	Page<CustomDTOs.GlobalPracticeResponseDto> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status) {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		Page<Practice> practices = practiceService.findAll(paging, query, status);
		return practices.map(CustomDTOs.GlobalPracticeResponseDto::convert);
	}

}
