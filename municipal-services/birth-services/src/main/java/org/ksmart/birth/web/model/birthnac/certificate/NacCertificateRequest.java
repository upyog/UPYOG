package org.ksmart.birth.web.model.birthnac.certificate;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacCertificateRequest {
	
	 @JsonProperty("RequestInfo")
	 private RequestInfo requestInfo;
	 
	 @JsonProperty("NacCertificate")
	 private List<NacCertificate> naccertificate;

}
