package org.bel.birthdeath.crdeath.web.models;

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
public class CrDeathDtlResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("CrDeathDtls")
    @Valid
    private List<CrDeathDtl> crDeathDtls;

    @JsonProperty("Count")
    private int count;

    public CrDeathDtlResponse addCrDeathDtl(CrDeathDtl crDeathDtl) {
        if (crDeathDtls == null) {
            crDeathDtls = new ArrayList<>();
        }
        crDeathDtls.add(crDeathDtl);

        return this;
    }
}
