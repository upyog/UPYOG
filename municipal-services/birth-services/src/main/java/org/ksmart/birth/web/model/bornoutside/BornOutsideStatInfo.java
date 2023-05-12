package org.ksmart.birth.web.model.bornoutside;

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
public class BornOutsideStatInfo {
     @Size(max = 64)
    @JsonProperty("relation")
    private String relation;

    @Size(max = 1000)
    @JsonProperty("informarNameEn")
    private String informarNameEn;

    @Size(max = 15)
    @JsonProperty("informarAadhar")
    private String informarAadhar;

    @Size(max = 2000)
    @JsonProperty("informarAddress")
    private String informarAddress;

    @Size(max = 20)
    @JsonProperty("informarMobile")
    private String informarMobile;

    @JsonProperty("birthWeight")
    private double birthWeight;
    @Size(max = 64)
    @JsonProperty("pregnancyDuration")
    private Integer pregnancyDuration;
    @Size(max = 64)
    @JsonProperty("medicalAttensionSub")
    private String medicalAttensionSub;
    @Size(max = 300)
    @JsonProperty("medicalAttensionSubEn")
    private String medicalAttensionSubEn;
    @Size(max = 300)
    @JsonProperty("medicalAttensionSubMl")
    private String medicalAttensionSubMl;
    @Size(max = 64)
    @JsonProperty("deliveryMethods")
    private String deliveryMethods;

    @Size(max = 300)
    @JsonProperty("deliveryMethodsEn")
    private String deliveryMethodsEn;

    @Size(max = 300)
    @JsonProperty("deliveryMethodsMl")
    private String deliveryMethodsMl;

    @Size(max = 64)
    @JsonProperty("orderofChildren")
    private Integer orderofChildren;


}
