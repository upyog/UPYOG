package org.egov.commons.exception;

import org.egov.common.entity.dcr.helper.ErrorDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EdcrExceptionHandler {

    @ExceptionHandler(EdcrException.class)
    public ResponseEntity<ErrorDetail> handleEdcrException(EdcrException ex) {

        ErrorDetail error = new ErrorDetail();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorMessage(ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }
}
