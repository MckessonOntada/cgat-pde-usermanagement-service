package com.mckesson.cgat.pde.usermanagement.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mckesson.cgat.pde.usermanagement.dto.PracticeLogoResponseDto;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
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
@Tag(name = "Practices", description = "Manages practice-level operations and data.")
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

	@RequirePermissions({ Permission.ADD_ORGANIZATION })
	@PostMapping(consumes = { "multipart/form-data" })
	@Operation(summary = "Create a new practice with an optional logo",
			description = "Creates a new practice based on the provided JSON data and optionally uploads a logo. The practice data is sent as a string in a multipart request part.")
	public ResponseEntity<Object> createPractice(@RequestPart("practiceStr") String practiceStr,
			@RequestPart(value = "logo", required = false) MultipartFile logo, BindingResult bindingResult)
			throws IOException, MethodArgumentNotValidException, UnauthorizedAccessException {

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
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper.readValue(practiceJson, Practice.class);
	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION, Permission.ADD_ORGANIZATION })
	@PutMapping(value = "/{practiceId}", consumes = { "multipart/form-data" })
	@Operation(summary = "Update an existing practice with an optional logo",
			description = "Updates an existing practice, identified by its ID, with the provided JSON data and optionally uploads a new logo.")
	public ResponseEntity<Object> updatePractice(@RequestPart("practiceStr") @Valid String practiceStr,
			@RequestPart(value = "logo", required = false) MultipartFile logo, @PathVariable String practiceId,
			BindingResult bindingResult) throws IOException, UnauthorizedAccessException {
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

	// @RequirePermissions({ Permission.VIEW_ORGANIZATION })
	@GetMapping("/{practiceId}")
	@Operation(summary = "Get a practice by its ID",
			description = "Retrieves the full details for a single practice by its unique ID.")
	public ResponseEntity<Practice> getPractice(@PathVariable String practiceId)
			throws NumberFormatException, ResourceNotFoundException, UnauthorizedAccessException {
		return new ResponseEntity<>(practiceService.getPractice(practiceId), HttpStatus.OK);
	}

	@GetMapping("/{practiceId}/logo")
	@Operation(summary = "Get a practice's logo by its ID",
			description = "Retrieves the logo for a specific practice, identified by its ID. The response will contain the binary image data.")
	public ResponseEntity<byte[]> getPracticeLogo(@PathVariable String practiceId)
			throws NumberFormatException, ResourceNotFoundException {
		PracticeLogoResponseDto practiceLogoResponseDto = practiceService.getPracticeLogo(practiceId);

		return ResponseEntity.status(HttpStatus.OK)
			.contentType(practiceLogoResponseDto.getLogoExtension())
			.body(practiceLogoResponseDto.getLogo());
	}

	@RequirePermissions({ Permission.EDIT_ORGANIZATION })
	@DeleteMapping("/{practiceId}")
	@Operation(summary = "Delete a practice by its ID", description = "Deletes a specified practice from the system.")
	public ResponseEntity<Object> deletePractice(@PathVariable String practiceId)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		practiceService.deletePractice(practiceId);
		return new ResponseEntity<>(practiceId, HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_ORGANIZATION_SCREEN })
	@GetMapping
	@Operation(summary = "Find and filter all practices with pagination",
			description = "Retrieves a paginated and filtered list of all practices, based on various criteria.")
	Page<Practice> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status, @RequestParam(required = false) List<String> location)
			throws UnauthorizedAccessException {
		Sort sort = CustomUtils.SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);

		return practiceService.findAll(paging, query, status, location);
	}

	@RequirePermissions({ Permission.THERAPY_SELECTION, Permission.APPROVE_ORDERS })
	@GetMapping("/{practiceId}/name")
	@Operation(summary = "Get a practice's name by its ID",
			description = "Retrieves the name of a practice, identified by its ID.")
	public ResponseEntity<String> getPracticeName(@PathVariable Long practiceId)
			throws NumberFormatException, ResourceNotFoundException {
		String practiceName = practiceService.getPracticeName(practiceId);
		return ResponseEntity.status(HttpStatus.OK).body(practiceName);
	}

}
