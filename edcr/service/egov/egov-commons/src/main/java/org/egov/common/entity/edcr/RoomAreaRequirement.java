package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomAreaRequirement extends MdmsFeatureRule {
	
	  @JsonProperty("roomArea2")
	    private BigDecimal roomArea2;
	    @JsonProperty("roomArea1")
	    private BigDecimal roomArea1;
	    @JsonProperty("roomWidth2")
	    private BigDecimal roomWidth2;
	    @JsonProperty("roomWidth1")
	    private BigDecimal roomWidth1;
	    @JsonProperty("acRoomHeight")
	    private BigDecimal acRoomHeight;
	    
	    @JsonProperty("roomHeight")
	    private BigDecimal roomHeight;
	    
	    @JsonProperty("hillyRoomHeight")
	    private BigDecimal hillyRoomHeight;

	    @JsonProperty("commercialGFheight")
	    private BigDecimal  commercialGFheight;
	    
	    @JsonProperty("commercialACroomHeight")
	    private BigDecimal  commercialACroomHeight;
	 
		public BigDecimal getCommercialACroomHeight() {
			return commercialACroomHeight;
		}
		public void setCommercialACroomHeight(BigDecimal commercialACroomHeight) {
			this.commercialACroomHeight = commercialACroomHeight;
		}
		public BigDecimal getCommercialGFheight() {
			return commercialGFheight;
		}
		public void setCommercialGFheight(BigDecimal commercialGFheight) {
			this.commercialGFheight = commercialGFheight;
		}
		public BigDecimal getHillyRoomHeight() {
			return hillyRoomHeight;
		}
		public void setHillyRoomHeight(BigDecimal hillyRoomHeight) {
			this.hillyRoomHeight = hillyRoomHeight;
		}
		public BigDecimal getRoomHeight() {
			return roomHeight;
		}
		public void setRoomHeight(BigDecimal roomHeight) {
			this.roomHeight = roomHeight;
		}
		public BigDecimal getAcRoomHeight() {
			return acRoomHeight;
		}
		public void setAcRoomHeight(BigDecimal acRoomHeight) {
			this.acRoomHeight = acRoomHeight;
		}
		public BigDecimal getRoomArea2() {
			return roomArea2;
		}
		public void setRoomArea2(BigDecimal roomArea2) {
			this.roomArea2 = roomArea2;
		}
		public BigDecimal getRoomArea1() {
			return roomArea1;
		}
		public void setRoomArea1(BigDecimal roomArea1) {
			this.roomArea1 = roomArea1;
		}
		public BigDecimal getRoomWidth2() {
			return roomWidth2;
		}
		public void setRoomWidth2(BigDecimal roomWidth2) {
			this.roomWidth2 = roomWidth2;
		}
		public BigDecimal getRoomWidth1() {
			return roomWidth1;
		}
		public void setRoomWidth1(BigDecimal roomWidth1) {
			this.roomWidth1 = roomWidth1;
		}
		@Override
		public String toString() {
			return "RoomAreaRequirement [roomArea2=" + roomArea2 + ", roomArea1=" + roomArea1 + ", roomWidth2="
					+ roomWidth2 + ", roomWidth1=" + roomWidth1 + ", acRoomHeight=" + acRoomHeight + ", roomHeight="
					+ roomHeight + ", hillyRoomHeight=" + hillyRoomHeight + "]";
		}
	
}
