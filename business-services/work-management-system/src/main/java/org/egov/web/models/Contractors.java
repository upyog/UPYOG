package org.egov.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contractors {
	
	
	@JsonProperty("agreement_no") 
	private String agreementNo=null;
	
	@JsonProperty("con_id") 
	private String conId=null;
	
	@JsonProperty("vendor_type")
	private String vendorType=null;
	@JsonProperty("vendor_name")
	private String vendorName=null;
	@JsonProperty("represented_by")
	private String representedBy=null;
	@JsonProperty("primary_party")
	private String primaryParty=null;

}
