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
public class Party2Details {
	
	@JsonProperty("vendor_type")
	private String vendorType=null;
	@JsonProperty("vendor_name")
	private String vendorName=null;
	@JsonProperty("represented_by")
	private String representedBy=null;
	@JsonProperty("primary_party")
	private String primaryParty=null;
	@JsonProperty("witness_name_p2")
	private String witnessNameP2=null;
	@JsonProperty("address_p2")
	private String addressP2=null;
	@JsonProperty("uid_p2")
	private String uidP2=null;

}
