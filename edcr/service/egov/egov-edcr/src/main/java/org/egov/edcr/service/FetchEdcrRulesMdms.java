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
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
//import org.egov.infra.mdms.controller.MDMSController;
import org.egov.infra.microservice.models.RequestInfo;
//import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class FetchEdcrRulesMdms {

	  @Autowired
	  private BpaMdmsUtil bpaMdmsUtil;
	  
	  private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
	  
	  public Map<String, List<Map<String, Object>>> getEdcrRules() {
		    LOG.info("Entered getEdcrRules method");

		    // Make the MDMS call
//		    Object mdmsData = bpaMdmsUtil.mDMSCallv2(new RequestInfo(), "pg");
		    Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), "pg");
		    log.info("----------------mdmsdata------------- :: " + mdmsData);
		    if (mdmsData == null) {
		        LOG.warn("MDMS data is null");
		        return createEmptyResult("MDMS data is null");
		    }

		    // Define required keys for BPA
		    Set<String> requiredKeys = new HashSet<>(Arrays.asList(
		        "Far", "Coverage", "Balcony", "Toilet", "Doors", "FrontSetBack", "Kitchen",
		        "Landing", "Lift", "NonHabitationalDoors", "NoOfRiser", "Parking", "Plantation",
		        "PlinthHeight", "RearSetBack", "RequiredTread", "RequiredWidth", "RiserHeight",
		        "RoomArea", "RoomWiseDoorArea", "RoomWiseVentilation", "Basement", "Bathroom", 
		        "BathroomWaterClosets", "BlockDistancesService", "Chimney", "ExitWidth", "FireStair",
		        "FireTenderMovement", "GovtBuildingDistance", "GuardRoom"
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
	                }else if (valueFromColumn.size() > 1 && ruleItem.containsKey("minDoorWidth")) {
	                    value.put("minDoorWidth", ruleItem.get("minDoorWidth"));
	                    value.put("minDoorHeight", ruleItem.get("minDoorHeight"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("minToiletArea")) {
	                    value.put("minToiletArea", ruleItem.get("minToiletArea"));
	                    value.put("minToiletWidth", ruleItem.get("minToiletWidth"));
	                    value.put("minToiletVentilation", ruleItem.get("minToiletVentilation"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("percent")) {
	                    value.put("percent", ruleItem.get("percent"));
	                    value.put("permissibleValue", ruleItem.get(EdcrRulesMdmsConstants.PERMISSIBLE));
	                   
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("kitchenHeight")) {
	                    value.put("kitchenHeight", ruleItem.get("kitchenHeight"));
	                    value.put("kitchenArea", ruleItem.get("kitchenArea"));
	                    value.put("kitchenWidth", ruleItem.get("kitchenWidth"));
	                    value.put("kitchenStoreArea", ruleItem.get("kitchenStoreArea"));
	                    value.put("kitchenStoreWidth", ruleItem.get("kitchenStoreWidth"));
	                    value.put("kitchenDiningWidth", ruleItem.get("kitchenDiningWidth"));
	                    value.put("kitchenDiningArea", ruleItem.get("kitchenDiningArea"));
	                           
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("roomArea2")) {
	                    value.put("roomArea2", ruleItem.get("roomArea2"));
	                    value.put("roomArea1", ruleItem.get("roomArea1"));
	                    value.put("roomWidth2", ruleItem.get("roomWidth2"));
	                    value.put("roomWidth1", ruleItem.get("roomWidth1"));
	                   
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("permissibleone")) {
	                    value.put("permissibleone", ruleItem.get("permissibleone"));
	                    value.put("permissibletwo", ruleItem.get("permissibletwo"));
	                    value.put("permissiblethree", ruleItem.get("permissiblethree"));
	                    value.put("permissiblefour", ruleItem.get("permissiblefour"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("bathroomRequiredArea")) {
	                    value.put("bathroomRequiredArea", ruleItem.get("bathroomRequiredArea"));
	                    value.put("bathroomRequiredWidth", ruleItem.get("bathroomRequiredWidth"));
	                    value.put("bathroomtotalArea", ruleItem.get("bathroomtotalArea"));
	                    value.put("bathroomMinWidth", ruleItem.get("bathroomMinWidth"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("bathroomWCRequiredArea")) {
	                    value.put("bathroomWCRequiredArea", ruleItem.get("bathroomWCRequiredArea"));
	                    value.put("bathroomWCRequiredWidth", ruleItem.get("bathroomWCRequiredWidth"));
	                    value.put("bathroomWCRequiredHeight", ruleItem.get("bathroomWCRequiredHeight"));
	                    value.put("bathroomWCRequiredminHeight", ruleItem.get("bathroomWCRequiredminHeight"));
	                    value.put("bathroomWCRequiredTotalArea", ruleItem.get("bathroomWCRequiredTotalArea"));
	                    value.put("bathroomWCRequiredminWidth", ruleItem.get("bathroomWCRequiredminWidth"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("exitWidthOccupancyTypeHandlerVal")) {
	                	value.put("exitWidthOccupancyTypeHandlerVal", ruleItem.get("exitWidthOccupancyTypeHandlerVal"));
	                	value.put("exitWidthNotOccupancyTypeHandlerVal", ruleItem.get("exitWidthNotOccupancyTypeHandlerVal"));
	                	value.put("exitWidth_A_occupantLoadDivisonFactor", ruleItem.get("exitWidth_A_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_A_noOfDoors", ruleItem.get("exitWidth_A_noOfDoors"));
	                	value.put("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_A_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_A_SR_occupantLoadDivisonFactor", ruleItem.get("exitWidth_A_SR_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_A_SR_noOfDoors", ruleItem.get("exitWidth_A_SR_noOfDoors"));
	                	value.put("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_A_SR_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_B_occupantLoadDivisonFactor", ruleItem.get("exitWidth_B_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_B_noOfDoors", ruleItem.get("exitWidth_B_noOfDoors"));
	                	value.put("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_B_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_C_occupantLoadDivisonFactor", ruleItem.get("exitWidth_C_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_C_noOfDoors", ruleItem.get("exitWidth_C_noOfDoors"));
	                	value.put("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_C_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_D_occupantLoadDivisonFactor", ruleItem.get("exitWidth_D_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_D_noOfDoors", ruleItem.get("exitWidth_D_noOfDoors"));
	                	value.put("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_D_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_E_occupantLoadDivisonFactor", ruleItem.get("exitWidth_E_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_E_noOfDoors", ruleItem.get("exitWidth_E_noOfDoors"));
	                	value.put("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_E_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_F_occupantLoadDivisonFactor", ruleItem.get("exitWidth_F_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_F_noOfDoors", ruleItem.get("exitWidth_F_noOfDoors"));
	                	value.put("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_F_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_G_occupantLoadDivisonFactor", ruleItem.get("exitWidth_G_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_G_noOfDoors", ruleItem.get("exitWidth_G_noOfDoors"));
	                	value.put("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_G_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_H_occupantLoadDivisonFactor", ruleItem.get("exitWidth_H_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_H_noOfDoors", ruleItem.get("exitWidth_H_noOfDoors"));
	                	value.put("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_H_noOfOccupantsPerUnitExitWidthOfStairWay"));
	                	value.put("exitWidth_I_occupantLoadDivisonFactor", ruleItem.get("exitWidth_I_occupantLoadDivisonFactor"));
	                	value.put("exitWidth_I_noOfDoors", ruleItem.get("exitWidth_I_noOfDoors"));
	                	value.put("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay", ruleItem.get("exitWidth_I_noOfOccupantsPerUnitExitWidthOfStairWay"));

	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("FireStairExpectedNoofRise")) {
	                    value.put("FireStairExpectedNoofRise", ruleItem.get("FireStairExpectedNoofRise"));
	                    value.put("FireStairMinimumWidth", ruleItem.get("FireStairMinimumWidth"));
	                    value.put("FireStairRequiredTread", ruleItem.get("FireStairRequiredTread"));
	                    value.put("FireStairTypicalRepititiveFloor", ruleItem.get("FireStairTypicalRepititiveFloor"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("FireTenderMovementValueOne")) {
	                    value.put("FireTenderMovementValueOne", ruleItem.get("FireTenderMovementValueOne"));
	                    value.put("FireTenderMovementValueTwo", ruleItem.get("FireTenderMovementValueTwo"));
	                } 
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("GovtBuildingDistanceValue")) {
	                    value.put("GovtBuildingDistanceValue", ruleItem.get("GovtBuildingDistanceValue"));
	                    value.put("GovtBuildingDistanceMin", ruleItem.get("GovtBuildingDistanceMin"));
	                    value.put("GovtBuildingDistanceMaxHeight", ruleItem.get("GovtBuildingDistanceMaxHeight"));
	                    value.put("GovtBuildingDistancePermitted", ruleItem.get("GovtBuildingDistancePermitted"));
	                }
	                else if (valueFromColumn.size() > 1 && ruleItem.containsKey("GuardRoomMinHeight")) {
	                    value.put("GuardRoomMinHeight", ruleItem.get("GuardRoomMinHeight"));
	                    value.put("GuardRoomMinWidth", ruleItem.get("GuardRoomMinWidth"));
	                    value.put("GuardRoomMinArea", ruleItem.get("GuardRoomMinArea"));
	                    value.put("GuardRoomMinCabinHeightOne", ruleItem.get("GuardRoomMinCabinHeightOne"));
	                    value.put("GuardRoomMinCabinHeightTwo", ruleItem.get("GuardRoomMinCabinHeightTwo"));
	                }
	                
	                
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
