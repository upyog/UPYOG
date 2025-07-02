package org.egov.edcr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.MdmsResponse;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;

@Service
public class CacheManagerMdms {

	@Autowired
	private BpaMdmsUtil bpaMdmsUtil;

	private static Logger LOG = LogManager.getLogger(CacheManagerMdms.class);

	Map<String, Map<RuleKey, List<Object>>> ruleMap;
	Map<String, Map<RuleKey, List<Object>>> cityRuleCache = new HashMap<>();

	
	public void getEdcrRulesFromMdms() {
		LOG.info("Entered getEdcrRules method");
		
		RuleKey cityaKey = new RuleKey("pg", "citya", "x", "y", "commercial", "low", "Far");
		RuleKey citybKey = new RuleKey("pg", "cityb", "a", "b", "commercial", "low", "Far");
		RuleKey Key3 = new RuleKey("pg", "citya", null, null, "commercial", "low", "Far");
		RuleKey Key4 = new RuleKey("pg", "citya", "a", "b", "residential", null, "Balcony");
		RuleKey Key5 = new RuleKey("pg", "citya", "a", "b", null, null, "Balcony");
		RuleKey Key6 = new RuleKey("pg", "citya", "a", null, null, null, "Far");
		RuleKey Key7 = new RuleKey("pg", "citya", "x", "y", "residential", null, "BathroomWaterClosets");
		 RuleKey key = new RuleKey(EdcrRulesMdmsConstants.STATE, "citya", "x", "y", "residential", null, "MezzanineFloorService");
        
		

		List<Object> cityaRules = getRules("citya", cityaKey);	
		List<Object> citybRules = getRules("cityb", citybKey);
		List<Object> key3 = getRules("citya", Key3);
		List<Object> key4 = getRules("citya", Key4);
		List<Object> key5 = getRules("citya", Key5);
		List<Object> key6 = getRules("citya", Key6);
		List<Object> key7 = getRules("citya", Key7);
		List<Object> key8 = getRules("citya", key);
		
	}
	

	public List<Object> getRules(String city, RuleKey lookupKey) {
		Map<RuleKey, List<Object>> cityRules = cityRuleCache.get(city);
        
		if (cityRules == null) {
			LOG.info("City [{}] not in cache, calling MDMS...", city);
			Object mdmsCityData = bpaMdmsUtil.mDMSCall(new RequestInfo(), "pg." + city);
			Map<String, Map<RuleKey, List<Object>>> transformed = transformCityLevelRulesByCity(mdmsCityData);

			if (transformed.containsKey(city)) {
				cityRules = transformed.get(city);
				cityRuleCache.put(city, cityRules); 
				LOG.info("City [{}] rules added to cache", city);
			} else {
				LOG.warn("City [{}] returned no rules from MDMS", city);
				return null;
			}
		} else {
			LOG.info("City [{}] rules found in cache", city);
		}
		
		

		return getRuleListForFeature(cityRules, lookupKey);
	}
	
	public Map<String, Map<RuleKey, List<Object>>> transformCityLevelRulesByCity(Object mdmsCityData) {
	    Map<String, Map<RuleKey, List<Object>>> cityWiseRuleMap = new HashMap<>();

	    if (mdmsCityData == null) return cityWiseRuleMap;
	    
	    
	    ObjectMapper mapper = new ObjectMapper();

	    MdmsResponse mdmsResponse = mapper.convertValue(mdmsCityData, MdmsResponse.class);
	    Map<String, JSONArray> bpaModuleMap = mdmsResponse.getMdmsRes().get("BPA");

	    if (bpaModuleMap == null) return cityWiseRuleMap;

	    for (Map.Entry<String, JSONArray> featureEntry : bpaModuleMap.entrySet()) {
	        String featureName = featureEntry.getKey();
	        JSONArray ruleArray = featureEntry.getValue();

	        List<MdmsFeatureRule> rules = mapper.convertValue(
	            ruleArray,
	            new TypeReference<List<MdmsFeatureRule>>() {}
	        );

	        for (MdmsFeatureRule rule : rules) {
	            if (!Boolean.TRUE.equals(rule.getActive())) continue;

	            RuleKey key = new RuleKey(
	                rule.getState(),
	                rule.getCity(),
	                rule.getZone(),
	                rule.getSubZone(),
	                rule.getOccupancy(),
	                rule.getRiskType(),
	                featureName
	            );

	            cityWiseRuleMap
	                .computeIfAbsent(rule.getCity(), k -> new HashMap<>())
	                .computeIfAbsent(key, k -> new ArrayList<>())
	                .add(rule);
	        }
	    }

	    return cityWiseRuleMap;
	}


	public List<Object> getRuleListForFeature(Map<RuleKey, List<Object>> ruleMap, RuleKey lookupKey) {
		if (ruleMap.containsKey(lookupKey)) {
			return ruleMap.get(lookupKey);
		}
		
		return null;
	}
	
}
