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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Practices", description = "Controller for Manages practices-related operations and data.")
@RequestMapping(path = "/practices", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class PracticeController {

	private static final String SORT_ORDER_ASC = "asc";

	@Autowired
	PracticeService practiceService;

	@PostMapping
	@Operation(summary = "Create or modify Practice")
	public ResponseEntity<String> createPractice(@RequestParam("logo") MultipartFile logo,
			@RequestParam(required = true, value = "data") String data)
			throws IOException, IllegalStateException, ResourceNotFoundException {

		String status = practiceService.createOrUpdatePractice(logo, data);
		if (status.equals(CustomUtils.operation_type.CREATE.name()))
			return ResponseEntity.status(HttpStatus.CREATED).body("Practice created successfully");
		else
			return ResponseEntity.status(HttpStatus.OK).body("Practice updated successfully");
	}

	@GetMapping("/{practiceId}")
	@Operation(summary = "Fetch Practice Details")
	public ResponseEntity<List<Practice>> getPractice(@PathVariable String practiceId) {
		List<Practice> response = new ArrayList<>();
		if (practiceId == null || practiceId.equals("null")) {
			response = practiceService.getAllPractices();
		} else {
			Practice practice = practiceService.getPractice(practiceId);
			if (practice != null)
				response.add(practice);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{practiceId}")
	@Operation(summary = "Delete Practice Details")
	public ResponseEntity<String> deletePractice(@PathVariable String practiceId) throws ResourceNotFoundException {
		String deletedPractice = practiceService.deletePractice(practiceId);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Practice deleted successfully with name : " + deletedPractice);

	}

	@GetMapping
	@Operation(summary = "Fetch all Practices based on filter provided with pagination capabilities.")
	Page<Practice> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "displayName") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> roles) {
		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		Page<Practice> pagedResult = practiceService.findAll(paging, query, roles, status);

		return pagedResult;
	}

}
