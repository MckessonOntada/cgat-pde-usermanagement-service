package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@Tag(name = "MaterialCrossWalk", description = "Controller for cross walk for bnb material with sp material")
@RequestMapping(path = "/cross-walk", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalMaterialCrosswalkController {

	private final MaterialCrossWalkService materialCrossWalkService;

	public GlobalMaterialCrosswalkController(MaterialCrossWalkService materialCrossWalkService) {
		this.materialCrossWalkService = materialCrossWalkService;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload bulk cross walks data")
	public ResponseEntity<byte[]> uploadCrossWalks(@RequestPart("crossWalkData") MultipartFile crossWalkData)
			throws FileNotValidException, UnauthorizedAccessException {

		if (crossWalkData == null
				|| !StringUtils.getFilenameExtension(crossWalkData.getOriginalFilename()).equalsIgnoreCase("csv")) {
			throw new FileNotValidException("Invalid file format. Please upload a CSV file.");
		}

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

}
