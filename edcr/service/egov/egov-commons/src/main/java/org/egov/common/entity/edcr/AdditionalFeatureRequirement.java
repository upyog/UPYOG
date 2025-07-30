package org.egov.common.entity.edcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class AdditionalFeatureRequirement extends MdmsFeatureRule {

    @JsonProperty("additionalFeatureMinRequiredFloorHeight")
    private BigDecimal additionalFeatureMinRequiredFloorHeight;
    @JsonProperty("additionalFeatureMaxPermissibleFloorHeight")
    private BigDecimal additionalFeatureMaxPermissibleFloorHeight;

    public BigDecimal getAdditionalFeatureMinRequiredFloorHeight() {
        return additionalFeatureMinRequiredFloorHeight;
    }

    public void setAdditionalFeatureMinRequiredFloorHeight(BigDecimal additionalFeatureMinRequiredFloorHeight) {
        this.additionalFeatureMinRequiredFloorHeight = additionalFeatureMinRequiredFloorHeight;
    }

    public BigDecimal getAdditionalFeatureMaxPermissibleFloorHeight() {
        return additionalFeatureMaxPermissibleFloorHeight;
    }

    public void setAdditionalFeatureMaxPermissibleFloorHeight(BigDecimal additionalFeatureMaxPermissibleFloorHeight) {
        this.additionalFeatureMaxPermissibleFloorHeight = additionalFeatureMaxPermissibleFloorHeight;
    }

    @Override
    public String toString() {
        return "AdditionalFeatureRequirement [additionalFeatureMinRequiredFloorHeight=" + additionalFeatureMinRequiredFloorHeight
                + ", additionalFeatureMaxPermissibleFloorHeight=" + additionalFeatureMaxPermissibleFloorHeight + "]";
    }

}
