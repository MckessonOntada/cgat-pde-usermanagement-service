package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.ShippingLocationService;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "SP Account Shipping Locations",
		description = "Manages shipping locations for a specific Specialty Pharma (SP) account.")
@RequestMapping(path = "/practices/{practiceId}/accounts/{accountId}/shipping-locations", consumes = "application/json",
		produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ShippingLocationController {

	private static final String SORT_ORDER_ASC = "asc";

	private final ShippingLocationService shippingLocationService;

	public ShippingLocationController(ShippingLocationService shippingLocationService) {
		this.shippingLocationService = shippingLocationService;
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT })
	@PostMapping(consumes = "application/json")
	@Operation(summary = "Create a new shipping location for an SP account",
			description = "Creates a new shipping location and associates it with the specified Specialty Pharma (SP) account.")
	public ResponseEntity<ShippingLocation> createShippingLocation(
			@Valid @RequestBody ShippingLocation shippingLocation) throws UnauthorizedAccessException {
		ShippingLocation createdShippingLocation = shippingLocationService.createShippingLocation(shippingLocation);
		log.info("Created new shipping location with ID: {}", createdShippingLocation.getId());
		return new ResponseEntity<>(createdShippingLocation, HttpStatus.CREATED);
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT })
	@PutMapping("/{shippingLocationId}")
	@Operation(summary = "Update an existing shipping location",
			description = "Updates the details of an existing shipping location, identified by its ID.")
	public ResponseEntity<ShippingLocation> updateShippingLocation(@PathVariable Long shippingLocationId,
			@Valid @RequestBody ShippingLocation shippingLocation)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		ShippingLocation updatedShippingLocation = shippingLocationService.updateShippingLocation(shippingLocationId,
				shippingLocation);
		log.info("Updated shipping location with ID: {}", updatedShippingLocation.getId());
		return ResponseEntity.ok(updatedShippingLocation);
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT })
	@DeleteMapping
	@Operation(summary = "Delete multiple shipping locations by their IDs",
			description = "Deletes one or more shipping locations from the system based on a list of their IDs.")
	public ResponseEntity<Void> removeShippingLocations(@RequestBody List<Long> shippingLocationIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		shippingLocationService.removeShippingLocations(shippingLocationIds);
		log.info("Deleted {} shipping locations", shippingLocationIds.size());
		return ResponseEntity.noContent().build();
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_SP_ACCOUNT, Permission.MANAGE_LINKED_ACCOUNTS })
	@GetMapping
	@Operation(summary = "Find and filter all shipping locations for an SP account",
			description = "Retrieves a paginated and filtered list of all shipping locations for the specified Specialty Pharma (SP) account.")
	Page<ShippingLocation> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable String accountId) throws ResourceNotFoundException, UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return shippingLocationService.findAll(paging, query, OrganizationContext.getCurrentOrgId(),
				Long.valueOf(accountId));
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_SP_ACCOUNT, Permission.ACCOUNT_SELECTION })
	@GetMapping("/{shippingId}")
	@Operation(summary = "Get a shipping location by its ID",
			description = "Retrieves the full details for a single shipping location by its unique ID.")
	ShippingLocation findById(@NotNull @PathVariable Long accountId, @NotNull @PathVariable Long shippingId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return shippingLocationService.findById(shippingId);
	}

}
