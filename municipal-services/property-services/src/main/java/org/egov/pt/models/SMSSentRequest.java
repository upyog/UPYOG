package org.egov.pt.models;

import org.egov.pt.models.enums.SMSCategory;

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