package org.egov.garbageservice.model.contract;

import java.util.List;

import org.egov.garbageservice.enums.NotificationType;
import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single notification item sent to the eGov notification service (SMS, email, or in-app).
 * Specifies channels (notificationTypes), target user, application/module context, message text,
 * optional file attachments, and navigation URL for citizen UI deep links.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDetailsRequest {
	private List<NotificationType> notificationTypes;

	@CustomSafeHtml
	private String userUuid;

	@CustomSafeHtml
	private String applicationNumber;

	@CustomSafeHtml
	private String module;

	@CustomSafeHtml
	private String message;

	@CustomSafeHtml
	private String navUrl;

	@Builder.Default
	private Boolean isBodyPresent = false;

	private List<String> fileStoreIds;

	@CustomSafeHtml
	private String emailSubject;

}
