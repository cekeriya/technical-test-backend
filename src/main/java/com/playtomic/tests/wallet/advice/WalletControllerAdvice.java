package com.playtomic.tests.wallet.advice;

import com.playtomic.tests.wallet.exception.ErrorResponse;
import com.playtomic.tests.wallet.exception.WalletException;
import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class WalletControllerAdvice {
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex){
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String message = ex.getConstraintViolations()
								 .stream()
								 .map(e -> e.getPropertyPath().toString() + " " + e.getMessage())
								 .collect(Collectors.joining(", "));

		return new ResponseEntity<>(new ErrorResponse(status.name(), status.value(), message), status);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<String> errors = new ArrayList<>();

		ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
		ex.getBindingResult().getGlobalErrors().forEach(objectError -> errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

		return new ResponseEntity<>(new ErrorResponse(status.name(), status.value(), errors.toString()), status);
	}

	@ExceptionHandler(WalletException.class)
	public ResponseEntity<ErrorResponse> handleWalletException(WalletException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		return new ResponseEntity<>(new ErrorResponse(status.name(), status.value(), e.getMessage()), status);
	}

	@ExceptionHandler(StripeAmountTooSmallException.class)
	public ResponseEntity<ErrorResponse> handleStripeAmountTooSmallException(StripeAmountTooSmallException e) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

		return new ResponseEntity<>(new ErrorResponse(status.name(), status.value(), e.getMessage()), status);
	}
}
