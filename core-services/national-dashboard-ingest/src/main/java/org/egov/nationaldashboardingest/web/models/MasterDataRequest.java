package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MasterDataRequest {

    @Valid
    @JsonProperty("MasterData")
    private MasterData masterData;

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

}
