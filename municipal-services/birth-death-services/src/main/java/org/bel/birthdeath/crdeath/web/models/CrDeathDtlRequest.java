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

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathDtlRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("CrDeathDtls")
    @Valid
    private List<CrDeathDtl> crDeathDtls;

    public CrDeathDtlRequest addCrDeathDtl(CrDeathDtl crDeathDtl) {
        if (crDeathDtls == null) {
            crDeathDtls = new ArrayList<>();
        }
        crDeathDtls.add(crDeathDtl);

        return this;
    }
}
