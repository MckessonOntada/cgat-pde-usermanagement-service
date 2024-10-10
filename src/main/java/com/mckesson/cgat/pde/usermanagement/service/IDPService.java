package com.mckesson.cgat.pde.usermanagement.service;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;

@Service
public interface IDPService {

	IdentityProviderMetaData getIDPInfo() throws Exception;

}
