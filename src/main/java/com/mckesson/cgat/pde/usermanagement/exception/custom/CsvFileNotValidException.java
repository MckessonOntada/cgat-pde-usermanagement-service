package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.util.List;

public class CsvFileNotValidException extends RuntimeException {

	private final List<String> errorCodes;

	public CsvFileNotValidException(List<String> errorCodes) {
		super("Invalid csv file.");
		this.errorCodes = errorCodes;
	}

	public List<String> getErrorCodes() {
		return errorCodes;
	}

}
