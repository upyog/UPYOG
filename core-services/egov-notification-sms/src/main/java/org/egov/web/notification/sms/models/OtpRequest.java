package org.egov.web.notification.sms.models;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpRequest {

	@Valid
	private Otp otp;

	public TokenRequest getTokenRequest() {
		return new TokenRequest(getIdentity(), getTenantId());
	}

//    public TokenSearchCriteria toSearchCriteria() {
//        return new TokenSearchCriteria(getUUID(), getTenantId());
//    }

	@JsonIgnore
	private String getIdentity() {
		return otp != null ? otp.getIdentity() : null;
	}

	@JsonIgnore
	private String getUUID() {
		return otp != null ? otp.getUuid() : null;
	}

	@JsonIgnore
	private String getTenantId() {
		return otp != null ? otp.getTenantId() : null;
	}
}
