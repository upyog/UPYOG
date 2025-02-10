package org.egov.ewst.web.contracts;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SMSRequest {
	private String mobileNumber;
	private String message;

}
