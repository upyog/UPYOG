package org.ksmart.birth.birthnacregistry.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
 

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
public class NacCertResponse {

	 @JsonProperty("responseInfo")
	  private ResponseInfo responseInfo = null;

	  @JsonProperty("nacCertificate")
	  private List<NacCertificate> nacCertificates = null;
	  
	  @JsonProperty("filestoreId")
	  private String filestoreId;
	  
	  @JsonProperty("consumerCode")
	  private String consumerCode;
	  
	  @JsonProperty("tenantId")
	  private String tenantId;

}
