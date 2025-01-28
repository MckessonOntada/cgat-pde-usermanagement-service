package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyConfigRequestDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

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
	@RequirePermissions({ Permission.VIEW_MATERIAL })
	@Operation(summary = "Fetch all therapies.")
	public ResponseEntity<Page<Therapy>> findAllThreapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllGlobalTherapies(query, paging), HttpStatus.OK);
	}

	@GetMapping("/{materialNumber}")
	@Operation(summary = "Find Therapy by Material Number")
	public ResponseEntity<Therapy> findByMaterialNumber(@PathVariable String materialNumber)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findByMaterialNumber(materialNumber), HttpStatus.OK);

	}

	@PostMapping("/config/update")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Update Therapy Configurations")
	public ResponseEntity<String> updateTherapyConfigs(@Valid @RequestBody TherapyConfigRequestDTO request) {
		log.debug("Updating therapy configs for materialNumbers: {}", request.getMaterialNumbers());
		therapyService.updateTherapyConfigs(request.getMaterialNumbers(), request.getIsPatientInformationRequired(),
				request.getIsCoiIdRequired());
		return new ResponseEntity<>("Therapy configurations updated successfully", HttpStatus.OK);
	}

	@GetMapping("/config/{materialNumber}")
	@RequirePermissions({ Permission.VIEW_MATERIAL })
	@Operation(summary = "Find Therapy details by Material Number")
	public ResponseEntity<MaterialResponseDTO> findTherapyDetailsByMaterialNumber(@PathVariable String materialNumber)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy details with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findTherapyDetailsByMaterialNumber(materialNumber), HttpStatus.OK);

	}

	@DeleteMapping("/config/{materialNumber}")
	@RequirePermissions({ Permission.MANAGE_MATERIAL })
	@Operation(summary = "Delete Configuration by Material Number")
	public ResponseEntity<Object> deleteConfigByMaterialNumber(
			@PathVariable @NotNull(message = "Material number cannot be null ") @NotEmpty(
					message = "Material number cannot be empty") String materialNumber)
			throws ResourceNotFoundException {

		therapyService.deleteConfigByMaterialNumber(materialNumber);
		return new ResponseEntity<>(materialNumber, HttpStatus.OK);
	}

}
