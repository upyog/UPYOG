package org.egov.garbageservice.model;

import org.egov.garbageservice.enums.SMSCategory;
import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
/**
 * Payload for sending an SMS through the eGov SMS gateway from garbage-service.
 * Includes mobile, message, template name, category, and optional expiry for OTP flows.
 */
@AllArgsConstructor
public class SMSSentRequest {
	@CustomSafeHtml
	private String mobileNumber;
	@CustomSafeHtml
	private String message;
	private SMSCategory category;
	@CustomSafeHtml
	private String templateId;
	@CustomSafeHtml
	private String templateName;
}