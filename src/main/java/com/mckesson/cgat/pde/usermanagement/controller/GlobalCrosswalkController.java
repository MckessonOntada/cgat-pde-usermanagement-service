package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
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

	private static final Map<String, String> crosswalk_sortby_columns = Map.of("spProductId", "m2.material_number",
			"spProductName", "m2.material_description", "bnbMaterialNumber", "m1.material_number",
			"bnbMaterialDescription", "m1.material_description");

	/*
	 * @RequirePermissions({ Permission.ADD_CROSSWALK })
	 * 
	 * @PostMapping(consumes = "application/json")
	 * 
	 * @Operation(summary = "Create Crosswalk") public ResponseEntity<Crosswalk>
	 * createCrosswalk(@Valid @RequestBody Crosswalk crosswalk) throws
	 * UnauthorizedAccessException, ResourceNotFoundException { return new
	 * ResponseEntity<>(crosswalkService.createCrosswalk(crosswalk), HttpStatus.OK);
	 * 
	 * }
	 */

	@RequirePermissions({ Permission.VIEW_MATERIAL_CROSSWALK })
	@GetMapping
	@Operation(summary = "Fetch all CrossWalks based on filter provided with pagination capabilities.")
	public ResponseEntity<Page<CrosswalkResponseDTO>> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "bnbMaterialDesc") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		String mappedSortBy = crosswalk_sortby_columns.getOrDefault(sortBy, "m1.material_description");

		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(mappedSortBy).ascending()
				: Sort.by(mappedSortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return new ResponseEntity<>(crosswalkService.findAll(paging, query, status), HttpStatus.OK);
	}

}
