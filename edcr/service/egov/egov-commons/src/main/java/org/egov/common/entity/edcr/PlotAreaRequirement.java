package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlotAreaRequirement extends MdmsFeatureRule {
	@JsonProperty("plotAreaValueOne")
    private BigDecimal plotAreaValueOne;
    @JsonProperty("plotAreaValueTwo")
    private BigDecimal plotAreaValueTwo;
	public BigDecimal getPlotAreaValueOne() {
		return plotAreaValueOne;
	}
	public void setPlotAreaValueOne(BigDecimal plotAreaValueOne) {
		this.plotAreaValueOne = plotAreaValueOne;
	}
	public BigDecimal getPlotAreaValueTwo() {
		return plotAreaValueTwo;
	}
	public void setPlotAreaValueTwo(BigDecimal plotAreaValueTwo) {
		this.plotAreaValueTwo = plotAreaValueTwo;
	}
	@Override
	public String toString() {
		return "PlotAreaRequirement [plotAreaValueOne=" + plotAreaValueOne + ", plotAreaValueTwo=" + plotAreaValueTwo
				+ "]";
	}

}
