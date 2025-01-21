package org.egov.garbageservice.model;

import org.egov.garbageservice.enums.SMSCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SMSSentRequest {
	private String mobileNumber;
	private String message;
	private SMSCategory category;
	private String templateId;
	private String templateName;
}