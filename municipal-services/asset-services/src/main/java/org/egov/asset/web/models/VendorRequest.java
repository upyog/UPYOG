package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorRequest {

    @JsonProperty("RequestInfo")
    @Valid
    private org.egov.common.contract.request.RequestInfo requestInfo;

    @JsonProperty("Vendor")
    @Valid
    private Vendor vendor;
}