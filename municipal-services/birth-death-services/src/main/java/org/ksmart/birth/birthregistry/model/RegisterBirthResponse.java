package org.ksmart.birth.birthregistry.model;

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
public class RegisterBirthResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("RegisterBirthDetails")
    @Valid
    private List<RegisterBirthDetail> registerDetails;

    @JsonProperty("Count")
    private int count;

    public RegisterBirthResponse addRegisterBirth(RegisterBirthDetail registerBirthDetail) {
        if (registerDetails == null) {
            registerDetails = new ArrayList<>();
        }
        registerDetails.add(registerBirthDetail);
        return this;
    }
}
