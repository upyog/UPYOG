
package org.egov.commons.mdms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.edcr.model.EdcrRequest;
import org.egov.commons.edcr.mdms.constants.MdmsConstants;
import org.egov.commons.edcr.mdms.filter.MdmsFilter;
import org.egov.commons.mdms.config.MdmsConfiguration;
import org.egov.commons.mdms.model.MasterDetail;
import org.egov.commons.mdms.model.MdmsCriteria;
import org.egov.commons.mdms.model.MdmsCriteriaReq;
import org.egov.commons.mdms.model.ModuleDetail;
import org.egov.commons.service.RestCallService;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BpaMdmsUtil {
	private RestCallService serviceRequestRepository;
	private MdmsConfiguration mdmsConfiguration;
	
	@Value("${egov.user.host}")
	private String userServiceHost;
	
	@Value("${egov.bpa.host}")
	private String bpaServiceHost;
	
	@Value("${user.search.url}")
	private String userServiceUrl;
	
	@Value("${bpa.search.url}")
	private String bpaServiceUrl;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Configuration JACKSON_CONFIG = Configuration.builder()
	        .mappingProvider(new JacksonMappingProvider())
	        .build();

	public BpaMdmsUtil(RestCallService serviceRequestRepository, MdmsConfiguration mdmsConfiguration) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.mdmsConfiguration = mdmsConfiguration;
	}

	public List<ModuleDetail> getBPAModuleRequest() {
		List<MasterDetail> bpaMasterDtls = new ArrayList<>();
		final String filterCode = "$.[?(@.active==true)].code";
		MasterDetail masterDetailAppType = new MasterDetail();
		masterDetailAppType.setName("ApplicationType");
		masterDetailAppType.setFilter(filterCode);
		bpaMasterDtls.add(masterDetailAppType);

		MasterDetail masterDetailServicetype = new MasterDetail();
		masterDetailServicetype.setName("ServiceType");
		masterDetailServicetype.setFilter(filterCode);
		bpaMasterDtls.add(masterDetailServicetype);

		MasterDetail masterDetailOccupancyType = new MasterDetail();
		masterDetailOccupancyType.setName("OccupancyType");
		masterDetailOccupancyType.setFilter("$.[?(@.active==true)]");
		bpaMasterDtls.add(masterDetailOccupancyType);

		MasterDetail masterDetailSubOccupancyType = new MasterDetail();
		masterDetailSubOccupancyType.setName("SubOccupancyType");
		masterDetailSubOccupancyType.setFilter("$.[?(@.active==true)]");
		bpaMasterDtls.add(masterDetailSubOccupancyType);

		MasterDetail masterDetailEdcrMdmsFeature = new MasterDetail();
		masterDetailEdcrMdmsFeature.setName("EdcrRulesFeatures");
		bpaMasterDtls.add(masterDetailEdcrMdmsFeature);

		MasterDetail masterDetailUsages = new MasterDetail();
		masterDetailUsages.setName("Usages");
		masterDetailUsages.setFilter("$.[?(@.active==true)]");
		bpaMasterDtls.add(masterDetailUsages);

		/*
		 * MasterDetail masterDetailSubfeatureColorCode = new MasterDetail();
		 * masterDetailSubfeatureColorCode.setName("SubFeatureColorCode");
		 * masterDetailSubfeatureColorCode.setFilter("$.*");
		 * bpaMasterDtls.add(masterDetailSubfeatureColorCode);
		 */

		ModuleDetail bpaModuleDtls = new ModuleDetail();
		bpaModuleDtls.setMasterDetails(bpaMasterDtls);
		bpaModuleDtls.setModuleName("BPA");
		return Arrays.asList(bpaModuleDtls);
	}

	private MdmsCriteriaReq getBpaMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getBPAModuleRequest();
		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);
		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setModuleDetails(moduleDetails);
		mdmsCriteria.setTenantId(tenantId);
		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);
		return mdmsCriteriaReq;
	}

	private MdmsCriteriaReq getBpaFARMDMSRequest(RequestInfo requestInfo, EdcrRequest edcrRequest, String occType, BigDecimal plotArea) {
		// List<ModuleDetail> moduleRequest = getBPAFARModuleRequest();
		// String code, String scheme, String fromFY, Long currentDate, String
		// occCategory
		String finYear;
		LocalDate today = LocalDate.now();
		if(today.getMonthValue() > 3)
			finYear = today.getYear() + "-" + ((today.getYear() % 2000) +1);
		else
			finYear = (today.getYear()-1) + "-" + (today.getYear()) % 2000;
		
		//List<ModuleDetail> moduleRequest = getMDMSModuleRequest1("101", "SCHEME1", "2025-26", 1757496600979L, occType, plotArea);
		List<ModuleDetail> moduleRequest = getMDMSModuleRequest1(MdmsConstants.MasterPlanCode, MdmsConstants.Scheme, finYear, System.currentTimeMillis(), occType, plotArea);

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);
		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setModuleDetails(moduleDetails);
//		mdmsCriteria.setTenantId(edcrRequest.getTenantId());
		mdmsCriteria.setTenantId("pb");
		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);
		return mdmsCriteriaReq;
	}
	
	private MdmsCriteriaReq getUlbTypeRequest(RequestInfo requestInfo, EdcrRequest edcrRequest) {
		List<ModuleDetail> moduleRequest = getUlbTypeRequest(edcrRequest.getTenantId());

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);
		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setModuleDetails(moduleDetails);
		mdmsCriteria.setTenantId("pb");
		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);
		return mdmsCriteriaReq;
	}

    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getBpaMDMSRequest(requestInfo,tenantId);
        //MdmsCriteriaReq mdmsCriteriaReq = getBpaFARMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        //return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
//        Map<String, List<Map<String, Object>>> farData = mdmsResponseMapper(result, MdmsFilter.FAR_PATH);
//
//        Map<String, List<Map<String, Object>>> categories = mdmsResponseMapper(result, MdmsFilter.CATEGORY_PATH);
//
//        Map<String, List<Map<String, Object>>> normalFAR = mdmsResponseMapper(result, MdmsFilter.NORMAL_FAR_PATH);

        return result;
    }

	public Object mDMSCall(RequestInfo requestInfo, EdcrRequest edcrRequest, String occType, BigDecimal plotArea) {
		MdmsCriteriaReq mdmsCriteriaReq = getBpaFARMDMSRequest(requestInfo, edcrRequest, occType, plotArea);

		try {
			return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);

		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			// Handles 4xx and 5xx HTTP errors
			System.err.println("MDMS server error: " + ex.getMessage());
			return Collections.singletonMap("error", "MDMS not responding");

		} catch (ResourceAccessException ex) {
			// Handles connection timeout / unreachable server
			System.err.println("MDMS connection error: " + ex.getMessage());
			return Collections.singletonMap("error", "MDMS not responding");

		} catch (Exception ex) {
			// Any other unexpected error
			System.err.println("Unexpected error during MDMS call: " + ex.getMessage());
			return Collections.singletonMap("error", ex.getMessage());
		}
	}
	
	public Object getUlbTypeFromMdms(RequestInfo requestInfo, EdcrRequest edcrRequest) {
		MdmsCriteriaReq mdmsCriteriaReq = getUlbTypeRequest(requestInfo, edcrRequest);

		try {
			return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);

		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			// Handles 4xx and 5xx HTTP errors
			System.err.println("MDMS server error: " + ex.getMessage());
			return Collections.singletonMap("error", "MDMS not responding");

		} catch (ResourceAccessException ex) {
			// Handles connection timeout / unreachable server
			System.err.println("MDMS connection error: " + ex.getMessage());
			return Collections.singletonMap("error", "MDMS not responding");

		} catch (Exception ex) {
			// Any other unexpected error
			System.err.println("Unexpected error during MDMS call: " + ex.getMessage());
			return Collections.singletonMap("error", ex.getMessage());
		}
	}

	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(mdmsConfiguration.getMdmsHost()).append(mdmsConfiguration.getMdmsSearchUrl());
	}
	
	public StringBuilder getUserSearchUrl() {
		return new StringBuilder().append(userServiceHost).append(userServiceUrl);
	}

	public StringBuilder getBPASearchUrl() {
		return new StringBuilder().append(bpaServiceHost).append(bpaServiceUrl);
	}
	
	public List<ModuleDetail> getBPAFARModuleRequest() {
		List<MasterDetail> bpaMasterDtls = new ArrayList<>();
		final String filterCode = "$.[?(@.active==true)].code";
		MasterDetail masterDetailAppType = new MasterDetail();
		masterDetailAppType.setName("ApplicationType");
		masterDetailAppType.setFilter(filterCode);
		bpaMasterDtls.add(masterDetailAppType);

//        MasterDetail masterDetailServicetype = new MasterDetail();
//        masterDetailServicetype.setName("ServiceType");
//        masterDetailServicetype.setFilter(filterCode);
//        bpaMasterDtls.add(masterDetailServicetype);

//        MasterDetail masterDetailOccupancyType = new MasterDetail();
//        masterDetailOccupancyType.setName("OccupancyType");
//        masterDetailOccupancyType.setFilter("$.[?(@.active==true)]");
//        bpaMasterDtls.add(masterDetailOccupancyType);

//        MasterDetail masterDetailSubOccupancyType = new MasterDetail();
//        masterDetailSubOccupancyType.setName("SubOccupancyType");
//        masterDetailSubOccupancyType.setFilter("$.[?(@.active==true)]");
//        bpaMasterDtls.add(masterDetailSubOccupancyType);

		MasterDetail masterDetailEdcrMdmsFeature = new MasterDetail();
		masterDetailEdcrMdmsFeature.setName("MasterPlan");
		bpaMasterDtls.add(masterDetailEdcrMdmsFeature);

		MasterDetail masterDetailUsages = new MasterDetail();
		masterDetailUsages.setName("Usages");
		masterDetailUsages.setFilter("$.[?(@.active==true)]");
		bpaMasterDtls.add(masterDetailUsages);

		/*
		 * MasterDetail masterDetailSubfeatureColorCode = new MasterDetail();
		 * masterDetailSubfeatureColorCode.setName("SubFeatureColorCode");
		 * masterDetailSubfeatureColorCode.setFilter("$.*");
		 * bpaMasterDtls.add(masterDetailSubfeatureColorCode);
		 */

		ModuleDetail bpaModuleDtls = new ModuleDetail();
		bpaModuleDtls.setMasterDetails(bpaMasterDtls);
		bpaModuleDtls.setModuleName("BPA");
		return Arrays.asList(bpaModuleDtls);
	}

	public List<ModuleDetail> getMDMSModuleRequest1(String code, String scheme, String fromFY, Long currentDate,
			String occType, BigDecimal plotArea) {

		List<MasterDetail> bpaMasterDtls = new ArrayList<>();

// ApplicationType Master
		MasterDetail masterDetailAppType = new MasterDetail();
		masterDetailAppType.setName("ApplicationType");
		masterDetailAppType.setFilter("$.[?(@.active==true)].code");
		bpaMasterDtls.add(masterDetailAppType);

// === Build dynamic JSONPath filter for MasterPlan ===
		StringBuilder filterBuilder = new StringBuilder("$.[?(@.active==true");

		if (code != null && !code.isEmpty()) {
			filterBuilder.append(" && @.code=='").append(code).append("'");
		}
		if (scheme != null && !scheme.isEmpty()) {
			filterBuilder.append(" && @.Scheme=='").append(scheme).append("'");
		}
		if (fromFY != null && !fromFY.isEmpty()) {
			filterBuilder.append(" && @.fromFY=='").append(fromFY).append("'");
		}
		if (currentDate != null) {
			filterBuilder.append(" && @.startingDate<=").append(currentDate).append(" && @.endingDate>=")
					.append(currentDate);
		}

// Close MasterPlan condition
		filterBuilder.append(")]");

// === Nested filter for OccType based on OccupancyType and Plot Area ===
		if (occType != null && !occType.isEmpty() && plotArea != null && plotArea.compareTo(BigDecimal.ZERO) > 0) {
		    filterBuilder.append(".OccType[?(@.OccupancyType=='")
		            .append(occType)
		            .append("' && @.minPlotArea<=")
		            .append(plotArea.toPlainString()) // ✅ Safe BigDecimal to numeric string
		            .append(" && @.maxPlotArea>=")
		            .append(plotArea.toPlainString())
		            .append(")]");
		} else if (occType != null && !occType.isEmpty()) {
			filterBuilder.append(".OccType[?(@.OccupancyType=='").append(occType).append("')]");
		}

// Final Filter String
		String finalFilter = filterBuilder.toString();

// MasterPlan Master
		MasterDetail masterDetailMasterPlan = new MasterDetail();
		masterDetailMasterPlan.setName("MasterPlan");
		masterDetailMasterPlan.setFilter(finalFilter);
		bpaMasterDtls.add(masterDetailMasterPlan);

// Usages Master
		MasterDetail masterDetailUsages = new MasterDetail();
		masterDetailUsages.setName("Usages");
		masterDetailUsages.setFilter("$.[?(@.active==true)]");
		bpaMasterDtls.add(masterDetailUsages);

// Module Detail
		ModuleDetail bpaModuleDtls = new ModuleDetail();
		bpaModuleDtls.setMasterDetails(bpaMasterDtls);
		bpaModuleDtls.setModuleName("EDCR");

		return Arrays.asList(bpaModuleDtls);
	}
	
	public List<ModuleDetail> getUlbTypeRequest(String loggedInUlb) {

		List<MasterDetail> bpaMasterDtls = new ArrayList<>();
		String filterBuilder = new String("$.[?(@.code == '%s')].city['ulbType','districtName']");
		filterBuilder = filterBuilder.format(filterBuilder, loggedInUlb);
		
		MasterDetail masterDetailAppType = new MasterDetail();
		masterDetailAppType.setName("tenants");
		masterDetailAppType.setFilter(filterBuilder);
		bpaMasterDtls.add(masterDetailAppType);

		ModuleDetail bpaModuleDtls = new ModuleDetail();
		bpaModuleDtls.setMasterDetails(bpaMasterDtls);
		bpaModuleDtls.setModuleName("tenant");

		return Arrays.asList(bpaModuleDtls);
	}

	public static Map<String, List<Map<String, Object>>> mdmsResponseMapper(Object mdmsData,
			String jsonPathExpression) {
		if (mdmsData == null || jsonPathExpression == null || jsonPathExpression.isEmpty()) {
			return Collections.emptyMap();
		}

		try {
			Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL,
					Option.SUPPRESS_EXCEPTIONS);

			Object data = JsonPath.using(conf).parse(mdmsData).read(jsonPathExpression);

			// Extract the last part of the JsonPath (e.g. FAR, Category, NormalFAR)
			String key = extractLastKey(jsonPathExpression);

			Map<String, List<Map<String, Object>>> result = new HashMap<>();

			if (data instanceof List) {
				for (Object obj : (List<?>) data) {
					if (obj instanceof Map) {
						result.computeIfAbsent(key, k -> new ArrayList<>()).add((Map<String, Object>) obj);
					} else {
						result.computeIfAbsent(key, k -> new ArrayList<>()).add(Collections.singletonMap("value", obj));
					}
				}
			} else if (data instanceof Map) {
				result.put(key, new ArrayList<>(Collections.singletonList((Map<String, Object>) data)));
			} else if (data != null) {
				result.put(key, new ArrayList<>(Collections.singletonList(Collections.singletonMap("value", data))));
			}

			return result;

		} catch (Exception e) {
			System.err.println("MDMS parsing failed: " + e.getMessage());
			return Collections.emptyMap();
		}
	}

//	// ✅ Utility method to extract last key from JSONPath expression
//	private static String extractLastKey(String jsonPath) {
//		if (jsonPath == null || jsonPath.trim().isEmpty())
//			return "unknown";
//		String[] parts = jsonPath.split("\\.");
//		return parts[parts.length - 1].replaceAll("[\\[\\]\\*]", ""); // remove [*] and special chars
//	}
	
	private static String extractLastKey(String jsonPath) {
	    if (jsonPath == null) return "value";
	    String cleaned = jsonPath.replaceAll("[\\[\\]']", "");
	    String[] parts = cleaned.split("\\.");

	    return parts.length > 0 ? parts[parts.length - 1] : "value";
	}

	
	public static <T> Optional<T> extractMdmsValue(Object mdmsData, String jsonPathExpression, Class<T> targetType) {
        if (mdmsData == null) {
            return Optional.empty();
        }

        try {
            T result = JsonPath.using(JACKSON_CONFIG)
                               .parse(mdmsData) 
                               .read(jsonPathExpression, targetType);
            
            return Optional.ofNullable(result);
            
        } catch (PathNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error extracting value with path " + jsonPathExpression + ": " + e.getMessage());
            return Optional.empty();
        }
    }
	
//	public static Optional<BigDecimal> findSetbackValueByHeight(
//            List<Map<String, Object>> frontSetBacks, BigDecimal targetHeight) {
//
//        if (frontSetBacks == null || frontSetBacks.isEmpty() || targetHeight == null) {
//            return Optional.empty();
//        }
//
//        // 1. Find the SMALLEST height entry (the correct rule) that is >= targetHeight.
//        Optional<Map<String, Object>> exactOrNextRuleOpt = frontSetBacks.stream()
//                .filter(map -> map.get("height") instanceof BigDecimal) // Filter out bad data
//                .filter(map -> {
//                    BigDecimal heightBd = (BigDecimal) map.get("height");
//                    // Filter: Keep only entries where heightBd >= targetHeight
//                    return heightBd.compareTo(targetHeight) >= 0;
//                })
//                // Sort by height ascending to find the smallest matching rule
//                .sorted(Comparator.comparing(map -> (BigDecimal) map.get("height")))
//                .findFirst();
//
//        // 2. If an appropriate rule was found (i.e., targetHeight is within the table range)
//        if (exactOrNextRuleOpt.isPresent()) {
//            return exactOrNextRuleOpt.map(map -> (BigDecimal) map.get("setback"));
//        }
//
//        // 3. If NO rule was found, the targetHeight must be GREATER THAN ALL DEFINED HEIGHTS.
//        // In this case, the setback should be based on the MAXIMUM defined height rule.
//        
//        // Find the maximum defined height in the table
//        Optional<Map<String, Object>> maxRuleOpt = frontSetBacks.stream()
//                .filter(map -> map.get("height") instanceof BigDecimal)
//                // Sort by height descending to find the maximum rule
//                .sorted(Comparator.comparing((Map<String, Object> map) -> (BigDecimal) map.get("height")).reversed())
//                .findFirst();
//
//        // If a max rule exists, return its setback value.
//        return maxRuleOpt.map(map -> (BigDecimal) map.get("setback"));
//    }
	
//	public static Optional<BigDecimal> findSetbackValueByHeight(
//            List<Map<String, Object>> frontSetBacks, BigDecimal targetHeight) {
//
//        if (frontSetBacks == null || frontSetBacks.isEmpty() || targetHeight == null) {
//            return Optional.empty();
//        }
//
//        // 1. FIRST PASS: Find the SMALLEST height entry (the correct rule) that is >= targetHeight.
//        Optional<Map<String, Object>> exactOrNextRuleOpt = frontSetBacks.stream()
//                // Filter 1A: Ensure the height object is BigDecimal (data consistency check)
//                .filter(map -> map.get("height") instanceof BigDecimal) 
//                
//                // Filter 1B: Keep rules where the rule height is greater than or equal to the target height
//                .filter(map -> {
//                    BigDecimal heightBd = (BigDecimal) map.get("height");
//                    // heightBd >= targetHeight (compareTo returns 0 or 1)
//                    return heightBd.compareTo(targetHeight) >= 0; 
//                })
//                // Sort by height ascending to find the smallest matching rule (e.g., 35 for 30.63)
//                .sorted(Comparator.comparing(map -> (BigDecimal) map.get("height")))
//                .findFirst();
//
//        // 2. If a rule was found (i.e., targetHeight is within or below the max rule)
//        if (exactOrNextRuleOpt.isPresent()) {
//            // Success: Return the setback value for that rule (e.g., 11 for height 35)
//            return exactOrNextRuleOpt.map(map -> (BigDecimal) map.get("setback"));
//        }
//
//        // 3. FALLBACK: The targetHeight must be GREATER THAN ALL DEFINED HEIGHTS (e.g., 60.00).
//        // In this case, the setback should be based on the MAXIMUM defined height rule (e.g., 55).
//        
//        // Find the maximum defined height rule (highest height, largest setback)
//        Optional<Map<String, Object>> maxRuleOpt = frontSetBacks.stream()
//                .filter(map -> map.get("height") instanceof BigDecimal) // Filter bad data
//                // Sort by height descending to find the maximum rule
//                .sorted(Comparator.comparing((Map<String, Object> map) -> (BigDecimal) map.get("height")).reversed())
//                .findFirst();
//
//        // If a max rule exists, return its setback value.
//        // If the list was empty or malformed, this will still be Optional.empty().
//        return maxRuleOpt.map(map -> (BigDecimal) map.get("setback"));
//    }
	 public static Optional<BigDecimal> findSetbackValueByHeight(
	            List<Map<String, Object>> frontSetBacks, BigDecimal targetHeight) {

	        if (frontSetBacks == null || frontSetBacks.isEmpty() || targetHeight == null) {
	            return Optional.empty();
	        }

	        return frontSetBacks.stream()
	                // 1. Filter: height >= targetHeight
	                .filter(map -> {
	                    Object heightObj = map.get("height");
	                    BigDecimal height = toBigDecimal(heightObj);
	                    return height != null && height.compareTo(targetHeight) >= 0;
	                })
	                // 2. Sort by height ascending → smallest height >= targetHeight
	                .sorted(Comparator.comparing(map -> toBigDecimal(map.get("height"))))
	                // 3. Take first
	                .findFirst()
	                // 4. Extract setback as BigDecimal
	                .map(map -> toBigDecimal(map.get("setback")));
	    }

	    private static BigDecimal toBigDecimal(Object value) {
	        if (value == null) return null;

	        if (value instanceof BigDecimal) return (BigDecimal) value;

	        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());

	        try {
	            return new BigDecimal(value.toString());
	        } catch (Exception e) {
	            return null; // invalid number
	        }
	    }


    /** Extracts a direct (non-list) key from a JSON Map */
    private static Object extractSingleValue(Object data, String key) {
        if (!(data instanceof Map)) return null;
        Map<?, ?> map = (Map<?, ?>) data;
        return map.get(key);
    }

}
