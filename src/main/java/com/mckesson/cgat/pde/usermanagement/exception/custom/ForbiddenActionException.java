package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.io.Serial;

public class ForbiddenActionException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 9137588552819081946L;

	public ForbiddenActionException(String message) {
		super(message);
	}

}
