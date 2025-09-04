package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mckesson.cgat.pde.usermanagement.service.OktaUserWebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/inlineHooks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Okta Webhooks", description = "Handles incoming Okta inline token hooks and event hooks.")
@Slf4j
public class OktaWebhookController {

	// @Autowired
	private OktaUserWebhookService oktaUserWebhookService;

	@PostMapping("/handleTokenHook")
	@Operation(summary = "Process Okta inline token hooks and event hooks",
			description = "This endpoint receives and processes POST requests from Okta for both inline token hooks and event hooks. It validates the request and then either adds custom claims to a token or processes a user event.")
	@ResponseStatus(code = HttpStatus.OK)
	public Map<?, ?> handleTokenHook(@RequestBody Map<String, Object> authenticationResponse,
			@RequestHeader HttpHeaders headers) {
		boolean isValidRequest = oktaUserWebhookService.validateToken(headers);
		if (!isValidRequest) {
			log.info("Invalid token, token validation failed");
			return new HashMap<>();
		}
		log.info("Inside Token Inline Hook to add custom authorization commands");
		JSONObject commands = new JSONObject();
		JSONObject requestBody = new JSONObject(authenticationResponse);
		if (requestBody.has("data")) {
			JSONObject data = (JSONObject) requestBody.get("data");
			if (data.has("events")) {
				log.info("Received event request");
				oktaUserWebhookService.addUpdateUserEvent(requestBody);
			}
			else if (data.has("access")) {
				log.info("Received inline token request");
				commands = oktaUserWebhookService.getCommandsForTokenInlineWebhook(requestBody);
			}
		}
		return commands.toMap();
	}

	@GetMapping("/handleTokenHook")
	@Operation(summary = "Respond to Okta's one-time verification challenge",
			description = "This endpoint handles GET requests from Okta for the one-time verification challenge. It responds with the value of the 'x-okta-verification-challenge' header to confirm the endpoint's validity.")
	@ResponseStatus(code = HttpStatus.OK)
	public Map<String, String> handleTokenHook(@RequestHeader HttpHeaders headers) {
		Map<String, String> response = new HashMap<>();
		response.put("verification", headers.get("x-okta-verification-challenge").get(0));
		return response;
	}

}
