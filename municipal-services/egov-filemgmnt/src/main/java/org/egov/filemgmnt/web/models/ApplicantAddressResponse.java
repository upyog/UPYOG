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

public class ApplicantAddressResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ApplicantAddresses")
    @Valid
    private List<ApplicantAddress> applicantAddresses;

    @JsonProperty("Count")
    private int count;

    public ApplicantAddressResponse addApplicantAddress(ApplicantAddress applicantAddress) {
        if (applicantAddresses == null) {

            applicantAddresses = new ArrayList<>();
        }
        applicantAddresses.add(applicantAddress);
        return this;
    }

}