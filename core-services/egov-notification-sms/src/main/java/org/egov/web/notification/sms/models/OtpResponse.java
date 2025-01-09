package org.egov.web.notification.sms.models;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpResponse {
	private ResponseInfo responseInfo;
	private Otp otp;

	public OtpResponse(Token token) {
		if (token != null) {
			otp = new Otp(token);
		}
	}
}
