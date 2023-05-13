package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitiatorDetail {

    @Size(max = 64)
    @JsonProperty("initiator")
    private String initiator;

    @Size(max = 64)
    @JsonProperty("relation")
    private String relation;

    @Size(max = 300)
    @JsonProperty("initiatorInstitutionName")
    private String initiatorInstitutionName;

    @Size(max = 1000)
    @JsonProperty("initiatorNameEn")
    private String initiatorNameEn;

    @Size(max = 15)
    @JsonProperty("initiatorAadhar")
    private String initiatorAadharNo;

    @Size(max = 12)
    @JsonProperty("initiatorMobile")
    private String initiatorMobileNo;

    @Size(max = 64)
    @JsonProperty("initiatorDesi")
    private String initiatorDesi;

    @Size(max = 2000)
    @JsonProperty("initiatorAddress")
    private String initiatorAddress;

    @JsonProperty("isInitiatorDeclaration")
    private Boolean isInitiatorDeclaration;

    @JsonProperty("isCaretaker")
    private Boolean isCaretaker;

    @JsonProperty("isGuardian")
    private Boolean isGuardian;

    @Size(max = 20)
    @JsonProperty("ipopList")
    private String ipopList;

    @Size(max = 200)
    @JsonProperty("ipopNumber")
    private String ipopNumber;

    @Size(max = 200)
    @JsonProperty("obstetricsNumber")
    private String obstetricsNumber;
}
