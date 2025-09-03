package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Global Contact Us", description = "Handles submissions from the global 'Contact Us' form.")
@RequestMapping(path = "/contact-us", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalContactUsController {

	private static final Logger logger = LoggerFactory.getLogger(GlobalContactUsController.class);

	private final EmailNotificationService emailNotificationService;

	public GlobalContactUsController(EmailNotificationService emailNotificationService) {
		this.emailNotificationService = emailNotificationService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Submit the 'Contact Us' form and trigger an email notification",
			description = "Accepts a submission from the 'Contact Us' form, validates the data, and triggers an email notification to the support team.")
	public ResponseEntity<Map<String, String>> submitContactUs(@Valid @RequestBody ContactUsDTO contactUsDTO) {

		emailNotificationService.sendContactUsNotification(contactUsDTO);
		logger.info("ContactUs form submitted: {}", contactUsDTO);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Contact information submitted successfully.");

		return ResponseEntity.ok(response);
	}

}
