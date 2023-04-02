package org.ksmart.birth.birthnacregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterNacRequest {

	
	 @JsonProperty("RequestInfo")
	    private RequestInfo requestInfo;
	 
	 @JsonProperty("RegisterNac")
	    @Valid
	    private List<RegisterNac> registernacDetails;
}
