package org.ksmart.birth.web.model.birthnac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<NacApplication> AdoptionDetails;
    public NacSearchResponse addKsmartBirthSearchApplication(NacApplication adoptionDetail) {
        if (AdoptionDetails == null) {
        	AdoptionDetails = new ArrayList<>();
        }
        AdoptionDetails.add(adoptionDetail);
        return this;
    }
}
