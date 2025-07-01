package com.mckesson.cgat.pde.usermanagement.exception.custom;

import lombok.Getter;

import java.util.List;

@Getter
public class FileNotValidException extends RuntimeException implements FileValidationError {

	private String errorMessage;

	public FileNotValidException(String errorMessage) {
		super("The file you uploaded isn’t supported. Please upload a Valid file.");
		this.errorMessage = errorMessage;
	}

	@Override
	public List<String> getErrorCodes() {
		return List.of(errorMessage);
	}

}
