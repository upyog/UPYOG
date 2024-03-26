package org.egov.web.models;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Scheme {
	
	@JsonProperty("scheme_id")
    private String id;
	@JsonProperty("tenantId")
    private String tenantId = null;
	@JsonProperty("start_date")
    private String startDate;
	@JsonProperty("end_date")
    private String endDate;
	

	
	
	@JsonProperty("source_of_fund")
	private String sourceOfFund=null;
	@JsonProperty("scheme_name_en")
	private String schemeNameEn=null;
	@JsonProperty("scheme_name_reg")
	private String schemeNameReg=null;
	@JsonProperty("fund")
	private String fund=null;
	@JsonProperty("description_of_the_scheme")
	private String schemeDescription=null;
	@JsonProperty("upload_document")
	private String uploadDocument=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	

}
