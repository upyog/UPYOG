package org.upyog.dx.upyogdata.pfms.exception;

/**
 * Raised when the service cannot publish an enriched PFMS payload to Kafka.
 */
public class PfmsKafkaPublishException extends RuntimeException {

    public PfmsKafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
