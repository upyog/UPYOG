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
public class SDPGBGDetails {
	
	@JsonProperty("agreement_no") 
	private String agreementNo=null;
	
	@JsonProperty("sdpg_id") 
	private String sdpgId=null;
	
	@JsonProperty("deposit_type")
	private String depositType=null;
	@JsonProperty("deposit_amount")
	private Integer depositAmount=null;
	/*
	 * @JsonProperty("work_description") private String workDescription=null;
	 */
	@JsonProperty("account_no")
	private Long accountNo=null;
	@JsonProperty("particulars")
	private String particulars=null;
	@JsonProperty("valid_from_date")
	private String validFromDate=null;
	@JsonProperty("valid_till_date")
	private String validTillDate=null;
	@JsonProperty("bank_branch_ifsc_code")
	private String bankBranchIfscCode=null;
	@JsonProperty("payment_mode")
	private String paymentMode=null;

}
