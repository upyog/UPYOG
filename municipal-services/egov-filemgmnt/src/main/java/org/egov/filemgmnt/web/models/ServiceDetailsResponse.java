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

public class ServiceDetailsResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ServiceDetails")
    @Valid
    private List<ServiceDetails> serviceDetailsreq;

    @JsonProperty("Count")
    private int count;

    public ServiceDetailsResponse addServiceDetails(ServiceDetails serviceDetails) {
        if (serviceDetailsreq == null) {
            serviceDetailsreq = new ArrayList<>();
        }
        serviceDetailsreq.add(serviceDetails);

        return this;

    }

}