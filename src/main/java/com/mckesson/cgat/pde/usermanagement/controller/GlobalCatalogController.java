package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.List;
import java.util.Map;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.FileValidationUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.SORT_ORDER_ASC;

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

	private static final Map<String, String> catalog_sortby_columns = Map.of("materialDescription",
			"materialDescriptionInLanguageEn", "manufacturer", "m.manufacturerName", "accountNumber",
			"cp.soldToCustomer");

	@RequirePermissions({ Permission.VIEW_CATALOG, Permission.VIEW_SP_CATALOG })
	@GetMapping
	@Operation(summary = "Fetch all catalogs for provided soldTo accounts lists.")
	public ResponseEntity<Page<CatalogPriceResponse>> findAllCatalogs(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> accountNumbers,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType,
			@RequestParam(defaultValue = "sapMaterial") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder,
			@RequestParam(required = false) List<Integer> practiceIdList) {

		String mappedSortBy = catalog_sortby_columns.getOrDefault(sortBy, "sapMaterial");
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(mappedSortBy).ascending()
				: Sort.by(mappedSortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return new ResponseEntity<>(
				therapyService.findAllGlobalCatalogs(accountNumbers, query, paging, channelType, practiceIdList),
				HttpStatus.OK);
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
	public ResponseEntity<CatalogDetailsResponseDTO> findCatalogConfiguration(@RequestParam String materialId,
			@RequestParam(required = false) String variant, @RequestParam String shipToAccount,
			@RequestParam String soldToAccount, @RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
			throws ResourceNotFoundException {
		CatalogDetailsResponseDTO catalogConfiguration = catalogService.findCatalogConfiguration(materialId, variant,
				shipToAccount, soldToAccount, channelType);
		return ResponseEntity.ok(catalogConfiguration);
	}

	@RequirePermissions({ Permission.MANAGE_CATALOG })
	@DeleteMapping("/configuration")
	@Operation(
			summary = "Delete catalog configuration using materialId, variant, soldTo account, shipTo account and channelType")
	public ResponseEntity<String> deleteCatalogConfiguration(@RequestParam String materialId,
			@RequestParam(required = false) String variant, @RequestParam String shipToAccount,
			@RequestParam String soldToAccount, @RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
			throws ResourceNotFoundException {
		catalogService.deleteCatalogConfiguration(materialId, variant, shipToAccount, soldToAccount, channelType);
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
			@PathVariable @NotNull(message = "'materialId' cannot be null") String materialId) {
		CatalogConfiguration configuration = catalogService.findCatalogConf(materialId, variant, shipToAccountId,
				soldToAccountId);
		return ResponseEntity.ok(configuration);
	}

	@RequirePermissions({ Permission.MANAGE_SP_CATALOG })
	@DeleteMapping("/specialityPharma/deLink")
	@Operation(summary = "Delink SP catalog configuration according to sap_material and sold_to_customer")
	public ResponseEntity<Void> deLinkSPCatalog(@RequestBody List<CatalogDeLinkRequestDTO> catalogList) {
		therapyService.deLinkSPCatalog(catalogList);
		return ResponseEntity.ok().build();
	}

	@RequirePermissions({ Permission.MANAGE_SP_CATALOG })
	@PostMapping("/specialityPharma")
	@Operation(summary = "Link SP catalog configuration according to SP Product ID and SP Account number")
	public ResponseEntity<CatalogLinkResponseDto> linkSPCatalog(@RequestBody List<CatalogRequestDTO> catalogList) {
		return ResponseEntity.ok(catalogService.linkSPCatalog(catalogList));
	}

	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@RequirePermissions({ Permission.MANAGE_SP_CATALOG })
	@Operation(summary = "Upload bulk SP catalogs")
	public ResponseEntity<?> uploadSPCatalogCsv(@RequestPart("catalogsData") MultipartFile catalogsData) {
		FileValidationUtils.validateMultiPartFile(catalogsData);

		CSVProcessingResult result = catalogService.uploadSPCatalog(catalogsData);

		if (CollectionsUtils.hasItems(result.getErrors())) {
			ByteArrayResource resource = new ByteArrayResource(result.getErrorFileContent());
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.contentType(MediaType.parseMediaType("text/csv"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=error_report.csv")
				.body(resource);
		}
		return ResponseEntity.ok("File processed successfully, No errors were found.");
	}

	@GetMapping("/download")
	@Operation(summary = "Download all sp catalogs for provided sp accounts as a CSV file.")
	public ResponseEntity<StreamingResponseBody> downloadAllCatalogs(
			@NotEmpty(message = "Account Numbers cannot be empty") @RequestParam List<String> accountIds,
			@RequestParam(defaultValue = "sap_material") String sortBy,
			@RequestParam(defaultValue = "desc") String sortOrder, @RequestParam(required = false) String query) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(0, Integer.MAX_VALUE, sort);
		StreamingResponseBody stream = catalogService.downloadAllGlobalCatalogs(accountIds, paging, query);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sp_catalogs.csv")
			.contentType(MediaType.parseMediaType("text/csv"))
			.body(stream);
	}

}
