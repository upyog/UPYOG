package org.ksmart.birth.web.model.abandoned;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbandonedRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("AbandonedDetails")
    @Valid
    private List<AbandonedApplication> birthDetails;

    public AbandonedRequest addBirthDetails(AbandonedApplication birthDetail) {
        if (birthDetails == null) {
            birthDetails = null;
        }
        return this;
    }
}
