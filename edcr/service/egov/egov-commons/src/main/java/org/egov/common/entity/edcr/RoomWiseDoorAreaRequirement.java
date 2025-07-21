package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomWiseDoorAreaRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("minDoorWidth")
	    private BigDecimal minDoorWidth;
	    @JsonProperty("minDoorHeight")
	    private BigDecimal minDoorHeight;
		public BigDecimal getMinDoorWidth() {
			return minDoorWidth;
		}
		public void setMinDoorWidth(BigDecimal minDoorWidth) {
			this.minDoorWidth = minDoorWidth;
		}
		public BigDecimal getMinDoorHeight() {
			return minDoorHeight;
		}
		public void setMinDoorHeight(BigDecimal minDoorHeight) {
			this.minDoorHeight = minDoorHeight;
		}
		@Override
		public String toString() {
			return "RoomWiseDoorAreaRequirement [minDoorWidth=" + minDoorWidth + ", minDoorHeight=" + minDoorHeight
					+ "]";
		}

}
