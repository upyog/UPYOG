package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

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
public class KsmartInitiatorDetail {

    @Size(max = 64)
    @JsonProperty("relation")
    private String relation;

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

}
