package org.egov.garbageservice.model.contract;

import java.util.List;

import org.egov.garbageservice.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDetailsRequest {
	private List<NotificationType> notificationTypes;

	private String userUuid;

	private String applicationNumber;

	private String module;

	private String message;

	private String navUrl;

	@Builder.Default
	private Boolean isBodyPresent = false;

	private List<String> fileStoreIds;

	private String emailSubject;

}
