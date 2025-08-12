package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlantationRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("percent")
	    private BigDecimal percent;

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "PlantationRequirement [percent=" + percent + "]";
	}

}
