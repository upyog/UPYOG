package org.egov.garbageservice.model;

import java.util.List;
import java.util.Set;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
/**
 * Email message content (recipients, subject, body) for the eGov email service.
 * Nested inside EmailRequest when NotificationService sends bill or status emails.
 */
@Builder
public class Email {

	private Set<String> emailTo;

	@CustomSafeHtml
	private String subject;

	@CustomSafeHtml
	private String body;

	@JsonProperty("isHTML")
	private boolean isHTML;

	private List<String> fileStoreIds;
}