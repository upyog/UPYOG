package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParapetRequirement extends MdmsFeatureRule {
	
	 @Override
	public String toString() {
		return "ParapetRequirement [parapetValueOne=" + parapetValueOne + ", parapetValueTwo=" + parapetValueTwo + "]";
	}
	@JsonProperty("parapetValueOne")
	    private BigDecimal parapetValueOne;
	    @JsonProperty("parapetValueTwo")
	    private BigDecimal parapetValueTwo;
		public BigDecimal getParapetValueOne() {
			return parapetValueOne;
		}

		public void setParapetValueOne(BigDecimal parapetValueOne) {
			this.parapetValueOne = parapetValueOne;
		}
		public BigDecimal getParapetValueTwo() {
			return parapetValueTwo;
		}
		public void setParapetValueTwo(BigDecimal parapetValueTwo) {
			this.parapetValueTwo = parapetValueTwo;
		}

}
