package com.mckesson.jbat.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.controller.PublicAccessController;
import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;
import com.mckesson.cgat.pde.usermanagement.service.IDPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicAccessControllerTest extends AbstractTest {

	@InjectMocks
	private PublicAccessController controller;

	@Mock
	private IDPService service;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void getIDPConfig() throws Exception {
		IdentityProviderMetaData identityProviderMetaData = new IdentityProviderMetaData();
		identityProviderMetaData.setClientId("client1");
		identityProviderMetaData.setBaseUrl("https://baseUrl");
		identityProviderMetaData.setProtocol("BaseProtocol");
		when(service.getIDPInfo()).thenReturn(identityProviderMetaData);
		ResultActions response = mockMvc
			.perform(get("/public/idpConfig").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		response.andReturn();
		response.andExpect(status().isOk());
	}

}
