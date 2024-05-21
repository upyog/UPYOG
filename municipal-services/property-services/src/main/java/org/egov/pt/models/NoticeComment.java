package org.egov.pt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
