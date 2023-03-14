package org.ksmart.birth.web.model.stillbirth;

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
public class StillBirthDetailRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("StillBirthChildDetails")
    @Valid
    private List<StillBirthApplication> birthDetails;

    public StillBirthDetailRequest addKsmartBirthDetails(StillBirthApplication birthDetail) {
        if (birthDetails == null) {
            birthDetails = null;
        }
        return this;
    }
}
