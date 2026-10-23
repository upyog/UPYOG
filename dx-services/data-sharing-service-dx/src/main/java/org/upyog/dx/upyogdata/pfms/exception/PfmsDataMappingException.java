package org.upyog.dx.upyogdata.pfms.exception;

/**
 * Raised when PFMS persistence data cannot be mapped to or from its stored representation
 * (for example JSONB {@code additionaldetails} deserialization).
 */
public class PfmsDataMappingException extends RuntimeException {

    public PfmsDataMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
