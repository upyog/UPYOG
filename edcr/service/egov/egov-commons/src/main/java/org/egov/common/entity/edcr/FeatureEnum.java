package org.egov.common.entity.edcr;

public enum FeatureEnum {
	
	    FAR("Far"),
	    BALCONY("Balcony"),
	    BASEMENT("Basement"),
	    BATHROOM("Bathroom"),
	    BATHROOM_WATER_CLOSETS("BathroomWaterClosets"),
	    BLOCK_DISTANCES_SERVICE("BlockDistancesService"),
	    CHIMNEY("Chimney"),
	    EXIT_WIDTH("ExitWidth"),
	    FIRE_STAIR("FireStair"),
	    FIRE_TENDER_MOVEMENT("FireTenderMovement"),
	    GOVT_BUILDING_DISTANCE("GovtBuildingDistance"),
	    GUARD_ROOM("GuardRoom"),
	    HEAD_ROOM("HeadRoom"),
	    LAND_USE("LandUse"),
	    INTERIOR_OPEN_SPACE_SERVICE("InteriorOpenSpaceService"),
	    MEZZANINE_FLOOR_SERVICE("MezzanineFloorService"),
	    MONUMENT_DISTANCE("MonumentDistance"),
	    OVERHEAD_ELECTRICAL_LINE_SERVICE("OverheadElectricalLineService"),
	    OVERHANGS("OverHangs"),
	    PARAPET("Parapet"),
	    PASSAGE_SERVICE("PassageService"),
	    PLANTATION_GREEN_STRIP("PlantationGreenStrip"),
	    PLOT_AREA("PlotArea"),
	    PORTICO_SERVICE("PorticoService"),
	    RAIN_WATER_HARVESTING("RainWaterHarvesting"),
	    RAMP_SERVICE("RampService"),
	    RIVER_DISTANCE("RiverDistance"),
	    ROAD_WIDTH("RoadWidth"),
	    ROOF_TANK("RoofTank"),
	    SEGREGATED_TOILET("SegregatedToilet"),
	    SEPTIC_TANK("SepticTank"),
	    SOLAR("Solar"),
	    SPIRAL_STAIR("SpiralStair"),
	    STAIR_COVER("StairCover"),
	    STORE_ROOM("StoreRoom"),
	    TERRACE_UTILITY_SERVICE("TerraceUtilityService"),
	    TRAVEL_DISTANCE_TO_EXIT("TravelDistanceToExit"),
	    VEHICLE_RAMP("VehicleRamp"),
	    VENTILATION("Ventilation"),
	    VERANDAH("Verandah"),
	    WATER_CLOSETS("WaterClosets"),
	    WATER_TANK_CAPACITY("WaterTankCapacity"),
	    SANITATION("Sanitation"),
	    SIDE_YARD_SERVICE("SideYardService"),
	    EDCR_MASTER_CONFIG("EdcrMasterConfig"),
	    RISK_TYPE_COMPUTATION("RiskTypeComputation"),
	    RESIDENTIAL("residential"),
	    COMMERCIAL("commercial"),
	    APARTMENT_FLAT("apartment/flat"),
	    INDUSTRIAL("industrial"),
	    WINDOW("Window"),
	    ROOM_WISE_WINDOW_AREA("Room Wise Window Area"),
	    DOOR_VENTILATION("Door Ventilation"),
	    ROOM_AREA("RoomArea"),
	    PLANTATION("Plantation"), 
	    PLINTH_HEIGHT("PlinthHeight"), 
	    PARKING("Parking"), 
	    LIFT("Lift"), 
	    NON_HABITATIONAL_DOORS("NonHabitationalDoors"), 
	    NO_OF_RISER("NoOfRiser"), 
	    LANDING("Landing"), 
	    KITCHEN("Kitchen"), 
	    REAR_SET_BACK("RearSetBack"), 
	    REQUIRED_TREAD("RequiredTread"),
	    RISER_HEIGHT("RiserHeight"), 
	    ROOM_WISE_DOOR_AREA("RoomWiseDoorArea"), 
	    REQUIRED_WIDTH("RequiredWidth"), 
	    ROOM_WISE_VENTILATION("RoomWiseVentilation"), 
	    TOILET("Toilet"),   
	    COVERAGE("Coverage"), 
	    DOORS("Doors"), 
	    FRONT_SET_BACK("FrontSetBack"),
	    ADDITIONAL_FEATURE("AdditionalFeature");

	    private final String value;

	    FeatureEnum(String value) {
	        this.value = value;
	    }

	 
		public String getValue() {
	        return value;
	    }

	    @Override
	    public String toString() {
	        return value;
	    }

}
