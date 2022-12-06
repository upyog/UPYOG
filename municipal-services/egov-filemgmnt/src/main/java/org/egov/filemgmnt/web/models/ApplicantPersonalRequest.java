package org.egov.filemgmnt.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Applicant personal request for create and update.")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApplicantPersonalRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ApplicantPersonals")
    @NotEmpty(message = "Applicant personal is required")
    private List<@Valid ApplicantPersonal> applicantPersonals;

    public ApplicantPersonalRequest addApplicantPersonal(ApplicantPersonal applicantPersonal) {
        if (applicantPersonals == null) {
            applicantPersonals = new ArrayList<>();
        }
        applicantPersonals.add(applicantPersonal);

        return this;
    }
}
