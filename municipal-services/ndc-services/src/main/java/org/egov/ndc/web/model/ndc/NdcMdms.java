package org.egov.ndc.web.model.ndc;

import java.util.List;

public class NdcMdms {
    private List<NdcFee> NdcFee; // Note: case-sensitive based on your JSON

    public List<NdcFee> getNdcFee() {
        return NdcFee;
    }

    public void setNdcFee(List<NdcFee> NdcFee) {
        this.NdcFee = NdcFee;
    }
}