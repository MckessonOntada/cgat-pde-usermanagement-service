package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.PracticeUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Practices", description = "Controller for Manages practices-related operations and data.")
@RequestMapping(path = "/practice", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class PracticeController {

	private static final String SORT_ORDER_ASC = "asc";

	@Autowired
	private PracticeService practiceService;

	@Autowired
	private PracticeUtils practiceUtils;

	@PostMapping
	@Operation(summary = "create Practice")
	public ResponseEntity<Object> createPractice(@RequestPart("practiceStr") @Valid String practiceStr,
			@RequestPart("logo") MultipartFile logo, BindingResult bindingResult) throws IOException {
		Practice practiceObj = parsePractice(practiceStr);
		practiceUtils.validatePractice(practiceObj, bindingResult);
		if (bindingResult.hasErrors()) {
			// Return validation errors as response
			return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
		}
		Practice savedPractice = practiceService.createPractice(practiceObj, logo);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPractice);
	}

	private Practice parsePractice(String practiceJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(practiceJson, Practice.class);
	}

	@PutMapping("/{practiceId}")
	@Operation(summary = "Update Practice")
	public ResponseEntity<Object> updatePractice(@RequestPart("practiceStr") @Valid String practiceStr,
			@RequestPart("logo") MultipartFile logo, @PathVariable String practiceId, BindingResult bindingResult)
			throws IOException {
		Practice practiceObj = parsePractice(practiceStr);
		practiceUtils.validatePractice(practiceObj, bindingResult);
		if (bindingResult.hasErrors()) {
			// Return validation errors as response
			return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
		}
		practiceObj.setId(Long.valueOf(practiceId));
		Practice practice = practiceService.createPractice(practiceObj, logo);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(practice);
	}

	@GetMapping("/{practiceId}")
	@Operation(summary = "Fetch Practice Details")
	public ResponseEntity<List<Practice>> getPractice(@PathVariable String practiceId)
			throws NumberFormatException, ResourceNotFoundException {
		List<Practice> response = new ArrayList<>();

		Practice practice = practiceService.getPractice(practiceId);
		if (practice != null)
			response.add(practice);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Fetch AllPractices Details")
	public ResponseEntity<List<Practice>> getAllPractice() throws NumberFormatException, ResourceNotFoundException {
		return new ResponseEntity<>(practiceService.getAllPractices(), HttpStatus.OK);
	}

	@DeleteMapping("/{practiceId}")
	@Operation(summary = "Delete Practice Details")
	public ResponseEntity<String> deletePractice(@PathVariable String practiceId) throws ResourceNotFoundException {
		String deletedPractice = practiceService.deletePractice(practiceId);
		return ResponseEntity.status(HttpStatus.OK)
			.body("Practice deleted successfully with name : " + deletedPractice);

	}

	@GetMapping("/filterPractices")
	@Operation(summary = "Fetch all Practices based on filter provided with pagination capabilities.")
	Page<Practice> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> roles) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return practiceService.findAll(paging, query, roles, status);
	}

}
