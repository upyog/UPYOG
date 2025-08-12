package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonumentDistanceRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("monumentDistance_distanceOne")
	    private BigDecimal monumentDistance_distanceOne;
	    @JsonProperty("monumentDistance_minDistanceTwo")
	    private BigDecimal monumentDistance_minDistanceTwo;
	    @JsonProperty("monumentDistance_maxHeightofbuilding")
	    private BigDecimal monumentDistance_maxHeightofbuilding;
	    @Override
		public String toString() {
			return "MonumentDistanceRequirement [monumentDistance_distanceOne=" + monumentDistance_distanceOne
					+ ", monumentDistance_minDistanceTwo=" + monumentDistance_minDistanceTwo
					+ ", monumentDistance_maxHeightofbuilding=" + monumentDistance_maxHeightofbuilding
					+ ", monumentDistance_maxbuildingheightblock=" + monumentDistance_maxbuildingheightblock + "]";
		}
		@JsonProperty("monumentDistance_maxbuildingheightblock")
	    private BigDecimal monumentDistance_maxbuildingheightblock;
		public BigDecimal getMonumentDistance_distanceOne() {
			return monumentDistance_distanceOne;
		}
		public void setMonumentDistance_distanceOne(BigDecimal monumentDistance_distanceOne) {
			this.monumentDistance_distanceOne = monumentDistance_distanceOne;
		}
		public BigDecimal getMonumentDistance_minDistanceTwo() {
			return monumentDistance_minDistanceTwo;
		}
		public void setMonumentDistance_minDistanceTwo(BigDecimal monumentDistance_minDistanceTwo) {
			this.monumentDistance_minDistanceTwo = monumentDistance_minDistanceTwo;
		}
		public BigDecimal getMonumentDistance_maxHeightofbuilding() {
			return monumentDistance_maxHeightofbuilding;
		}
		public void setMonumentDistance_maxHeightofbuilding(BigDecimal monumentDistance_maxHeightofbuilding) {
			this.monumentDistance_maxHeightofbuilding = monumentDistance_maxHeightofbuilding;
		}
		public BigDecimal getMonumentDistance_maxbuildingheightblock() {
			return monumentDistance_maxbuildingheightblock;
		}
		public void setMonumentDistance_maxbuildingheightblock(BigDecimal monumentDistance_maxbuildingheightblock) {
			this.monumentDistance_maxbuildingheightblock = monumentDistance_maxbuildingheightblock;
		}

}
