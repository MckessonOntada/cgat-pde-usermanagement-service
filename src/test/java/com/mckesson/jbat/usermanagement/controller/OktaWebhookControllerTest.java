package com.mckesson.jbat.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.controller.OktaWebhookController;
import com.mckesson.cgat.pde.usermanagement.service.OktaUserWebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OktaWebhookControllerTest extends AbstractTest {

    @InjectMocks
    private OktaWebhookController controller;
    @Mock
    private OktaUserWebhookService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void handleTokenHook() throws Exception {
        ResultActions response = mockMvc.perform(post("/inlineHooks/handleTokenHook")
                .content(getOktaInlineTokenHookRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header("x-okta-auth", "YWRtaW5AMTIz"));
        response.andExpect(status().isOk());
        String content = response.andReturn().getResponse().getContentAsString();
        assertTrue(content.equals("{}"));
    }

    @Test
    void handleTokenHook_InvalidToken() throws Exception {
        ResultActions response = mockMvc.perform(post("/inlineHooks/handleTokenHook")
                .content(getOktaInlineTokenHookRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header("x-okta-auth", "YWRtaW5AMTIz_L"));
        response.andExpect(status().isOk());
        String content = response.andReturn().getResponse().getContentAsString();
        assertTrue(content.equals("{}"));

    }

    @Test
    void testHandleTokenHook() throws Exception {
        ResultActions response = mockMvc.perform(get("/inlineHooks/handleTokenHook")
                .content(getOktaInlineTokenHookRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .header("x-okta-verification-challenge", "abcdef123456"));
        response.andExpect(status().isOk());
    }
    private String getOktaInlineTokenHookRequest() {
        return "{\"test\":\"test value\"}";
    }

}