package org.upyog.sv.web.models.notification;

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
