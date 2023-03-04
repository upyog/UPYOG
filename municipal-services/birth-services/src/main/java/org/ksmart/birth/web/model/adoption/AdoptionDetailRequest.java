package org.ksmart.birth.web.model.adoption;

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
public class AdoptionDetailRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<NewBirthApplication> newBirthDetails;

    public AdoptionDetailRequest addKsmartBirthDetails(NewBirthApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = null;
        }
        return this;
    }
}
