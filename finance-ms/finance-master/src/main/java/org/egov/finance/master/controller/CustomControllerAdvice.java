package org.egov.finance.master.controller;

import java.time.LocalDateTime;

import org.egov.finance.master.errorresponse.ErrorResponse;
import org.egov.finance.master.exception.SingularityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {
	
	@ExceptionHandler(SingularityException.class)
	public ResponseEntity<ErrorResponse> handleSingularityException(SingularityException singularityException)
	{
		ErrorResponse errorResponse=new ErrorResponse(
				LocalDateTime.now(), 
				HttpStatus.BAD_REQUEST.value(), 
				singularityException.getErrors());
		
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}

}
