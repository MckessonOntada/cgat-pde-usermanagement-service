package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.IOException;
import java.util.List;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import org.aspectj.apache.bcel.classfile.Module.Require;
import org.ini4j.InvalidFileFormatException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Practices", description = "Controller for Manages practices-related operations and data.")
@RequestMapping(path = "/practices", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class PracticeController {

	private final PracticeService practiceService;

	private final PracticeUtils practiceUtils;

	private static final long max_file_size = 1048576; // 1 MB in bytes

	public PracticeController(PracticeService practiceService, PracticeUtils practiceUtils) {
		this.practiceService = practiceService;
		this.practiceUtils = practiceUtils;
	}

	@RequirePermissions({Permission.ADD_ORGANIZATION})
	@PostMapping(consumes = { "multipart/form-data" })
	@Operation(summary = "Create Practice")
	public ResponseEntity<Object> createPractice(@RequestPart("practiceStr") String practiceStr,
			@RequestPart(value = "logo", required = false) MultipartFile logo, BindingResult bindingResult)
			throws IOException, MethodArgumentNotValidException {

		Practice practiceObj = parsePractice(practiceStr);
		practiceUtils.validatePractice(practiceObj, bindingResult, logo);
		if (bindingResult.hasErrors()) {
			throw new MethodArgumentNotValidException(null, bindingResult);
		}

		Practice savedPractice = practiceService.createPractice(practiceObj, logo);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPractice);
	}

	private Practice parsePractice(String practiceJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(practiceJson, Practice.class);
	}

	@RequirePermissions({Permission.EDIT_ORGANIZATION, Permission.ADD_ORGANIZATION})
	@PutMapping("/{practiceId}")
	@Operation(summary = "Update Practice")
	public ResponseEntity<Object> updatePractice(@RequestPart("practiceStr") @Valid String practiceStr,
			@RequestPart(value = "logo", required = false) MultipartFile logo, @PathVariable String practiceId,
			BindingResult bindingResult) throws IOException {
		Practice practiceObj = parsePractice(practiceStr);
		practiceUtils.validatePractice(practiceObj, bindingResult, null);
		if (bindingResult.hasErrors()) {
			// Return validation errors as response
			return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
		}
		practiceObj.setId(Long.valueOf(practiceId));
		Practice practice = practiceService.createPractice(practiceObj, logo);
		return ResponseEntity.status(HttpStatus.OK).body(practice);
	}

	@RequirePermissions({Permission.VIEW_ORGANIZATION})
	@GetMapping("/{practiceId}")
	@Operation(summary = "Get Practice by Id")
	public ResponseEntity<Practice> getPractice(@PathVariable String practiceId)
			throws NumberFormatException, ResourceNotFoundException {
		return new ResponseEntity<>(practiceService.getPractice(practiceId), HttpStatus.OK);
	}

	@RequirePermissions({Permission.EDIT_ORGANIZATION})
	@DeleteMapping("/{practiceId}")
	@Operation(summary = "Delete Practice by Id")
	public ResponseEntity<Object> deletePractice(@PathVariable String practiceId) throws ResourceNotFoundException {
		practiceService.deletePractice(practiceId);
		return new ResponseEntity<>(practiceId, HttpStatus.OK);
	}

	@RequirePermissions({Permission.MANAGE_ORGANIZATION_SCREEN})
	@GetMapping
	@Operation(summary = "Fetch all Practices based on filter provided with pagination capabilities.")
	Page<Practice> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status,
			@RequestParam(required = false) List<String> location) {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return practiceService.findAll(paging, query, status, location);
	}

}
