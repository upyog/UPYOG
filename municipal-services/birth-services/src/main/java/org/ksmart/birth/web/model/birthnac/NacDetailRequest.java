package org.ksmart.birth.web.model.birthnac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacDetailRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("AdoptionDetails")
    @Valid
    private List<NacApplication> adoptionDetails;

    public NacDetailRequest addAdoptionBirth(NacApplication adoptionDetail) {
        if (adoptionDetails == null) {
        	adoptionDetails = null;
        }
        return this;
    }
}
