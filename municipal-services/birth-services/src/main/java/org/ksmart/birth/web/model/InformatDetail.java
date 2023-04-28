package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformatDetail {
    @Size(max = 1000)
    @JsonProperty("infomantFirstNameEn")
    private String infomantFirstNameEn;

    @Size(max = 1000)
    @JsonProperty("infomantAadhar")
    private String infomantAadhar;

    @Size(max = 1000)
    @JsonProperty("infomantinstitution")
    private String infomantinstitution;

    @Size(max = 12)
    @JsonProperty("infomantMobile")
    private String infomantMobile;

    @Size(max = 64)
    @JsonProperty("informerDesi")
    private String informerDesi;

    @Size(max = 2500)
    @JsonProperty("informerAddress")
    private String informerAddress;

    @Size(max = 64)
    @JsonProperty("isDeclarationInfo")
    private String isDeclarationInfo;
}





