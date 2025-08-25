package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VentilationRequirement extends MdmsFeatureRule {
	
	  @JsonProperty("ventilationValueOne")
	    private BigDecimal ventilationValueOne;
	    @JsonProperty("ventilationValueTwo")
	    private BigDecimal ventilationValueTwo;
		public BigDecimal getVentilationValueOne() {
			return ventilationValueOne;
		}
		public void setVentilationValueOne(BigDecimal ventilationValueOne) {
			this.ventilationValueOne = ventilationValueOne;
		}
		public BigDecimal getVentilationValueTwo() {
			return ventilationValueTwo;
		}
		public void setVentilationValueTwo(BigDecimal ventilationValueTwo) {
			this.ventilationValueTwo = ventilationValueTwo;
		}
		@Override
		public String toString() {
			return "VentilationRequirement [ventilationValueOne=" + ventilationValueOne + ", ventilationValueTwo="
					+ ventilationValueTwo + "]";
		}

}
