package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyConfigRequestDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TradeCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.SORT_ORDER_ASC;

@RestController
@Tag(name = "Therapies", description = "Controller for getting all therapies.")
@RequestMapping(path = "/therapies", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j

public class GlobalTherapyController {

	private final TherapyService therapyService;

	public GlobalTherapyController(TherapyService therapyService) {
		this.therapyService = therapyService;
	}

	@GetMapping
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.VIEW_SP_MATERIAL, Permission.ADD_CROSSWALK })
	@Operation(summary = "Fetch all therapies.")
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
	@RequirePermissions({ Permission.VIEW_MATERIAL })
	@Operation(summary = "Fetch all therapies.")
	public ResponseEntity<Page<Therapy>> findAllSPTherapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query,
			@RequestParam(defaultValue = "material_number") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) List<String> status) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return new ResponseEntity<>(therapyService.findAllGlobalSPTherapies(query, paging, status), HttpStatus.OK);
	}

	@GetMapping("/{materialNumber}")
	@Operation(summary = "Find Therapy by Material Number")
	public ResponseEntity<Therapy> findByMaterialNumber(@PathVariable String materialNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findByMaterialNumber(materialNumber, channelType), HttpStatus.OK);
	}

	@PostMapping("/config/update")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update Therapy Configurations")
	public ResponseEntity<String> updateTherapyConfigs(@Valid @RequestBody TherapyConfigRequestDTO request) {
		log.debug("Updating therapy configs for ndcNumbers: {}", request.getNdcNumbers());
		therapyService.updateTherapyConfigs(request.getNdcNumbers(), request.getIsPatientInformationRequired(),
				request.getIsCoiIdRequired(), request.getChannelType());
		return new ResponseEntity<>("Therapy configurations updated successfully", HttpStatus.OK);
	}

	@GetMapping("/config/{tradeName}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.EDIT_ORDERS, Permission.VIEW_SP_MATERIAL })
	@Operation(summary = "Find Therapy details by Trade Name")
	public ResponseEntity<List<MaterialDetailsResponseDTO>> findTherapyDetailsByTradeName(
			@PathVariable String tradeName, @RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with tradeName: {}", tradeName);
		return new ResponseEntity<>(therapyService.findTherapyDetailsWithConfigByTradeName(tradeName, channelType),
				HttpStatus.OK);

	}

	@DeleteMapping("/config/ndc/{ndcNumber}")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Delete Configuration by Ndc Number")
	public ResponseEntity<String> deleteConfigByNdcNumber(
			@PathVariable @NotNull(message = "Ndc number cannot be null ") @NotEmpty(
					message = "Ndc number cannot be empty") String ndcNumber,
			@RequestParam(defaultValue = "BUY_AND_BILL") ChannelType channelType) throws ResourceNotFoundException {

		therapyService.deleteConfigByNdcNumber(ndcNumber, channelType);
		return new ResponseEntity<>(ndcNumber, HttpStatus.OK);
	}

	@GetMapping("/config/ndc/{ndcNumber}")
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.ADD_ORDERS, Permission.EDIT_ORDERS })
	@Operation(summary = "Find Therapy details by Ndc Number")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsByNdc(
			@PathVariable @NotNull(message = "Ndc number cannot be null ") @NotEmpty(
					message = "Ndc number cannot be empty") String ndcNumber,
			@RequestParam ChannelType channelType) throws ResourceNotFoundException {
		log.debug("Fetching therapy details by Ndc number: {}, channel Type: {}", ndcNumber, channelType);
		return new ResponseEntity<>(therapyService.findTherapyDetailsByNdc(ndcNumber, channelType), HttpStatus.OK);

	}

	@GetMapping("/config/ndc/{ndcNumber}/soldtoaccount/{soldToAccountId}/shiptoaccount/{shipToAccountId}/therapy")
	@RequirePermissions({ Permission.ADD_ORDERS, Permission.EDIT_ORDERS })
	@Operation(summary = "Find Therapy details by Ndc Number and soldTo account")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsByNdcAndSoldToAccount(
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
	@RequirePermissions({ Permission.VIEW_MATERIAL, Permission.ADD_ORDERS, Permission.EDIT_ORDERS })
	@Operation(summary = "Find Therapy details by Material Number")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsById(@PathVariable Long id)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with materialNumber: {}", id);
		return new ResponseEntity<>(therapyService.findTherapyDetailsById(id), HttpStatus.OK);

	}

	@GetMapping(value = "/download", produces = "text/csv")
	@RequirePermissions({ Permission.MANAGE_SP_CATALOG, Permission.MANAGE_CROSSWALK })
	@Operation(summary = "Download CSV file for materials based on channel type")
	public ResponseEntity<StreamingResponseBody> downloadMaterialsCsvFile(
			@RequestParam(defaultValue = "SPECIALITY_PHARMA") ChannelType channelType) {

		// Default to SPECIALITY_PHARMA
		String fileName = "SP_Product_info.csv";
		String headerLine;

		// Override if BUY_AND_BILL
		if (channelType == ChannelType.BUY_AND_BILL) {
			fileName = "Material_info.csv";
			headerLine = "Buy & Bill Material Id,Buy & Bill Material Description,Buy & Bill Material NDC";
		}
		else {
			headerLine = "Product ID,Product Name,Product NDC";
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

}
