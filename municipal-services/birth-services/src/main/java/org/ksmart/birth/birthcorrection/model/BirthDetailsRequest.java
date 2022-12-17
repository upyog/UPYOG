package org.ksmart.birth.birthcorrection.model;

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
public class BirthDetailsRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("BirthDetails")
    @Valid
    private List<BirthDetail> birthDetails;

    public BirthDetailsRequest addBirthDetails(BirthDetail birthDetail) {
        if (birthDetails == null) {
            birthDetails = null;
        }
        return this;
    }
}
