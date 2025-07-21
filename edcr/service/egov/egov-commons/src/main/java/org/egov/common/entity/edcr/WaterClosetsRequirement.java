package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaterClosetsRequirement extends MdmsFeatureRule {
	
	@JsonProperty("waterClosetsVentilationArea")
    private BigDecimal waterClosetsVentilationArea;
    @JsonProperty("waterClosetsHeight")
    private BigDecimal waterClosetsHeight;
    @JsonProperty("waterClosetsArea")
    private BigDecimal waterClosetsArea;
    @JsonProperty("waterClosetsWidth")
    private BigDecimal waterClosetsWidth;
	public BigDecimal getWaterClosetsVentilationArea() {
		return waterClosetsVentilationArea;
	}
	public void setWaterClosetsVentilationArea(BigDecimal waterClosetsVentilationArea) {
		this.waterClosetsVentilationArea = waterClosetsVentilationArea;
	}
	public BigDecimal getWaterClosetsHeight() {
		return waterClosetsHeight;
	}
	public void setWaterClosetsHeight(BigDecimal waterClosetsHeight) {
		this.waterClosetsHeight = waterClosetsHeight;
	}
	public BigDecimal getWaterClosetsArea() {
		return waterClosetsArea;
	}
	public void setWaterClosetsArea(BigDecimal waterClosetsArea) {
		this.waterClosetsArea = waterClosetsArea;
	}
	public BigDecimal getWaterClosetsWidth() {
		return waterClosetsWidth;
	}
	public void setWaterClosetsWidth(BigDecimal waterClosetsWidth) {
		this.waterClosetsWidth = waterClosetsWidth;
	}
	@Override
	public String toString() {
		return "WaterClosetsRequirement [waterClosetsVentilationArea=" + waterClosetsVentilationArea
				+ ", waterClosetsHeight=" + waterClosetsHeight + ", waterClosetsArea=" + waterClosetsArea
				+ ", waterClosetsWidth=" + waterClosetsWidth + "]";
	}

}
