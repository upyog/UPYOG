package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KitchenRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("kitchenHeight")
	    private BigDecimal kitchenHeight;
	    @JsonProperty("kitchenArea")
	    private BigDecimal kitchenArea;
	    
	    @JsonProperty("kitchenDiningVentilationArea")
	    private BigDecimal kitchenDiningVentilationArea;
	    
	    @JsonProperty("kitchenDiningVentilationWidth")
	    private BigDecimal kitchenDiningVentilationWidth;

		public BigDecimal getKitchenHeight() {
			return kitchenHeight;
		}
		@Override
		public String toString() {
			return "KitchenRequirement [kitchenHeight=" + kitchenHeight + ", kitchenArea=" + kitchenArea
					+ ", kitchenWidth=" + kitchenWidth + ", kitchenStoreArea=" + kitchenStoreArea
					+ ", kitchenStoreWidth=" + kitchenStoreWidth + ", kitchenDiningWidth=" + kitchenDiningWidth
					+ ", kitchenDiningArea=" + kitchenDiningArea + "]";
		}
		public void setKitchenHeight(BigDecimal kitchenHeight) {
			this.kitchenHeight = kitchenHeight;
		}
		public BigDecimal getKitchenDiningVentilationArea() {
			return kitchenDiningVentilationArea;
		}
		public void setKitchenDiningVentilationArea(BigDecimal kitchenDiningVentilationArea) {
			this.kitchenDiningVentilationArea = kitchenDiningVentilationArea;
		}
		public BigDecimal getKitchenDiningVentilationWidth() {
			return kitchenDiningVentilationWidth;
		}
		public void setKitchenDiningVentilationWidth(BigDecimal kitchenDiningVentilationWidth) {
			this.kitchenDiningVentilationWidth = kitchenDiningVentilationWidth;
		}
		public BigDecimal getKitchenArea() {
			return kitchenArea;
		}
		public void setKitchenArea(BigDecimal kitchenArea) {
			this.kitchenArea = kitchenArea;
		}
		public BigDecimal getKitchenWidth() {
			return kitchenWidth;
		}
		public void setKitchenWidth(BigDecimal kitchenWidth) {
			this.kitchenWidth = kitchenWidth;
		}
		public BigDecimal getKitchenStoreArea() {
			return kitchenStoreArea;
		}
		public void setKitchenStoreArea(BigDecimal kitchenStoreArea) {
			this.kitchenStoreArea = kitchenStoreArea;
		}
		public BigDecimal getKitchenStoreWidth() {
			return kitchenStoreWidth;
		}
		public void setKitchenStoreWidth(BigDecimal kitchenStoreWidth) {
			this.kitchenStoreWidth = kitchenStoreWidth;
		}
		public BigDecimal getKitchenDiningWidth() {
			return kitchenDiningWidth;
		}
		public void setKitchenDiningWidth(BigDecimal kitchenDiningWidth) {
			this.kitchenDiningWidth = kitchenDiningWidth;
		}
		public BigDecimal getKitchenDiningArea() {
			return kitchenDiningArea;
		}
		public void setKitchenDiningArea(BigDecimal kitchenDiningArea) {
			this.kitchenDiningArea = kitchenDiningArea;
		}
		@JsonProperty("kitchenWidth")
	    private BigDecimal kitchenWidth;
	    @JsonProperty("kitchenStoreArea")
	    private BigDecimal kitchenStoreArea;
	    @JsonProperty("kitchenStoreWidth")
	    private BigDecimal kitchenStoreWidth;
	    @JsonProperty("kitchenDiningWidth")
	    private BigDecimal kitchenDiningWidth;
	    @JsonProperty("kitchenDiningArea")
	    private BigDecimal kitchenDiningArea;

}
