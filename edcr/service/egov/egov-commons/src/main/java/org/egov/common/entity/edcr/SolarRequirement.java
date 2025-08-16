package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolarRequirement extends MdmsFeatureRule {
	 @JsonProperty("solarValueOne")
	    private BigDecimal solarValueOne;
	    @JsonProperty("solarValueTwo")
	    private BigDecimal solarValueTwo;
		public BigDecimal getSolarValueOne() {
			return solarValueOne;
		}
		public void setSolarValueOne(BigDecimal solarValueOne) {
			this.solarValueOne = solarValueOne;
		}
		public BigDecimal getSolarValueTwo() {
			return solarValueTwo;
		}
		public void setSolarValueTwo(BigDecimal solarValueTwo) {
			this.solarValueTwo = solarValueTwo;
		}
		@Override
		public String toString() {
			return "SolarRequirement [solarValueOne=" + solarValueOne + ", solarValueTwo=" + solarValueTwo + "]";
		}

}
