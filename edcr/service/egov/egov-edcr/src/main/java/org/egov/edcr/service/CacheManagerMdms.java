package org.egov.edcr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.MdmsResponse;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.commons.mdms.config.MdmsConfiguration;
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
	
	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;
	
	private static Logger LOG = LogManager.getLogger(CacheManagerMdms.class);

	Map<String, Map<RuleKey, List<Object>>> ruleMap;
	Map<String, Map<RuleKey, List<Object>>> cityRuleCache = new HashMap<>();

	/**
	 * Retrieves a list of rules for a given city and rule lookup key.
	 * <p>
	 * This method first checks if the rules for the specified city are available in the local cache.
	 * If not, it performs an MDMS call to fetch the rules, transforms them into a map structure,
	 * and stores them in the cache for future use. If the rules are still not available after the MDMS call,
	 * it returns {@code null}. If found, it retrieves and returns the list of rules that match the specified {@link RuleKey}.
	 * </p>
	 *
	 * @param city      The city identifier for which the rules are to be retrieved.
	 * @param lookupKey The {@link RuleKey} object representing the criteria to fetch matching rules.
	 * @return A list of matching rule objects if available, or {@code null} if no rules are found.
	 */

	public List<Object> getRules(String city, RuleKey lookupKey) {
		Map<RuleKey, List<Object>> cityRules = cityRuleCache.get(city);

		if (cityRules == null) {
			LOG.info("City [{}] not in cache, calling MDMS...", city);
			Object mdmsCityData = bpaMdmsUtil.mDMSCall(new RequestInfo(), EdcrRulesMdmsConstants.STATE + "." + city);
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
	
	
//	public List<Object> getRules(String city, RuleKey lookupKey) {
//	    Map<RuleKey, List<Object>> rulesToUse = cityRuleCache.get(city);
//
//	    if (rulesToUse == null) {
//	        LOG.info("City [{}] not in cache, calling MDMS...", city);
//	        Object mdmsCityData = bpaMdmsUtil.mDMSCall(new RequestInfo(), EdcrRulesMdmsConstants.STATE + "." + city);
//
//	        boolean isEmptyMdms = mdmsCityData == null ||
//	                (mdmsCityData instanceof Map && ((Map<?, ?>) mdmsCityData).isEmpty()) ||
//	                (mdmsCityData instanceof List && ((List<?>) mdmsCityData).isEmpty()) ||
//	                (mdmsCityData instanceof String && ((String) mdmsCityData).trim().isEmpty());
//
//	        if (!isEmptyMdms) {
//	            Map<String, Map<RuleKey, List<Object>>> transformed = transformCityLevelRulesByCity(mdmsCityData);
//
//	            if (transformed != null && transformed.containsKey(city)) {
//	                rulesToUse = transformed.get(city);
//	                cityRuleCache.put(city, rulesToUse);
//	                LOG.info("City [{}] rules added to cache", city);
//	            }
//	        }
//
//	        if (rulesToUse == null) {
//	            LOG.warn("City [{}] rules not found, falling back to STATE level", city);
//	            String stateKey = EdcrRulesMdmsConstants.STATE;
//	            rulesToUse = cityRuleCache.get(stateKey);
//
//	            if (rulesToUse == null) {
//	                Object mdmsStateData = bpaMdmsUtil.mDMSCall(new RequestInfo(), stateKey);
//	                Map<String, Map<RuleKey, List<Object>>> stateTransformed = transformCityLevelRulesByCity(mdmsStateData);
//
//	                if (stateTransformed != null && stateTransformed.containsKey(stateKey)) {
//	                    rulesToUse = stateTransformed.get(stateKey);
//	                    cityRuleCache.put(stateKey, rulesToUse);
//	                    LOG.info("State-level rules added to cache under [{}]", stateKey);
//	                } else {
//	                    LOG.error("State [{}] rules not found in MDMS", stateKey);
//	                    return null;
//	                }
//	            }
//	        }
//	    } else {
//	        LOG.info("City [{}] rules found in cache", city);
//	    }
//
//	    return getRuleListForFeature(rulesToUse, lookupKey);
//	}


	/**
	 * Transforms the MDMS city-level rules data into a nested map structure.
	 * <p>
	 * This method parses the provided MDMS response object to extract BPA-related rules.
	 * It organizes the rules in a map, grouped first by city name and then by a {@link RuleKey}
	 * that uniquely identifies each rule set based on attributes like state, city, zone,
	 * sub-zone, occupancy, risk type, and feature name.
	 * Only active rules are included in the resulting map.
	 * </p>
	 *
	 * @param mdmsCityData The raw MDMS data object obtained from the MDMS call.
	 * @return A map where the key is the city name, and the value is another map
	 *         mapping {@link RuleKey} to a list of rule objects ({@link MdmsFeatureRule}).
	 *         Returns an empty map if the input data is null or improperly structured.
	 */

	
	public Map<String, Map<RuleKey, List<Object>>> transformCityLevelRulesByCity(Object mdmsCityData) {
	    Map<String, Map<RuleKey, List<Object>>> cityWiseRuleMap = new HashMap<>();

	    if (mdmsCityData == null) return cityWiseRuleMap;
	    
	    
	    ObjectMapper mapper = new ObjectMapper();

	    MdmsResponse mdmsResponse = mapper.convertValue(mdmsCityData, MdmsResponse.class);
	    Map<String, JSONArray> bpaModuleMap = mdmsResponse.getMdmsRes().get(EdcrRulesMdmsConstants.BPA);

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
	
	/**
	 * Retrieves the list of rules for a given feature based on the provided {@link RuleKey}.
	 * <p>
	 * This method looks up the rule map for the exact match of the provided key.
	 * If found, it returns the corresponding list of rules. If no match is found,
	 * it returns {@code null}.
	 * </p>
	 *
	 * @param ruleMap   A map where keys are {@link RuleKey} objects and values are lists of rules.
	 * @param lookupKey The key used to look up the rules in the map.
	 * @return A list of rules associated with the given key, or {@code null} if the key is not present.
	 */

	public List<Object> getRuleListForFeature(Map<RuleKey, List<Object>> ruleMap, RuleKey lookupKey) {
		if (ruleMap.containsKey(lookupKey)) {
			return ruleMap.get(lookupKey);
		}
		
		return null;
	}
	
	/**
	 * Retrieves MDMS rules for a given feature using plan details and feature name.
	 *
	 * @param plan The building plan object
	 * @param feature The feature name (e.g., "BALCONY", "STAIRCASE", etc.)
	 * @param includeRiskType Whether to include riskType in the RuleKey
	 * @return List of rules as List<Object> or null if no rules are found
	 */
	
	public List<Object> getFeatureRules(Plan plan, String feature,
	                                    boolean includeRiskType
	                                   ) {

	    String occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan).toLowerCase();
	    String tenantId = plan.getTenantId();
	    String zone = plan.getPlanInformation().getZone().toLowerCase();
	    String subZone = plan.getPlanInformation().getSubZone().toLowerCase();
	    String riskType = includeRiskType ? fetchEdcrRulesMdms.getRiskType(plan).toLowerCase() : null;

	    RuleKey key = new RuleKey(
	        EdcrRulesMdmsConstants.STATE,
	        tenantId,
	        zone,
	        subZone,
	        occupancyName,
	        riskType,
	        feature
	    );

	    return getRules(tenantId, key);
	}
	
}
