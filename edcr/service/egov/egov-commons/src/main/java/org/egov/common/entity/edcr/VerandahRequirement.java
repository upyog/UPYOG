package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerandahRequirement extends MdmsFeatureRule {
	
	@JsonProperty("verandahWidth")
    private BigDecimal verandahWidth;
    @JsonProperty("verandahDepth")
    private BigDecimal verandahDepth;
	public BigDecimal getVerandahWidth() {
		return verandahWidth;
	}
	public void setVerandahWidth(BigDecimal verandahWidth) {
		this.verandahWidth = verandahWidth;
	}
	public BigDecimal getVerandahDepth() {
		return verandahDepth;
	}
	public void setVerandahDepth(BigDecimal verandahDepth) {
		this.verandahDepth = verandahDepth;
	}
	@Override
	public String toString() {
		return "VerandahRequirement [verandahWidth=" + verandahWidth + ", verandahDepth=" + verandahDepth + "]";
	}

}
