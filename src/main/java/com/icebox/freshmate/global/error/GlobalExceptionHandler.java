package com.icebox.freshmate.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.icebox.freshmate.global.error.exception.AuthenticationException;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidValueException.class)
	public ResponseEntity<ErrorResponse> handleInvalidValueException(InvalidValueException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
}
