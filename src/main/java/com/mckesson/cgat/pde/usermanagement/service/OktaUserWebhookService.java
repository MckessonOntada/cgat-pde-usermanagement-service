package com.mckesson.cgat.pde.usermanagement.service;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;

public interface OktaUserWebhookService {

    JSONObject getCommandsForTokenInlineWebhook(JSONObject authenticationResponse);

    boolean validateToken(HttpHeaders headers);

    void addUpdateUserEvent(JSONObject requestBody);
}
