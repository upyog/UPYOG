package org.bel.birthdeath.crdeath.web.models;

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
     * 
     */

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathDtlRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("deathCertificateDtls")
    @Valid
    private List<CrDeathDtl> deathCertificateDtls;

    public CrDeathDtlRequest addCrDeathDtl(CrDeathDtl crDeathDtl) {
        if (deathCertificateDtls == null) {
            deathCertificateDtls = new ArrayList<>();
        }
        deathCertificateDtls.add(crDeathDtl);

        return this;
    }
}
