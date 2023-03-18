package org.ksmart.birth.web.model.abandoned;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MotherDetail {

    @Size(max = 64)
    @JsonProperty("motherUuid")
    private String motherUuid;
    @Size(max = 64)
    @JsonProperty("motherFirstNameEn")
    private String firstNameEn;
    @Size(max = 64)
    @JsonProperty("motherFirstNameMl")
    private String firstNameMl;
    @Size(max = 64)
    @JsonProperty("motherAadhar")
    private String motherAadhar;

    @Size(max = 2500)
    @JsonProperty("addressOfMother")
    private String addressOfMother;
    @Size(max = 64)
    @JsonProperty("motherBioAdopt")
    private String motherBioAdopt;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
