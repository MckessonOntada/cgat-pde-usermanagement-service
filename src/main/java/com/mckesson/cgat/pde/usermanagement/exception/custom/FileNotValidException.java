package com.mckesson.cgat.pde.usermanagement.exception.custom;

import lombok.Getter;

@Getter
public class FileNotValidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Throwable ex;

	private String errorMessage;

	public FileNotValidException(String errorMessage, Throwable ex) {
		super(errorMessage, ex);
	}

	public FileNotValidException(String errorMessage) {
		super(errorMessage, null);
	}

}
