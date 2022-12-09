package org.bel.birthdeath.crdeath.web.models;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on  05/12/2022
     */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestInfoWrapper {
   
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
}
