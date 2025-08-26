package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoadWidthRequirement extends MdmsFeatureRule {
	
	 public BigDecimal getPermissibleULB() {
		return permissibleULB;
	}

	public void setPermissibleULB(BigDecimal permissibleULB) {
		this.permissibleULB = permissibleULB;
	}

	public BigDecimal getPermissibleOutsideULB() {
		return permissibleOutsideULB;
	}

	public void setPermissibleOutsideULB(BigDecimal permissibleOutsideULB) {
		this.permissibleOutsideULB = permissibleOutsideULB;
	}

	@JsonProperty("permissibleULB")
	 private BigDecimal permissibleULB;
	 
	 @Override
	public String toString() {
		return "RoadWidthRequirement [permissibleULB=" + permissibleULB + ", permissibleOutsideULB="
				+ permissibleOutsideULB + "]";
	}

	@JsonProperty("permissibleOutsideULB")
	 private BigDecimal permissibleOutsideULB;

}
