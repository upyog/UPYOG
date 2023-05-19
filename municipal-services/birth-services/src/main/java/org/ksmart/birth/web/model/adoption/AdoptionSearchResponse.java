package org.ksmart.birth.web.model.adoption;

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
public class AdoptionSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<AdoptionApplication> AdoptionDetails;

    @JsonProperty("Count")
    private int count;

    public AdoptionSearchResponse addKsmartBirthSearchApplication(AdoptionApplication adoptionDetail) {
        if (AdoptionDetails == null) {
        	AdoptionDetails = new ArrayList<>();
        }
        AdoptionDetails.add(adoptionDetail);
        return this;
    }
}
