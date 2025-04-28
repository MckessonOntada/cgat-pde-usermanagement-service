package com.mckesson.cgat.pde.usermanagement.model;

public enum ProviderStatus {

	ACTIVE, INACTIVE;

	public static ProviderStatus fromString(String input) {
		if (input == null) {
			return INACTIVE; // Default value
		}
		for (ProviderStatus status : ProviderStatus.values()) {
			if (status.name().equalsIgnoreCase(input)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid ProviderStatus: " + input);
	}

}
