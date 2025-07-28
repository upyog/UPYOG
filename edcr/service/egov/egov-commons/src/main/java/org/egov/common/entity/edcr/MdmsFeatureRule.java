package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MdmsFeatureRule {

	 @JsonProperty("id")
	    private Integer id;

	    @JsonProperty("fromPlotArea")
	    private BigDecimal fromPlotArea;

	    @JsonProperty("toPlotArea")
	    private BigDecimal toPlotArea;

	    @JsonProperty("state")
	    private String state;

	    @JsonProperty("city")
	    private String city;

	    @JsonProperty("zone")
	    private String zone;

	    @JsonProperty("subZone")
	    private String subZone;

	    @JsonProperty("occupancy")
	    private String occupancy;
	    
	    @JsonProperty("fromPlotDepth")
	    private BigDecimal fromPlotDepth;

	    @JsonProperty("toPlotDepth")
	    private BigDecimal toPlotDepth;
	    
	    @JsonProperty("fromBuildingHeight")
	    private BigDecimal fromBuildingHeight;

	    @JsonProperty("toBuildingHeigh")
	    private BigDecimal toBuildingHeight;
	    
	    public BigDecimal getFromBuildingHeight() {
			return fromBuildingHeight;
		}
		public void setFromBuildingHeight(BigDecimal fromBuildingHeight) {
			this.fromBuildingHeight = fromBuildingHeight;
		}
		public BigDecimal getToBuildingHeight() {
			return toBuildingHeight;
		}
		public void setToBuildingHeight(BigDecimal toBuildingHeight) {
			this.toBuildingHeight = toBuildingHeight;
		}
		public BigDecimal getFromPlotDepth() {
			return fromPlotDepth;
		}
		public void setFromPlotDepth(BigDecimal fromPlotDepth) {
			this.fromPlotDepth = fromPlotDepth;
		}
		public BigDecimal getToPlotDepth() {
			return toPlotDepth;
		}
		public void setToPlotDepth(BigDecimal toPlotDepth) {
			this.toPlotDepth = toPlotDepth;
		}
		public String getSubOccupancy() {
			return subOccupancy;
		}
		public void setSubOccupancy(String subOccupancy) {
			this.subOccupancy = subOccupancy;
		}

		@JsonProperty("subOccupancy")
	    private String subOccupancy;

	    @JsonProperty("riskType")
	    private String riskType;

	    @JsonProperty("permissible")
	    private BigDecimal permissible;
	    
	    @JsonProperty("fromRoadWidth")
	    private BigDecimal fromRoadWidth;

	    @JsonProperty("toRoadWidth")
	    private BigDecimal toRoadWidth;

//	    @JsonProperty("permissibleone")
//	    private BigDecimal permissibleOne;
//
//	    @JsonProperty("permissibletwo")
//	    private BigDecimal permissibleTwo;
//
//	    @JsonProperty("permissiblethree")
//	    private BigDecimal permissibleThree;

//	    @JsonProperty("bathroomWCRequiredArea")
//	    private BigDecimal bathroomWCRequiredArea;
//
//	    @JsonProperty("bathroomWCRequiredWidth")
//	    private BigDecimal bathroomWCRequiredWidth;
//
//	    @JsonProperty("bathroomWCRequiredHeight")
//	    private BigDecimal bathroomWCRequiredHeight;
//	    
//	    @JsonProperty("mezzanineArea")
//	    private BigDecimal mezzanineArea;
//	    
//	    @JsonProperty("mezzanineHeight")
//	    private BigDecimal mezzanineHeight;
//	    
//	    @JsonProperty("mezzanineBuiltUpArea")
//	    private BigDecimal mezzanineBuiltUpArea;
//
//	    @JsonProperty("fromRoadWidth")
//	    private BigDecimal fromRoadWidth;
//
//	    @JsonProperty("toRoadWidth")
//	    private BigDecimal toRoadWidth;

	    @JsonProperty("active")
	    private Boolean active;

	    // Standard Getters & Setters
	    public Integer getId() { return id; }
	    public void setId(Integer id) { this.id = id; }

	    public BigDecimal getFromPlotArea() { return fromPlotArea; }
	    public void setFromPlotArea(BigDecimal fromPlotArea) { this.fromPlotArea = fromPlotArea; }
	    
	    public BigDecimal getFromRoadWidth() { return fromRoadWidth; }
	    public void setFromRoadWidth(BigDecimal fromRoadWidth) { this.fromRoadWidth = fromRoadWidth; }
	    
	    public BigDecimal getToRoadWidth() { return toRoadWidth; }
	    public void setToRoadWidth(BigDecimal toRoadWidth) { this.toRoadWidth = toRoadWidth; }

	    public BigDecimal getToPlotArea() { return toPlotArea; }
	    public void setToPlotArea(BigDecimal toPlotArea) { this.toPlotArea = toPlotArea; }

	    public String getState() { return state; }
	    public void setState(String state) { this.state = state; }

	    public String getCity() { return city; }
	    public void setCity(String city) { this.city = city; }

	    public String getZone() { return zone; }
	    public void setZone(String zone) { this.zone = zone; }

	    public String getSubZone() { return subZone; }
	    public void setSubZone(String subZone) { this.subZone = subZone; }

	    public String getOccupancy() { return occupancy; }
	    public void setOccupancy(String occupancy) { this.occupancy = occupancy; }

	    public String getRiskType() { return riskType; }
	    public void setRiskType(String riskType) { this.riskType = riskType; }

	    public BigDecimal getPermissible() { return permissible; }
	    public void setPermissible(BigDecimal permissible) { this.permissible = permissible; }

//	    public BigDecimal getPermissibleOne() { return permissibleOne; }
//	    public void setPermissibleOne(BigDecimal permissibleOne) { this.permissibleOne = permissibleOne; }
//
//	    public BigDecimal getPermissibleTwo() { return permissibleTwo; }
//	    public void setPermissibleTwo(BigDecimal permissibleTwo) { this.permissibleTwo = permissibleTwo; }
//
//	    public BigDecimal getPermissibleThree() { return permissibleThree; }
//	    public void setPermissibleThree(BigDecimal permissibleThree) { this.permissibleThree = permissibleThree; }

//	    public BigDecimal getBathroomWCRequiredArea() { return bathroomWCRequiredArea; }
//	    public void setBathroomWCRequiredArea(BigDecimal bathroomWCRequiredArea) { this.bathroomWCRequiredArea = bathroomWCRequiredArea; }
//
//	    public BigDecimal getMezzanineArea() { return mezzanineArea; }
//	    public void setMezzanineArea(BigDecimal mezzanineArea) { this.mezzanineArea = mezzanineArea; }
//	    
//	    public BigDecimal getMezzanineHeight() { return mezzanineHeight; }
//	    public void setMezzanineHeighth(BigDecimal mezzanineHeight) { this.mezzanineHeight = mezzanineHeight; }
//
//	    public BigDecimal getMezzanineBuiltUpArea() { return mezzanineBuiltUpArea; }
//	    public void setMezzanineBuiltUpArea(BigDecimal mezzanineBuiltUpArea) { this.mezzanineBuiltUpArea = mezzanineBuiltUpArea; }
//
//
////	    public BigDecimal getBathroomWCRequiredHeight() { return bathroomWCRequiredHeight; }
////	    public void setBathroomWCRequiredHeight(BigDecimal bathroomWCRequiredHeight) { this.bathroomWCRequiredHeight = bathroomWCRequiredHeight; }
//
//	    public BigDecimal getFromRoadWidth() { return fromRoadWidth; }
//	    public void setFromRoadWidth(BigDecimal fromRoadWidth) { this.fromRoadWidth = fromRoadWidth; }
//
//	    public BigDecimal getToRoadWidth() { return toRoadWidth; }
//	    public void setToRoadWidth(BigDecimal toRoadWidth) { this.toRoadWidth = toRoadWidth; }
	    
//	    public BigDecimal getBathroomWCRequiredWidth() { return bathroomWCRequiredWidth; }
//	    public void setBathroomWCRequiredWidth(BigDecimal bathroomWCRequiredWidth) { this.bathroomWCRequiredWidth = bathroomWCRequiredWidth; }

	    
	    @JsonProperty("featureName")
	    private String featureName;

	    public String getFeatureName() {
	        return featureName;
	    }

	    public void setFeatureName(String featureName) {
	        this.featureName = featureName;
	    }
	    
	    @JsonProperty("valuePermissible")
	    private List<String> valuePermissible;

	    public List<String> getValuePermissible() {
	        return valuePermissible;
	    }

	    public void setValuePermissible(List<String> valuePermissible) {
	        this.valuePermissible = valuePermissible;
	    }
	    
//	    @JsonProperty("plotArea")
//	    private BigDecimal plotArea;
//
//	    public BigDecimal getPlotArea() {
//	        return plotArea;
//	    }
//
//	    public void setPlotArea(BigDecimal plotArea) {
//	        this.plotArea = plotArea;
//	    }
	    
//	    @JsonProperty("rDminDistanceFromProtectionWall")
//	    private BigDecimal rDminDistanceFromProtectionWall;
//
//	    @JsonProperty("rDminDistanceFromEmbankment")
//	    private BigDecimal rDminDistanceFromEmbankment;
//
//	    @JsonProperty("rDminDistanceFromMainRiverEdge")
//	    private BigDecimal rDminDistanceFromMainRiverEdge;
//
//	    @JsonProperty("rDminDistanceFromSubRiver")
//	    private BigDecimal rDminDistanceFromSubRiver;
//
//	    // Getters and Setters
//
//	    public BigDecimal getrDminDistanceFromProtectionWall() {
//	        return rDminDistanceFromProtectionWall;
//	    }
//
//	    public void setrDminDistanceFromProtectionWall(BigDecimal rDminDistanceFromProtectionWall) {
//	        this.rDminDistanceFromProtectionWall = rDminDistanceFromProtectionWall;
//	    }
//
//	    public BigDecimal getrDminDistanceFromEmbankment() {
//	        return rDminDistanceFromEmbankment;
//	    }
//
//	    public void setrDminDistanceFromEmbankment(BigDecimal rDminDistanceFromEmbankment) {
//	        this.rDminDistanceFromEmbankment = rDminDistanceFromEmbankment;
//	    }
//
//	    public BigDecimal getrDminDistanceFromMainRiverEdge() {
//	        return rDminDistanceFromMainRiverEdge;
//	    }
//
//	    public void setrDminDistanceFromMainRiverEdge(BigDecimal rDminDistanceFromMainRiverEdge) {
//	        this.rDminDistanceFromMainRiverEdge = rDminDistanceFromMainRiverEdge;
//	    }
//
//	    public BigDecimal getrDminDistanceFromSubRiver() {
//	        return rDminDistanceFromSubRiver;
//	    }
//
//	    public void setrDminDistanceFromSubRiver(BigDecimal rDminDistanceFromSubRiver) {
//	        this.rDminDistanceFromSubRiver = rDminDistanceFromSubRiver;
//	    }
	    
//	    @JsonProperty("bathroomtotalArea")
//	    private BigDecimal bathroomtotalArea;
//	    @JsonProperty("bathroomMinWidth")
//	    private BigDecimal bathroomMinWidth;
//	    @JsonProperty("buildingHeightMaximumDistancetoRoad")
//	    private BigDecimal buildingHeightMaximumDistancetoRoad;
//	    @JsonProperty("buildingHeightMaxBuildingHeight")
//	    private BigDecimal buildingHeightMaxBuildingHeight;
//	    @JsonProperty("fireStairExpectedNoofRise")
//	    private BigDecimal fireStairExpectedNoofRise;
//	    @JsonProperty("fireStairMinimumWidth")
//	    private BigDecimal fireStairMinimumWidth;
//	    @JsonProperty("fireStairRequiredTread")
//	    private BigDecimal fireStairRequiredTread;
//	    @JsonProperty("fireTenderMovementValueOne")
//	    private BigDecimal fireTenderMovementValueOne;
//	    @JsonProperty("fireTenderMovementValueTwo")
//	    private BigDecimal fireTenderMovementValueTwo;
//	    @JsonProperty("GovtBuildingDistanceValue")
//	    private BigDecimal GovtBuildingDistanceValue;
//	    @JsonProperty("GovtBuildingDistanceMaxHeight")
//	    private BigDecimal GovtBuildingDistanceMaxHeight;
//	    @JsonProperty("guardRoomMinHeight")
//	    private BigDecimal guardRoomMinHeight;
//	    @JsonProperty("guardRoomMinWidth")
//	    private BigDecimal guardRoomMinWidth;
//	    @JsonProperty("guardRoomMinArea")
//	    private BigDecimal guardRoomMinArea;
//	    @JsonProperty("guardRoomMinCabinHeightOne")
//	    private BigDecimal guardRoomMinCabinHeightOne;
//	    @JsonProperty("guardRoomMinCabinHeightTwo")
//	    private BigDecimal guardRoomMinCabinHeightTwo;
//	    @JsonProperty("minInteriorAreaValueOne")
//	    private BigDecimal minInteriorAreaValueOne;
//	    @JsonProperty("minInteriorAreaValueTwo")
//	    private BigDecimal minInteriorAreaValueTwo;
//	    @JsonProperty("minInteriorWidthValueOne")
//	    private BigDecimal minInteriorWidthValueOne;
//	    @JsonProperty("minInteriorWidthValueTwo")
//	    private BigDecimal minInteriorWidthValueTwo;
//	    @JsonProperty("minVentilationAreaValueOne")
//	    private BigDecimal minVentilationAreaValueOne;
//	    @JsonProperty("minVentilationAreaValueTwo")
//	    private BigDecimal minVentilationAreaValueTwo;
//	    @JsonProperty("minVentilationWidthValueOne")
//	    private BigDecimal minVentilationWidthValueOne;
//	    @JsonProperty("minVentilationWidthValueTwo")
//	    private BigDecimal minVentilationWidthValueTwo;
//	    @JsonProperty("monumentDistance_distanceOne")
//	    private BigDecimal monumentDistance_distanceOne;
//	    @JsonProperty("monumentDistance_minDistanceTwo")
//	    private BigDecimal monumentDistance_minDistanceTwo;
//	    @JsonProperty("monumentDistance_maxHeightofbuilding")
//	    private BigDecimal monumentDistance_maxHeightofbuilding;
//	    @JsonProperty("monumentDistance_maxbuildingheightblock")
//	    private BigDecimal monumentDistance_maxbuildingheightblock;
//	    @JsonProperty("minDoorWidth")
//	    private BigDecimal minDoorWidth;
//	    @JsonProperty("minDoorHeight")
//	    private BigDecimal minDoorHeight;
//	    @JsonProperty("overheadVerticalDistance_11000")
//	    private BigDecimal overheadVerticalDistance_11000;
//	    @JsonProperty("overheadVerticalDistance_33000")
//	    private BigDecimal overheadVerticalDistance_33000;
//	    @JsonProperty("overheadHorizontalDistance_11000")
//	    private BigDecimal overheadHorizontalDistance_11000;
//	    @JsonProperty("overheadHorizontalDistance_33000")
//	    private BigDecimal overheadHorizontalDistance_33000;
//	    @JsonProperty("overheadVoltage_11000")
//	    private BigDecimal overheadVoltage_11000;
//	    @JsonProperty("overheadVoltage_33000")
//	    private BigDecimal overheadVoltage_33000;
//	    @JsonProperty("parapetValueOne")
//	    private BigDecimal parapetValueOne;
//	    @JsonProperty("parapetValueTwo")
//	    private BigDecimal parapetValueTwo;
//	    @JsonProperty("kitchenHeight")
//	    private BigDecimal kitchenHeight;
//	    @JsonProperty("kitchenArea")
//	    private BigDecimal kitchenArea;
//	    @JsonProperty("kitchenWidth")
//	    private BigDecimal kitchenWidth;
//	    @JsonProperty("kitchenStoreArea")
//	    private BigDecimal kitchenStoreArea;
//	    @JsonProperty("kitchenStoreWidth")
//	    private BigDecimal kitchenStoreWidth;
//	    @JsonProperty("kitchenDiningWidth")
//	    private BigDecimal kitchenDiningWidth;
//	    @JsonProperty("kitchenDiningArea")
//	    private BigDecimal kitchenDiningArea;
//	    @JsonProperty("plotAreaValueOne")
//	    private BigDecimal plotAreaValueOne;
//	    @JsonProperty("plotAreaValueTwo")
//	    private BigDecimal plotAreaValueTwo;
//	    @JsonProperty("noOfParking")
//	    private BigDecimal noOfParking;
//	    @JsonProperty("plantationGreenStripPlanValue")
//	    private BigDecimal plantationGreenStripPlanValue;
//	    @JsonProperty("plantationGreenStripMinWidth")
//	    private BigDecimal plantationGreenStripMinWidth;
//	    @JsonProperty("percent")
//	    private BigDecimal percent;
//	    @JsonProperty("passageServiceValueOne")
//	    private BigDecimal passageServiceValueOne;
//	    @JsonProperty("passageServiceValueTwo")
//	    private BigDecimal passageServiceValueTwo;
//	    @JsonProperty("exitWidthOccupancyTypeHandlerVal")
//	    private BigDecimal exitWidthOccupancyTypeHandlerVal;
//	    @JsonProperty("exitWidthNotOccupancyTypeHandlerVal")
//	    private BigDecimal exitWidthNotOccupancyTypeHandlerVal;
//	    @JsonProperty("exitWidth_A_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_A_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_A_noOfDoors")
//	    private BigDecimal exitWidth_A_noOfDoors;
//	    @JsonProperty("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_A_SR_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_A_SR_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_A_SR_noOfDoors")
//	    private BigDecimal exitWidth_A_SR_noOfDoors;
//	    @JsonProperty("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_B_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_B_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_B_noOfDoors")
//	    private BigDecimal exitWidth_B_noOfDoors;
//	    @JsonProperty("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_C_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_C_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_C_noOfDoors")
//	    private BigDecimal exitWidth_C_noOfDoors;
//	    @JsonProperty("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_D_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_D_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_D_noOfDoors")
//	    private BigDecimal exitWidth_D_noOfDoors;
//	    @JsonProperty("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_E_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_E_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_E_noOfDoors")
//	    private BigDecimal exitWidth_E_noOfDoors;
//	    @JsonProperty("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_F_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_F_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_F_noOfDoors")
//	    private BigDecimal exitWidth_F_noOfDoors;
//	    @JsonProperty("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_G_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_G_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_G_noOfDoors")
//	    private BigDecimal exitWidth_G_noOfDoors;
//	    @JsonProperty("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_H_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_H_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_H_noOfDoors")
//	    private BigDecimal exitWidth_H_noOfDoors;
//	    @JsonProperty("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("exitWidth_I_occupantLoadDivisonFactor")
//	    private BigDecimal exitWidth_I_occupantLoadDivisonFactor;
//	    @JsonProperty("exitWidth_I_noOfDoors")
//	    private BigDecimal exitWidth_I_noOfDoors;
//	    @JsonProperty("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay")
//	    private BigDecimal exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay;
//	    @JsonProperty("rampServiceValueOne")
//	    private BigDecimal rampServiceValueOne;
//	    @JsonProperty("rampServiceExpectedSlopeOne")
//	    private BigDecimal rampServiceExpectedSlopeOne;
//	    @JsonProperty("rampServiceDivideExpectedSlope")
//	    private BigDecimal rampServiceDivideExpectedSlope;
//	    @JsonProperty("rampServiceSlopValue")
//	    private BigDecimal rampServiceSlopValue;
//	    @JsonProperty("rampServiceBuildingHeight")
//	    private BigDecimal rampServiceBuildingHeight;
//	    @JsonProperty("rampServiceTotalLength")
//	    private BigDecimal rampServiceTotalLength;
//	    @JsonProperty("rampServiceExpectedSlopeTwo")
//	    private BigDecimal rampServiceExpectedSlopeTwo;
//	    @JsonProperty("rampServiceExpectedSlopeCompare")
//	    private BigDecimal rampServiceExpectedSlopeCompare;
//	    @JsonProperty("rampServiceExpectedSlopeCompareTrue")
//	    private BigDecimal rampServiceExpectedSlopeCompareTrue;
//	    @JsonProperty("rampServiceExpectedSlopeCompareFalse")
//	    private BigDecimal rampServiceExpectedSlopeCompareFalse;
//	    @JsonProperty("roomArea2")
//	    private BigDecimal roomArea2;
//	    @JsonProperty("roomArea1")
//	    private BigDecimal roomArea1;
//	    @JsonProperty("roomWidth2")
//	    private BigDecimal roomWidth2;
//	    @JsonProperty("roomWidth1")
//	    private BigDecimal roomWidth1;
//	    @JsonProperty("sanitationMinAreaofSPWC")
//	    private BigDecimal sanitationMinAreaofSPWC;
//	    @JsonProperty("sanitationMinDimensionofSPWC")
//	    private BigDecimal sanitationMinDimensionofSPWC;
//	    @JsonProperty("sanitationMinatGroundFloor")
//	    private BigDecimal sanitationMinatGroundFloor;
//	    @JsonProperty("sanitationFloorMultiplier")
//	    private BigDecimal sanitationFloorMultiplier;
//	    @JsonProperty("sTValueOne")
//	    private BigDecimal sTValueOne;
//	    @JsonProperty("sTValueTwo")
//	    private BigDecimal sTValueTwo;
//	    @JsonProperty("sTValueThree")
//	    private BigDecimal sTValueThree;
//	    @JsonProperty("sTValueFour")
//	    private BigDecimal sTValueFour;
//	    @JsonProperty("sTSegregatedToiletRequired")
//	    private BigDecimal sTSegregatedToiletRequired;
//	    @JsonProperty("sTSegregatedToiletProvided")
//	    private BigDecimal sTSegregatedToiletProvided;
//	    @JsonProperty("sTminDimensionRequired")
//	    private BigDecimal sTminDimensionRequired;
//	    @JsonProperty("septicTankMinDisWatersrc")
//	    private BigDecimal septicTankMinDisWatersrc;
//	    @JsonProperty("septicTankMinDisBuilding")
//	    private BigDecimal septicTankMinDisBuilding;
//	    @JsonProperty("sideYardValueOne")
//	    private BigDecimal sideYardValueOne;
//	    @JsonProperty("sideYardValueTwo")
//	    private BigDecimal sideYardValueTwo;
//	    @JsonProperty("sideYardValueThree")
//	    private BigDecimal sideYardValueThree;
//	    @JsonProperty("sideYardValueFour")
//	    private BigDecimal sideYardValueFour;
//	    @JsonProperty("sideYardValueFive")
//	    private BigDecimal sideYardValueFive;
//	    @JsonProperty("sideYardValueSix")
//	    private BigDecimal sideYardValueSix;
//	    @JsonProperty("sideYardValueSeven")
//	    private BigDecimal sideYardValueSeven;
//	    @JsonProperty("sideYardValueEight")
//	    private BigDecimal sideYardValueEight;
//	    @JsonProperty("sideYardValueNine")
//	    private BigDecimal sideYardValueNine;
//	    @JsonProperty("sideYardValueTen")
//	    private BigDecimal sideYardValueTen;
//	    @JsonProperty("sideYardValueEleven")
//	    private BigDecimal sideYardValueEleven;
//	    @JsonProperty("sideYardValueTwelve")
//	    private BigDecimal sideYardValueTwelve;
//	    @JsonProperty("sideYardValueThirteen")
//	    private BigDecimal sideYardValueThirteen;
//	    @JsonProperty("sideYardValueFourteen")
//	    private BigDecimal sideYardValueFourteen;
//	    @JsonProperty("sideYardValueFifteen")
//	    private BigDecimal sideYardValueFifteen;
//	    @JsonProperty("sideYardValueSixteen")
//	    private BigDecimal sideYardValueSixteen;
//	    @JsonProperty("sideYardValueSeventeen")
//	    private BigDecimal sideYardValueSeventeen;
//	    @JsonProperty("sideYardValueEighteen")
//	    private BigDecimal sideYardValueEighteen;
//	    @JsonProperty("sideYardValueNineteen")
//	    private BigDecimal sideYardValueNineteen;
//	    @JsonProperty("sideYardValueTwenty")
//	    private BigDecimal sideYardValueTwenty;
//	    @JsonProperty("sideYardValueTwentyOne")
//	    private BigDecimal sideYardValueTwentyOne;
//	    @JsonProperty("sideYardValueTwentyTwo")
//	    private BigDecimal sideYardValueTwentyTwo;
//	    @JsonProperty("sideYardValueTwentyThree")
//	    private BigDecimal sideYardValueTwentyThree;
//	    @JsonProperty("sideYardValueTwentyFour")
//	    private BigDecimal sideYardValueTwentyFour;
//	    @JsonProperty("sideYardValueTwentyFive")
//	    private BigDecimal sideYardValueTwentyFive;
//	    @JsonProperty("sideYardValueTwentySix")
//	    private BigDecimal sideYardValueTwentySix;
//	    @JsonProperty("sideYardValueTwentySeven")
//	    private BigDecimal sideYardValueTwentySeven;
//	    @JsonProperty("sideYardValueTwentyEight")
//	    private BigDecimal sideYardValueTwentyEight;
//	    @JsonProperty("solarValueOne")
//	    private BigDecimal solarValueOne;
//	    @JsonProperty("solarValueTwo")
//	    private BigDecimal solarValueTwo;
//	    @JsonProperty("spiralStairValue")
//	    private BigDecimal spiralStairValue;
//	    @JsonProperty("spiralStairExpectedDiameter")
//	    private BigDecimal spiralStairExpectedDiameter;
//	    @JsonProperty("spiralStairRadius")
//	    private BigDecimal spiralStairRadius;
//	    @JsonProperty("storeRoomValueOne")
//	    private BigDecimal storeRoomValueOne;
//	    @JsonProperty("storeRoomValueTwo")
//	    private BigDecimal storeRoomValueTwo;
//	    @JsonProperty("storeRoomValueThree")
//	    private BigDecimal storeRoomValueThree;
//	    @JsonProperty("minToiletArea")
//	    private BigDecimal minToiletArea;
//	    @JsonProperty("minToiletWidth")
//	    private BigDecimal minToiletWidth;
//	    @JsonProperty("minToiletVentilation")
//	    private BigDecimal minToiletVentilation;
//	    @JsonProperty("travelDistanceToExitValueOne")
//	    private BigDecimal travelDistanceToExitValueOne;
//	    @JsonProperty("travelDistanceToExitValueTwo")
//	    private BigDecimal travelDistanceToExitValueTwo;
//	    @JsonProperty("travelDistanceToExitValueThree")
//	    private BigDecimal travelDistanceToExitValueThree;
//	    @JsonProperty("vehicleRampValue")
//	    private BigDecimal vehicleRampValue;
//	    @JsonProperty("vehicleRampSlopeValueOne")
//	    private BigDecimal vehicleRampSlopeValueOne;
//	    @JsonProperty("vehicleRampSlopeValueTwo")
//	    private BigDecimal vehicleRampSlopeValueTwo;
//	    @JsonProperty("vehicleRampSlopeMinWidthValueOne")
//	    private BigDecimal vehicleRampSlopeMinWidthValueOne;
//	    @JsonProperty("vehicleRampSlopeMinWidthValueTwo")
//	    private BigDecimal vehicleRampSlopeMinWidthValueTwo;
//	    @JsonProperty("vehicleRampSlopeMinWidthValueThree")
//	    private BigDecimal vehicleRampSlopeMinWidthValueThree;
//	    @JsonProperty("ventilationValueOne")
//	    private BigDecimal ventilationValueOne;
//	    @JsonProperty("ventilationValueTwo")
//	    private BigDecimal ventilationValueTwo;
//	    @JsonProperty("verandahWidth")
//	    private BigDecimal verandahWidth;
//	    @JsonProperty("verandahDepth")
//	    private BigDecimal verandahDepth;
//	    @JsonProperty("waterClosetsVentilationArea")
//	    private BigDecimal waterClosetsVentilationArea;
//	    @JsonProperty("waterClosetsHeight")
//	    private BigDecimal waterClosetsHeight;
//	    @JsonProperty("waterClosetsArea")
//	    private BigDecimal waterClosetsArea;
//	    @JsonProperty("waterClosetsWidth")
//	    private BigDecimal waterClosetsWidth;
//	    @JsonProperty("waterTankCapacityArea")
//	    private BigDecimal waterTankCapacityArea;
//	    @JsonProperty("waterTankCapacityExpected")
//	    private BigDecimal waterTankCapacityExpected;
	    @JsonProperty("basementValue")
	    private BigDecimal basementValue;
	    @JsonProperty("basementValuetwo")
	    private BigDecimal basementValuetwo;
	    @JsonProperty("basementValuethree")
	    private BigDecimal basementValuethree;


//	    public BigDecimal getBathroomtotalArea() { return bathroomtotalArea; }
//	    public void setBathroomtotalArea(BigDecimal bathroomtotalArea) { this.bathroomtotalArea = bathroomtotalArea; }
//	    public BigDecimal getBathroomMinWidth() { return bathroomMinWidth; }
//	    public void setBathroomMinWidth(BigDecimal bathroomMinWidth) { this.bathroomMinWidth = bathroomMinWidth; }
//	    public BigDecimal getBuildingHeightMaximumDistancetoRoad() { return buildingHeightMaximumDistancetoRoad; }
//	    public void setBuildingHeightMaximumDistancetoRoad(BigDecimal buildingHeightMaximumDistancetoRoad) { this.buildingHeightMaximumDistancetoRoad = buildingHeightMaximumDistancetoRoad; }
//	    public BigDecimal getBuildingHeightMaxBuildingHeight() { return buildingHeightMaxBuildingHeight; }
//	    public void setBuildingHeightMaxBuildingHeight(BigDecimal buildingHeightMaxBuildingHeight) { this.buildingHeightMaxBuildingHeight = buildingHeightMaxBuildingHeight; }
//	    public BigDecimal getFireStairExpectedNoofRise() { return fireStairExpectedNoofRise; }
//	    public void setFireStairExpectedNoofRise(BigDecimal fireStairExpectedNoofRise) { this.fireStairExpectedNoofRise = fireStairExpectedNoofRise; }
//	    public BigDecimal getFireStairMinimumWidth() { return fireStairMinimumWidth; }
//	    public void setFireStairMinimumWidth(BigDecimal fireStairMinimumWidth) { this.fireStairMinimumWidth = fireStairMinimumWidth; }
//	    public BigDecimal getFireStairRequiredTread() { return fireStairRequiredTread; }
//	    public void setFireStairRequiredTread(BigDecimal fireStairRequiredTread) { this.fireStairRequiredTread = fireStairRequiredTread; }
//	    public BigDecimal getFireTenderMovementValueOne() { return fireTenderMovementValueOne; }
//	    public void setFireTenderMovementValueOne(BigDecimal fireTenderMovementValueOne) { this.fireTenderMovementValueOne = fireTenderMovementValueOne; }
//	    public BigDecimal getFireTenderMovementValueTwo() { return fireTenderMovementValueTwo; }
//	    public void setFireTenderMovementValueTwo(BigDecimal fireTenderMovementValueTwo) { this.fireTenderMovementValueTwo = fireTenderMovementValueTwo; }
//	    public BigDecimal getGovtBuildingDistanceValue() { return GovtBuildingDistanceValue; }
//	    public void setGovtBuildingDistanceValue(BigDecimal GovtBuildingDistanceValue) { this.GovtBuildingDistanceValue = GovtBuildingDistanceValue; }
//	    public BigDecimal getGovtBuildingDistanceMaxHeight() { return GovtBuildingDistanceMaxHeight; }
//	    public void setGovtBuildingDistanceMaxHeight(BigDecimal GovtBuildingDistanceMaxHeight) { this.GovtBuildingDistanceMaxHeight = GovtBuildingDistanceMaxHeight; }
//	    public BigDecimal getGuardRoomMinHeight() { return guardRoomMinHeight; }
//	    public void setGuardRoomMinHeight(BigDecimal guardRoomMinHeight) { this.guardRoomMinHeight = guardRoomMinHeight; }
//	    public BigDecimal getGuardRoomMinWidth() { return guardRoomMinWidth; }
//	    public void setGuardRoomMinWidth(BigDecimal guardRoomMinWidth) { this.guardRoomMinWidth = guardRoomMinWidth; }
//	    public BigDecimal getGuardRoomMinArea() { return guardRoomMinArea; }
//	    public void setGuardRoomMinArea(BigDecimal guardRoomMinArea) { this.guardRoomMinArea = guardRoomMinArea; }
//	    public BigDecimal getGuardRoomMinCabinHeightOne() { return guardRoomMinCabinHeightOne; }
//	    public void setGuardRoomMinCabinHeightOne(BigDecimal guardRoomMinCabinHeightOne) { this.guardRoomMinCabinHeightOne = guardRoomMinCabinHeightOne; }
//	    public BigDecimal getGuardRoomMinCabinHeightTwo() { return guardRoomMinCabinHeightTwo; }
//	    public void setGuardRoomMinCabinHeightTwo(BigDecimal guardRoomMinCabinHeightTwo) { this.guardRoomMinCabinHeightTwo = guardRoomMinCabinHeightTwo; }
//	    public BigDecimal getMinInteriorAreaValueOne() { return minInteriorAreaValueOne; }
//	    public void setMinInteriorAreaValueOne(BigDecimal minInteriorAreaValueOne) { this.minInteriorAreaValueOne = minInteriorAreaValueOne; }
//	    public BigDecimal getMinInteriorAreaValueTwo() { return minInteriorAreaValueTwo; }
//	    public void setMinInteriorAreaValueTwo(BigDecimal minInteriorAreaValueTwo) { this.minInteriorAreaValueTwo = minInteriorAreaValueTwo; }
//	    public BigDecimal getMinInteriorWidthValueOne() { return minInteriorWidthValueOne; }
//	    public void setMinInteriorWidthValueOne(BigDecimal minInteriorWidthValueOne) { this.minInteriorWidthValueOne = minInteriorWidthValueOne; }
//	    public BigDecimal getMinInteriorWidthValueTwo() { return minInteriorWidthValueTwo; }
//	    public void setMinInteriorWidthValueTwo(BigDecimal minInteriorWidthValueTwo) { this.minInteriorWidthValueTwo = minInteriorWidthValueTwo; }
//	    public BigDecimal getMinVentilationAreaValueOne() { return minVentilationAreaValueOne; }
//	    public void setMinVentilationAreaValueOne(BigDecimal minVentilationAreaValueOne) { this.minVentilationAreaValueOne = minVentilationAreaValueOne; }
//	    public BigDecimal getMinVentilationAreaValueTwo() { return minVentilationAreaValueTwo; }
//	    public void setMinVentilationAreaValueTwo(BigDecimal minVentilationAreaValueTwo) { this.minVentilationAreaValueTwo = minVentilationAreaValueTwo; }
//	    public BigDecimal getMinVentilationWidthValueOne() { return minVentilationWidthValueOne; }
//	    public void setMinVentilationWidthValueOne(BigDecimal minVentilationWidthValueOne) { this.minVentilationWidthValueOne = minVentilationWidthValueOne; }
//	    public BigDecimal getMinVentilationWidthValueTwo() { return minVentilationWidthValueTwo; }
//	    public void setMinVentilationWidthValueTwo(BigDecimal minVentilationWidthValueTwo) { this.minVentilationWidthValueTwo = minVentilationWidthValueTwo; }
//	    public BigDecimal getMonumentDistance_distanceOne() { return monumentDistance_distanceOne; }
//	    public void setMonumentDistance_distanceOne(BigDecimal monumentDistance_distanceOne) { this.monumentDistance_distanceOne = monumentDistance_distanceOne; }
//	    public BigDecimal getMonumentDistance_minDistanceTwo() { return monumentDistance_minDistanceTwo; }
//	    public void setMonumentDistance_minDistanceTwo(BigDecimal monumentDistance_minDistanceTwo) { this.monumentDistance_minDistanceTwo = monumentDistance_minDistanceTwo; }
//	    public BigDecimal getMonumentDistance_maxHeightofbuilding() { return monumentDistance_maxHeightofbuilding; }
//	    public void setMonumentDistance_maxHeightofbuilding(BigDecimal monumentDistance_maxHeightofbuilding) { this.monumentDistance_maxHeightofbuilding = monumentDistance_maxHeightofbuilding; }
//	    public BigDecimal getMonumentDistance_maxbuildingheightblock() { return monumentDistance_maxbuildingheightblock; }
//	    public void setMonumentDistance_maxbuildingheightblock(BigDecimal monumentDistance_maxbuildingheightblock) { this.monumentDistance_maxbuildingheightblock = monumentDistance_maxbuildingheightblock; }
//	    public BigDecimal getMinDoorWidth() { return minDoorWidth; }
//	    public void setMinDoorWidth(BigDecimal minDoorWidth) { this.minDoorWidth = minDoorWidth; }
//	    public BigDecimal getMinDoorHeight() { return minDoorHeight; }
//	    public void setMinDoorHeight(BigDecimal minDoorHeight) { this.minDoorHeight = minDoorHeight; }
//	    public BigDecimal getOverheadVerticalDistance_11000() { return overheadVerticalDistance_11000; }
//	    public void setOverheadVerticalDistance_11000(BigDecimal overheadVerticalDistance_11000) { this.overheadVerticalDistance_11000 = overheadVerticalDistance_11000; }
//	    public BigDecimal getOverheadVerticalDistance_33000() { return overheadVerticalDistance_33000; }
//	    public void setOverheadVerticalDistance_33000(BigDecimal overheadVerticalDistance_33000) { this.overheadVerticalDistance_33000 = overheadVerticalDistance_33000; }
//	    public BigDecimal getOverheadHorizontalDistance_11000() { return overheadHorizontalDistance_11000; }
//	    public void setOverheadHorizontalDistance_11000(BigDecimal overheadHorizontalDistance_11000) { this.overheadHorizontalDistance_11000 = overheadHorizontalDistance_11000; }
//	    public BigDecimal getOverheadHorizontalDistance_33000() { return overheadHorizontalDistance_33000; }
//	    public void setOverheadHorizontalDistance_33000(BigDecimal overheadHorizontalDistance_33000) { this.overheadHorizontalDistance_33000 = overheadHorizontalDistance_33000; }
//	    public BigDecimal getOverheadVoltage_11000() { return overheadVoltage_11000; }
//	    public void setOverheadVoltage_11000(BigDecimal overheadVoltage_11000) { this.overheadVoltage_11000 = overheadVoltage_11000; }
//	    public BigDecimal getOverheadVoltage_33000() { return overheadVoltage_33000; }
//	    public void setOverheadVoltage_33000(BigDecimal overheadVoltage_33000) { this.overheadVoltage_33000 = overheadVoltage_33000; }
//	    public BigDecimal getParapetValueOne() { return parapetValueOne; }
//	    public void setParapetValueOne(BigDecimal parapetValueOne) { this.parapetValueOne = parapetValueOne; }
//	    public BigDecimal getParapetValueTwo() { return parapetValueTwo; }
//	    public void setParapetValueTwo(BigDecimal parapetValueTwo) { this.parapetValueTwo = parapetValueTwo; }
//	    public BigDecimal getKitchenHeight() { return kitchenHeight; }
//	    public void setKitchenHeight(BigDecimal kitchenHeight) { this.kitchenHeight = kitchenHeight; }
//	    public BigDecimal getKitchenArea() { return kitchenArea; }
//	    public void setKitchenArea(BigDecimal kitchenArea) { this.kitchenArea = kitchenArea; }
//	    public BigDecimal getKitchenWidth() { return kitchenWidth; }
//	    public void setKitchenWidth(BigDecimal kitchenWidth) { this.kitchenWidth = kitchenWidth; }
//	    public BigDecimal getKitchenStoreArea() { return kitchenStoreArea; }
//	    public void setKitchenStoreArea(BigDecimal kitchenStoreArea) { this.kitchenStoreArea = kitchenStoreArea; }
//	    public BigDecimal getKitchenStoreWidth() { return kitchenStoreWidth; }
//	    public void setKitchenStoreWidth(BigDecimal kitchenStoreWidth) { this.kitchenStoreWidth = kitchenStoreWidth; }
//	    public BigDecimal getKitchenDiningWidth() { return kitchenDiningWidth; }
//	    public void setKitchenDiningWidth(BigDecimal kitchenDiningWidth) { this.kitchenDiningWidth = kitchenDiningWidth; }
//	    public BigDecimal getKitchenDiningArea() { return kitchenDiningArea; }
//	    public void setKitchenDiningArea(BigDecimal kitchenDiningArea) { this.kitchenDiningArea = kitchenDiningArea; }
//	    public BigDecimal getPlotAreaValueOne() { return plotAreaValueOne; }
//	    public void setPlotAreaValueOne(BigDecimal plotAreaValueOne) { this.plotAreaValueOne = plotAreaValueOne; }
//	    public BigDecimal getPlotAreaValueTwo() { return plotAreaValueTwo; }
//	    public void setPlotAreaValueTwo(BigDecimal plotAreaValueTwo) { this.plotAreaValueTwo = plotAreaValueTwo; }
//	    public BigDecimal getNoOfParking() { return noOfParking; }
//	    public void setNoOfParking(BigDecimal noOfParking) { this.noOfParking = noOfParking; }
//	    public BigDecimal getPlantationGreenStripPlanValue() { return plantationGreenStripPlanValue; }
//	    public void setPlantationGreenStripPlanValue(BigDecimal plantationGreenStripPlanValue) { this.plantationGreenStripPlanValue = plantationGreenStripPlanValue; }
//	    public BigDecimal getPlantationGreenStripMinWidth() { return plantationGreenStripMinWidth; }
//	    public void setPlantationGreenStripMinWidth(BigDecimal plantationGreenStripMinWidth) { this.plantationGreenStripMinWidth = plantationGreenStripMinWidth; }
//	    public BigDecimal getPercent() { return percent; }
//	    public void setPercent(BigDecimal percent) { this.percent = percent; }
//	    public BigDecimal getPassageServiceValueOne() { return passageServiceValueOne; }
//	    public void setPassageServiceValueOne(BigDecimal passageServiceValueOne) { this.passageServiceValueOne = passageServiceValueOne; }
//	    public BigDecimal getPassageServiceValueTwo() { return passageServiceValueTwo; }
//	    public void setPassageServiceValueTwo(BigDecimal passageServiceValueTwo) { this.passageServiceValueTwo = passageServiceValueTwo; }
////	    public BigDecimal getExitWidthOccupancyTypeHandlerVal() { return exitWidthOccupancyTypeHandlerVal; }
//	    public void setExitWidthOccupancyTypeHandlerVal(BigDecimal exitWidthOccupancyTypeHandlerVal) { this.exitWidthOccupancyTypeHandlerVal = exitWidthOccupancyTypeHandlerVal; }
//	    public BigDecimal getExitWidthNotOccupancyTypeHandlerVal() { return exitWidthNotOccupancyTypeHandlerVal; }
//	    public void setExitWidthNotOccupancyTypeHandlerVal(BigDecimal exitWidthNotOccupancyTypeHandlerVal) { this.exitWidthNotOccupancyTypeHandlerVal = exitWidthNotOccupancyTypeHandlerVal; }
//	    public BigDecimal getExitWidth_A_occupantLoadDivisonFactor() { return exitWidth_A_occupantLoadDivisonFactor; }
//	    public void setExitWidth_A_occupantLoadDivisonFactor(BigDecimal exitWidth_A_occupantLoadDivisonFactor) { this.exitWidth_A_occupantLoadDivisonFactor = exitWidth_A_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_A_noOfDoors() { return exitWidth_A_noOfDoors; }
//	    public void setExitWidth_A_noOfDoors(BigDecimal exitWidth_A_noOfDoors) { this.exitWidth_A_noOfDoors = exitWidth_A_noOfDoors; }
//	    public BigDecimal getExitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_A_SR_occupantLoadDivisonFactor() { return exitWidth_A_SR_occupantLoadDivisonFactor; }
//	    public void setExitWidth_A_SR_occupantLoadDivisonFactor(BigDecimal exitWidth_A_SR_occupantLoadDivisonFactor) { this.exitWidth_A_SR_occupantLoadDivisonFactor = exitWidth_A_SR_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_A_SR_noOfDoors() { return exitWidth_A_SR_noOfDoors; }
//	    public void setExitWidth_A_SR_noOfDoors(BigDecimal exitWidth_A_SR_noOfDoors) { this.exitWidth_A_SR_noOfDoors = exitWidth_A_SR_noOfDoors; }
//	    public BigDecimal getExitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_B_occupantLoadDivisonFactor() { return exitWidth_B_occupantLoadDivisonFactor; }
//	    public void setExitWidth_B_occupantLoadDivisonFactor(BigDecimal exitWidth_B_occupantLoadDivisonFactor) { this.exitWidth_B_occupantLoadDivisonFactor = exitWidth_B_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_B_noOfDoors() { return exitWidth_B_noOfDoors; }
//	    public void setExitWidth_B_noOfDoors(BigDecimal exitWidth_B_noOfDoors) { this.exitWidth_B_noOfDoors = exitWidth_B_noOfDoors; }
//	    public BigDecimal getExitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_C_occupantLoadDivisonFactor() { return exitWidth_C_occupantLoadDivisonFactor; }
//	    public void setExitWidth_C_occupantLoadDivisonFactor(BigDecimal exitWidth_C_occupantLoadDivisonFactor) { this.exitWidth_C_occupantLoadDivisonFactor = exitWidth_C_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_C_noOfDoors() { return exitWidth_C_noOfDoors; }
//	    public void setExitWidth_C_noOfDoors(BigDecimal exitWidth_C_noOfDoors) { this.exitWidth_C_noOfDoors = exitWidth_C_noOfDoors; }
//	    public BigDecimal getExitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_D_occupantLoadDivisonFactor() { return exitWidth_D_occupantLoadDivisonFactor; }
//	    public void setExitWidth_D_occupantLoadDivisonFactor(BigDecimal exitWidth_D_occupantLoadDivisonFactor) { this.exitWidth_D_occupantLoadDivisonFactor = exitWidth_D_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_D_noOfDoors() { return exitWidth_D_noOfDoors; }
//	    public void setExitWidth_D_noOfDoors(BigDecimal exitWidth_D_noOfDoors) { this.exitWidth_D_noOfDoors = exitWidth_D_noOfDoors; }
//	    public BigDecimal getExitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_E_occupantLoadDivisonFactor() { return exitWidth_E_occupantLoadDivisonFactor; }
//	    public void setExitWidth_E_occupantLoadDivisonFactor(BigDecimal exitWidth_E_occupantLoadDivisonFactor) { this.exitWidth_E_occupantLoadDivisonFactor = exitWidth_E_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_E_noOfDoors() { return exitWidth_E_noOfDoors; }
//	    public void setExitWidth_E_noOfDoors(BigDecimal exitWidth_E_noOfDoors) { this.exitWidth_E_noOfDoors = exitWidth_E_noOfDoors; }
//	    public BigDecimal getExitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_F_occupantLoadDivisonFactor() { return exitWidth_F_occupantLoadDivisonFactor; }
//	    public void setExitWidth_F_occupantLoadDivisonFactor(BigDecimal exitWidth_F_occupantLoadDivisonFactor) { this.exitWidth_F_occupantLoadDivisonFactor = exitWidth_F_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_F_noOfDoors() { return exitWidth_F_noOfDoors; }
//	    public void setExitWidth_F_noOfDoors(BigDecimal exitWidth_F_noOfDoors) { this.exitWidth_F_noOfDoors = exitWidth_F_noOfDoors; }
//	    public BigDecimal getExitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_G_occupantLoadDivisonFactor() { return exitWidth_G_occupantLoadDivisonFactor; }
//	    public void setExitWidth_G_occupantLoadDivisonFactor(BigDecimal exitWidth_G_occupantLoadDivisonFactor) { this.exitWidth_G_occupantLoadDivisonFactor = exitWidth_G_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_G_noOfDoors() { return exitWidth_G_noOfDoors; }
//	    public void setExitWidth_G_noOfDoors(BigDecimal exitWidth_G_noOfDoors) { this.exitWidth_G_noOfDoors = exitWidth_G_noOfDoors; }
//	    public BigDecimal getExitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_H_occupantLoadDivisonFactor() { return exitWidth_H_occupantLoadDivisonFactor; }
//	    public void setExitWidth_H_occupantLoadDivisonFactor(BigDecimal exitWidth_H_occupantLoadDivisonFactor) { this.exitWidth_H_occupantLoadDivisonFactor = exitWidth_H_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_H_noOfDoors() { return exitWidth_H_noOfDoors; }
//	    public void setExitWidth_H_noOfDoors(BigDecimal exitWidth_H_noOfDoors) { this.exitWidth_H_noOfDoors = exitWidth_H_noOfDoors; }
//	    public BigDecimal getExitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getExitWidth_I_occupantLoadDivisonFactor() { return exitWidth_I_occupantLoadDivisonFactor; }
//	    public void setExitWidth_I_occupantLoadDivisonFactor(BigDecimal exitWidth_I_occupantLoadDivisonFactor) { this.exitWidth_I_occupantLoadDivisonFactor = exitWidth_I_occupantLoadDivisonFactor; }
//	    public BigDecimal getExitWidth_I_noOfDoors() { return exitWidth_I_noOfDoors; }
//	    public void setExitWidth_I_noOfDoors(BigDecimal exitWidth_I_noOfDoors) { this.exitWidth_I_noOfDoors = exitWidth_I_noOfDoors; }
//	    public BigDecimal getExitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay() { return exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public void setExitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay(BigDecimal exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay) { this.exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay = exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay; }
//	    public BigDecimal getRampServiceValueOne() { return rampServiceValueOne; }
//	    public void setRampServiceValueOne(BigDecimal rampServiceValueOne) { this.rampServiceValueOne = rampServiceValueOne; }
//	    public BigDecimal getRampServiceExpectedSlopeOne() { return rampServiceExpectedSlopeOne; }
//	    public void setRampServiceExpectedSlopeOne(BigDecimal rampServiceExpectedSlopeOne) { this.rampServiceExpectedSlopeOne = rampServiceExpectedSlopeOne; }
//	    public BigDecimal getRampServiceDivideExpectedSlope() { return rampServiceDivideExpectedSlope; }
//	    public void setRampServiceDivideExpectedSlope(BigDecimal rampServiceDivideExpectedSlope) { this.rampServiceDivideExpectedSlope = rampServiceDivideExpectedSlope; }
//	    public BigDecimal getRampServiceSlopValue() { return rampServiceSlopValue; }
//	    public void setRampServiceSlopValue(BigDecimal rampServiceSlopValue) { this.rampServiceSlopValue = rampServiceSlopValue; }
//	    public BigDecimal getRampServiceBuildingHeight() { return rampServiceBuildingHeight; }
//	    public void setRampServiceBuildingHeight(BigDecimal rampServiceBuildingHeight) { this.rampServiceBuildingHeight = rampServiceBuildingHeight; }
//	    public BigDecimal getRampServiceTotalLength() { return rampServiceTotalLength; }
//	    public void setRampServiceTotalLength(BigDecimal rampServiceTotalLength) { this.rampServiceTotalLength = rampServiceTotalLength; }
//	    public BigDecimal getRampServiceExpectedSlopeTwo() { return rampServiceExpectedSlopeTwo; }
//	    public void setRampServiceExpectedSlopeTwo(BigDecimal rampServiceExpectedSlopeTwo) { this.rampServiceExpectedSlopeTwo = rampServiceExpectedSlopeTwo; }
//	    public BigDecimal getRampServiceExpectedSlopeCompare() { return rampServiceExpectedSlopeCompare; }
//	    public void setRampServiceExpectedSlopeCompare(BigDecimal rampServiceExpectedSlopeCompare) { this.rampServiceExpectedSlopeCompare = rampServiceExpectedSlopeCompare; }
//	    public BigDecimal getRampServiceExpectedSlopeCompareTrue() { return rampServiceExpectedSlopeCompareTrue; }
//	    public void setRampServiceExpectedSlopeCompareTrue(BigDecimal rampServiceExpectedSlopeCompareTrue) { this.rampServiceExpectedSlopeCompareTrue = rampServiceExpectedSlopeCompareTrue; }
//	    public BigDecimal getRampServiceExpectedSlopeCompareFalse() { return rampServiceExpectedSlopeCompareFalse; }
//	    public void setRampServiceExpectedSlopeCompareFalse(BigDecimal rampServiceExpectedSlopeCompareFalse) { this.rampServiceExpectedSlopeCompareFalse = rampServiceExpectedSlopeCompareFalse; }
//	    public BigDecimal getRoomArea2() { return roomArea2; }
//	    public void setRoomArea2(BigDecimal roomArea2) { this.roomArea2 = roomArea2; }
//	    public BigDecimal getRoomArea1() { return roomArea1; }
//	    public void setRoomArea1(BigDecimal roomArea1) { this.roomArea1 = roomArea1; }
//	    public BigDecimal getRoomWidth2() { return roomWidth2; }
//	    public void setRoomWidth2(BigDecimal roomWidth2) { this.roomWidth2 = roomWidth2; }
//	    public BigDecimal getRoomWidth1() { return roomWidth1; }
//	    public void setRoomWidth1(BigDecimal roomWidth1) { this.roomWidth1 = roomWidth1; }
//	    public BigDecimal getSanitationMinAreaofSPWC() { return sanitationMinAreaofSPWC; }
//	    public void setSanitationMinAreaofSPWC(BigDecimal sanitationMinAreaofSPWC) { this.sanitationMinAreaofSPWC = sanitationMinAreaofSPWC; }
//	    public BigDecimal getSanitationMinDimensionofSPWC() { return sanitationMinDimensionofSPWC; }
//	    public void setSanitationMinDimensionofSPWC(BigDecimal sanitationMinDimensionofSPWC) { this.sanitationMinDimensionofSPWC = sanitationMinDimensionofSPWC; }
//	    public BigDecimal getSanitationMinatGroundFloor() { return sanitationMinatGroundFloor; }
//	    public void setSanitationMinatGroundFloor(BigDecimal sanitationMinatGroundFloor) { this.sanitationMinatGroundFloor = sanitationMinatGroundFloor; }
//	    public BigDecimal getSanitationFloorMultiplier() { return sanitationFloorMultiplier; }
//	    public void setSanitationFloorMultiplier(BigDecimal sanitationFloorMultiplier) { this.sanitationFloorMultiplier = sanitationFloorMultiplier; }
//	    public BigDecimal getSTValueOne() { return sTValueOne; }
//	    public void setSTValueOne(BigDecimal sTValueOne) { this.sTValueOne = sTValueOne; }
//	    public BigDecimal getSTValueTwo() { return sTValueTwo; }
//	    public void setSTValueTwo(BigDecimal sTValueTwo) { this.sTValueTwo = sTValueTwo; }
//	    public BigDecimal getSTValueThree() { return sTValueThree; }
//	    public void setSTValueThree(BigDecimal sTValueThree) { this.sTValueThree = sTValueThree; }
//	    public BigDecimal getSTValueFour() { return sTValueFour; }
//	    public void setSTValueFour(BigDecimal sTValueFour) { this.sTValueFour = sTValueFour; }
//	    public BigDecimal getSTSegregatedToiletRequired() { return sTSegregatedToiletRequired; }
//	    public void setSTSegregatedToiletRequired(BigDecimal sTSegregatedToiletRequired) { this.sTSegregatedToiletRequired = sTSegregatedToiletRequired; }
//	    public BigDecimal getSTSegregatedToiletProvided() { return sTSegregatedToiletProvided; }
//	    public void setSTSegregatedToiletProvided(BigDecimal sTSegregatedToiletProvided) { this.sTSegregatedToiletProvided = sTSegregatedToiletProvided; }
//	    public BigDecimal getSTminDimensionRequired() { return sTminDimensionRequired; }
//	    public void setSTminDimensionRequired(BigDecimal sTminDimensionRequired) { this.sTminDimensionRequired = sTminDimensionRequired; }
//	    public BigDecimal getSepticTankMinDisWatersrc() { return septicTankMinDisWatersrc; }
//	    public void setSepticTankMinDisWatersrc(BigDecimal septicTankMinDisWatersrc) { this.septicTankMinDisWatersrc = septicTankMinDisWatersrc; }
//	    public BigDecimal getSepticTankMinDisBuilding() { return septicTankMinDisBuilding; }
//	    public void setSepticTankMinDisBuilding(BigDecimal septicTankMinDisBuilding) { this.septicTankMinDisBuilding = septicTankMinDisBuilding; }
//	    public BigDecimal getSideYardValueOne() { return sideYardValueOne; }
//	    public void setSideYardValueOne(BigDecimal sideYardValueOne) { this.sideYardValueOne = sideYardValueOne; }
//	    public BigDecimal getSideYardValueTwo() { return sideYardValueTwo; }
//	    public void setSideYardValueTwo(BigDecimal sideYardValueTwo) { this.sideYardValueTwo = sideYardValueTwo; }
//	    public BigDecimal getSideYardValueThree() { return sideYardValueThree; }
//	    public void setSideYardValueThree(BigDecimal sideYardValueThree) { this.sideYardValueThree = sideYardValueThree; }
//	    public BigDecimal getSideYardValueFour() { return sideYardValueFour; }
//	    public void setSideYardValueFour(BigDecimal sideYardValueFour) { this.sideYardValueFour = sideYardValueFour; }
//	    public BigDecimal getSideYardValueFive() { return sideYardValueFive; }
//	    public void setSideYardValueFive(BigDecimal sideYardValueFive) { this.sideYardValueFive = sideYardValueFive; }
//	    public BigDecimal getSideYardValueSix() { return sideYardValueSix; }
//	    public void setSideYardValueSix(BigDecimal sideYardValueSix) { this.sideYardValueSix = sideYardValueSix; }
//	    public BigDecimal getSideYardValueSeven() { return sideYardValueSeven; }
//	    public void setSideYardValueSeven(BigDecimal sideYardValueSeven) { this.sideYardValueSeven = sideYardValueSeven; }
//	    public BigDecimal getSideYardValueEight() { return sideYardValueEight; }
//	    public void setSideYardValueEight(BigDecimal sideYardValueEight) { this.sideYardValueEight = sideYardValueEight; }
//	    public BigDecimal getSideYardValueNine() { return sideYardValueNine; }
//	    public void setSideYardValueNine(BigDecimal sideYardValueNine) { this.sideYardValueNine = sideYardValueNine; }
//	    public BigDecimal getSideYardValueTen() { return sideYardValueTen; }
//	    public void setSideYardValueTen(BigDecimal sideYardValueTen) { this.sideYardValueTen = sideYardValueTen; }
//	    public BigDecimal getSideYardValueEleven() { return sideYardValueEleven; }
//	    public void setSideYardValueEleven(BigDecimal sideYardValueEleven) { this.sideYardValueEleven = sideYardValueEleven; }
//	    public BigDecimal getSideYardValueTwelve() { return sideYardValueTwelve; }
//	    public void setSideYardValueTwelve(BigDecimal sideYardValueTwelve) { this.sideYardValueTwelve = sideYardValueTwelve; }
//	    public BigDecimal getSideYardValueThirteen() { return sideYardValueThirteen; }
//	    public void setSideYardValueThirteen(BigDecimal sideYardValueThirteen) { this.sideYardValueThirteen = sideYardValueThirteen; }
//	    public BigDecimal getSideYardValueFourteen() { return sideYardValueFourteen; }
//	    public void setSideYardValueFourteen(BigDecimal sideYardValueFourteen) { this.sideYardValueFourteen = sideYardValueFourteen; }
//	    public BigDecimal getSideYardValueFifteen() { return sideYardValueFifteen; }
//	    public void setSideYardValueFifteen(BigDecimal sideYardValueFifteen) { this.sideYardValueFifteen = sideYardValueFifteen; }
//	    public BigDecimal getSideYardValueSixteen() { return sideYardValueSixteen; }
//	    public void setSideYardValueSixteen(BigDecimal sideYardValueSixteen) { this.sideYardValueSixteen = sideYardValueSixteen; }
//	    public BigDecimal getSideYardValueSeventeen() { return sideYardValueSeventeen; }
//	    public void setSideYardValueSeventeen(BigDecimal sideYardValueSeventeen) { this.sideYardValueSeventeen = sideYardValueSeventeen; }
//	    public BigDecimal getSideYardValueEighteen() { return sideYardValueEighteen; }
//	    public void setSideYardValueEighteen(BigDecimal sideYardValueEighteen) { this.sideYardValueEighteen = sideYardValueEighteen; }
//	    public BigDecimal getSideYardValueNineteen() { return sideYardValueNineteen; }
//	    public void setSideYardValueNineteen(BigDecimal sideYardValueNineteen) { this.sideYardValueNineteen = sideYardValueNineteen; }
//	    public BigDecimal getSideYardValueTwenty() { return sideYardValueTwenty; }
//	    public void setSideYardValueTwenty(BigDecimal sideYardValueTwenty) { this.sideYardValueTwenty = sideYardValueTwenty; }
//	    public BigDecimal getSideYardValueTwentyOne() { return sideYardValueTwentyOne; }
//	    public void setSideYardValueTwentyOne(BigDecimal sideYardValueTwentyOne) { this.sideYardValueTwentyOne = sideYardValueTwentyOne; }
//	    public BigDecimal getSideYardValueTwentyTwo() { return sideYardValueTwentyTwo; }
//	    public void setSideYardValueTwentyTwo(BigDecimal sideYardValueTwentyTwo) { this.sideYardValueTwentyTwo = sideYardValueTwentyTwo; }
//	    public BigDecimal getSideYardValueTwentyThree() { return sideYardValueTwentyThree; }
//	    public void setSideYardValueTwentyThree(BigDecimal sideYardValueTwentyThree) { this.sideYardValueTwentyThree = sideYardValueTwentyThree; }
//	    public BigDecimal getSideYardValueTwentyFour() { return sideYardValueTwentyFour; }
//	    public void setSideYardValueTwentyFour(BigDecimal sideYardValueTwentyFour) { this.sideYardValueTwentyFour = sideYardValueTwentyFour; }
//	    public BigDecimal getSideYardValueTwentyFive() { return sideYardValueTwentyFive; }
//	    public void setSideYardValueTwentyFive(BigDecimal sideYardValueTwentyFive) { this.sideYardValueTwentyFive = sideYardValueTwentyFive; }
//	    public BigDecimal getSideYardValueTwentySix() { return sideYardValueTwentySix; }
//	    public void setSideYardValueTwentySix(BigDecimal sideYardValueTwentySix) { this.sideYardValueTwentySix = sideYardValueTwentySix; }
//	    public BigDecimal getSideYardValueTwentySeven() { return sideYardValueTwentySeven; }
//	    public void setSideYardValueTwentySeven(BigDecimal sideYardValueTwentySeven) { this.sideYardValueTwentySeven = sideYardValueTwentySeven; }
//	    public BigDecimal getSideYardValueTwentyEight() { return sideYardValueTwentyEight; }
//	    public void setSideYardValueTwentyEight(BigDecimal sideYardValueTwentyEight) { this.sideYardValueTwentyEight = sideYardValueTwentyEight; }
//	    public BigDecimal getSolarValueOne() { return solarValueOne; }
//	    public void setSolarValueOne(BigDecimal solarValueOne) { this.solarValueOne = solarValueOne; }
//	    public BigDecimal getSolarValueTwo() { return solarValueTwo; }
//	    public void setSolarValueTwo(BigDecimal solarValueTwo) { this.solarValueTwo = solarValueTwo; }
//	    public BigDecimal getSpiralStairValue() { return spiralStairValue; }
//	    public void setSpiralStairValue(BigDecimal spiralStairValue) { this.spiralStairValue = spiralStairValue; }
//	    public BigDecimal getSpiralStairExpectedDiameter() { return spiralStairExpectedDiameter; }
//	    public void setSpiralStairExpectedDiameter(BigDecimal spiralStairExpectedDiameter) { this.spiralStairExpectedDiameter = spiralStairExpectedDiameter; }
//	    public BigDecimal getSpiralStairRadius() { return spiralStairRadius; }
//	    public void setSpiralStairRadius(BigDecimal spiralStairRadius) { this.spiralStairRadius = spiralStairRadius; }
//	    public BigDecimal getStoreRoomValueOne() { return storeRoomValueOne; }
//	    public void setStoreRoomValueOne(BigDecimal storeRoomValueOne) { this.storeRoomValueOne = storeRoomValueOne; }
//	    public BigDecimal getStoreRoomValueTwo() { return storeRoomValueTwo; }
//	    public void setStoreRoomValueTwo(BigDecimal storeRoomValueTwo) { this.storeRoomValueTwo = storeRoomValueTwo; }
//	    public BigDecimal getStoreRoomValueThree() { return storeRoomValueThree; }
//	    public void setStoreRoomValueThree(BigDecimal storeRoomValueThree) { this.storeRoomValueThree = storeRoomValueThree; }
//	    public BigDecimal getMinToiletArea() { return minToiletArea; }
//	    public void setMinToiletArea(BigDecimal minToiletArea) { this.minToiletArea = minToiletArea; }
//	    public BigDecimal getMinToiletWidth() { return minToiletWidth; }
//	    public void setMinToiletWidth(BigDecimal minToiletWidth) { this.minToiletWidth = minToiletWidth; }
//	    public BigDecimal getMinToiletVentilation() { return minToiletVentilation; }
//	    public void setMinToiletVentilation(BigDecimal minToiletVentilation) { this.minToiletVentilation = minToiletVentilation; }
//	    public BigDecimal getTravelDistanceToExitValueOne() { return travelDistanceToExitValueOne; }
//	    public void setTravelDistanceToExitValueOne(BigDecimal travelDistanceToExitValueOne) { this.travelDistanceToExitValueOne = travelDistanceToExitValueOne; }
//	    public BigDecimal getTravelDistanceToExitValueTwo() { return travelDistanceToExitValueTwo; }
//	    public void setTravelDistanceToExitValueTwo(BigDecimal travelDistanceToExitValueTwo) { this.travelDistanceToExitValueTwo = travelDistanceToExitValueTwo; }
//	    public BigDecimal getTravelDistanceToExitValueThree() { return travelDistanceToExitValueThree; }
//	    public void setTravelDistanceToExitValueThree(BigDecimal travelDistanceToExitValueThree) { this.travelDistanceToExitValueThree = travelDistanceToExitValueThree; }
//	    public BigDecimal getVehicleRampValue() { return vehicleRampValue; }
//	    public void setVehicleRampValue(BigDecimal vehicleRampValue) { this.vehicleRampValue = vehicleRampValue; }
//	    public BigDecimal getVehicleRampSlopeValueOne() { return vehicleRampSlopeValueOne; }
//	    public void setVehicleRampSlopeValueOne(BigDecimal vehicleRampSlopeValueOne) { this.vehicleRampSlopeValueOne = vehicleRampSlopeValueOne; }
//	    public BigDecimal getVehicleRampSlopeValueTwo() { return vehicleRampSlopeValueTwo; }
//	    public void setVehicleRampSlopeValueTwo(BigDecimal vehicleRampSlopeValueTwo) { this.vehicleRampSlopeValueTwo = vehicleRampSlopeValueTwo; }
//	    public BigDecimal getVehicleRampSlopeMinWidthValueOne() { return vehicleRampSlopeMinWidthValueOne; }
//	    public void setVehicleRampSlopeMinWidthValueOne(BigDecimal vehicleRampSlopeMinWidthValueOne) { this.vehicleRampSlopeMinWidthValueOne = vehicleRampSlopeMinWidthValueOne; }
//	    public BigDecimal getVehicleRampSlopeMinWidthValueTwo() { return vehicleRampSlopeMinWidthValueTwo; }
//	    public void setVehicleRampSlopeMinWidthValueTwo(BigDecimal vehicleRampSlopeMinWidthValueTwo) { this.vehicleRampSlopeMinWidthValueTwo = vehicleRampSlopeMinWidthValueTwo; }
//	    public BigDecimal getVehicleRampSlopeMinWidthValueThree() { return vehicleRampSlopeMinWidthValueThree; }
//	    public void setVehicleRampSlopeMinWidthValueThree(BigDecimal vehicleRampSlopeMinWidthValueThree) { this.vehicleRampSlopeMinWidthValueThree = vehicleRampSlopeMinWidthValueThree; }
//	    public BigDecimal getVentilationValueOne() { return ventilationValueOne; }
//	    public void setVentilationValueOne(BigDecimal ventilationValueOne) { this.ventilationValueOne = ventilationValueOne; }
//	    public BigDecimal getVentilationValueTwo() { return ventilationValueTwo; }
//	    public void setVentilationValueTwo(BigDecimal ventilationValueTwo) { this.ventilationValueTwo = ventilationValueTwo; }
//	    public BigDecimal getVerandahWidth() { return verandahWidth; }
//	    public void setVerandahWidth(BigDecimal verandahWidth) { this.verandahWidth = verandahWidth; }
//	    public BigDecimal getVerandahDepth() { return verandahDepth; }
//	    public void setVerandahDepth(BigDecimal verandahDepth) { this.verandahDepth = verandahDepth; }
//	    public BigDecimal getWaterClosetsVentilationArea() { return waterClosetsVentilationArea; }
//	    public void setWaterClosetsVentilationArea(BigDecimal waterClosetsVentilationArea) { this.waterClosetsVentilationArea = waterClosetsVentilationArea; }
//	    public BigDecimal getWaterClosetsHeight() { return waterClosetsHeight; }
//	    public void setWaterClosetsHeight(BigDecimal waterClosetsHeight) { this.waterClosetsHeight = waterClosetsHeight; }
//	    public BigDecimal getWaterClosetsArea() { return waterClosetsArea; }
//	    public void setWaterClosetsArea(BigDecimal waterClosetsArea) { this.waterClosetsArea = waterClosetsArea; }
//	    public BigDecimal getWaterClosetsWidth() { return waterClosetsWidth; }
//	    public void setWaterClosetsWidth(BigDecimal waterClosetsWidth) { this.waterClosetsWidth = waterClosetsWidth; }
//	    public BigDecimal getWaterTankCapacityArea() { return waterTankCapacityArea; }
//	    public void setWaterTankCapacityArea(BigDecimal waterTankCapacityArea) { this.waterTankCapacityArea = waterTankCapacityArea; }
//	    public BigDecimal getWaterTankCapacityExpected() { return waterTankCapacityExpected; }
//	    public void setWaterTankCapacityExpected(BigDecimal waterTankCapacityExpected) { this.waterTankCapacityExpected = waterTankCapacityExpected; }
	    public BigDecimal getBasementValue() {
	        return basementValue;
	    }

	    public void setBasementValue(BigDecimal basementValue) {
	        this.basementValue = basementValue;
	    }

	    public BigDecimal getBasementValuetwo() {
	        return basementValuetwo;
	    }

	    public void setBasementValuetwo(BigDecimal basementValuetwo) {
	        this.basementValuetwo = basementValuetwo;
	    }

	    public BigDecimal getBasementValuethree() {
	        return basementValuethree;
	    }

	    public void setBasementValuethree(BigDecimal basementValuethree) {
	        this.basementValuethree = basementValuethree;
	    }


	    public Boolean getActive() { return active; }
	    public void setActive(Boolean active) { this.active = active; }
   
	    
	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder("{");

	        if (id != null) sb.append("id=").append(id).append(", ");
	        if (fromPlotArea != null) sb.append("fromPlotArea=").append(fromPlotArea).append(", ");
	        if (toPlotArea != null) sb.append("toPlotArea=").append(toPlotArea).append(", ");
	        if (fromRoadWidth != null) sb.append("fromRoadWidth=").append(fromRoadWidth).append(", ");
	        if (toRoadWidth != null) sb.append("toRoadWidth=").append(toRoadWidth).append(", ");
	        if (state != null) sb.append("state=").append(state).append(", ");
	        if (city != null) sb.append("city=").append(city).append(", ");
	        if (zone != null) sb.append("zone=").append(zone).append(", ");
	        if (subZone != null) sb.append("subZone=").append(subZone).append(", ");
	        if (occupancy != null) sb.append("occupancy=").append(occupancy).append(", ");
	        if (riskType != null) sb.append("riskType=").append(riskType).append(", ");
	        if (permissible != null) sb.append("permissible=").append(permissible).append(", ");
//	        if (permissibleOne != null) sb.append("permissibleOne=").append(permissibleOne).append(", ");
//	        if (permissibleTwo != null) sb.append("permissibleTwo=").append(permissibleTwo).append(", ");
//	        if (permissibleThree != null) sb.append("permissibleThree=").append(permissibleThree).append(", ");
//	        if (bathroomWCRequiredArea != null) sb.append("bathroomWCRequiredArea=").append(bathroomWCRequiredArea).append(", ");
//	        if (bathroomWCRequiredWidth != null) sb.append("bathroomWCRequiredWidth=").append(bathroomWCRequiredWidth).append(", ");
//	        if (bathroomWCRequiredHeight != null) sb.append("bathroomWCRequiredHeight=").append(bathroomWCRequiredHeight).append(", ");
//	        if (mezzanineArea != null) sb.append("mezzanineArea=").append(mezzanineArea).append(", ");
//	        if (mezzanineHeight != null) sb.append("mezzanineHeight=").append(mezzanineHeight).append(", ");
//	        if (mezzanineBuiltUpArea != null) sb.append("mezzanineBuiltUpArea=").append(mezzanineBuiltUpArea).append(", ");
//	        if (fromRoadWidth != null) sb.append("fromRoadWidth=").append(fromRoadWidth).append(", ");
//	        if (toRoadWidth != null) sb.append("toRoadWidth=").append(toRoadWidth).append(", ");
	        if (active != null) sb.append("active=").append(active).append(", ");
	        if (featureName != null) sb.append("featureName=").append(featureName).append(", ");
	        if (valuePermissible != null) sb.append("valuePermissible=").append(valuePermissible).append(", ");
	     //   if (plotArea != null) sb.append("plotArea=").append(plotArea).append(", ");
//	        if(rDminDistanceFromProtectionWall != null) sb.append("rDminDistanceFromProtectionWall=").append(rDminDistanceFromProtectionWall).append(", ");
//	        if(rDminDistanceFromEmbankment != null) sb.append("rDminDistanceFromEmbankment=").append(rDminDistanceFromEmbankment).append(", ");
//	        if(rDminDistanceFromMainRiverEdge != null) sb.append("rDminDistanceFromMainRiverEdge=").append(rDminDistanceFromMainRiverEdge).append(", ");
//	        if(rDminDistanceFromSubRiver != null) sb.append("rDminDistanceFromSubRiver=").append(rDminDistanceFromSubRiver).append(", ");
//	        if (bathroomtotalArea != null) sb.append("bathroomtotalArea=").append(bathroomtotalArea).append(", ");
//	        if (bathroomMinWidth != null) sb.append("bathroomMinWidth=").append(bathroomMinWidth).append(", ");
//	        if (buildingHeightMaximumDistancetoRoad != null) sb.append("buildingHeightMaximumDistancetoRoad=").append(buildingHeightMaximumDistancetoRoad).append(", ");
//	        if (buildingHeightMaxBuildingHeight != null) sb.append("buildingHeightMaxBuildingHeight=").append(buildingHeightMaxBuildingHeight).append(", ");
//	        if (fireStairExpectedNoofRise != null) sb.append("fireStairExpectedNoofRise=").append(fireStairExpectedNoofRise).append(", ");
//	        if (fireStairMinimumWidth != null) sb.append("fireStairMinimumWidth=").append(fireStairMinimumWidth).append(", ");
//	        if (fireStairRequiredTread != null) sb.append("fireStairRequiredTread=").append(fireStairRequiredTread).append(", ");
//	        if (fireTenderMovementValueOne != null) sb.append("fireTenderMovementValueOne=").append(fireTenderMovementValueOne).append(", ");
//	        if (fireTenderMovementValueTwo != null) sb.append("fireTenderMovementValueTwo=").append(fireTenderMovementValueTwo).append(", ");
//	        if (GovtBuildingDistanceValue != null) sb.append("GovtBuildingDistanceValue=").append(GovtBuildingDistanceValue).append(", ");
//	        if (GovtBuildingDistanceMaxHeight != null) sb.append("GovtBuildingDistanceMaxHeight=").append(GovtBuildingDistanceMaxHeight).append(", ");
//	        if (guardRoomMinHeight != null) sb.append("guardRoomMinHeight=").append(guardRoomMinHeight).append(", ");
//	        if (guardRoomMinWidth != null) sb.append("guardRoomMinWidth=").append(guardRoomMinWidth).append(", ");
//	        if (guardRoomMinArea != null) sb.append("guardRoomMinArea=").append(guardRoomMinArea).append(", ");
//	        if (guardRoomMinCabinHeightOne != null) sb.append("guardRoomMinCabinHeightOne=").append(guardRoomMinCabinHeightOne).append(", ");
//	        if (guardRoomMinCabinHeightTwo != null) sb.append("guardRoomMinCabinHeightTwo=").append(guardRoomMinCabinHeightTwo).append(", ");
//	        if (minInteriorAreaValueOne != null) sb.append("minInteriorAreaValueOne=").append(minInteriorAreaValueOne).append(", ");
//	        if (minInteriorAreaValueTwo != null) sb.append("minInteriorAreaValueTwo=").append(minInteriorAreaValueTwo).append(", ");
//	        if (minInteriorWidthValueOne != null) sb.append("minInteriorWidthValueOne=").append(minInteriorWidthValueOne).append(", ");
//	        if (minInteriorWidthValueTwo != null) sb.append("minInteriorWidthValueTwo=").append(minInteriorWidthValueTwo).append(", ");
//	        if (minVentilationAreaValueOne != null) sb.append("minVentilationAreaValueOne=").append(minVentilationAreaValueOne).append(", ");
//	        if (minVentilationAreaValueTwo != null) sb.append("minVentilationAreaValueTwo=").append(minVentilationAreaValueTwo).append(", ");
//	        if (minVentilationWidthValueOne != null) sb.append("minVentilationWidthValueOne=").append(minVentilationWidthValueOne).append(", ");
//	        if (minVentilationWidthValueTwo != null) sb.append("minVentilationWidthValueTwo=").append(minVentilationWidthValueTwo).append(", ");
//	        if (monumentDistance_distanceOne != null) sb.append("monumentDistance_distanceOne=").append(monumentDistance_distanceOne).append(", ");
//	        if (monumentDistance_minDistanceTwo != null) sb.append("monumentDistance_minDistanceTwo=").append(monumentDistance_minDistanceTwo).append(", ");
//	        if (monumentDistance_maxHeightofbuilding != null) sb.append("monumentDistance_maxHeightofbuilding=").append(monumentDistance_maxHeightofbuilding).append(", ");
//	        if (monumentDistance_maxbuildingheightblock != null) sb.append("monumentDistance_maxbuildingheightblock=").append(monumentDistance_maxbuildingheightblock).append(", ");
//	        if (minDoorWidth != null) sb.append("minDoorWidth=").append(minDoorWidth).append(", ");
//	        if (minDoorHeight != null) sb.append("minDoorHeight=").append(minDoorHeight).append(", ");
//	        if (overheadVerticalDistance_11000 != null) sb.append("overheadVerticalDistance_11000=").append(overheadVerticalDistance_11000).append(", ");
//	        if (overheadVerticalDistance_33000 != null) sb.append("overheadVerticalDistance_33000=").append(overheadVerticalDistance_33000).append(", ");
//	        if (overheadHorizontalDistance_11000 != null) sb.append("overheadHorizontalDistance_11000=").append(overheadHorizontalDistance_11000).append(", ");
//	        if (overheadHorizontalDistance_33000 != null) sb.append("overheadHorizontalDistance_33000=").append(overheadHorizontalDistance_33000).append(", ");
//	        if (overheadVoltage_11000 != null) sb.append("overheadVoltage_11000=").append(overheadVoltage_11000).append(", ");
//	        if (overheadVoltage_33000 != null) sb.append("overheadVoltage_33000=").append(overheadVoltage_33000).append(", ");
//	        if (parapetValueOne != null) sb.append("parapetValueOne=").append(parapetValueOne).append(", ");
//	        if (parapetValueTwo != null) sb.append("parapetValueTwo=").append(parapetValueTwo).append(", ");
//	        if (kitchenHeight != null) sb.append("kitchenHeight=").append(kitchenHeight).append(", ");
//	        if (kitchenArea != null) sb.append("kitchenArea=").append(kitchenArea).append(", ");
//	        if (kitchenWidth != null) sb.append("kitchenWidth=").append(kitchenWidth).append(", ");
//	        if (kitchenStoreArea != null) sb.append("kitchenStoreArea=").append(kitchenStoreArea).append(", ");
//	        if (kitchenStoreWidth != null) sb.append("kitchenStoreWidth=").append(kitchenStoreWidth).append(", ");
//	        if (kitchenDiningWidth != null) sb.append("kitchenDiningWidth=").append(kitchenDiningWidth).append(", ");
//	        if (kitchenDiningArea != null) sb.append("kitchenDiningArea=").append(kitchenDiningArea).append(", ");
//	        if (plotAreaValueOne != null) sb.append("plotAreaValueOne=").append(plotAreaValueOne).append(", ");
//	        if (plotAreaValueTwo != null) sb.append("plotAreaValueTwo=").append(plotAreaValueTwo).append(", ");
//	        if (noOfParking != null) sb.append("noOfParking=").append(noOfParking).append(", ");
//	        if (plantationGreenStripPlanValue != null) sb.append("plantationGreenStripPlanValue=").append(plantationGreenStripPlanValue).append(", ");
//	        if (plantationGreenStripMinWidth != null) sb.append("plantationGreenStripMinWidth=").append(plantationGreenStripMinWidth).append(", ");
//	        if (percent != null) sb.append("percent=").append(percent).append(", ");
//	        if (passageServiceValueOne != null) sb.append("passageServiceValueOne=").append(passageServiceValueOne).append(", ");
//	        if (passageServiceValueTwo != null) sb.append("passageServiceValueTwo=").append(passageServiceValueTwo).append(", ");
//	        if (exitWidthOccupancyTypeHandlerVal != null) sb.append("exitWidthOccupancyTypeHandlerVal=").append(exitWidthOccupancyTypeHandlerVal).append(", ");
//	        if (exitWidthNotOccupancyTypeHandlerVal != null) sb.append("exitWidthNotOccupancyTypeHandlerVal=").append(exitWidthNotOccupancyTypeHandlerVal).append(", ");
//	        if (exitWidth_A_occupantLoadDivisonFactor != null) sb.append("exitWidth_A_occupantLoadDivisonFactor=").append(exitWidth_A_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_A_noOfDoors != null) sb.append("exitWidth_A_noOfDoors=").append(exitWidth_A_noOfDoors).append(", ");
//	        if (exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_A_SR_occupantLoadDivisonFactor != null) sb.append("exitWidth_A_SR_occupantLoadDivisonFactor=").append(exitWidth_A_SR_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_A_SR_noOfDoors != null) sb.append("exitWidth_A_SR_noOfDoors=").append(exitWidth_A_SR_noOfDoors).append(", ");
//	        if (exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_B_occupantLoadDivisonFactor != null) sb.append("exitWidth_B_occupantLoadDivisonFactor=").append(exitWidth_B_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_B_noOfDoors != null) sb.append("exitWidth_B_noOfDoors=").append(exitWidth_B_noOfDoors).append(", ");
//	        if (exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_C_occupantLoadDivisonFactor != null) sb.append("exitWidth_C_occupantLoadDivisonFactor=").append(exitWidth_C_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_C_noOfDoors != null) sb.append("exitWidth_C_noOfDoors=").append(exitWidth_C_noOfDoors).append(", ");
//	        if (exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_D_occupantLoadDivisonFactor != null) sb.append("exitWidth_D_occupantLoadDivisonFactor=").append(exitWidth_D_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_D_noOfDoors != null) sb.append("exitWidth_D_noOfDoors=").append(exitWidth_D_noOfDoors).append(", ");
//	        if (exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_E_occupantLoadDivisonFactor != null) sb.append("exitWidth_E_occupantLoadDivisonFactor=").append(exitWidth_E_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_E_noOfDoors != null) sb.append("exitWidth_E_noOfDoors=").append(exitWidth_E_noOfDoors).append(", ");
//	        if (exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_F_occupantLoadDivisonFactor != null) sb.append("exitWidth_F_occupantLoadDivisonFactor=").append(exitWidth_F_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_F_noOfDoors != null) sb.append("exitWidth_F_noOfDoors=").append(exitWidth_F_noOfDoors).append(", ");
//	        if (exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_G_occupantLoadDivisonFactor != null) sb.append("exitWidth_G_occupantLoadDivisonFactor=").append(exitWidth_G_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_G_noOfDoors != null) sb.append("exitWidth_G_noOfDoors=").append(exitWidth_G_noOfDoors).append(", ");
//	        if (exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_H_occupantLoadDivisonFactor != null) sb.append("exitWidth_H_occupantLoadDivisonFactor=").append(exitWidth_H_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_H_noOfDoors != null) sb.append("exitWidth_H_noOfDoors=").append(exitWidth_H_noOfDoors).append(", ");
//	        if (exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (exitWidth_I_occupantLoadDivisonFactor != null) sb.append("exitWidth_I_occupantLoadDivisonFactor=").append(exitWidth_I_occupantLoadDivisonFactor).append(", ");
//	        if (exitWidth_I_noOfDoors != null) sb.append("exitWidth_I_noOfDoors=").append(exitWidth_I_noOfDoors).append(", ");
//	        if (exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay != null) sb.append("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay=").append(exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay).append(", ");
//	        if (rampServiceValueOne != null) sb.append("rampServiceValueOne=").append(rampServiceValueOne).append(", ");
//	        if (rampServiceExpectedSlopeOne != null) sb.append("rampServiceExpectedSlopeOne=").append(rampServiceExpectedSlopeOne).append(", ");
//	        if (rampServiceDivideExpectedSlope != null) sb.append("rampServiceDivideExpectedSlope=").append(rampServiceDivideExpectedSlope).append(", ");
//	        if (rampServiceSlopValue != null) sb.append("rampServiceSlopValue=").append(rampServiceSlopValue).append(", ");
//	        if (rampServiceBuildingHeight != null) sb.append("rampServiceBuildingHeight=").append(rampServiceBuildingHeight).append(", ");
//	        if (rampServiceTotalLength != null) sb.append("rampServiceTotalLength=").append(rampServiceTotalLength).append(", ");
//	        if (rampServiceExpectedSlopeTwo != null) sb.append("rampServiceExpectedSlopeTwo=").append(rampServiceExpectedSlopeTwo).append(", ");
//	        if (rampServiceExpectedSlopeCompare != null) sb.append("rampServiceExpectedSlopeCompare=").append(rampServiceExpectedSlopeCompare).append(", ");
//	        if (rampServiceExpectedSlopeCompareTrue != null) sb.append("rampServiceExpectedSlopeCompareTrue=").append(rampServiceExpectedSlopeCompareTrue).append(", ");
//	        if (rampServiceExpectedSlopeCompareFalse != null) sb.append("rampServiceExpectedSlopeCompareFalse=").append(rampServiceExpectedSlopeCompareFalse).append(", ");
//	        if (roomArea2 != null) sb.append("roomArea2=").append(roomArea2).append(", ");
//	        if (roomArea1 != null) sb.append("roomArea1=").append(roomArea1).append(", ");
//	        if (roomWidth2 != null) sb.append("roomWidth2=").append(roomWidth2).append(", ");
//	        if (roomWidth1 != null) sb.append("roomWidth1=").append(roomWidth1).append(", ");
//	        if (sanitationMinAreaofSPWC != null) sb.append("sanitationMinAreaofSPWC=").append(sanitationMinAreaofSPWC).append(", ");
//	        if (sanitationMinDimensionofSPWC != null) sb.append("sanitationMinDimensionofSPWC=").append(sanitationMinDimensionofSPWC).append(", ");
//	        if (sanitationMinatGroundFloor != null) sb.append("sanitationMinatGroundFloor=").append(sanitationMinatGroundFloor).append(", ");
//	        if (sanitationFloorMultiplier != null) sb.append("sanitationFloorMultiplier=").append(sanitationFloorMultiplier).append(", ");
//	        if (sTValueOne != null) sb.append("sTValueOne=").append(sTValueOne).append(", ");
//	        if (sTValueTwo != null) sb.append("sTValueTwo=").append(sTValueTwo).append(", ");
//	        if (sTValueThree != null) sb.append("sTValueThree=").append(sTValueThree).append(", ");
//	        if (sTValueFour != null) sb.append("sTValueFour=").append(sTValueFour).append(", ");
//	        if (sTSegregatedToiletRequired != null) sb.append("sTSegregatedToiletRequired=").append(sTSegregatedToiletRequired).append(", ");
//	        if (sTSegregatedToiletProvided != null) sb.append("sTSegregatedToiletProvided=").append(sTSegregatedToiletProvided).append(", ");
//	        if (sTminDimensionRequired != null) sb.append("sTminDimensionRequired=").append(sTminDimensionRequired).append(", ");
//	        if (septicTankMinDisWatersrc != null) sb.append("septicTankMinDisWatersrc=").append(septicTankMinDisWatersrc).append(", ");
//	        if (septicTankMinDisBuilding != null) sb.append("septicTankMinDisBuilding=").append(septicTankMinDisBuilding).append(", ");
//	        if (sideYardValueOne != null) sb.append("sideYardValueOne=").append(sideYardValueOne).append(", ");
//	        if (sideYardValueTwo != null) sb.append("sideYardValueTwo=").append(sideYardValueTwo).append(", ");
//	        if (sideYardValueThree != null) sb.append("sideYardValueThree=").append(sideYardValueThree).append(", ");
//	        if (sideYardValueFour != null) sb.append("sideYardValueFour=").append(sideYardValueFour).append(", ");
//	        if (sideYardValueFive != null) sb.append("sideYardValueFive=").append(sideYardValueFive).append(", ");
//	        if (sideYardValueSix != null) sb.append("sideYardValueSix=").append(sideYardValueSix).append(", ");
//	        if (sideYardValueSeven != null) sb.append("sideYardValueSeven=").append(sideYardValueSeven).append(", ");
//	        if (sideYardValueEight != null) sb.append("sideYardValueEight=").append(sideYardValueEight).append(", ");
//	        if (sideYardValueNine != null) sb.append("sideYardValueNine=").append(sideYardValueNine).append(", ");
//	        if (sideYardValueTen != null) sb.append("sideYardValueTen=").append(sideYardValueTen).append(", ");
//	        if (sideYardValueEleven != null) sb.append("sideYardValueEleven=").append(sideYardValueEleven).append(", ");
//	        if (sideYardValueTwelve != null) sb.append("sideYardValueTwelve=").append(sideYardValueTwelve).append(", ");
//	        if (sideYardValueThirteen != null) sb.append("sideYardValueThirteen=").append(sideYardValueThirteen).append(", ");
//	        if (sideYardValueFourteen != null) sb.append("sideYardValueFourteen=").append(sideYardValueFourteen).append(", ");
//	        if (sideYardValueFifteen != null) sb.append("sideYardValueFifteen=").append(sideYardValueFifteen).append(", ");
//	        if (sideYardValueSixteen != null) sb.append("sideYardValueSixteen=").append(sideYardValueSixteen).append(", ");
//	        if (sideYardValueSeventeen != null) sb.append("sideYardValueSeventeen=").append(sideYardValueSeventeen).append(", ");
//	        if (sideYardValueEighteen != null) sb.append("sideYardValueEighteen=").append(sideYardValueEighteen).append(", ");
//	        if (sideYardValueNineteen != null) sb.append("sideYardValueNineteen=").append(sideYardValueNineteen).append(", ");
//	        if (sideYardValueTwenty != null) sb.append("sideYardValueTwenty=").append(sideYardValueTwenty).append(", ");
//	        if (sideYardValueTwentyOne != null) sb.append("sideYardValueTwentyOne=").append(sideYardValueTwentyOne).append(", ");
//	        if (sideYardValueTwentyTwo != null) sb.append("sideYardValueTwentyTwo=").append(sideYardValueTwentyTwo).append(", ");
//	        if (sideYardValueTwentyThree != null) sb.append("sideYardValueTwentyThree=").append(sideYardValueTwentyThree).append(", ");
//	        if (sideYardValueTwentyFour != null) sb.append("sideYardValueTwentyFour=").append(sideYardValueTwentyFour).append(", ");
//	        if (sideYardValueTwentyFive != null) sb.append("sideYardValueTwentyFive=").append(sideYardValueTwentyFive).append(", ");
//	        if (sideYardValueTwentySix != null) sb.append("sideYardValueTwentySix=").append(sideYardValueTwentySix).append(", ");
//	        if (sideYardValueTwentySeven != null) sb.append("sideYardValueTwentySeven=").append(sideYardValueTwentySeven).append(", ");
//	        if (sideYardValueTwentyEight != null) sb.append("sideYardValueTwentyEight=").append(sideYardValueTwentyEight).append(", ");
//	        if (solarValueOne != null) sb.append("solarValueOne=").append(solarValueOne).append(", ");
//	        if (solarValueTwo != null) sb.append("solarValueTwo=").append(solarValueTwo).append(", ");
//	        if (spiralStairValue != null) sb.append("spiralStairValue=").append(spiralStairValue).append(", ");
//	        if (spiralStairExpectedDiameter != null) sb.append("spiralStairExpectedDiameter=").append(spiralStairExpectedDiameter).append(", ");
//	        if (spiralStairRadius != null) sb.append("spiralStairRadius=").append(spiralStairRadius).append(", ");
//	        if (storeRoomValueOne != null) sb.append("storeRoomValueOne=").append(storeRoomValueOne).append(", ");
//	        if (storeRoomValueTwo != null) sb.append("storeRoomValueTwo=").append(storeRoomValueTwo).append(", ");
//	        if (storeRoomValueThree != null) sb.append("storeRoomValueThree=").append(storeRoomValueThree).append(", ");
//	        if (minToiletArea != null) sb.append("minToiletArea=").append(minToiletArea).append(", ");
//	        if (minToiletWidth != null) sb.append("minToiletWidth=").append(minToiletWidth).append(", ");
//	        if (minToiletVentilation != null) sb.append("minToiletVentilation=").append(minToiletVentilation).append(", ");
//	        if (travelDistanceToExitValueOne != null) sb.append("travelDistanceToExitValueOne=").append(travelDistanceToExitValueOne).append(", ");
//	        if (travelDistanceToExitValueTwo != null) sb.append("travelDistanceToExitValueTwo=").append(travelDistanceToExitValueTwo).append(", ");
//	        if (travelDistanceToExitValueThree != null) sb.append("travelDistanceToExitValueThree=").append(travelDistanceToExitValueThree).append(", ");
//	        if (vehicleRampValue != null) sb.append("vehicleRampValue=").append(vehicleRampValue).append(", ");
//	        if (vehicleRampSlopeValueOne != null) sb.append("vehicleRampSlopeValueOne=").append(vehicleRampSlopeValueOne).append(", ");
//	        if (vehicleRampSlopeValueTwo != null) sb.append("vehicleRampSlopeValueTwo=").append(vehicleRampSlopeValueTwo).append(", ");
//	        if (vehicleRampSlopeMinWidthValueOne != null) sb.append("vehicleRampSlopeMinWidthValueOne=").append(vehicleRampSlopeMinWidthValueOne).append(", ");
//	        if (vehicleRampSlopeMinWidthValueTwo != null) sb.append("vehicleRampSlopeMinWidthValueTwo=").append(vehicleRampSlopeMinWidthValueTwo).append(", ");
//	        if (vehicleRampSlopeMinWidthValueThree != null) sb.append("vehicleRampSlopeMinWidthValueThree=").append(vehicleRampSlopeMinWidthValueThree).append(", ");
//	        if (ventilationValueOne != null) sb.append("ventilationValueOne=").append(ventilationValueOne).append(", ");
//	        if (ventilationValueTwo != null) sb.append("ventilationValueTwo=").append(ventilationValueTwo).append(", ");
//	        if (verandahWidth != null) sb.append("verandahWidth=").append(verandahWidth).append(", ");
//	        if (verandahDepth != null) sb.append("verandahDepth=").append(verandahDepth).append(", ");
//	        if (waterClosetsVentilationArea != null) sb.append("waterClosetsVentilationArea=").append(waterClosetsVentilationArea).append(", ");
//	        if (waterClosetsHeight != null) sb.append("waterClosetsHeight=").append(waterClosetsHeight).append(", ");
//	        if (waterClosetsArea != null) sb.append("waterClosetsArea=").append(waterClosetsArea).append(", ");
//	        if (waterClosetsWidth != null) sb.append("waterClosetsWidth=").append(waterClosetsWidth).append(", ");
//	        if (waterTankCapacityArea != null) sb.append("waterTankCapacityArea=").append(waterTankCapacityArea).append(", ");
//	        if (waterTankCapacityExpected != null) sb.append("waterTankCapacityExpected=").append(waterTankCapacityExpected).append(", ");
	        if (basementValue != null) sb.append("basementValue=").append(basementValue).append(", ");
	        if (basementValuetwo != null) sb.append("basementValuetwo=").append(basementValuetwo).append(", ");
	        if (basementValuethree != null) sb.append("basementValuethree=").append(basementValuethree).append(", ");

	        // Remove last comma and space if present
	        if (sb.lastIndexOf(", ") == sb.length() - 2) {
	            sb.delete(sb.length() - 2, sb.length());
	        }

	        sb.append("}");
	        return sb.toString();
	    }
	    
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        MdmsFeatureRule that = (MdmsFeatureRule) o;
	        return Objects.equals(id, that.id); // or use a combination of fields
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id); // or relevant fields
	    }


}
