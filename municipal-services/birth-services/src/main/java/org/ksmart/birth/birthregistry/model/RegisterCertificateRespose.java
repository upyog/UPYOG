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
public class RegisterCertificateRespose {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("RegisterBirthCert")
    @Valid
    private List<RegisterCertificateData> registerBirthCerts;

    @JsonProperty("Count")
    private int count;

    public RegisterCertificateRespose addRegisterBirth(RegisterCertificateData registerCertificateData) {
        if (registerBirthCerts == null) {
            registerBirthCerts = new ArrayList<>();
        }
        registerBirthCerts.add(registerCertificateData);
        return this;
    }
}
