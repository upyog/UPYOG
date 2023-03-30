package org.ksmart.birth.web.model.birthnac.certificate;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
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
public class CertificateResponse {
	
	    @JsonProperty("ResponseInfo")
	    private ResponseInfo responseInfo;

	    @JsonProperty("CertificateDetails")
	    private List<CertificateDetails> certificateDetails;

	    public CertificateResponse addCertificateDetails(final CertificateDetails certificateDetail) {

	        if (certificateDetails == null) {
	            certificateDetails = new ArrayList<>();
	        }
	        certificateDetails.add(certificateDetail);
	        return this;

	    }

}
