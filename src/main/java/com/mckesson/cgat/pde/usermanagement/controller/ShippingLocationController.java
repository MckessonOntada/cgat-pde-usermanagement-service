package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.ShippingLocationService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

	@RequirePermissions({ Permission.ADD_SHIPPING_LOCATION })
	@PostMapping(consumes = "application/json")
	@Operation(summary = "Create new shipping location")
	public ResponseEntity<ShippingLocation> createShippingLocation(
			@Valid @RequestBody ShippingLocation shippingLocation) {
		ShippingLocation createdShippingLocation = shippingLocationService.createShippingLocation(shippingLocation);
		return new ResponseEntity<>(createdShippingLocation, HttpStatus.CREATED);
	}

	@RequirePermissions({ Permission.ADD_SHIPPING_LOCATION, Permission.EDIT_SHIPPING_LOCATION })
	@PutMapping("/{shippingAccountNumber}")
	@Operation(summary = "Update existing shipping location")
	public ResponseEntity<ShippingLocation> updateShippingLocation(@PathVariable Long shippingAccountNumber,
			@Valid @RequestBody ShippingLocation shippingLocation) throws ResourceNotFoundException {
		ShippingLocation updatedShippingLocation = shippingLocationService.updateShippingLocation(shippingAccountNumber,
				shippingLocation);
		return ResponseEntity.ok(updatedShippingLocation);
	}

	@RequirePermissions({ Permission.EDIT_SHIPPING_LOCATION })
	@DeleteMapping("/{shippingAccountNumber}")
	@Operation(summary = "Delete existing shipping location")
	public ResponseEntity<Void> removeShippingLocation(@PathVariable Long shippingAccountNumber)
			throws ResourceNotFoundException {
		shippingLocationService.removeShippingLocation(shippingAccountNumber);
		return ResponseEntity.noContent().build();
	}

	@RequirePermissions({ Permission.EDIT_SHIPPING_LOCATION })
	@DeleteMapping("/remove-multiple")
	@Operation(summary = "Delete multiple shipping locations")
	public ResponseEntity<Void> removeMultipleShippingLocations(@RequestBody List<ShippingLocation> shippingLocations)
			throws ResourceNotFoundException {
		shippingLocationService.removeMultipleShippingLocations(shippingLocations);
		return ResponseEntity.noContent().build();
	}

	@RequirePermissions({ Permission.ADD_SHIPPING_LOCATION, Permission.EDIT_SHIPPING_LOCATION })
	@GetMapping
	@Operation(summary = "Fetch all Shipping Locations based on filter provided with pagination capabilities.")
	Page<ShippingLocation> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "shipping_account_number") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable String practiceId, @PathVariable String accountId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return shippingLocationService.findAll(paging, query, Long.valueOf(practiceId), Long.valueOf(accountId));
	}

}
