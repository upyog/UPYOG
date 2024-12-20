package org.egov.schedulerservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerServiceException extends RuntimeException {

	private static final long serialVersionUID = -8549181961861103922L;

	private final String message;
	private final String errorCode;

	public SchedulerServiceException(String errorCode, String message) {
		super();
		this.message = message;
		this.errorCode = errorCode;
	}

}
