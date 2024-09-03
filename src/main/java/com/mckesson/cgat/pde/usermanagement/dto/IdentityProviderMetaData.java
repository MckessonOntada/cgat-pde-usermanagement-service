package com.mckesson.cgat.pde.usermanagement.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class IdentityProviderMetaData implements Serializable{

	private static final long serialVersionUID = -492052672419412275L;
	private String baseUrl;
	private String protocol;
	private String authServerId;
	private String clientId;
	private String redirectUri;
	private String endSessionRedirectUri;	
	private String scopes;
	private Map<String,String> state=new HashMap<>();
	
}
