package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ToiletRequirement extends MdmsFeatureRule {
	
	  @JsonProperty("minToiletArea")
	    private BigDecimal minToiletArea;
	    @JsonProperty("minToiletWidth")
	    private BigDecimal minToiletWidth;
	    @JsonProperty("minToiletVentilation")
	    private BigDecimal minToiletVentilation;
		public BigDecimal getMinToiletArea() {
			return minToiletArea;
		}
		public void setMinToiletArea(BigDecimal minToiletArea) {
			this.minToiletArea = minToiletArea;
		}
		public BigDecimal getMinToiletWidth() {
			return minToiletWidth;
		}
		public void setMinToiletWidth(BigDecimal minToiletWidth) {
			this.minToiletWidth = minToiletWidth;
		}
		public BigDecimal getMinToiletVentilation() {
			return minToiletVentilation;
		}
		public void setMinToiletVentilation(BigDecimal minToiletVentilation) {
			this.minToiletVentilation = minToiletVentilation;
		}
		@Override
		public String toString() {
			return "ToiletRequirement [minToiletArea=" + minToiletArea + ", minToiletWidth=" + minToiletWidth
					+ ", minToiletVentilation=" + minToiletVentilation + "]";
		}

}
