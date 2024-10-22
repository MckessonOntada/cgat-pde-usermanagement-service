package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends Exception {

	@Serial
	private static final long serialVersionUID = 9137588552819081946L;

	public UnauthorizedAccessException(String message) {
		super(message);
	}

}
