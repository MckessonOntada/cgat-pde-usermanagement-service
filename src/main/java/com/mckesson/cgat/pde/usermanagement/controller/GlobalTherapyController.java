package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyConfigRequestDTO;
import com.mckesson.cgat.pde.usermanagement.model.TherapyConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@GetMapping("/config/{materialNumber}")
	@Operation(summary = "Find Therapy config by Material Number")
	public ResponseEntity<TherapyConfig> findConfigByMaterialNumber(@PathVariable String materialNumber)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy config with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findConfigByMaterialNumber(materialNumber), HttpStatus.OK);
	}

	@PostMapping("/config/update")
	@Operation(summary = "Update Therapy Configurations")
	public ResponseEntity<String> updateTherapyConfigs(@RequestBody TherapyConfigRequestDTO request)
			throws ResourceNotFoundException {
		log.debug("Updating therapy configs for materialNumbers: {}", request.getMaterialNumbers());

		therapyService.updateTherapyConfigs(request.getMaterialNumbers(), request.isPatientInfo(), request.isCoiId());
		return new ResponseEntity<>("Therapy configurations updated successfully", HttpStatus.OK);
	}

}
