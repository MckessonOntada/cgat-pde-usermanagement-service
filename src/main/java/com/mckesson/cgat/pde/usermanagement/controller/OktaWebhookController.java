package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.service.OktaUserWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/inlineHooks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "InlineHooks", description = "Okta Inline Webhook Handlers to handle okta token and event hooks.")
@Slf4j
public class OktaWebhookController {

    @Autowired
    private OktaUserWebhookService oktaUserWebhookService;

    @PostMapping("/handleTokenHook")
    @Operation(summary = "Send authorization information as commands to token hook to embed the authorization information inside the authentication token")
    @ResponseStatus(code = HttpStatus.OK)
    public Map<?, ?> handleTokenHook(@RequestBody Map<String, Object> authenticationResponse, @RequestHeader HttpHeaders headers) {
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
            }else if(data.has("access")) {
                log.info("Received inline token request");
                commands = oktaUserWebhookService.getCommandsForTokenInlineWebhook(requestBody);
            }
        }
        return commands.toMap();
    }

    @GetMapping("/handleTokenHook")
    @Operation(summary = "Dummy API for OKTA to accept the service is valid.")
    @ResponseStatus(code = HttpStatus.OK)
    public Map<String, String> handleTokenHook(@RequestHeader HttpHeaders headers) {
        Map<String, String> response = new HashMap<>();
        response.put("verification", headers.get("x-okta-verification-challenge").get(0));
        return response;
    }
}
