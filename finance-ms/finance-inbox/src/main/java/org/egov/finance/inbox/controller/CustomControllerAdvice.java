/**
 * 
 *
 * @author bpattanayak
 */
package org.egov.finance.inbox.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.egov.finance.inbox.errorresponse.ErrorResponse;
import org.egov.finance.inbox.exception.InboxServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {

	@ExceptionHandler(InboxServiceException.class)
	public ResponseEntity<ErrorResponse> handleSingularityException(InboxServiceException singularityException) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				singularityException.getErrors());

		log.error("MasterServiceException-{}",singularityException.getLocalizedMessage());
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
		
		log.error("MethodArgumentNotValidException-{}",argumentNotValidException.getLocalizedMessage());
		
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);

	}

}
