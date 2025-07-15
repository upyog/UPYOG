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
import org.egov.common.entity.edcr.EdcrMasterConfig;
import org.egov.common.entity.edcr.MdmsResponse;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.commons.mdms.BpaMdmsUtil;
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

	private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
	private Map<RuleKey, List<Object>> ruleMap = new HashMap<>();
	private boolean isMdmsReloaded = false; // to track if fallback MDMS call was already done


	public String getOccupancyName(Plan pl) {
		if (pl.getPlanInformation() == null || pl.getPlanInformation().getOccupancy() == null) {
			return null;
		}

		String occupancyName = pl.getPlanInformation().getOccupancy();

		Log.info("Occupancy Name : " + occupancyName);

		if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.RESIDENTIAL)) {
			return EdcrRulesMdmsConstants.RESIDENTIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.INDUSTRIAL)) {
			return EdcrRulesMdmsConstants.INDUSTRIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.COMMERCIAL)) {
			return EdcrRulesMdmsConstants.COMMERCIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.STORAGE)) {
			return EdcrRulesMdmsConstants.STORAGE;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.ASSEMBLY)) {
			return EdcrRulesMdmsConstants.ASSEMBLY;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.BUSINESS)) {
			return EdcrRulesMdmsConstants.BUSINESS;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.HAZARDOUS)) {
			return EdcrRulesMdmsConstants.HAZARDOUS;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.EDUCATIONAL)) {
			return EdcrRulesMdmsConstants.EDUCATIONAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.MEDICAL)) {
			return EdcrRulesMdmsConstants.MEDICAL;
		}

		return occupancyName;
	}
	
	
	private List<Map<String, Object>> riskTypeRules = new ArrayList<>();

	public String getRiskType(Plan pl) {
	    if (riskTypeRules.isEmpty()) {
	        ObjectMapper mapper = new ObjectMapper();

	        Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), "pg");
	        MdmsResponse mdmsResponse = mapper.convertValue(mdmsData, MdmsResponse.class);
	        JSONArray jsonArray = mdmsResponse.getMdmsRes().get("BPA").get("RiskTypeComputation");

	        for (int i = 0; i < jsonArray.size(); i++) {
	            @SuppressWarnings("unchecked")
	            Map<String, Object> rule = (Map<String, Object>) jsonArray.get(i);
	            riskTypeRules.add(rule);
	        }
	    }

	    BigDecimal plotArea = pl.getPlot().getArea();
	    BigDecimal height = pl.getVirtualBuilding().getBuildingHeight();

	    for (Map<String, Object> rule : riskTypeRules) {
	        BigDecimal fromPlotArea = new BigDecimal(rule.get("fromPlotArea").toString());
	        BigDecimal toPlotArea = new BigDecimal(rule.get("toPlotArea").toString());
	        BigDecimal fromHeight = new BigDecimal(rule.get("fromBuildingHeight").toString());
	        BigDecimal toHeight = new BigDecimal(rule.get("toBuildingHeight").toString());

	        boolean plotAreaInRange = plotArea.compareTo(fromPlotArea) >= 0 && plotArea.compareTo(toPlotArea) < 0;
	        //boolean heightInRange = height.compareTo(fromHeight) >= 0 && height.compareTo(toHeight) < 0;

	        if (plotAreaInRange) { //add height if required
	            return rule.get("riskType").toString();
	        }
	    }

	    return null;
	}



	
//	    public String getEdcrRuleSource(String featureName) {
//	        List<Map<String, Object>> configs = masterConfigList.get(MdmsFeatureConstants.EDCR_MASTER_CONFIG);
//
//	        if (configs == null || configs.isEmpty()) return "city"; // default to city
//
//	        for (Map<String, Object> config : configs) {
//	            if (featureName.equalsIgnoreCase((String) config.get("featureName"))) {
//	                boolean hasZone = "yes".equalsIgnoreCase((String) config.getOrDefault("zone", "no"));
//	                boolean hasSubZone = "yes".equalsIgnoreCase((String) config.getOrDefault("subZone", "no"));
//	                boolean hasPlotArea = "yes".equalsIgnoreCase((String) config.getOrDefault("plotArea", "no"));
//	                boolean hasRoadWidth = "yes".equalsIgnoreCase((String) config.getOrDefault("roadWidth", "no"));
//
//	                if (hasZone || hasSubZone || hasPlotArea || hasRoadWidth) {
//	                    return "state";
//	                } else {
//	                    return "city";
//	                }
//	            }
//	        }
//	        return "city"; // default fallback
//	    }
//

	public Map<RuleKey, List<Object>> transformCityLevelRules(Object mdmsCityData) {
		Map<RuleKey, List<Object>> ruleMap = new HashMap<>();

		if (mdmsCityData == null)
			return ruleMap;

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> mdmsMap = mapper.convertValue(mdmsCityData, new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> mdmsResMap = (Map<String, Object>) mdmsMap.get("MdmsRes");

		Map<String, Object> bpaModuleMap = (Map<String, Object>) mdmsResMap.get("BPA");

		if (bpaModuleMap == null)
			return ruleMap;

		for (Map.Entry<String, Object> featureEntry : bpaModuleMap.entrySet()) {
			String featureName = featureEntry.getKey();
			List<Map<String, Object>> rules = (List<Map<String, Object>>) featureEntry.getValue();

			for (Map<String, Object> rule : rules) {
				if (!Boolean.TRUE.equals(rule.get("active")))
					continue;

				RuleKey key = new RuleKey((String) rule.get("state"), (String) rule.get("city"),
						(String) rule.get("zone"), (String) rule.get("subZone"), (String) rule.get("occupancy"),
						(String) rule.get("riskType"), featureName);

				ruleMap.computeIfAbsent(key, k -> new ArrayList<>()).add(rule);
			}
		}

		return ruleMap;
	}

//	public List<Object> getRuleListForFeature(Map<RuleKey, List<Object>> ruleMap, String state, String city,
//			String zone, String subZone, String occupancy, String riskType, String featureName) {
//
//		RuleKey lookupKey = new RuleKey(state, city, zone, subZone, occupancy, riskType, featureName);
//
//// Try exact match
//		if (ruleMap.containsKey(lookupKey)) {
//			return ruleMap.get(lookupKey);
//		}
//
//// Fallback: Try partial match (e.g., if zone or subZone is null in keys)
//		for (Map.Entry<RuleKey, List<Object>> entry : ruleMap.entrySet()) {
//			RuleKey key = entry.getKey();
//			if (Objects.equals(key.getState(), state) && Objects.equals(key.getCity(), city)
//					&& Objects.equals(key.getZone(), zone) && Objects.equals(key.getSubZone(), subZone)
//					&& Objects.equals(key.getOccupancy(), occupancy) && Objects.equals(key.getRiskType(), riskType)
//					&& Objects.equals(key.getFeatureName(), featureName)) {
//				return entry.getValue();
//			}
//		}
//
//// Fallback to null if nothing matches
//		return null;
//	}
	
	public List<Object> getRuleListForFeature(Map<RuleKey, List<Object>> ruleMap, RuleKey lookupKey) {
	    if (ruleMap.containsKey(lookupKey)) {
	        return ruleMap.get(lookupKey);
	    }

	    for (Map.Entry<RuleKey, List<Object>> entry : ruleMap.entrySet()) {
	        RuleKey key = entry.getKey();
	        if (Objects.equals(key.getState(), lookupKey.getState()) &&
	            Objects.equals(key.getCity(), lookupKey.getCity()) &&
	            Objects.equals(key.getZone(), lookupKey.getZone()) &&
	            Objects.equals(key.getSubZone(), lookupKey.getSubZone()) &&
	            Objects.equals(key.getOccupancy(), lookupKey.getOccupancy()) &&
	            Objects.equals(key.getRiskType(), lookupKey.getRiskType()) &&
	            Objects.equals(key.getFeatureName(), lookupKey.getFeatureName())) {
	            return entry.getValue();
	        }
	    }

	    return null;
	}

	

}
