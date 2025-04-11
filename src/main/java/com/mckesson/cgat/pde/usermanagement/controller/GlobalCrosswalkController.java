package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.service.CrosswalkService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "CrossWalk", description = "Controller for Manages crosswalk-related operations and data.")
@RequestMapping(path = "/crosswalks", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalCrosswalkController {

	public GlobalCrosswalkController(CrosswalkService crosswalkService) {
		this.crosswalkService = crosswalkService;
	}

	private final CrosswalkService crosswalkService;

	private static final String SORT_ORDER_ASC = "asc";

	// @RequirePermissions({ Permission.MANAGE_ORGANIZATION_SCREEN,
	// Permission.MANAGE_USERS_SCREEN,
	// Permission.MANAGE_SP_ACCOUNT })
	@GetMapping
	@Operation(summary = "Fetch all CrossWalks based on filter provided with pagination capabilities.")
	Page<CrosswalkResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "material_description") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return crosswalkService.findAll(paging, query, status);
	}

}
