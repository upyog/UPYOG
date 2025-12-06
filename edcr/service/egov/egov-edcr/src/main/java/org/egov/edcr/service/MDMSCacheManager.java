package org.egov.edcr.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.FeatureEnum;
import org.egov.common.entity.edcr.FeatureRuleKey;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.MdmsResponse;
import org.egov.common.entity.edcr.Plan;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.config.EdcrConfigProperties;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.minidev.json.JSONArray;

import static org.egov.edcr.constants.CommonFeatureConstants.*;

@Service
public class MDMSCacheManager {

	@Autowired
	private BpaMdmsUtil bpaMdmsUtil;

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;

	private static final Logger LOG = LogManager.getLogger(MDMSCacheManager.class);
	/**
	 * Cache to store MDMS rules for different features and cities. The key is the
	 * city or state, and the value is a map of FeatureRuleKey to list of rules.
	 */
	Map<String, Map<FeatureRuleKey, List<Object>>> featureRuleCache = new HashMap<>();
    @Autowired
    private EdcrConfigProperties edcrConfigProperties;

	/**
	 * Retrieves applicable BPA rules from cache or MDMS for a given lookup key.
	 *
	 * @param lookupKey The composite key representing city, zone, occupancy, etc.
	 * @return List of rules (if any), or an empty list if none are found
	 */
	public List<Object> getRules(FeatureRuleKey lookupKey) {
		String city = lookupKey.getCity();
		String state = lookupKey.getState();

		// Use city if available; otherwise, fallback to state
		String cacheKey = city != null ? city : state;

		LOG.info("Fetching rules for FeatureRuleKey: {}", lookupKey);

		// Try fetching from in-memory cache
		Map<FeatureRuleKey, List<Object>> cityRules = featureRuleCache.get(cacheKey);

		// If not found, fetch from MDMS and populate cache
		if (cityRules == null) {
			LOG.info("Cache miss for key: '{}'. Initiating MDMS fetch...", cacheKey);

			// Construct tenantId (state or state.city format)
			String tenantId = city != null ? state + "." + city : state;
			LOG.info("Constructed tenantId for MDMS request: '{}'", tenantId);

			try {
				Object mdmsCityData = bpaMdmsUtil.mDMSCall(new RequestInfo(), tenantId);
				if (mdmsCityData != null) {
					transformCityRules(mdmsCityData);
					LOG.info("MDMS rules transformed and cached successfully for tenantId: '{}'", tenantId);
				} else {
					LOG.warn("MDMS response was null for tenantId: '{}'", tenantId);
				}
			} catch (Exception e) {
				LOG.error("Error occurred while fetching or transforming MDMS rules for tenantId: '{}'", tenantId, e);
				return Collections.emptyList(); // Fail-safe
			}

			// Retry fetching from cache after transformation
			cityRules = featureRuleCache.get(cacheKey);
		}

		// Fetch rules for the exact FeatureRuleKey
		List<Object> rules = cityRules != null ? cityRules.get(lookupKey) : null;

		if (rules == null || rules.isEmpty()) {
			LOG.warn("No rules found for key: '{}' under cache entry: '{}'", lookupKey, cacheKey);
			return Collections.emptyList();
		}

		LOG.info("Returning {} rule(s) for lookupKey: '{}'", rules.size(), lookupKey);
		return rules;
	}

	/**
	 * Transforms MDMS raw JSON data into structured in-memory rule cache. This
	 * method parses and groups the rules based on FeatureRuleKey.
	 *
	 * @param mdmsCityData Raw response from MDMS containing rules
	 */
	private void transformCityRules(Object mdmsCityData) {
		ObjectMapper mapper = new ObjectMapper();

		// Convert generic response to domain-specific MdmsResponse
		MdmsResponse mdmsResponse = mapper.convertValue(mdmsCityData, MdmsResponse.class);
		Map<String, JSONArray> bpaModuleMap = mdmsResponse.getMdmsRes().getOrDefault(EdcrRulesMdmsConstants.BPA,
				Collections.emptyMap());

		if (bpaModuleMap.isEmpty()) {
			LOG.warn("No BPA module data found in MDMS response.");
			return;
		}

		LOG.info("Transforming MDMS data: {} features found in BPA module", bpaModuleMap.size());

		// Iterate over all features like "PlantationGreenStrip", "SetBack", etc.
		for (Map.Entry<String, JSONArray> featureEntry : bpaModuleMap.entrySet()) {
			String featureName = featureEntry.getKey();
			JSONArray ruleArray = featureEntry.getValue();

			// Convert raw array to list of rules
			FeatureEnum featureEnum = getFeatureEnumFromString(featureName);
	        Class<? extends MdmsFeatureRule> ruleClass = fetchEdcrRulesMdms.getRuleClassForFeature(featureEnum != null ? featureEnum : null);
	        List<MdmsFeatureRule> rules = new ArrayList<>();

	        for (int i = 0; i < ruleArray.size(); i++) {
	            try {
	                JsonNode jsonNode = mapper.valueToTree(ruleArray.get(i));
	                if (jsonNode instanceof ObjectNode) {
						/* We add the feature name and default state explicitly to each rule's JSON node before deserialization
						 to ensure that these important contextual fields are always present in the MdmsFeatureRule object.
						 This is necessary because the original MDMS data might not include these fields directly within each rule,
						 but downstream processing relies on them being available for proper identification, filtering, and caching.
						 Adding the default state from configuration also ensures consistency when the rule does not specify a state.
						 */
	                    ((ObjectNode) jsonNode).put(FEATURE_NAME_STRING, featureName);
	                    ((ObjectNode) jsonNode).put(STATE_STRING, edcrConfigProperties.getDefaultState());

	                    MdmsFeatureRule rule = mapper.treeToValue(jsonNode, ruleClass);
	  
	                    LOG.info("Parsed rule: {}, State: {}", rule, rule.getState());

	                    if (Boolean.TRUE.equals(rule.getActive())) {
	                        rules.add(rule);
	                    }
	                }
	            } catch (Exception e) {
	                LOG.error("Failed to deserialize rule for feature [{}]: {}", featureName, e.getMessage(), e);
	            }
	        }

			// Group and cache rules by city and FeatureRuleKey
			for (MdmsFeatureRule rule : rules) {
				if (!Boolean.TRUE.equals(rule.getActive())) {
					LOG.debug("Skipping inactive rule for feature [{}], city [{}]", featureName, rule.getCity());
					continue;
				}

				// Use city for cache if present; fallback to state
				String cityKey = rule.getCity() != null ? rule.getCity() : rule.getState();

				FeatureRuleKey key = new FeatureRuleKey(rule.getState(), rule.getCity(), rule.getZone(),
						rule.getSubZone(), rule.getOccupancy(), rule.getRiskType(), featureName);

				// Cache structure: Map<City, Map<FeatureRuleKey, List<Rule>>>

				    //Added this because it was giving duplicate etries 
					List<Object> existing = featureRuleCache
					.computeIfAbsent(cityKey, k -> new HashMap<>())
					.computeIfAbsent(key, k -> new ArrayList<>());

				if (!existing.contains(rule)) {
					existing.add(rule);
				}
				// featureRuleCache.computeIfAbsent(cityKey, k -> new HashMap<>())
				// 		.computeIfAbsent(key, k -> new ArrayList<>()).add(rule);
			}

			LOG.debug("Processed feature '{}': {} active rules", featureName, rules.size());
		}
	}

	/**
	 * Retrieves a list of feature rules for a given plan and feature name, based on occupancy,
	 * zone, sub-zone, and optionally risk type. Constructs a {@link FeatureRuleKey} and uses it
	 * to fetch the corresponding rules from the in-memory or MDMS-backed cache.
	 *
	 * @param plan             The {@link Plan} object containing zone, sub-zone, occupancy, and tenant information.
	 * @param feature          The name of the feature for which rules are to be retrieved.
	 * @param includeRiskType  Whether to include risk type as part of the rule key for filtering rules.
	 * @return A list of applicable rules for the given feature and plan context. Returns an empty list if no rules match.
	 */
	
	public List<Object> getFeatureRules(Plan plan, String feature, boolean includeRiskType) {

		LOG.info("Fetching feature rules: feature='{}', includeRiskType={}, tenantId='{}'",
				feature, includeRiskType, plan.getTenantId());

		String occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan).toLowerCase();
		String tenantId = plan.getTenantId();
		String zone = plan.getPlanInformation().getZone() != null
	            ? plan.getPlanInformation().getZone().toLowerCase()
	            : null;

	    String subZone = plan.getPlanInformation().getSubZone() != null
	            ? plan.getPlanInformation().getSubZone().toLowerCase()
	            : null;

		String riskType = includeRiskType ? fetchEdcrRulesMdms.getRiskType(plan).toLowerCase() : null;

		String checkedTenantId = edcrConfigProperties.getIsStateWise() ? null : tenantId;
		FeatureRuleKey key = new FeatureRuleKey(edcrConfigProperties.getDefaultState(), checkedTenantId, zone, subZone, occupancyName, riskType, feature);

		return getRules(key);
	}
	
	/**
	 * Converts a feature name string to its corresponding {@link FeatureEnum} constant.
	 * Matching is case-insensitive.
	 *
	 * @param featureName The name of the feature to be matched.
	 * @return The matching {@link FeatureEnum} value, or {@code null} if no match is found.
	 */

	private FeatureEnum getFeatureEnumFromString(String featureName) {
	    for (FeatureEnum feature : FeatureEnum.values()) {
	        if (feature.getValue().equalsIgnoreCase(featureName)) {
	            return feature;
	        }
	    }
	    return null; // or throw exception or return a default
	}

}