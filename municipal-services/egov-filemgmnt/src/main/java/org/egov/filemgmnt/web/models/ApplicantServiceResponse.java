package org.egov.filemgmnt.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
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

public class ApplicantServiceResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ApplicantServices")
    @Valid
    private List<ApplicantService> applicantServices;

    @JsonProperty("Count")
    private int count;

    public ApplicantServiceResponse addApplicantService(ApplicantService applicantService) {
        if (applicantServices == null) {
            applicantServices = new ArrayList<>();
        }
        applicantServices.add(applicantService);

        return this;

    }
}