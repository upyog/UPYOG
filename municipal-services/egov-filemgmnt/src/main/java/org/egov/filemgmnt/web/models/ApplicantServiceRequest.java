package org.egov.filemgmnt.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
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

public class ApplicantServiceRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ApplicantServices")
    @Valid
    private List<ApplicantService> applicantServices;

    public ApplicantServiceRequest addApplicantService(ApplicantService applicantService) {
        if (applicantServices == null) {
            applicantServices = new ArrayList<>();
        }
        applicantServices.add(applicantService);

        return this;
    }
}