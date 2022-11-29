package org.bel.birthdeath.crdeathregistry.web.models;

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

/**
     * Creates request 
     * Rakhi S IKM
     * on 28.11.2022
     */

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathRegistryRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("deathCertificateDtls")
    @Valid
    private List<CrDeathRegistryDtl> deathCertificateDtls;

    public CrDeathRegistryRequest addCrDeathDtl(CrDeathRegistryDtl crDeathRegistryDtl) {
        if (deathCertificateDtls == null) {
            deathCertificateDtls = new ArrayList<>();
        }
        deathCertificateDtls.add(crDeathRegistryDtl);

        return this;
    }
}
