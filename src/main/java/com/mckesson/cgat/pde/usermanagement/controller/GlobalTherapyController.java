package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.*;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.SORT_ORDER_ASC;

@RestController
@Tag(name = "Global Therapies",
		description = "Manages global material data for both Buy and Bill and Specialty Pharma channels.")
@RequestMapping(path = "/therapies", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j

public class GlobalTherapyController {

	private final TherapyService therapyService;

	public GlobalTherapyController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.VIEW_SP_MATERIAL, Permission.ADD_CROSSWALK })
	@Operation(summary = "Find and filter all therapies",
			description = "Retrieves a paginated list of all therapies, with extensive filtering options.")
	public ResponseEntity<Page<TradeCatalogResponseDTO>> findAllTherapies(
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(required = false) String query, @RequestParam(defaultValue = "trade_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) List<ChannelType> channelTypes,
			@RequestParam(required = false) List<String> status,
			@RequestParam(required = false) Boolean showNonCrosswalked,
			@RequestParam(defaultValue = "false") Boolean ignoreCrosswalkedSP) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		if (Boolean.TRUE.equals(showNonCrosswalked) && channelTypes != null
				&& channelTypes.contains(ChannelType.SPECIALITY_PHARMA)) {

			return new ResponseEntity<>(
					therapyService.findNonCrosswalkedSPGlobalTherapies(query, paging, channelTypes, status, false),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				therapyService.findAllGlobalTherapies(query, paging, channelTypes, status, ignoreCrosswalkedSP),
				HttpStatus.OK);
	}

	@GetMapping("/type/speciality-pharma")
	@RequirePermissions({ Permission.VIEW_SP_MATERIAL })
	@Operation(summary = "Find and filter all Specialty Pharma therapies",
			description = "Retrieves a paginated list of all Specialty Pharma therapies, with options for filtering by status and manufacturer.")
	public ResponseEntity<Page<Therapy>> findAllSPTherapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(required = false) String sortBy, @RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam Status status, @RequestParam(required = false) String manufacturer) {
		Pageable paging;
		if (StringUtils.isNotBlank(sortBy)) {
			Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
					: Sort.by(sortBy).descending();
			paging = PageRequest.of(pageNo, pageSize, sort);
		}
		else {
			paging = PageRequest.of(pageNo, pageSize);
		}

		return new ResponseEntity<>(therapyService.findAllGlobalSPTherapies(query, paging, status, manufacturer),
				HttpStatus.OK);
	}

	@GetMapping("/{materialNumber}")
	@Operation(summary = "Get therapy details by material number",
			description = "Retrieves the details for a single therapy, identified by its material number and channel type.")
	public ResponseEntity<Therapy> findByMaterialNumber(@PathVariable String materialNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findByMaterialNumber(materialNumber, channelType), HttpStatus.OK);
	}

	@PostMapping("/config/update")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update Buy and Bill therapy configurations in bulk",
			description = "Allows for the bulk update of Buy and Bill therapy configurations based on a list of NDC numbers.")
	public ResponseEntity<String> updateTherapyConfigs(@Valid @RequestBody TherapyConfigRequestDTO request) {
		log.debug("Updating therapy configs for ndcNumbers: {}", request.getNdcNumbers());
		therapyService.updateTherapyConfigs(request);
		return new ResponseEntity<>("Therapy configurations updated successfully", HttpStatus.OK);
	}

	@GetMapping("/config/{tradeName}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.VIEW_SP_MATERIAL, Permission.THERAPY_SELECTION,
			Permission.ACCOUNT_SELECTION, Permission.UPDATE_PATIENTS_DETAILS, Permission.UPDATE_ADDITIONAL_DETAILS,
			Permission.SUBMIT_ORDER })
	@Operation(summary = "Get therapy details by trade name",
			description = "Retrieves detailed information for a therapy and its associated configuration, identified by its trade name and channel type.")
	public ResponseEntity<List<MaterialDetailsResponseDTO>> findTherapyDetailsByTradeName(
			@PathVariable String tradeName, @RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with tradeName: {}", tradeName);
		return new ResponseEntity<>(therapyService.findTherapyDetailsWithConfigByTradeName(tradeName, channelType),
				HttpStatus.OK);

	}

	@PostMapping("/sp/config/update")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update Specialty Pharma therapy configurations in bulk",
			description = "Allows for the bulk update of Specialty Pharma therapy configurations based on a list of SP product IDs.")
	public ResponseEntity<String> updateSPTherapyConfigs(@Valid @RequestBody SPTherapyConfigRequestDTO request) {
		log.debug("Updating sp therapy configs for spProductIds: {}", request.getSpProductIds());
		therapyService.updateSPTherapyConfigs(request);
		return new ResponseEntity<>("SP Therapy configurations updated successfully", HttpStatus.OK);
	}

	@PatchMapping("sp/config/{spProductId}")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Remove a Specialty Pharma therapy configuration",
			description = "Removes a specific Specialty Pharma therapy configuration from the system.")
	public ResponseEntity<String> removeSPTherapyConfig(@NotBlank @PathVariable String spProductId)
			throws ResourceNotFoundException {
		log.debug("Remove sp therapy config for SP Material: {}", spProductId);
		therapyService.removeSPTherapyConfig(spProductId);
		return new ResponseEntity<>("SP Therapy configurations removed successfully", HttpStatus.OK);
	}

	@PatchMapping("/sp/config/{spProductId}/diagnosis-codes")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update diagnosis codes for a Specialty Pharma therapy configuration",
			description = "Updates the list of diagnosis codes associated with a specific Specialty Pharma therapy configuration.")
	public ResponseEntity<SPTherapyConfigs> updateSPTherapyConfigDiagnosisCodes(
			@PathVariable @NotNull(message = "Sp product ID can not be null") String spProductId,
			@NotEmpty(message = "Diagnosis codes can not be empty") @RequestBody List<Long> diagnosisCodeIDs)
			throws ResourceNotFoundException {
		log.debug("Updating diagnosis codes for SP Material: {}", spProductId);
		SPTherapyConfigs spTherapyConfigs = therapyService.updateSPTherapyConfigDiagnosisCodes(spProductId,
				diagnosisCodeIDs);
		return new ResponseEntity<>(spTherapyConfigs, HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@DeleteMapping("/sp/config/{spProductId}/diagnosis-codes")
	@Operation(summary = "Remove diagnosis codes from an sp therapy",
			description = "Removes the association between a specified sp therapy and diagnosis codes.")
	public ResponseEntity<Void> delinkSPProductFromDiagnosisCodes(@PathVariable String spProductId,
			@NotNull(message = "Diagnosis codes can not be empty") @RequestBody List<Long> diagnosisCodeIDs)
			throws ResourceNotFoundException {
		log.debug("Delinking diagnosis codes from SP Material: {}", spProductId);
		therapyService.delinkSPProductFromDiagnosisCodes(spProductId, diagnosisCodeIDs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@GetMapping("/sp/config/{spProductId}/diagnosis-codes")
	@Operation(summary = "Find and filter all diagnosis code that are not linked to a specific sp product",
			description = "Retrieves a paginated and filtered list of all diagnosis code that are not linked to a specific sp product.")
	Page<DiagnosisCode> findAllDiagnosisCodes(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "code") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@PathVariable String spProductId, @RequestParam(required = false) Boolean showLinkedCodes)
			throws ResourceNotFoundException {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		showLinkedCodes = Boolean.TRUE.equals(showLinkedCodes);
		return therapyService.findAllDiagnosisCodesNotLinkedToSPProduct(query, paging, spProductId, showLinkedCodes);

	}

	@GetMapping("/sp/config/{materialNumber}")
	@RequirePermissions({ Permission.VIEW_SP_MATERIAL })
	@Operation(summary = "Get Specialty Pharma therapy details by material number",
			description = "Retrieves detailed information for a Specialty Pharma therapy, identified by its material number.")
	public ResponseEntity<MaterialDetailsResponseDTO> findSPTherapyDetailsByMaterialNumber(
			@PathVariable String materialNumber,
			@RequestParam(defaultValue = "SPECIALITY_PHARMA") ChannelType channelType)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findSPTherapyDetailsByMaterialNumber(materialNumber, channelType),
				HttpStatus.OK);

	}

	@DeleteMapping("/config/ndc/{ndcNumber}")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Delete a therapy configuration by NDC number",
			description = "Deletes a therapy configuration from the system, identified by its NDC number and channel type.")
	public ResponseEntity<String> deleteConfigByNdcNumber(
			@PathVariable @NotNull(message = "Ndc number cannot be null ") @NotEmpty(
					message = "Ndc number cannot be empty") String ndcNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {

		therapyService.deleteConfigByNdcNumber(ndcNumber, channelType);
		return new ResponseEntity<>(ndcNumber, HttpStatus.OK);
	}

	@GetMapping("/config/ndc/{ndcNumber}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.THERAPY_SELECTION, Permission.ACCOUNT_SELECTION,
			Permission.UPDATE_PATIENTS_DETAILS, Permission.UPDATE_ADDITIONAL_DETAILS, Permission.SUBMIT_ORDER })
	@Operation(summary = "Get therapy and configuration details by NDC number",
			description = "Retrieves detailed information for a therapy and its associated configuration, identified by its NDC number and channel type.")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsByNdc(
			@PathVariable @NotNull(message = "Ndc number cannot be null ") @NotEmpty(
					message = "Ndc number cannot be empty") String ndcNumber,
			@RequestParam ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy details by Ndc number: {}, channel Type: {}", ndcNumber, channelType);
		return new ResponseEntity<>(therapyService.findTherapyDetailsByNdc(ndcNumber, channelType), HttpStatus.OK);

	}

	@GetMapping("/config/ndc/{ndcNumber}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/therapy")
	@RequirePermissions({ Permission.ACCOUNT_SELECTION })
	@Operation(summary = "Get therapy details by NDC, sold-to, and ship-to accounts",
			description = "Retrieves therapy details based on NDC number, sold-to account, ship-to account, and other criteria.")
	public ResponseEntity<List<MaterialResponseDTO>> findTherapyDetailsByNdcAndSoldToAccount(
			@PathVariable @NotBlank(message = "Ndc number is required") String ndcNumber,
			@PathVariable @NotBlank(message = "Sold To Account is required") String soldToAccountId,
			@PathVariable @NotBlank(message = "Ship To Account is required") String shipToAccountId,
			@RequestParam(required = false) String program, @RequestParam ChannelType ndcChannelType,
			@RequestParam ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy details by Ndc number: {}, channel Type: {}, soldTo: {}, shipTo: {}, program: {}",
				ndcNumber, channelType, soldToAccountId, shipToAccountId, program);
		return new ResponseEntity<>(therapyService.findTherapyDetailsByNdcAndSoldToAndProgram(ndcNumber,
				soldToAccountId, shipToAccountId, program, ndcChannelType, channelType), HttpStatus.OK);

	}

	@GetMapping("/config/therapyId/{id}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.UPDATE_ADDITIONAL_DETAILS })
	@Operation(summary = "Get therapy and configuration details by internal therapy ID",
			description = "Retrieves detailed information for a therapy and its associated configuration, identified by its internal database ID.")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsById(@PathVariable Long id)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with id: {}", id);
		return new ResponseEntity<>(therapyService.findTherapyDetailsById(id), HttpStatus.OK);

	}

	@GetMapping(value = "/download", produces = "text/csv")
	@RequirePermissions({ Permission.MANAGE_SP_CATALOG, Permission.MANAGE_CROSSWALK })
	@Operation(summary = "Download a CSV file of materials for a specific channel",
			description = "Generates and downloads a CSV file containing material information for either the Specialty Pharma or Buy and Bill channel.")
	public ResponseEntity<StreamingResponseBody> downloadMaterialsCsvFile(
			@RequestParam(defaultValue = "SPECIALITY_PHARMA") ChannelType channelType) {

		// Default to SPECIALITY_PHARMA
		String fileName = "SP_Product_info.csv";
		String headerLine;

		// Override if BUY_AND_BILL
		if (channelType == ChannelType.BUY_AND_BILL) {
			fileName = "Material_info.csv";
			headerLine = "SAP Material Id,SAP Material Description,SAP NDC";
		}
		else {
			headerLine = "Product ID,Product Name,SP NDC";
		}

		StreamingResponseBody stream = outputStream -> {
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
				writer.write(headerLine);
				writer.newLine();

				for (Therapy therapy : therapyService.getTherapiesByChannelType(channelType)) {
					writer.write(String.format("\"%s\",\"%s\",\"%s\"", therapy.getMaterialNumber(),
							therapy.getMaterialDescription(), therapy.getNdcNumber()));
					writer.newLine();
				}

				writer.flush();
			}
		};

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
			.contentType(MediaType.parseMediaType("text/csv"))
			.body(stream);
	}

	@GetMapping("/manufacturers")
	@RequirePermissions({ Permission.VIEW_SP_MATERIAL })
	@Operation(summary = "Get unique manufacturer names for Specialty Pharma therapies",
			description = "Retrieves a list of unique manufacturer names for Specialty Pharma therapies, filtered by status.")
	public ResponseEntity<List<String>> getUniqueManufacturersByStatus(@RequestParam Status status) {

		log.debug("Fetching unique manufacturers for channelType=SPECIALITY_PHARMA and itemStatus={}", status);

		List<String> manufacturerNames = therapyService.getUniqueManufacturersByStatus(ChannelType.SPECIALITY_PHARMA,
				status);

		return new ResponseEntity<>(manufacturerNames, HttpStatus.OK);
	}

	@GetMapping("/therapy/{ndcNumber}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.VIEW_SP_MATERIAL, Permission.THERAPY_SELECTION,
			Permission.ACCOUNT_SELECTION, Permission.UPDATE_PATIENTS_DETAILS, Permission.UPDATE_ADDITIONAL_DETAILS,
			Permission.SUBMIT_ORDER })
	@Operation(summary = "Find therapy details by NDC number",
			description = "Retrieves a list of therapy details, identified by NDC number and channel type.")
	public ResponseEntity<List<Therapy>> findTherapyDetailsByNdcNumber(
			@PathVariable @NotNull(message = "Ndc number cannot be null ") @NotEmpty(
					message = "Ndc number cannot be empty") String ndcNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy details with ndcNumber: {}", ndcNumber);
		return new ResponseEntity<>(therapyService.findTherapyDetailsByNdcNumber(ndcNumber, channelType),
				HttpStatus.OK);
	}

	@GetMapping("/{therapyId}/manufacturer/logo")
	@Operation(summary = "Get a manufacturer's logo by therapy ID",
			description = "Retrieves the logo for a manufacturer, identified by a therapy ID. The response will contain the binary image data.")
	public ResponseEntity<byte[]> getManufacturerLogo(@NotNull @PathVariable Long therapyId)
			throws NumberFormatException, ResourceNotFoundException {
		TherapyLogoResponseDto therapyLogoResponseDto = therapyService.getManufacturerLogo(therapyId);

		return ResponseEntity.status(HttpStatus.OK)
			.contentType(therapyLogoResponseDto.getLogoExtension())
			.body(therapyLogoResponseDto.getLogo());
	}

	@PostMapping("/config/update-quantity")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update minimum and maximum quantity for therapies in bulk",
			description = "Allows updating min and max quantities for a list of NDC numbers.")
	public ResponseEntity<String> updateTherapyQuantities(@Valid @RequestBody TherapyQuantityUpdateRequestDTO request) {
		log.debug("Updating therapy quantities for ndcNumbers: {}", request.getNdcNumbers());
		therapyService.updateTherapyQuantities(request);
		return new ResponseEntity<>("Therapy quantity configurations updated successfully", HttpStatus.OK);
	}

}
