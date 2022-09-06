package org.egov.filemgmnt.api.controllers;

import org.egov.filemgmnt.common.exception.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(annotations = { Controller.class, RestController.class })
@Slf4j
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GlobalException.class })
    ResponseEntity<Object> handleGlobalExceptions(GlobalException ex, WebRequest request) {
        String message = (ex != null) ? ex.getMessage() : "Not available";
        HttpStatus httpStatus = (ex != null) ? ex.getHttpStatus()
                : HttpStatus.INTERNAL_SERVER_ERROR;

        log.info("*** handleGlobalExceptions - {}", message);
        return new ResponseEntity<>(message, httpStatus);
    }

    @ExceptionHandler // (value = { Exception.class, RuntimeException.class })
    ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request) {
        String message = (ex != null) ? ex.getMessage() : "Not available";
        log.info("*** handleExceptions - {}", message);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
