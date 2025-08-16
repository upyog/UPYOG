package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpiralStairRequirement extends MdmsFeatureRule {
	 @JsonProperty("spiralStairValue")
	    private BigDecimal spiralStairValue;
	    @JsonProperty("spiralStairExpectedDiameter")
	    private BigDecimal spiralStairExpectedDiameter;
	    @JsonProperty("spiralStairRadius")
	    private BigDecimal spiralStairRadius;
		public BigDecimal getSpiralStairValue() {
			return spiralStairValue;
		}
		public void setSpiralStairValue(BigDecimal spiralStairValue) {
			this.spiralStairValue = spiralStairValue;
		}
		public BigDecimal getSpiralStairExpectedDiameter() {
			return spiralStairExpectedDiameter;
		}
		public void setSpiralStairExpectedDiameter(BigDecimal spiralStairExpectedDiameter) {
			this.spiralStairExpectedDiameter = spiralStairExpectedDiameter;
		}
		public BigDecimal getSpiralStairRadius() {
			return spiralStairRadius;
		}

		public void setSpiralStairRadius(BigDecimal spiralStairRadius) {
			this.spiralStairRadius = spiralStairRadius;
		}
		@Override
		public String toString() {
			return "SpiralStairRequirement [spiralStairValue=" + spiralStairValue + ", spiralStairExpectedDiameter="
					+ spiralStairExpectedDiameter + ", spiralStairRadius=" + spiralStairRadius + "]";
		}

}
