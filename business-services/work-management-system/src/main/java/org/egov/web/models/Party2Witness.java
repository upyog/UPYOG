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
public class Party2Witness {
	
	@JsonProperty("agreement_no") 
	private String agreementNo=null;
	
	@JsonProperty("pw_id") 
	private String pwId=null;
	
	@JsonProperty("witness_name_p2")
	private String witnessNameP2=null;
	@JsonProperty("address_p2")
	private String addressP2=null;
	@JsonProperty("uid_p2")
	private String uidP2=null;

}
