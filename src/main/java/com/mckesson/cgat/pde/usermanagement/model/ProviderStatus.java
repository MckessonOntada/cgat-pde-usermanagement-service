package com.mckesson.cgat.pde.usermanagement.model;

public enum ProviderStatus {

	ACTIVE, INACTIVE;

	public static ProviderStatus fromString(String input) {
		if (input == null || input.isBlank()) {
			throw new IllegalArgumentException("Status cannot be null or blank");
		}
		try {
			return ProviderStatus.valueOf(input.trim().toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid Status: " + input);
		}
	}

}
