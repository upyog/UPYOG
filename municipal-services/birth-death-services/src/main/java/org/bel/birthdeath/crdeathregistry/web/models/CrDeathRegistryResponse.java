package org.bel.birthdeath.crdeathregistry.web.models;

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

/**
     * Creates response 
     * Rakhi S IKM
     * on 28.11.2022
     */

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathRegistryResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("deathCertificateDtls")
    @Valid
    private List<CrDeathRegistryDtl> deathCertificateDtls;

    @JsonProperty("Count")
    private int count;

    public CrDeathRegistryResponse addCrDeathDtl(CrDeathRegistryDtl crDeathDtl) {
        if (deathCertificateDtls == null) {
            deathCertificateDtls = new ArrayList<>();
        }
        deathCertificateDtls.add(crDeathDtl);

        return this;
    }
}
