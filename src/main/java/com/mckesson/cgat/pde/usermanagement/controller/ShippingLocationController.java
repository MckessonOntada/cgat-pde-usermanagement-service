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
@Tag(name = "Shipping Location", description = "Controller for Managing Shipping Location for SP Account.")
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
	@Operation(summary = "Create new shipping location")
	public ResponseEntity<ShippingLocation> createShippingLocation(
			@Valid @RequestBody ShippingLocation shippingLocation) throws UnauthorizedAccessException {
		ShippingLocation createdShippingLocation = shippingLocationService.createShippingLocation(shippingLocation);
		log.info("Created new shipping location with ID: {}", createdShippingLocation.getId());
		return new ResponseEntity<>(createdShippingLocation, HttpStatus.CREATED);
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT })
	@PutMapping("/{shippingLocationId}")
	@Operation(summary = "Update existing shipping location")
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
	@Operation(summary = "Delete shipping locations by IDs")
	public ResponseEntity<Void> removeShippingLocations(@RequestBody List<Long> shippingLocationIds)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		shippingLocationService.removeShippingLocations(shippingLocationIds);
		log.info("Deleted {} shipping locations", shippingLocationIds.size());
		return ResponseEntity.noContent().build();
	}

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_SP_ACCOUNT, Permission.MANAGE_LINKED_ACCOUNTS })
	@GetMapping
	@Operation(summary = "Fetch all Shipping Locations based on filter provided with pagination capabilities.")
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

	@RequirePermissions({ Permission.ADD_SP_ACCOUNT, Permission.MANAGE_SP_ACCOUNT, Permission.THERAPY_SELECTION,
			Permission.ACCOUNT_SELECTION, Permission.UPDATE_PATIENTS_DETAILS, Permission.UPDATE_ADDITIONAL_DETAILS,
			Permission.SUBMIT_ORDER })
	@GetMapping("/{shippingId}")
	@Operation(summary = "Fetch Shipping Location by shippingId")
	ShippingLocation findById(@NotNull @PathVariable Long accountId, @NotNull @PathVariable Long shippingId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		return shippingLocationService.findById(shippingId);
	}

}
