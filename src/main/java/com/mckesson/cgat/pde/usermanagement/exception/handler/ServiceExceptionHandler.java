package com.mckesson.cgat.pde.usermanagement.exception.handler;

import com.mckesson.cgat.pde.usermanagement.exception.custom.ForbiddenActionException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.auth.module.exception.JWTNotFoundException;
import com.mckesson.jbat.auth.module.exception.UserAuthenticationException;
import com.mckesson.jbat.common.util.exception.ApiErrorResponse;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ServiceExceptionHandler {

	@ExceptionHandler({ Exception.class })
	public final ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ JWTNotFoundException.class })
	public final ResponseEntity<?> handleJWTNotFoundException(JWTNotFoundException ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ UserAuthenticationException.class })
	public final ResponseEntity<?> handleUserAuthenticationException(UserAuthenticationException ex,
			WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ UnauthorizedAccessException.class })
	public final ResponseEntity<?> handleUserAuthenticationException(UnauthorizedAccessException ex,
			WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<?> buildResponseEntity(Exception ex, WebRequest request, HttpStatus httpStatus) {
		log.error("Error occurred for request:: {}", request.getDescription(false), ex);
		ApiErrorResponse apiErrorResponse = new ApiErrorResponse(httpStatus, new Date(), ex.getMessage());
		return new ResponseEntity<>(apiErrorResponse, httpStatus);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.toList());
		ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, new Date(), errors.toString());
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ForbiddenActionException.class)
	public ResponseEntity<?> handleValidationErrors(ForbiddenActionException ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({ AccessDeniedException.class })
	public final ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		Throwable rootCause = ex.getCause();
		if (rootCause instanceof ConstraintViolationException constraintEx) {
			String constraintName = constraintEx.getConstraintName();
			String message = String.format("Constraint violation: '%s'", constraintName);
			ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.CONFLICT, new Date(), message);
			return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
		}
		ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.CONFLICT, new Date(),
				"Data integrity violation occurred." + ex.getMessage());
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
	}

	private Map<String, List<String>> getErrorsMap(List<String> errors) {
		Map<String, List<String>> errorResponse = new HashMap<>();
		errorResponse.put("errors", errors);
		return errorResponse;
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> badRequestException(BadRequestException ex, WebRequest request) {
		return buildResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested API not found " + e.getMessage());
	}

}
