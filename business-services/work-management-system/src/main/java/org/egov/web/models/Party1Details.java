package org.egov.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ApiModel(description = "Collection of audit related fields used by most models")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-10-25T21:43:19.662+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Party1Details {
	
	@JsonProperty("agreement_no") 
	private String agreementNo=null;
	
	@JsonProperty("party1_id") 
	private String party1Id=null;
	@JsonProperty("department_name_party1")
	private String departmentNameParty1=null;
	@JsonProperty("designation")
	private String designation=null;
	@JsonProperty("employee_name")
	private String employeeName=null;
	@JsonProperty("witness_name_p1")
	private String witnessNameP1=null;
	@JsonProperty("address_p1")
	private String addressP1=null;
	@JsonProperty("uid_p1")
	private String uidP1=null;

}
