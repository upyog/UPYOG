package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PassageRequirement extends MdmsFeatureRule{
	
	  @JsonProperty("passageServiceValueOne")
	    private BigDecimal passageServiceValueOne;
	    @JsonProperty("passageServiceValueTwo")
	    private BigDecimal passageServiceValueTwo;
		public BigDecimal getPassageServiceValueOne() {
			return passageServiceValueOne;
		}
		public void setPassageServiceValueOne(BigDecimal passageServiceValueOne) {
			this.passageServiceValueOne = passageServiceValueOne;
		}
		public BigDecimal getPassageServiceValueTwo() {
			return passageServiceValueTwo;
		}
		public void setPassageServiceValueTwo(BigDecimal passageServiceValueTwo) {
			this.passageServiceValueTwo = passageServiceValueTwo;
		}
		@Override
		public String toString() {
			return "PassageRequirement [passageServiceValueOne=" + passageServiceValueOne + ", passageServiceValueTwo="
					+ passageServiceValueTwo + "]";
		}

}
