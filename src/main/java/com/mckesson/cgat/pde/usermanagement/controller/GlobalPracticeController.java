package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.PracticeLogoResponseDto;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Practice", description = "Controller for Manages practice related operations and data.")
@RequestMapping(path = "/practice", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GlobalPracticeController {

	private final PracticeService practiceService;

	public GlobalPracticeController(PracticeService practiceService) {
		this.practiceService = practiceService;
	}

	@GetMapping("/{practiceId}/logo")
	@Operation(summary = "Get Practice Logo by Practice Id")
	public ResponseEntity<byte[]> getPracticeLogo(@PathVariable String practiceId)
			throws NumberFormatException, ResourceNotFoundException {
		log.info("Fetching logo for practiceId: {}", practiceId);
		PracticeLogoResponseDto practiceLogoResponseDto = practiceService.getPracticeLogo(practiceId);
		log.info("Logo fetched successfully for practiceId: {}", practiceId);
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(practiceLogoResponseDto.getLogoExtension())
			.body(practiceLogoResponseDto.getLogo());
	}

}
