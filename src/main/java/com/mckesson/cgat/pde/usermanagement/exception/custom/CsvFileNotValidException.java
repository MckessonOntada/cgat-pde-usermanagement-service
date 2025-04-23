package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.util.List;

public class CsvFileNotValidException extends RuntimeException {

	private final List<String> errorCodes;

	public CsvFileNotValidException(List<String> errorCodes) {
		super("Error: The uploaded file is invalid. Please upload a valid file.");
		this.errorCodes = errorCodes;
	}

	public List<String> getErrorCodes() {
		return errorCodes;
	}

}
