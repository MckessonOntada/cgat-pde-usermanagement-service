package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.service.MaterialCrossWalkService;
import com.mckesson.cgat.pde.usermanagement.utils.UserRequestAuthorizationValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@RestController
@Tag(name = "MaterialCrossWalk", description = "Controller for cross walk for bnb material with sp material")
@RequestMapping(path = "/cross-walk", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalMaterialCrosswalkController {

	private final MaterialCrossWalkService materialCrossWalkService;

	public GlobalMaterialCrosswalkController(MaterialCrossWalkService materialCrossWalkService) {
		this.materialCrossWalkService = materialCrossWalkService;
	}


	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@Operation(summary = "Upload bulk cross walks data")
	public ResponseEntity<byte[]> uploadCrossWalks(@RequestPart("crossWalkData") MultipartFile crossWalkData,
											  @NotNull(message = "Practice Id is Required") @PathVariable Long practiceId)
			throws FileNotValidException, UnauthorizedAccessException {


		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Boolean uploadStatus = materialCrossWalkService.uploadCrossWalks(crossWalkData, practiceId, outputStream);
		HttpStatus status = uploadStatus ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;

		return ResponseEntity.status(status)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FailedUsers.csv")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(outputStream.toByteArray());
	}



}
