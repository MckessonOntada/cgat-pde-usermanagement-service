package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.model.CatalogPrice;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Catalogs", description = "Controller for Managing all catalogs.")
@RequestMapping(path = "/catalogs", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
public class GlobalCatalogController {

	private final TherapyService therapyService;

	private final CatalogService catalogService;

	public GlobalCatalogController(TherapyService therapyService, CatalogService catalogService) {
		this.therapyService = therapyService;
		this.catalogService = catalogService;
	}

	@GetMapping
	@Operation(summary = "Fetch all catalogs for provided soldTo accounts lists.")
	public ResponseEntity<Page<CatalogPrice>> findAllCatalogs(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@NotEmpty(message = "Account Numbers can not be empty") @RequestParam List<String> accountNumbers) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllGlobalCatalogs(accountNumbers, query, paging), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_CATALOG })
	@PostMapping("/configure")
	@Operation(
			summary = "Configure the catalog materials according to materialId, variant, soldTo account and shipTo account")
	public ResponseEntity<List<CatalogConfiguration>> configureCatalogMaterial(
			@Valid @RequestBody CatalogConfigurationDTO configuration) {
		return new ResponseEntity<>(catalogService.saveCatalogConfiguration(configuration), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.VIEW_CATALOG })
	@GetMapping("/configuration")
	@Operation(
			summary = "Get catalog configuration with catalog price according to materialId, variant, soldTo account and shipTo account")
	public ResponseEntity<CatalogResponseDTO> findCatalogConfiguration(@RequestParam String materialId,
			@RequestParam(required = false) String variant, @RequestParam String shipToAccount,
			@RequestParam String soldToAccount) throws ResourceNotFoundException {
		CatalogResponseDTO catalogConfiguration = catalogService.findCatalogConfiguration(materialId, variant,
				shipToAccount, soldToAccount);
		return ResponseEntity.ok(catalogConfiguration);
	}

	@RequirePermissions({ Permission.MANAGE_CATALOG })
	@DeleteMapping("/configuration")
	@Operation(summary = "Delete catalog configuration using materialId, variant, soldTo account and shipTo account")
	public ResponseEntity<String> deleteCatalogConfiguration(@RequestParam String materialId,
			@RequestParam(required = false) String variant, @RequestParam String shipToAccount,
			@RequestParam String soldToAccount) throws ResourceNotFoundException {
		catalogService.deleteCatalogConfiguration(materialId, variant, shipToAccount, soldToAccount);
		return ResponseEntity.ok("Configuration removed");
	}

	@RequirePermissions({ Permission.ADD_ORDERS, Permission.EDIT_ORDERS })
	@GetMapping("/configuration/material/{materialId}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}")
	@Operation(
			summary = "Get catalog configuration according to materialId, variant, soldTo account and shipTo account")
	public ResponseEntity<CatalogConfiguration> findCatalogConf(
			@PathVariable @NotNull(message = "'soldToAccountId' cannot be null") @NotEmpty(
					message = "'soldToAccount' cannot be empty") String soldToAccountId,
			@PathVariable @NotNull(message = "'shipToAccountId' cannot be null") @NotEmpty(
					message = "'shipToAccount' cannot be empty") String shipToAccountId,
			@RequestParam(required = false) String variant,
			@PathVariable @NotNull(message = "'materialId' cannot be null") String materialId)
			throws ResourceNotFoundException {
		CatalogConfiguration configuration = catalogService.findCatalogConf(materialId, variant, shipToAccountId,
				soldToAccountId);
		return ResponseEntity.ok(configuration);
	}

}
