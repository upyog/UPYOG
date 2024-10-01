package org.egov.edcr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class FetchEdcrRulesMdms {

	  @Autowired
	  private BpaMdmsUtil bpaMdmsUtil;
	  
	  private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);

	  
	 public Map<String, List<Map<String, Object>>> getEdcrRules1() {
	        LOG.info("Entered getEdcrRules method");

	        Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), "pg");

	        Map<String, Object> dataMap = (Map<String, Object>) mdmsData;
	        Map<String, Object> mdmsRes = (Map<String, Object>) dataMap.get("MdmsRes");
	        if (mdmsRes != null) {
	            Map<String, Object> bpa = (Map<String, Object>) mdmsRes.get("BPA");
	            if (bpa != null) {
	                List<?> edcrRules = (List<?>) bpa.get("EdcrRulesFeatures");
	                if (edcrRules != null && !edcrRules.isEmpty()) {

	                    Map<String, List<Map<String, Object>>> edcrRulesMap = new HashMap<>();
	                    for (Object rule : edcrRules) {
	                        if (rule instanceof Map) {
	                            @SuppressWarnings("unchecked")
	                            Map<String, Object> ruleMap = (Map<String, Object>) rule;

	                            for (Map.Entry<String, Object> entry : ruleMap.entrySet()) {
	                                String ruleType = entry.getKey();
	                                Object ruleDetails = entry.getValue();

	                                if (ruleDetails instanceof List) {
	                                    List<?> ruleDetailsList = (List<?>) ruleDetails;

	                                    for (Object detail : ruleDetailsList) {
	                                        if (detail instanceof Map) {
	                                            @SuppressWarnings("unchecked")
	                                            Map<String, Object> detailMap = (Map<String, Object>) detail;

	                                            edcrRulesMap
	                                                .computeIfAbsent(ruleType, k -> new ArrayList<>())
	                                                .add(detailMap);
	                                        }
	                                    }
	                                } else if (ruleDetails instanceof Map) {
	                                    @SuppressWarnings("unchecked")
	                                    Map<String, Object> detailMap = (Map<String, Object>) ruleDetails;

	                                    edcrRulesMap
	                                        .computeIfAbsent(ruleType, k -> new ArrayList<>())
	                                        .add(detailMap);
	                                }
	                            }
	                        }
	                    }

	                    LOG.info("edcrRulesFeatures: {}", edcrRulesMap);
	                    return edcrRulesMap;

	                } else {
	                    LOG.info("EdcrRules is empty.");
	                    Map<String, List<Map<String, Object>>> emptyMap = new HashMap<>();
	                    emptyMap.put("message", Collections.singletonList(Collections.singletonMap("message", "No EdcrRules data found")));
	                    return emptyMap;
	                }
	            }
	        }

	       
	        return new HashMap<>();
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
	                    String ruleOccupancy = ruleItem.get("occupancy").toString().toLowerCase(); 
	                    if (ruleOccupancy.equals(paramsOccupancy)) { // Filter by occupancy
	                        filteredRules.add(ruleItem);
	                    }
	                }
	            }
	        }

	     // Step 2: Process the filtered rules
	        for (Map<String, Object> ruleItem : filteredRules) {
	            // Extract area and feature name from the rule
	            BigDecimal ruleFromArea = ruleItem.containsKey("fromPlotArea") ? getBigDecimal(ruleItem.get("fromPlotArea")) : null;
	            BigDecimal ruleToArea = ruleItem.containsKey("toPlotArea") ? getBigDecimal(ruleItem.get("toPlotArea")) : null;
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

	            // Case 1: Both plotArea and featureName are required, and both match
	            if (paramsPlotArea != null && paramsFeatureName != null) {
	                if (plotAreaMatches && featureNameMatches) {
	                    Map<String, Object> value = new HashMap<>();
	                    if (valueFromColumn.size() == 1) {
	                        value.put("permissibleValue", ruleItem.get("permissible"));
	                    } else if (valueFromColumn.size() > 1 && ruleItem.containsKey("min_value")) {
	                        value.put("minValue", ruleItem.get("min_value"));
	                        value.put("maxValue", ruleItem.get("max_value"));
	                    }
	                    result.add(value);
	                    break; // Exit after finding the first matching rule
	                }
	            }
	            // Case 2: Only plotArea is required, and it matches
	            else if (paramsPlotArea != null && paramsFeatureName == null) {
	                if (plotAreaMatches) {
	                    Map<String, Object> value = new HashMap<>();
	                    if (valueFromColumn.size() == 1) {
	                        value.put("permissibleValue", ruleItem.get("permissible"));
	                    } else if (valueFromColumn.size() > 1 && ruleItem.containsKey("min_value")) {
	                        value.put("minValue", ruleItem.get("min_value"));
	                        value.put("maxValue", ruleItem.get("max_value"));
	                    }
	                    result.add(value);
	                    break; // Exit after finding the first matching rule
	                }
	            }
	            // Case 3: Only featureName is required, and it matches
	            else if (paramsFeatureName != null && paramsPlotArea == null) {
	                if (featureNameMatches) {
	                    Map<String, Object> value = new HashMap<>();
	                    if (valueFromColumn.size() == 1) {
	                        value.put("permissibleValue", ruleItem.get("permissible"));
	                    } else if (valueFromColumn.size() > 1 && ruleItem.containsKey("min_value")) {
	                        value.put("minValue", ruleItem.get("min_value"));
	                        value.put("maxValue", ruleItem.get("max_value"));
	                    }
	                    result.add(value);
	                    break; // Exit after finding the first matching rule
	                }
	            }
	            // Case 4: Neither plotArea nor featureName is required, get permissible value
	            else {
	                Map<String, Object> value = new HashMap<>();
	                if (valueFromColumn.size() == 1) {
	                    value.put("permissibleValue", ruleItem.get("permissible"));
	                } else if (valueFromColumn.size() > 1  && ruleItem.containsKey("min_value")) {
	                    value.put("minValue", ruleItem.get("min_value"));
	                    value.put("maxValue", ruleItem.get("max_value"));
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
	                    value.put("permissibleValue", ruleItem.get("permissible"));
	                   
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
