package org.egov.notice.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NoticeComment {
	
	private String uuid;
	private String comment;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
}
