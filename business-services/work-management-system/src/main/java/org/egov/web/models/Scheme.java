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
	
	@JsonProperty("Scheme_ID")
    private Long id;
	
	@JsonProperty("Start_Date")
    private String startDate;
	@JsonProperty("End_Date")
    private String endDate;
	

	
	
	@JsonProperty("Source_Of_Fund")
	private String sourceOfFund=null;
	@JsonProperty("Scheme_Name_En")
	private String schemeNameEn=null;
	@JsonProperty("Scheme_Name_Reg")
	private String schemeNameReg=null;
	@JsonProperty("Fund")
	private String fund=null;
	@JsonProperty("Description_Of_the_Scheme")
	private String schemeDescription=null;
	@JsonProperty("Upload_Document")
	private String uploadDocument=null;
	

}
