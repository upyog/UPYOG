package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlantationGreenStripRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("plantationGreenStripPlanValue")
	    private BigDecimal plantationGreenStripPlanValue;
	    @JsonProperty("plantationGreenStripMinWidth")
	    private BigDecimal plantationGreenStripMinWidth;
		public BigDecimal getPlantationGreenStripPlanValue() {
			return plantationGreenStripPlanValue;
		}
		public void setPlantationGreenStripPlanValue(BigDecimal plantationGreenStripPlanValue) {
			this.plantationGreenStripPlanValue = plantationGreenStripPlanValue;
		}
		public BigDecimal getPlantationGreenStripMinWidth() {
			return plantationGreenStripMinWidth;
		}
		public void setPlantationGreenStripMinWidth(BigDecimal plantationGreenStripMinWidth) {
			this.plantationGreenStripMinWidth = plantationGreenStripMinWidth;
		}
		@Override
		public String toString() {
			return "PlantationGreenStripRequirement [plantationGreenStripPlanValue=" + plantationGreenStripPlanValue
					+ ", plantationGreenStripMinWidth=" + plantationGreenStripMinWidth + "]";
		}

}
