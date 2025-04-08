package org.egov.edcr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
//import org.egov.infra.mdms.controller.MDMSController;
import org.egov.infra.microservice.models.RequestInfo;
//import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FetchEdcrRulesMdms {

	  @Autowired
	  private BpaMdmsUtil bpaMdmsUtil;
	  
	  private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
	  
	  public Map<String, List<Map<String, Object>>> getEdcrRules() {
		    LOG.info("Entered getEdcrRules method");

		    // Make the MDMS call
		    Object mdmsData = bpaMdmsUtil.mDMSCallv2(new RequestInfo(), "pg");
//		    Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), "pg");
//		    log.info("----------------mdmsdata------------- :: " + mdmsData);
		    if (mdmsData == null) {
		        LOG.warn("MDMS data is null");
		        return createEmptyResult("MDMS data is null");
		    }

		    // Define required keys for BPA
		    Set<String> requiredKeys = new HashSet<>(Arrays.asList(
		        MdmsFeatureConstants.TOILET,
		        MdmsFeatureConstants.DOORS,
		        MdmsFeatureConstants.FRONT_SETBACK,
		        MdmsFeatureConstants.KITCHEN,
		        MdmsFeatureConstants.LANDING,
		        MdmsFeatureConstants.LIFT,
		        MdmsFeatureConstants.NON_HABITATIONAL_DOORS,
		        MdmsFeatureConstants.NO_OF_RISER,
		        MdmsFeatureConstants.PARKING,
		        MdmsFeatureConstants.PLANTATION,
		        MdmsFeatureConstants.PLINTH_HEIGHT,
		        MdmsFeatureConstants.REAR_SETBACK,
		        MdmsFeatureConstants.REQUIRED_TREAD,
		        MdmsFeatureConstants.REQUIRED_WIDTH,
		        MdmsFeatureConstants.RISER_HEIGHT,
		        MdmsFeatureConstants.ROOM_AREA,
		        MdmsFeatureConstants.ROOM_WISE_DOOR_AREA,
		        MdmsFeatureConstants.ROOM_WISE_VENTILATION,
		        MdmsFeatureConstants.COVERAGE,
		        MdmsFeatureConstants.USAGES,
		        MdmsFeatureConstants.APPLICATION_TYPE,
		        MdmsFeatureConstants.SERVICE_TYPE,
		        MdmsFeatureConstants.OCCUPANCY_TYPE,
		        MdmsFeatureConstants.SUB_OCCUPANCY_TYPE,
		        MdmsFeatureConstants.EDCR_RULES_FEATURES,
		        MdmsFeatureConstants.FAR,
		        MdmsFeatureConstants.BALCONY,
		        
		        MdmsFeatureConstants.BASEMENT,
		        MdmsFeatureConstants.BATHROOM,
		        MdmsFeatureConstants.BATHROOM_WATER_CLOSETS,
		        MdmsFeatureConstants.BLOCK_DISTANCES_SERVICE,
		        MdmsFeatureConstants.CHIMNEY,
		        MdmsFeatureConstants.EXIT_WIDTH,
		        MdmsFeatureConstants.FIRE_STAIR,
		        MdmsFeatureConstants.FIRE_TENDER_MOVEMENT,
		        MdmsFeatureConstants.GOVT_BUILDING_DISTANCE,
		        MdmsFeatureConstants.GUARD_ROOM,
		        MdmsFeatureConstants.HEAD_ROOM,
		        MdmsFeatureConstants.LAND_USE,
		        MdmsFeatureConstants.INTERIOR_OPEN_SPACE_SERVICE,
		        MdmsFeatureConstants.MEZZANINE_FLOOR_SERVICE,
		        MdmsFeatureConstants.MONUMENT_DISTANCE,
		        MdmsFeatureConstants.OVERHEAD_ELECTRICAL_LINE_SERVICE,
		        MdmsFeatureConstants.OVERHANGS,
		        MdmsFeatureConstants.PARAPET,
		        MdmsFeatureConstants.PASSAGE_SERVICE,
		        MdmsFeatureConstants.PLANTATION_GREEN_STRIP,
		        MdmsFeatureConstants.PLOT_AREA,
		        MdmsFeatureConstants.PORTICO_SERVICE,
		        MdmsFeatureConstants.RAIN_WATER_HARVESTING,
		        MdmsFeatureConstants.RAMP_SERVICE,
		        MdmsFeatureConstants.RIVER_DISTANCE,
		        MdmsFeatureConstants.ROAD_WIDTH,
		        MdmsFeatureConstants.ROOF_TANK,
		        MdmsFeatureConstants.SEGREGATED_TOILET,
		        MdmsFeatureConstants.SEPTIC_TANK,
		        MdmsFeatureConstants.SOLAR,
		        MdmsFeatureConstants.SPIRAL_STAIR,
		        MdmsFeatureConstants.STAIR_COVER,
		        MdmsFeatureConstants.STORE_ROOM,
		        MdmsFeatureConstants.TERRACE_UTILITY_SERVICE,
		        MdmsFeatureConstants.TRAVEL_DISTANCE_TO_EXIT,
		        MdmsFeatureConstants.VEHICLE_RAMP,
		        MdmsFeatureConstants.VENTILATION,
		        MdmsFeatureConstants.VERANDAH,
		        MdmsFeatureConstants.WATER_CLOSETS,
		        MdmsFeatureConstants.WATER_TANK_CAPACITY,
		        MdmsFeatureConstants.SANITATION,
		        MdmsFeatureConstants.SIDE_YARD_SERVICE
		    ));

		    Map<String, List<Map<String, Object>>> edcrRulesFeatures = new HashMap<>();
		    try {
		        // Extract MDMS response and BPA data
		        Map<String, Object> mdmsRes = extractMap(mdmsData, "MdmsRes");
		        Map<String, Object> bpaData = mdmsRes != null ? extractMap(mdmsRes, "BPA") : null;

		        if (bpaData == null) {
		            LOG.warn("No BPA data found in MDMS response.");
		            return createEmptyResult("No BPA data found");
		        }

		        // Process required keys using a traditional loop
		        for (String key : requiredKeys) {
		            if (bpaData.containsKey(key)) {
		                Object featureData = bpaData.get(key);

		                if (featureData instanceof List) {
		                    List<Map<String, Object>> featureList = (List<Map<String, Object>>) featureData;
		                    edcrRulesFeatures.put(key, featureList);
		                } else {
		                    LOG.warn("Unexpected data type for feature: {}", key);
		                }
		            }
		        }

		        if (edcrRulesFeatures.isEmpty()) {
		            LOG.info("EdcrRules is empty.");
		            return createEmptyResult("No EdcrRules data found");
		        }

		        LOG.info("Processed EdcrRulesMap: {}", edcrRulesFeatures);
		        return edcrRulesFeatures;
		    } catch (Exception e) {
		        LOG.error("Error processing MDMS data: ", e);
		        return createEmptyResult("Error processing MDMS data");
		    }
		}

		private Map<String, Object> extractMap(Object data, String key) {
		    if (data instanceof Map) {
		        return (Map<String, Object>) ((Map<?, ?>) data).get(key);
		    }
		    return null;
		}

		private Map<String, List<Map<String, Object>>> createEmptyResult(String message) {
		    return Collections.singletonMap("message",
		        Collections.singletonList(Collections.singletonMap("message", message))
		    );
		}



	    public List<Map<String, Object>> getPermissibleValue(
	            Map<String, List<Map<String, Object>>> edcrRuleList,
	            Map<String, Object> params,
	            ArrayList<String> valueFromColumn) {

	        System.out.println("inside getPermissibleValue method" + edcrRuleList);
	        
	       
	        String paramsFeature = params.get("feature").toString().toLowerCase(); 
	        String paramsOccupancy = params.get("occupancy").toString().toLowerCase(); 
	        BigDecimal paramsPlotArea = params.containsKey("plotArea") ? (BigDecimal) params.get("plotArea") : null;
	        String paramsFeatureName = params.containsKey("featureName") ? (String) params.get("featureName") : null;

	        List<Map<String, Object>> result = new ArrayList<>();
	        List<Map<String, Object>> filteredRules = new ArrayList<>();

	        // Step 1: Filter the rules based on feature and occupancy
	        for (Map.Entry<String, List<Map<String, Object>>> entry : edcrRuleList.entrySet()) {
	            String ruleType = entry.getKey().toLowerCase(); 
	            List<Map<String, Object>> ruleList = entry.getValue();

	            if (ruleType.equals(paramsFeature)) { // Filter by feature
	                for (Map<String, Object> ruleItem : ruleList) {
	                    String ruleOccupancy = ruleItem.get(EdcrRulesMdmsConstants.OCCUPANCY).toString().toLowerCase(); 
	                    if (ruleOccupancy.equals(paramsOccupancy)) { // Filter by occupancy
	                        filteredRules.add(ruleItem);
	                    }
	                }
	            }
	        }

	     // Step 2: Process the filtered rules
	        for (Map<String, Object> ruleItem : filteredRules) {
	            // Extract area and feature name from the rule
	            BigDecimal ruleFromArea = ruleItem.containsKey(EdcrRulesMdmsConstants.FROMPLOTAREA) ? getBigDecimal(ruleItem.get(EdcrRulesMdmsConstants.FROMPLOTAREA)) : null;
	            BigDecimal ruleToArea = ruleItem.containsKey(EdcrRulesMdmsConstants.TOPLOTAREA) ? getBigDecimal(ruleItem.get(EdcrRulesMdmsConstants.TOPLOTAREA)) : null;
	            String ruleFeatureName = ruleItem.containsKey("featureName") ? ruleItem.get("featureName").toString() : null;

	            // Initialize flags for whether plotArea and featureName match
	            boolean plotAreaMatches = false;
	            boolean featureNameMatches = false;

	            // Check if plotArea matches within the range (only if both paramsPlotArea and rule area exist)
	            if (paramsPlotArea != null && ruleFromArea != null && ruleToArea != null) {
	                plotAreaMatches = paramsPlotArea.compareTo(ruleFromArea) >= 0 &&
	                                  paramsPlotArea.compareTo(ruleToArea) <= 0;
	            }

	            // Check if featureName matches (only if both paramsFeatureName and ruleFeatureName exist)
	            if (paramsFeatureName != null && ruleFeatureName != null) {
	                featureNameMatches = paramsFeatureName.equalsIgnoreCase(ruleFeatureName);
	            }

	            
	            // Case 2: Only plotArea is required, and it matches
	            else if (paramsPlotArea != null && paramsFeatureName == null) {
	                if (plotAreaMatches) {
	                    Map<String, Object> value = new HashMap<>();
	                    if (valueFromColumn.size() == 1) {
	                        value.put("permissibleValue", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE));
	                    } else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MIN_VALUE)) {
	                        value.put("minValue", ruleItem.get(EdcrRulesMdmsConstants.MIN_VALUE));
	                        value.put("maxValue", ruleItem.get(EdcrRulesMdmsConstants.MAX_VALUE));
	                    }
	                    result.add(value);
	                    break; // Exit after finding the first matching rule
	                }
	            }
	            
	            // Case 4: Neither plotArea nor featureName is required, get permissible value
	            else {
	                Map<String, Object> value = new HashMap<>();
	                if (valueFromColumn.size() == 1) {
	                    value.put("permissibleValue", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE));
	                } else if (valueFromColumn.size() > 1  && ruleItem.containsKey("min_value")) {
	                    value.put("minValue", ruleItem.get(EdcrRulesMdmsConstants.MIN_VALUE));
	                    value.put("maxValue", ruleItem.get(EdcrRulesMdmsConstants.MAX_VALUE));
	                }else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MIN_DOOR_WIDTH)) {
	                    value.put("minDoorWidth", ruleItem.get(EdcrRulesMdmsConstants.MIN_DOOR_WIDTH));
	                    value.put("minDoorHeight", ruleItem.get(EdcrRulesMdmsConstants.MIN_DOOR_HEIGHT));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MIN_TOILET_AREA)) {
	                    value.put("minToiletArea", ruleItem.get(EdcrRulesMdmsConstants.MIN_TOILET_AREA));
	                    value.put("minToiletWidth", ruleItem.get(EdcrRulesMdmsConstants.MIN_TOILET_WIDTH));
	                    value.put("minToiletVentilation", ruleItem.get(EdcrRulesMdmsConstants.MIN_TOILET_VENTILATION));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PERCENT)) {
	                    value.put("percent", ruleItem.get(EdcrRulesMdmsConstants.PERCENT));
	                    value.put("permissibleValue", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.KITCHEN_HEIGHT)) {
	                    value.put("kitchenHeight", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_HEIGHT));
	                    value.put("kitchenArea", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_AREA));
	                    value.put("kitchenWidth", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_WIDTH));
	                    value.put("kitchenStoreArea", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_STORE_AREA));
	                    value.put("kitchenStoreWidth", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_STORE_WIDTH));
	                    value.put("kitchenDiningWidth", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_DINING_WIDTH));
	                    value.put("kitchenDiningArea", ruleItem.get(EdcrRulesMdmsConstants.KITCHEN_DINING_AREA));
	                           
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.ROOM_AREA_2)) {
	                    value.put("roomArea2", ruleItem.get(EdcrRulesMdmsConstants.ROOM_AREA_2));
	                    value.put("roomArea1", ruleItem.get(EdcrRulesMdmsConstants.ROOM_AREA_1));
	                    value.put("roomWidth2", ruleItem.get(EdcrRulesMdmsConstants.ROOM_WIDTH_2));
	                    value.put("roomWidth1", ruleItem.get(EdcrRulesMdmsConstants.ROOM_WIDTH_1));  
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_ONE)) {
	                    value.put("permissibleone", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE_ONE));
	                    value.put("permissibletwo", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE_TWO));
	                    value.put("permissiblethree", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE_THREE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.BATHROOM_TOTAL_AREA)) {
	                    value.put("bathroomtotalArea", ruleItem.get(EdcrRulesMdmsConstants.BATHROOM_TOTAL_AREA));
	                    value.put("bathroomMinWidth", ruleItem.get(EdcrRulesMdmsConstants.BATHROOM_MIN_WIDTH));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.BATHROOM_WC_REQUIRED_AREA)) {
	                    value.put("bathroomWCRequiredArea", ruleItem.get(EdcrRulesMdmsConstants.BATHROOM_WC_REQUIRED_AREA));
	                    value.put("bathroomWCRequiredWidth", ruleItem.get(EdcrRulesMdmsConstants.BATHROOM_WC_REQUIRED_WIDTH));
	                    value.put("bathroomWCRequiredHeight", ruleItem.get(EdcrRulesMdmsConstants.BATHROOM_WC_REQUIRED_HEIGHT));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.EXIT_WIDTH_OCCUPANCY_TYPE_HANDLER_VAL)) {
	                	value.put("exitWidthOccupancyTypeHandlerVal", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_OCCUPANCY_TYPE_HANDLER_VAL));
	                	value.put("exitWidthNotOccupancyTypeHandlerVal", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_NOT_OCCUPANCY_TYPE_HANDLER_VAL));
	                	value.put("exitWidth_A_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_A_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_NO_OF_DOORS));
	                	value.put("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_A_SR_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_SR_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_A_SR_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_SR_NO_OF_DOORS));
	                	value.put("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_A_SR_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_B_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_B_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_B_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_B_NO_OF_DOORS));
	                	value.put("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_B_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_C_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_C_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_C_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_C_NO_OF_DOORS));
	                	value.put("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_C_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_D_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_D_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_D_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_D_NO_OF_DOORS));
	                	value.put("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_D_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_E_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_E_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_E_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_E_NO_OF_DOORS));
	                	value.put("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_E_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_F_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_F_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_F_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_F_NO_OF_DOORS));
	                	value.put("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_F_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_G_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_G_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_G_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_G_NO_OF_DOORS));
	                	value.put("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_G_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_H_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_H_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_H_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_H_NO_OF_DOORS));
	                	value.put("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_H_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));
	                	value.put("exitWidth_I_occupantLoadDivisonFactor", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_I_OCCUPANT_LOAD_DIVISON_FACTOR));
	                	value.put("exitWidth_I_noOfDoors", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_I_NO_OF_DOORS));
	                	value.put("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get(EdcrRulesMdmsConstants.EXIT_WIDTH_I_NO_OF_OCCUPANTS_PER_UNIT_EXIT_WIDTH_OF_STAIRWAY));

	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.FIRE_STAIR_EXPECTED_NO_OF_RISE)) {
	                    value.put("fireStairExpectedNoofRise", ruleItem.get(EdcrRulesMdmsConstants.FIRE_STAIR_EXPECTED_NO_OF_RISE));
	                    value.put("fireStairMinimumWidth", ruleItem.get(EdcrRulesMdmsConstants.FIRE_STAIR_MIN_WIDTH));
	                    value.put("fireStairRequiredTread", ruleItem.get(EdcrRulesMdmsConstants.FIRE_STAIR_REQUIRED_TREAD));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.FIRE_TENDER_MOVEMENT_VALUE_ONE)) {
	                    value.put("fireTenderMovementValueOne", ruleItem.get(EdcrRulesMdmsConstants.FIRE_TENDER_MOVEMENT_VALUE_ONE));
	                    value.put("fireTenderMovementValueTwo", ruleItem.get(EdcrRulesMdmsConstants.FIRE_TENDER_MOVEMENT_VALUE_TWO));
	                } 
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.GOVT_BUILDING_DISTANCE_VALUE)) {
	                    value.put("govtBuildingDistanceValue", ruleItem.get(EdcrRulesMdmsConstants.GOVT_BUILDING_DISTANCE_VALUE));
	                    value.put("govtBuildingDistanceMin", ruleItem.get(EdcrRulesMdmsConstants.GOVT_BUILDING_DISTANCE_MIN));
	                    value.put("govtBuildingDistanceMaxHeight", ruleItem.get(EdcrRulesMdmsConstants.GOVT_BUILDING_DISTANCE_MAX_HEIGHT));
	                    value.put("govtBuildingDistancePermitted", ruleItem.get(EdcrRulesMdmsConstants.GOVT_BUILDING_DISTANCE_PERMITTED));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_HEIGHT)) {
	                    value.put("guardRoomMinHeight", ruleItem.get(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_HEIGHT));
	                    value.put("guardRoomMinWidth", ruleItem.get(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_WIDTH));
	                    value.put("guardRoomMinArea", ruleItem.get(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_AREA));
	                    value.put("guardRoomMinCabinHeightOne", ruleItem.get(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_CABIN_HEIGHT_ONE));
	                    value.put("guardRoomMinCabinHeightTwo", ruleItem.get(EdcrRulesMdmsConstants.GUARD_ROOM_MIN_CABIN_HEIGHT_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MIN_INTERIOR_AREA_VALUE_ONE)) {
	                    value.put("minInteriorAreaValueOne", ruleItem.get(EdcrRulesMdmsConstants.MIN_INTERIOR_AREA_VALUE_ONE));
	                    value.put("minInteriorAreaValueTwo", ruleItem.get(EdcrRulesMdmsConstants.MIN_INTERIOR_AREA_VALUE_TWO));
	                    value.put("minInteriorWidthValueOne", ruleItem.get(EdcrRulesMdmsConstants.MIN_INTERIOR_WIDTH_VALUE_ONE));
	                    value.put("minInteriorWidthValueTwo", ruleItem.get(EdcrRulesMdmsConstants.MIN_INTERIOR_WIDTH_VALUE_TWO));
	                    value.put("minVentilationAreaValueOne", ruleItem.get(EdcrRulesMdmsConstants.MIN_VENTILATION_AREA_VALUE_ONE));
	                    value.put("minVentilationAreaValueTwo", ruleItem.get(EdcrRulesMdmsConstants.MIN_VENTILATION_AREA_VALUE_TWO));
	                    value.put("minVentilationWidthValueOne", ruleItem.get(EdcrRulesMdmsConstants.MIN_VENTILATION_WIDTH_VALUE_ONE));
	                    value.put("minVentilationWidthValueTwo", ruleItem.get(EdcrRulesMdmsConstants.MIN_VENTILATION_WIDTH_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MEZZANINE_AREA)) {
	                    value.put("mezzanineArea", ruleItem.get(EdcrRulesMdmsConstants.MEZZANINE_AREA));
	                    value.put("mezzanineHeight", ruleItem.get(EdcrRulesMdmsConstants.MEZZANINE_HEIGHT));
	                    value.put("mezzanineBuiltUpArea", ruleItem.get(EdcrRulesMdmsConstants.MEZZANINE_BUILT_UP_AREA));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.MONUMENT_DISTANCE_ONE)) {
	                    value.put("monumentDistance_distanceOne", ruleItem.get(EdcrRulesMdmsConstants.MONUMENT_DISTANCE_ONE));
	                    value.put("monumentDistance_minDistanceTwo", ruleItem.get(EdcrRulesMdmsConstants.MONUMENT_DISTANCE_MIN_TWO));
	                    value.put("monumentDistance_maxHeightofbuilding", ruleItem.get(EdcrRulesMdmsConstants.MONUMENT_DISTANCE_MAX_BUILDING_HEIGHT));
	                    value.put("monumentDistance_maxbuildingheightblock", ruleItem.get(EdcrRulesMdmsConstants.MONUMENT_DISTANCE_MAX_BLOCK_HEIGHT));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_11000)) {
	                    value.put("overheadVerticalDistance_11000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_11000));
	                    value.put("overheadVerticalDistance_33000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_33000));
	                    value.put("overheadHorizontalDistance_11000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_11000));
	                    value.put("overheadHorizontalDistance_33000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_33000));
	                    value.put("overheadVoltage_11000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_11000));
	                    value.put("overheadVoltage_33000", ruleItem.get(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_33000));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PARAPET_VALUE_ONE)) {
	                    value.put("parapetValueOne", ruleItem.get(EdcrRulesMdmsConstants.PARAPET_VALUE_ONE));
	                    value.put("parapetValueTwo", ruleItem.get(EdcrRulesMdmsConstants.PARAPET_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PASSAGE_SERVICE_VALUE_ONE)) {
	                    value.put("passageServiceValueOne", ruleItem.get(EdcrRulesMdmsConstants.PASSAGE_SERVICE_VALUE_ONE));
	                    value.put("passageServiceValueTwo", ruleItem.get(EdcrRulesMdmsConstants.PASSAGE_SERVICE_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_PLAN_VALUE)) {
	                    value.put("plantationGreenStripPlanValue", ruleItem.get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_PLAN_VALUE));
	                    value.put("plantationGreenStripMinWidth", ruleItem.get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_MIN_WIDTH));
	                    value.put("plantationGreenStripbuildResult", ruleItem.get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_BUILD_RESULT));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_ONE)) {
	                    value.put("plotAreaValueOne", ruleItem.get(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_ONE));
	                    value.put("plotAreaValueTwo", ruleItem.get(EdcrRulesMdmsConstants.PLOT_AREA_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.RAMP_SERVICE_VALUE_ONE)) {
	                    value.put("rampServiceValueOne", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_VALUE_ONE));
	                    value.put("rampServiceExpectedSlopeOne", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_EXPECTED_SLOPE_ONE));
	                    value.put("rampServiceDivideExpectedSlope", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_DIVIDE_EXPECTED_SLOPE));
	                    value.put("rampServiceSlopValue", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_SLOPE_VALUE));
	                    value.put("rampServiceBuildingHeight", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_BUILDING_HEIGHT));
	                    value.put("rampServiceTotalLength", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_TOTAL_LENGTH));
	                    value.put("rampServiceExpectedSlopeTwo", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_EXPECTED_SLOPE_TWO));
	                    value.put("rampServiceExpectedSlopeCompare", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_EXPECTED_SLOPE_COMPARE));
	                    value.put("rampServiceExpectedSlopeCompareTrue", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_EXPECTED_SLOPE_COMPARE_TRUE));
	                    value.put("rampServiceExpectedSlopeCompareFalse", ruleItem.get(EdcrRulesMdmsConstants.RAMP_SERVICE_EXPECTED_SLOPE_COMPARE_FALSE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL)) {
	                    value.put("rDminDistanceFromProtectionWall", ruleItem.get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL));
	                    value.put("rDminDistanceFromEmbankment", ruleItem.get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_EMBANKMENT));
	                    value.put("rDminDistanceFromMainRiverEdge", ruleItem.get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_MAIN_RIVER_EDGE));
	                    value.put("rDminDistanceFromSubRiver", ruleItem.get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_SUB_RIVER));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.ST_VALUE_ONE)) {
	                    value.put("sTValueOne", ruleItem.get(EdcrRulesMdmsConstants.ST_VALUE_ONE));
	                    value.put("sTValueTwo", ruleItem.get(EdcrRulesMdmsConstants.ST_VALUE_TWO));
	                    value.put("sTValueThree", ruleItem.get(EdcrRulesMdmsConstants.ST_VALUE_THREE));
	                    value.put("sTValueFour", ruleItem.get(EdcrRulesMdmsConstants.ST_VALUE_FOUR));
	                    value.put("sTSegregatedToiletRequired", ruleItem.get(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_REQUIRED));
	                    value.put("sTSegregatedToiletProvided", ruleItem.get(EdcrRulesMdmsConstants.ST_SEGREGATED_TOILET_PROVIDED));
	                    value.put("sTminDimensionRequired", ruleItem.get(EdcrRulesMdmsConstants.ST_MIN_DIMENSION_REQUIRED));
	                    }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_WATERSRC)) {
	                    value.put("septicTankMinDisWatersrc", ruleItem.get(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_WATERSRC));
	                    value.put("septicTankMinDisBuilding", ruleItem.get(EdcrRulesMdmsConstants.SEPTIC_TANK_MIN_DISTANCE_BUILDING));
	                }	                
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.SOLAR_VALUE_ONE)) {
	                    value.put("solarValueOne", ruleItem.get(EdcrRulesMdmsConstants.SOLAR_VALUE_ONE));
	                    value.put("solarValueTwo", ruleItem.get(EdcrRulesMdmsConstants.SOLAR_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.SPIRAL_STAIR_EXPECTED_DIAMETER)) {
	                    value.put("spiralStairExpectedDiameter", ruleItem.get(EdcrRulesMdmsConstants.SPIRAL_STAIR_EXPECTED_DIAMETER));
	                    value.put("spiralStairRadius", ruleItem.get(EdcrRulesMdmsConstants.SPIRAL_STAIR_RADIUS));
	                    value.put("spiralStairValue", ruleItem.get(EdcrRulesMdmsConstants.SPIRAL_STAIR_VALUE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_ONE)) {
	                    value.put("storeRoomValueOne", ruleItem.get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_ONE));
	                    value.put("storeRoomValueTwo", ruleItem.get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_TWO));
	                    value.put("storeRoomValueThree", ruleItem.get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_THREE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_ONE)) {
	                    value.put("travelDistanceToExitValueOne", ruleItem.get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_ONE));
	                    value.put("travelDistanceToExitValueTwo", ruleItem.get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_TWO));
	                    value.put("travelDistanceToExitValueThree", ruleItem.get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_THREE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.VEHICLE_RAMP_VALUE)) {
	                    value.put("vehicleRampValue", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_VALUE));
	                    value.put("vehicleRampSlopeValueOne", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_SLOPE_VALUE_ONE));
	                    value.put("vehicleRampSlopeValueTwo", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_SLOPE_VALUE_TWO));
	                    value.put("vehicleRampSlopeMinWidthValueOne", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_SLOPE_MIN_WIDTH_VALUE_ONE));
	                    value.put("vehicleRampSlopeMinWidthValueTwo", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_SLOPE_MIN_WIDTH_VALUE_TWO));
	                    value.put("vehicleRampSlopeMinWidthValueThree", ruleItem.get(EdcrRulesMdmsConstants.VEHICLE_RAMP_SLOPE_MIN_WIDTH_VALUE_THREE));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.VENTILATION_VALUE_ONE)) {
	                    value.put("ventilationValueOne", ruleItem.get(EdcrRulesMdmsConstants.VENTILATION_VALUE_ONE));
	                    value.put("ventilationValueTwo", ruleItem.get(EdcrRulesMdmsConstants.VENTILATION_VALUE_TWO));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.VERANDAH_WIDTH)) {
	                    value.put("verandahWidth", ruleItem.get(EdcrRulesMdmsConstants.VERANDAH_WIDTH));
	                    value.put("verandahDepth", ruleItem.get(EdcrRulesMdmsConstants.VERANDAH_DEPTH));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.WATER_CLOSETS_VENTILATION_AREA)) {
	                    value.put("waterClosetsVentilationArea", ruleItem.get(EdcrRulesMdmsConstants.WATER_CLOSETS_VENTILATION_AREA));
	                    value.put("waterClosetsHeight", ruleItem.get(EdcrRulesMdmsConstants.WATER_CLOSETS_HEIGHT));
	                    value.put("waterClosetsArea", ruleItem.get(EdcrRulesMdmsConstants.WATER_CLOSETS_AREA));
	                    value.put("waterClosetsWidth", ruleItem.get(EdcrRulesMdmsConstants.WATER_CLOSETS_WIDTH));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.WATER_TANK_CAPACITY_AREA)) {
	                    value.put("waterTankCapacityArea", ruleItem.get(EdcrRulesMdmsConstants.WATER_TANK_CAPACITY_AREA));
	                    value.put("waterTankCapacityExpected", ruleItem.get(EdcrRulesMdmsConstants.WATER_TANK_CAPACITY_EXPECTED));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.SANITATION_MIN_AREA_SPWC)) {
	                    value.put("sanitationMinAreaofSPWC", ruleItem.get(EdcrRulesMdmsConstants.SANITATION_MIN_AREA_SPWC));
	                    value.put("sanitationMinDimensionofSPWC", ruleItem.get(EdcrRulesMdmsConstants.SANITATION_MIN_DIMENSION_SPWC));
	                    value.put("sanitationMinatGroundFloor", ruleItem.get(EdcrRulesMdmsConstants.SANITATION_MIN_AT_GROUND_FLOOR));
	                    value.put("sanitationFloorMultiplier", ruleItem.get(EdcrRulesMdmsConstants.SANITATION_FLOOR_MULTIPLIER));
	                }
	                
	                /*
	                // Todo: SideYardService feature is not using the methods currently, in future when the methods are used, this else if condition will be needed
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_ONE)) {
	                	value.put("sideYardValueOne", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_ONE));
	                	value.put("sideYardValueTwo", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWO));
	                	value.put("sideYardValueThree", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_THREE));
	                	value.put("sideYardValueFour", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_FOUR));
	                	value.put("sideYardValueFive", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_FIVE));
	                	value.put("sideYardValueSix", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_SIX));
	                	value.put("sideYardValueSeven", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_SEVEN));
	                	value.put("sideYardValueEight", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_EIGHT));
	                	value.put("sideYardValueNine", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_NINE));
	                	value.put("sideYardValueTen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TEN));
	                	value.put("sideYardValueEleven", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_ELEVEN));
	                	value.put("sideYardValueTwelve", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWELVE));
	                	value.put("sideYardValueThirteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_THIRTEEN));
	                	value.put("sideYardValueFourteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_FOURTEEN));
	                	value.put("sideYardValueFifteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_FIFTEEN));
	                	value.put("sideYardValueSixteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_SIXTEEN));
	                	value.put("sideYardValueSeventeen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_SEVENTEEN));
	                	value.put("sideYardValueEighteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_EIGHTEEN));
	                	value.put("sideYardValueNineteen", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_NINETEEN));
	                	value.put("sideYardValueTwenty", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY));
	                	value.put("sideYardValueTwentyOne", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_ONE));
	                	value.put("sideYardValueTwentyTwo", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_TWO));
	                	value.put("sideYardValueTwentyThree", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_THREE));
	                	value.put("sideYardValueTwentyFour", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_FOUR));
	                	value.put("sideYardValueTwentyFive", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_FIVE));
	                	value.put("sideYardValueTwentySix", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_SIX));
	                	value.put("sideYardValueTwentySeven", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_SEVEN));
	                	value.put("sideYardValueTwentyEight", ruleItem.get(EdcrRulesMdmsConstants.SIDE_YARD_VALUE_TWENTY_EIGHT));
	                }
	                */
	                
	                result.add(value);
	                break; // Exit after finding the first matching rule
	            }
	        }



	        return result;
	    }


	    private BigDecimal getBigDecimal(Object value) {
	        if (value instanceof BigDecimal) {
	            return (BigDecimal) value;
	        } else if (value instanceof Number) {
	            return new BigDecimal(((Number) value).doubleValue());
	        }
	        return null;
	    }

}
