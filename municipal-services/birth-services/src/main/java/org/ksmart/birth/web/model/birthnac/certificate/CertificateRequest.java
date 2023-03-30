package org.ksmart.birth.web.model.birthnac.certificate;

import java.util.ArrayList;
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
public class CertificateRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

	@JsonProperty("CertificateDetails")
	private List<CertificateDetails> certificateDetails;

	public CertificateRequest addCertificateDetails(final CertificateDetails certificateDetail) {
	        if (certificateDetails == null) {
	            certificateDetails = new ArrayList<>();
	        }
	        certificateDetails.add(certificateDetail);

	        return this;
   }


}
