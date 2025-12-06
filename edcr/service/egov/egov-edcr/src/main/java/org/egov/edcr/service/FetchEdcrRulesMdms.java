package org.egov.edcr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.*;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.config.EdcrConfigProperties;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
//import org.egov.infra.mdms.controller.MDMSController;
import org.egov.infra.microservice.models.RequestInfo;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


@Service
public class FetchEdcrRulesMdms {

	@Autowired
	private BpaMdmsUtil bpaMdmsUtil;

	@Autowired
	private EdcrConfigProperties edcrConfigProperties;

	private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
	private List<Map<String, Object>> riskTypeRules = new ArrayList<>();

	public String getOccupancyName(Plan pl) {
		if (pl.getPlanInformation() == null || pl.getPlanInformation().getOccupancy() == null) {
			return null;
		}

		String occupancyName = pl.getPlanInformation().getOccupancy();
		LOG.info("Occupancy Name : " + occupancyName);

		switch (occupancyName.toLowerCase()) {
	    case EdcrRulesMdmsConstants.RESIDENTIAL:
	        return EdcrRulesMdmsConstants.RESIDENTIAL;
	    case EdcrRulesMdmsConstants.INDUSTRIAL:
	        return EdcrRulesMdmsConstants.INDUSTRIAL;
	    case EdcrRulesMdmsConstants.COMMERCIAL:
	        return EdcrRulesMdmsConstants.COMMERCIAL;
	    case EdcrRulesMdmsConstants.STORAGE:
	        return EdcrRulesMdmsConstants.STORAGE;
	    case EdcrRulesMdmsConstants.ASSEMBLY:
	        return EdcrRulesMdmsConstants.ASSEMBLY;
	    case EdcrRulesMdmsConstants.BUSINESS:
	        return EdcrRulesMdmsConstants.BUSINESS;
	    case EdcrRulesMdmsConstants.HAZARDOUS:
	        return EdcrRulesMdmsConstants.HAZARDOUS;
	    case EdcrRulesMdmsConstants.EDUCATIONAL:
	        return EdcrRulesMdmsConstants.EDUCATIONAL;
	    case EdcrRulesMdmsConstants.MEDICAL:
	        return EdcrRulesMdmsConstants.MEDICAL;
	    default:
	        return occupancyName;
	}
	}

	public String getRiskType(Plan pl) {
	    if (riskTypeRules.isEmpty()) {
	        ObjectMapper mapper = new ObjectMapper();

	        Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), edcrConfigProperties.getDefaultState());
	        MdmsResponse mdmsResponse = mapper.convertValue(mdmsData, MdmsResponse.class);
	        JSONArray jsonArray = mdmsResponse.getMdmsRes().get(EdcrRulesMdmsConstants.BPA).get(EdcrRulesMdmsConstants.RISK_TYPE_COMPUTATION);

	        for (int i = 0; i < jsonArray.size(); i++) {
	            @SuppressWarnings("unchecked")
	            Map<String, Object> rule = (Map<String, Object>) jsonArray.get(i);
	            riskTypeRules.add(rule);
	        }
			LOG.info("RiskTypeRules: " + riskTypeRules);
	    }

	    BigDecimal plotArea = pl.getPlot().getArea();
		BigDecimal height = pl.getBlocks().get(0).getBuilding().getBuildingHeight();

	    for (Map<String, Object> rule : riskTypeRules) {
	        BigDecimal fromPlotArea = new BigDecimal(rule.get(EdcrRulesMdmsConstants.FROM_PLOT_AREA).toString());
	        BigDecimal toPlotArea = new BigDecimal(rule.get(EdcrRulesMdmsConstants.TO_PLOT_AREA).toString());
	        BigDecimal fromHeight = new BigDecimal(rule.get(EdcrRulesMdmsConstants.FROM_BUILDING_HEIGHT).toString());
	        BigDecimal toHeight = new BigDecimal(rule.get(EdcrRulesMdmsConstants.TO_BUILDING_HEIGHT).toString());

			LOG.info("RULES:: fromPlotArea: " + fromPlotArea + ", toPlotArea: " + toPlotArea + ", fromHeight: " + fromHeight + ", toHeight: " + toHeight);

	        boolean plotAreaInRange = plotArea.compareTo(fromPlotArea) >= 0 && plotArea.compareTo(toPlotArea) < 0;
	        boolean heightInRange = height.compareTo(fromHeight) >= 0 && height.compareTo(toHeight) < 0;

	        if (plotAreaInRange || heightInRange) { //add height if required
	            return rule.get(EdcrRulesMdmsConstants.RISK_TYPE).toString();
	        }
	    }

	    return null;
	}

	/**
	 * Returns the specific subclass of {@link MdmsFeatureRule} associated with the given {@link FeatureEnum}.
	 * This method is used to determine the appropriate rule class type for a feature, so that
	 * MDMS rule data can be deserialized into the correct Java object.
	 *
	 * @param featureName The feature enum for which the rule class is to be determined.
	 * @return The corresponding subclass of {@link MdmsFeatureRule} for the provided feature.
	 *         If the feature is null or not explicitly handled, {@link MdmsFeatureRule} is returned as a fallback.
	 */
	public Class<? extends MdmsFeatureRule> getRuleClassForFeature(FeatureEnum featureName) {
	    if (featureName == null) return MdmsFeatureRule.class;

	    switch (featureName) {
	        case BATHROOM_WATER_CLOSETS:
	            return BathroomWCRequirement.class;
	        case BALCONY:
	            return BalconyRequirement.class;
	        case BATHROOM:
	            return BathroomRequirement.class;
	        case BASEMENT:
	            return BasementRequirement.class;
	        case FAR:
	            return FarRequirement.class;
	        case SOLAR:
	            return SolarRequirement.class;
	        case STAIR_COVER:
	            return StairCoverRequirement.class;
	        case SEPTIC_TANK:
	            return SepticTankRequirement.class;
	        case VENTILATION:
	            return VentilationRequirement.class;
	        case FIRE_TENDER_MOVEMENT:
	            return FireTenderMovementRequirement.class;
	        case ROOM_AREA:
	            return RoomAreaRequirement.class;
	        case BLOCK_DISTANCES_SERVICE:
	            return BlockDistancesServiceRequirement.class;
	        case CHIMNEY:
	            return ChimneyRequirement.class;
	        case COVERAGE:
	            return CoverageRequirement.class;
	        case DOORS:
	            return DoorsRequirement.class;
	        case EXIT_WIDTH:
	            return ExitWidthRequirement.class;
	        case FIRE_STAIR:
	            return FireStairRequirement.class;
	        case FRONT_SET_BACK:
	            return FrontSetBackRequirement.class;
	        case GOVT_BUILDING_DISTANCE:
	            return GovtBuildingDistanceRequirement.class;
	        case GUARD_ROOM:
	            return GuardRoomRequirement.class;
	        case HEAD_ROOM:
	            return HeadRoomRequirement.class;
	        case INTERIOR_OPEN_SPACE_SERVICE:
	            return InteriorOpenSpaceServiceRequirement.class;
	        case KITCHEN:
	            return KitchenRequirement.class;
	        case LANDING:
	            return LandingRequirement.class;
	        case LAND_USE:
	            return LandUseRequirement.class;
	        case LIFT:
	            return LiftRequirement.class;
	        case MEZZANINE_FLOOR_SERVICE:
	            return MezzanineFloorServiceRequirement.class;
	        case MONUMENT_DISTANCE:
	            return MonumentDistanceRequirement.class;
	        case NON_HABITATIONAL_DOORS:
	            return NonHabitationalDoorsRequirement.class;
	        case NO_OF_RISER:
	            return NoOfRiserRequirement.class;
	        case OVERHANGS:
	            return OverHangsRequirement.class;
	        case OVERHEAD_ELECTRICAL_LINE_SERVICE:
	            return OverheadElectricalLineServiceRequirement.class;
	        case PARAPET:
	            return ParapetRequirement.class;
	        case PARKING:
	            return ParkingRequirement.class;
	        case PASSAGE_SERVICE:
	            return PassageRequirement.class;
	        case PLANTATION:
	            return PlantationRequirement.class;
	        case PLANTATION_GREEN_STRIP:
	            return PlantationGreenStripRequirement.class;
	        case PLINTH_HEIGHT:
	            return PlinthHeightRequirement.class;
	        case PLOT_AREA:
	            return PlotAreaRequirement.class;
	        case PORTICO_SERVICE:
	            return PorticoServiceRequirement.class;
	        case RAIN_WATER_HARVESTING:
	            return RainWaterHarvestingRequirement.class;
	        case RAMP_SERVICE:
	            return RampServiceRequirement.class;
	        case REAR_SET_BACK:
	            return RearSetBackRequirement.class;
	        case REQUIRED_TREAD:
	            return RequiredTreadRequirement.class;
	        case REQUIRED_WIDTH:
	            return RequiredWidthRequirement.class;
	        case RISER_HEIGHT:
	            return RiserHeightRequirement.class;
	        case RIVER_DISTANCE:
	            return RiverDistanceRequirement.class;
	        case ROAD_WIDTH:
	            return RoadWidthRequirement.class;
	        case ROOF_TANK:
	            return RoofTankRequirement.class;
	        case ROOM_WISE_DOOR_AREA:
	            return RoomWiseDoorAreaRequirement.class;
	        case ROOM_WISE_VENTILATION:
	            return RoomWiseVentilationRequirement.class;
	        case SANITATION:
	            return SanitationRequirement.class;
	        case SEGREGATED_TOILET:
	            return SegregatedToiletRequirement.class;
	        case SIDE_YARD_SERVICE:
	            return SideYardServiceRequirement.class;
	        case SPIRAL_STAIR:
	            return SpiralStairRequirement.class;
	        case TERRACE_UTILITY_SERVICE:
	            return TerraceUtilityServiceRequirement.class;
	        case TOILET:
	            return ToiletRequirement.class;
	        case TRAVEL_DISTANCE_TO_EXIT:
	            return TravelDistanceToExitRequirement.class;
	        case VEHICLE_RAMP:
	            return VehicleRampRequirement.class;
	        case VERANDAH:
	            return VerandahRequirement.class;
	        case WATER_CLOSETS:
	            return WaterClosetsRequirement.class;
	        case WATER_TANK_CAPACITY:
	            return WaterTankCapacityRequirement.class;
			case ADDITIONAL_FEATURE:
				return AdditionalFeatureRequirement.class;

	        default:
	            return MdmsFeatureRule.class; // Fallback
	    }
	}



}
