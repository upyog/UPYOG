package org.ksmart.birth.birthnacregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthnacregistry.model.RegisterCertificateData;
import org.springframework.validation.annotation.Validated;

import java.util.List;
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacPdfRegisterRequest {
	   @JsonProperty("RequestInfo")
	    private RequestInfo requestInfo = null;

	    @JsonProperty("NacCertificate")
	    private List<RegisterCertificateData> nacCertificate;
	

}
