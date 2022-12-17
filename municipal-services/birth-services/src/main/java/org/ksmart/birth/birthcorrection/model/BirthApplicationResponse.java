package org.ksmart.birth.birthcorrection.model;

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


public class BirthApplicationResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("BirthDetails")
    @Valid
    private List<BirthDetail> birthDetails;

    @JsonProperty("Count")
    private int count;

    public BirthApplicationResponse addBirthApplication(BirthDetail birthDetail) {
        if (birthDetails == null) {
            birthDetails = new ArrayList<>();
        }
        birthDetails.add(birthDetail);
        return this;
    }
}
