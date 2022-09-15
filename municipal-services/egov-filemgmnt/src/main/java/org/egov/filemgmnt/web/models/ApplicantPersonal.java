package org.egov.filemgmnt.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantPersonal {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @NotNull
    @Size(max = 64)
    @JsonProperty("firstName")
    private String firstName;

//    @JsonProperty("auditDetails")
//    private AuditDetails auditDetails;
}
