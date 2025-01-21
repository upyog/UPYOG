package org.egov.web.notification.sms.exception;

import org.egov.web.notification.sms.models.TokenRequest;

import lombok.Getter;

public class InvalidTokenRequestException extends RuntimeException {
	private static final long serialVersionUID = -1900986732529893867L;

	@Getter
	private TokenRequest tokenRequest;

	public InvalidTokenRequestException(TokenRequest tokenRequest) {

		this.tokenRequest = tokenRequest;
	}
}
