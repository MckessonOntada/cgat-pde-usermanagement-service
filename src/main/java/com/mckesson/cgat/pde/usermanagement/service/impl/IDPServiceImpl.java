package com.mckesson.cgat.pde.usermanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;
import com.mckesson.cgat.pde.usermanagement.service.IDPService;
import com.mckesson.jbat.auth.module.config.AppPropertiesConfig;

@Service
@ComponentScan(basePackages = "com.mckesson.jbat.auth.module.config")
public class IDPServiceImpl implements IDPService {

	private final AppPropertiesConfig appPropertiesConfig;

	@Autowired
	public IDPServiceImpl(AppPropertiesConfig appPropertiesConfig) {
		this.appPropertiesConfig = appPropertiesConfig;
	}

	@Override
	public IdentityProviderMetaData getIDPInfo() throws Exception {
		IdentityProviderMetaData idpProviderMetaData = getOktaIDPConfig();
		return idpProviderMetaData;
	}

	/**
	 * Retrieve the OKTA config from the properties file and load it into a
	 * IdentityProviderMetaData.
	 * 
	 */
	private IdentityProviderMetaData getOktaIDPConfig() {
		IdentityProviderMetaData idpProviderMetaData = new IdentityProviderMetaData();
		idpProviderMetaData.setBaseUrl(appPropertiesConfig.getBaseUrl());
		idpProviderMetaData.setClientId(appPropertiesConfig.getClientId());
		idpProviderMetaData.setProtocol(appPropertiesConfig.getProtocol());
		idpProviderMetaData.setScopes(appPropertiesConfig.getScopes());
		idpProviderMetaData.setRedirectUri(appPropertiesConfig.getRedirectUri());
		idpProviderMetaData.setEndSessionRedirectUri(appPropertiesConfig.getEndSessionRedirectUri());
		idpProviderMetaData.setState(appPropertiesConfig.getState());
		idpProviderMetaData.setAuthServerId(appPropertiesConfig.getAuthServerId());
		return idpProviderMetaData;
	}
}
