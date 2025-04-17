package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.util.List;

public class ResourceConflictException extends RuntimeException {

	private final List<String> errorCodes;

	public ResourceConflictException(List<String> errorCodes) {
		super("Resource conflict occurred.");
		this.errorCodes = errorCodes;
	}

	public List<String> getErrorCodes() {
		return errorCodes;
	}

}
