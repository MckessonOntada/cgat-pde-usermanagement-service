package com.mckesson.jbat.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;
import com.mckesson.cgat.pde.usermanagement.service.IDPService;
import com.mckesson.cgat.pde.usermanagement.service.impl.IDPServiceImpl;
import com.mckesson.jbat.auth.module.config.AppPropertiesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IDPServiceImplTest {

	@Mock
	AppPropertiesConfig appPropertiesConfig;

	IDPService service;

	@BeforeEach
	void setUp() {
		service = new IDPServiceImpl(appPropertiesConfig);
	}

	@Test
	public void testGetIDPInfo() throws Exception {
		Mockito.when(appPropertiesConfig.getBaseUrl()).thenReturn("https://sample.okta.com");
		Mockito.when(appPropertiesConfig.getClientId()).thenReturn("client123");
		Mockito.when(appPropertiesConfig.getProtocol()).thenReturn("oauth2");
		IdentityProviderMetaData result = service.getIDPInfo();
		assertEquals("https://sample.okta.com", result.getBaseUrl());
		assertEquals("client123", result.getClientId());
		assertEquals("oauth2", result.getProtocol());
	}

}
