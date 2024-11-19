package com.mckesson.cgat.pde.usermanagement.exception.custom;

import java.io.Serial;

public class CGATHttpException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 6137588154219581934L;

	public CGATHttpException(String message) {
		super(message);
	}

	public CGATHttpException(Exception e) {
		super(e.getMessage(), e);
	}

}
