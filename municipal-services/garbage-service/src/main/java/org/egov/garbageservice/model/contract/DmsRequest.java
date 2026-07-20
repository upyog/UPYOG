package org.egov.garbageservice.model.contract;

import org.egov.tracer.annotations.CustomSafeHtml;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document management upload request for Alfresco or similar DMS integration.
 * Carries the file resource plus object metadata (objectId, objectName, service type, status, comments).
 * Built in GarbageAccountService and AlfrescoService when storing certificates or application documents.
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DmsRequest {

	private Resource file;
	@CustomSafeHtml
	private String userId;
	@CustomSafeHtml
	private String objectId;
	@CustomSafeHtml
	private String description;
	@CustomSafeHtml
	private String id;
	@CustomSafeHtml
	private String type;
	@CustomSafeHtml
	private String objectName;
	@CustomSafeHtml
	private String comments;
	@CustomSafeHtml
	private String status;
	@CustomSafeHtml
	private String servicetype;
	@CustomSafeHtml
	private String documentType;
	private Long documentId;
	
}
