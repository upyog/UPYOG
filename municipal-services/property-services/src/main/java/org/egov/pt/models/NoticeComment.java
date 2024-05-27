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
	
	@JsonProperty("particulars")
	private String particulars;
	
	@JsonProperty("asPerReturnFiled")
	private String asreturnFiled;
	
	@JsonProperty("asPerMunicipality")
	private String asperMunispality;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
