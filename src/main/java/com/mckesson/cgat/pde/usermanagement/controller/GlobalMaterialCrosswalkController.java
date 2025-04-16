package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
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
	public void uploadCrossWalks(@RequestPart("crossWalkData") MultipartFile crossWalkData,
			HttpServletResponse response) throws FileNotValidException, UnauthorizedAccessException {

		if (crossWalkData == null
				|| !StringUtils.getFilenameExtension(crossWalkData.getOriginalFilename()).equalsIgnoreCase("csv")) {
			throw new FileNotValidException("Invalid file format. Please upload a CSV file.");
		}
		try {
			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FailedCrossWalk.csv");

			boolean uploadStatus = materialCrossWalkService.uploadCrossWalks(crossWalkData, response.getOutputStream());

			if (uploadStatus) {
				response.reset(); // Clear any written CSV data
				response.setStatus(HttpServletResponse.SC_OK); // 200
				response.setContentType("application/json");
				response.getWriter().write("Upload successful. No failed records.");
			}
			else {

				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206
			}

		}
		catch (IOException e) {
			throw new RuntimeException("Error writing stream to response", e);
		}
	}

}
