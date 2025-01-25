package org.egov.web.notification.sms.models;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTPSentRequest {

	private String number;
	private String userUuid;

	public boolean isValid() {
		return isNotEmpty(number) && isNotEmpty(userUuid);
	}

}
