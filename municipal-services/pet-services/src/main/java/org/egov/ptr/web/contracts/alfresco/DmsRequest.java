package org.egov.ptr.web.contracts.alfresco;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DmsRequest {

	private Resource file;
	private String userId;
	private String objectId;
	private String description;
	private String id;
	private String type;
	private String objectName;
	private String comments;
	private String status;
	private String servicetype;
	private String documentType;
	private Long documentId;
	
}
