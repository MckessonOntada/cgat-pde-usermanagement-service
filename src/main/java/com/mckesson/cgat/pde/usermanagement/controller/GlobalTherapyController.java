package com.mckesson.cgat.pde.usermanagement.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.dto.TherapyResponseDto;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

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
	public ResponseEntity<Page<TherapyResponseDto>> findAllThreapies(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String query) {
		Pageable paging = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(therapyService.findAllGlobalTherapies(query, paging), HttpStatus.OK);

	@GetMapping("/{materialNumber}")
	@Operation(summary = "Find Therapy by Material Number")
	public ResponseEntity<Therapy> findByMaterialNumber(@PathVariable String materialNumber)
			throws ResourceNotFoundException {
		log.debug("Fetching therapy with materialNumber: {}", materialNumber);
		return new ResponseEntity<>(therapyService.findByMaterialNumber(materialNumber), HttpStatus.OK);

	}

}
