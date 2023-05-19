package org.ksmart.birth.web.model.abandoned;

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
public class AbandonedSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("AbandonedDetails")
    @Valid
    private List<AbandonedApplication> newBirthDetails;

    @JsonProperty("Count")
    private int count;


    public AbandonedSearchResponse addKsmartBirthSearchApplication(AbandonedApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = new ArrayList<>();
        }
        newBirthDetails.add(birthDetail);
        return this;
    }
}
