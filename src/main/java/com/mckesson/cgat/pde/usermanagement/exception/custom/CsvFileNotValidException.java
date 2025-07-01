package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.util.List;

public class CsvFileNotValidException extends RuntimeException implements FileValidationError {

	private final List<String> errorCodes;

	public CsvFileNotValidException(List<String> errorCodes) {
		super("The file you uploaded isn’t supported. Please upload a Valid file.");
		this.errorCodes = errorCodes;
	}

	@Override
	public List<String> getErrorCodes() {
		return errorCodes;
	}

}
