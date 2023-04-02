package org.ksmart.birth.web.model.birthnac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.ksmart.birth.common.model.AuditDetails;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacApplicantDetail {
	@Size(max = 64)
    @JsonProperty("id")
    private String id;
    @Size(max = 1000)
    @JsonProperty("applicantNameEn")
    private String applicantNameEn;
    
     
    @Size(max = 64)
    @JsonProperty("careofapplicant")
    private String careofapplicant;
    
    @Size(max = 1000)
    @JsonProperty("applicantAddressEn")
    private String applicantAddressEn;
    @Size(max = 12)
    @JsonProperty("aadharNo")
    private String aadharNo;
    @Size(max = 64)
    @JsonProperty("mobileNo")
    private String mobileNo;
    @Size(max = 2500)
    @JsonProperty("isDeclared")
    private Boolean isDeclared;
    
    @Size(max = 64)
    @JsonProperty("isunderstood")
    private Boolean isunderstood;
    
    
    
    @Size(max = 64)
    @JsonProperty("declarationId")
    private String declarationId;
    @Size(max = 64)
    @JsonProperty("isEsigned")
    private Boolean isEsigned;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
