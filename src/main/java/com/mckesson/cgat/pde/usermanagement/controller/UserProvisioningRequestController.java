package com.mckesson.cgat.pde.usermanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningRequest;
import com.mckesson.cgat.pde.usermanagement.model.UserProvisioningStaus;
import com.mckesson.cgat.pde.usermanagement.service.UserProvisioningRequestService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

	@PatchMapping("/status/{userProvisioningReqId}")
	@Operation(summary = "Update user provisioning status")
	public ResponseEntity<Object> updateUserProvisioningStatus(@RequestParam UserProvisioningStaus status,
			@PathVariable Long userProvisioningReqId) throws ResourceNotFoundException {
		return new ResponseEntity<>(
				userProvisioningRequestService.updateUserProvisioningStatus(userProvisioningReqId, status),
				HttpStatus.OK);
	}

	@GetMapping("/request")
	@Operation(summary = "Fetch all user provisioning request List.")
	public List<UserProvisioningRequest> getAllUserProvisioning() throws ResourceNotFoundException {
		return userProvisioningRequestService.getAllUserProvisioning();
	}

	@GetMapping
	@Operation(
			summary = "Fetch all user provisioning request List based on filter provided with pagination capabilities.")
	Page<UserProvisioningRequest> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "first_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam UserProvisioningStaus status,
			@RequestParam(required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = CustomUtils.API_DATE_FORMAT) LocalDate endDate)
			throws BadRequestException, UnauthorizedAccessException {

		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		if (!((null == startDate && null == endDate) || (null != startDate && null != endDate)))
			throw new BadRequestException("Either enter both dates or none for the filter as its a part of date range");
		return userProvisioningRequestService.findAll(paging, query, status, OrganizationContext.getCurrentOrgId(),
				startDate, endDate);

	}

}
