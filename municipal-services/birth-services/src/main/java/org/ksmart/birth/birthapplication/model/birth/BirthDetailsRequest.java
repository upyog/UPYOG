package org.ksmart.birth.birthapplication.model.birth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
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
    private List<BirthApplicationDetail> birthDetails;

    public BirthDetailsRequest addBirthDetails(BirthApplicationDetail birthDetail) {
        if (birthDetails == null) {
            birthDetails = null;
        }
        return this;
    }
}
