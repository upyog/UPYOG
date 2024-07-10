package org.egov.tl.web.models.contract.Alfresco;

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

	private MultipartFile file;
	private String userId;
	private String objectId;
	private String description;
	private String id;
	private String type;
	private String objectName;
	private String comments;
	private String status;
	
}
