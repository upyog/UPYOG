package org.egov.ndc.calculator.web.models.ndc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NdcMdms {
    @JsonProperty("NdcFee")
    private List<NdcFee> ndcFee;

    public List<NdcFee> getNdcFee() {
        return ndcFee;
    }

    public void setNdcFee(List<NdcFee> ndcFee) {
        this.ndcFee = ndcFee;
    }
}
