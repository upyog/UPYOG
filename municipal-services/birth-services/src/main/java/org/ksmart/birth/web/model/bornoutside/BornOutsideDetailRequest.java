package org.ksmart.birth.web.model.bornoutside;

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
public class BornOutsideDetailRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("BornOutsideChildDetails")
    @Valid
    private List<BornOutsideApplication> newBirthDetails;

    public BornOutsideDetailRequest addBirthDetails(BornOutsideApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = null;
        }
        return this;
    }
}
