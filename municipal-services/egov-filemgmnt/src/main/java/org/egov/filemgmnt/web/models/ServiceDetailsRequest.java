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
public class ServiceDetailsRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ServiceDetails")
    @Valid
    private List<ServiceDetails> serviceDetails;

    public ServiceDetailsRequest addServiceDetails(ServiceDetails serviceDetails) {
        if (serviceDetails == null) {
            this.serviceDetails = new ArrayList<>();
        }
        this.serviceDetails.add(serviceDetails);

        return this;
    }

}