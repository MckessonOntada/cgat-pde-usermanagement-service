package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.IdentityProviderMetaData;

public interface IDPService {

	IdentityProviderMetaData getIDPInfo() throws Exception;
    
}
