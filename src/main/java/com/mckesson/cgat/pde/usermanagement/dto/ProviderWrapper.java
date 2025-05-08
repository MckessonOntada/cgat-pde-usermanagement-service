package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Provider;

public class ProviderWrapper {

	private Provider provider;

	private String rawStatusInput;

	public ProviderWrapper(Provider provider, String rawStatusInput) {
		this.provider = provider;
		this.rawStatusInput = rawStatusInput;
	}

	public Provider getProvider() {
		return provider;
	}

	public String getRawStatusInput() {
		return rawStatusInput;
	}

}
