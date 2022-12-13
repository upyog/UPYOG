package org.ksmart.birth.birthregistry.model;

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
public class RegisterBirthDetailsRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("RegisterBirthDetails")
    @Valid
    private List<RegisterBirthDetail> registerBirthDetails;

    public RegisterBirthDetailsRequest addRegisterBirthDetails(RegisterBirthDetail registerBirthDetail) {
        if (registerBirthDetails == null) {
            registerBirthDetails = null;
        }
        return this;
    }


}
