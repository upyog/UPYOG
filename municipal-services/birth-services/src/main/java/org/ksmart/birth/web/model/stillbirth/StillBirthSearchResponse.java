package org.ksmart.birth.web.model.stillbirth;

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
public class StillBirthSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<StillBirthApplication> newBirthDetails;

    @JsonProperty("Count")
    private int count;
    public StillBirthSearchResponse addKsmartBirthSearchApplication(StillBirthApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = new ArrayList<>();
        }
        newBirthDetails.add(birthDetail);
        return this;
    }
}
