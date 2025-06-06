/**
 * 
 *
 * @author bpattanayak
 */
package org.egov.finance.master.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.egov.finance.master.errorresponse.ErrorResponse;
import org.egov.finance.master.exception.MasterServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {

	@ExceptionHandler(MasterServiceException.class)
	public ResponseEntity<ErrorResponse> handleSingularityException(MasterServiceException singularityException) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				singularityException.getErrors());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException argumentNotValidException) {
		Map<String, String> errors = new HashMap<>();
		argumentNotValidException.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		ErrorResponse errorResponse=new ErrorResponse(
				LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errors);
		
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);

	}

}
