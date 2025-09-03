package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkRequestDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CrosswalkResponseDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.RequirePermissions;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.FileValidationUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Global Material Crosswalk",
		description = "Manages the mapping between Buy and Bill (BnB) and Specialty Pharma (SP) materials.")
@RequestMapping(path = "/cross-walk", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalMaterialCrosswalkController {

	private final MaterialCrossWalkService materialCrossWalkService;

	public GlobalMaterialCrosswalkController(MaterialCrossWalkService materialCrossWalkService) {
		this.materialCrossWalkService = materialCrossWalkService;
	}

	private static final String SORT_ORDER_ASC = "asc";

	private static final Map<String, String> crosswalk_sortby_columns = Map.of("spProductId", "m2.material_number",
			"spProductName", "m2.material_description", "bnbMaterialNumber", "m1.material_number",
			"bnbMaterialDescription", "m1.material_description");

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload and process a CSV file of material crosswalks",
			description = "Allows for bulk uploading of material crosswalks from a CSV file. If any records fail validation, a CSV file with the failed records is returned.")
	@RequirePermissions({ Permission.UPLOAD_CROSSWALK })
	public ResponseEntity<byte[]> uploadCrossWalks(@RequestPart("crossWalkData") MultipartFile crossWalkData)
			throws FileNotValidException, UnauthorizedAccessException {

		FileValidationUtils.validateMultiPartFile(crossWalkData);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		boolean uploadStatus = materialCrossWalkService.uploadCrossWalks(crossWalkData, outputStream);
		HttpStatus status = uploadStatus ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;

		if (uploadStatus) {
			// Success: return simple message
			return ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body("Upload successful. No failed records.".getBytes());
		}
		else {
			// Partial success: return failed rows as CSV
			return ResponseEntity.status(status)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FailedCrossWalk.csv")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(outputStream.toByteArray());
		}

	}

	@RequirePermissions({ Permission.ADD_CROSSWALK })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create multiple material crosswalks",
			description = "Creates one or more material crosswalks from the provided list of crosswalk requests.")
	public ResponseEntity<Integer> createCrosswalks(@Valid @RequestBody List<CrosswalkRequestDTO> crosswalks)
			throws UnauthorizedAccessException, ResourceNotFoundException {
		int count = materialCrossWalkService.createCrosswalks(crosswalks);
		return ResponseEntity.ok(count);
	}

	@RequirePermissions({ Permission.VIEW_MATERIAL_CROSSWALK })
	@GetMapping
	@Operation(summary = "Find and filter all material crosswalks with pagination",
			description = "Retrieves a paginated and filtered list of all material crosswalks.")
	public ResponseEntity<Page<CrosswalkResponseDTO>> findAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize,
			@RequestParam(defaultValue = "trade_name") String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(required = false) String query,
			@RequestParam(required = false) List<String> status)
			throws ResourceNotFoundException, UnauthorizedAccessException {
		String mappedSortBy = crosswalk_sortby_columns.getOrDefault(sortBy, "m1.trade_name");

		Sort sort = SORT_ORDER_ASC.equalsIgnoreCase(sortOrder) ? Sort.by(mappedSortBy).ascending()
				: Sort.by(mappedSortBy).descending();
		Pageable paging = PageRequest.of(pageNo, pageSize, sort);
		return new ResponseEntity<>(materialCrossWalkService.findAll(paging, query, status), HttpStatus.OK);
	}

	@RequirePermissions({ Permission.MANAGE_CROSSWALK })
	@DeleteMapping("/{crosswalkId}/deLink")
	@Operation(summary = "Delink a material crosswalk by ID",
			description = "Removes the link for a specific material crosswalk, identified by its ID.")
	public ResponseEntity<Void> deLinkMaterialCrosswalk(
			@PathVariable @NotNull(message = "'crosswalkId' cannot be null") Long crosswalkId)
			throws ResourceNotFoundException {
		materialCrossWalkService.deLinkMaterialCrosswalk(crosswalkId);
		return ResponseEntity.ok().build();
	}

}
