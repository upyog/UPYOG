package org.egov.filemgmnt.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -5939615959961000836L;

    @Getter
    private final HttpStatus httpStatus;

    public GlobalException(String message) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public GlobalException(String message,
            Throwable cause) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

    public GlobalException(HttpStatus httpStatus,
            String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public GlobalException(HttpStatus httpStatus,
            String message,
            Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
