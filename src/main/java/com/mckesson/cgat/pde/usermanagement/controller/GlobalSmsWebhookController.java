package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.SmsEventRequestDTO;
import com.mckesson.cgat.pde.usermanagement.service.SmsWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/acs", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class GlobalSmsWebhookController {

	private final SmsWebhookService smsWebhookService;

	@PostMapping("/sms-webhook")
	@Operation(summary = "Handle incoming ACS SMS webhook",
			description = "Receives an SMS event payload from Azure Communication Services (ACS). "
					+ "Logs or processes the SMS details (sender, recipient, message, etc.) "
					+ "and returns the same payload with a 200 OK response for confirmation.")
	public ResponseEntity<SmsEventRequestDTO> receiveSmsWebhook(@RequestBody SmsEventRequestDTO requestDto) {

		log.info("Received SMS webhook: {}", requestDto);

		smsWebhookService.processSmsEvent(requestDto);

		return ResponseEntity.ok(requestDto);
	}

}
