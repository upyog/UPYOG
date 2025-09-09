package org.upyog.chb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

/**
 * This utility class provides caching functionality for calculation types
 * used in the Community Hall Booking module.
 * 
 * Purpose:
 * - To cache calculation types for community halls to reduce redundant API calls.
 * - To improve performance by retrieving calculation types from the cache when available.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for API endpoints.
 * - ServiceRequestRepository: Sends HTTP requests to fetch calculation types from external services.
 * - ObjectMapper: Parses JSON responses into Java objects.
 * 
 * Features:
 * - Maintains an in-memory cache of calculation types mapped by hall codes.
 * - Fetches calculation types from external services when not available in the cache.
 * - Logs cache operations and errors for debugging and monitoring purposes.
 * 
 * Fields:
 * - feeTypeCache: A static map that stores calculation types for each hall code.
 * 
 * Methods:
 * 1. getcalculationType:
 *    - Retrieves calculation types for a given hall code from the cache.
 *    - If not available in the cache, fetches the data from an external service and updates the cache.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever caching of calculation types is required.
 * - It ensures consistent and efficient retrieval of calculation types across the module.
 */
@Component
@Slf4j
public class CalculationTypeCache {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	private static Map<String, List<CalculationType>> feeTypeCache = new HashMap<>();

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName,
			CommunityHallBookingDetail bookingDetail) {

		String hallCode = bookingDetail.getCommunityHallCode();

		if (feeTypeCache.isEmpty() || !feeTypeCache.containsKey(hallCode)) {

			List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
			StringBuilder uri = new StringBuilder();
			uri.append(config.getMdmsHost()).append(config.getMdmsPath());

			MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName,
					bookingDetail.getCommunityHallCode());
			
			MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
					MdmsResponse.class);
			if (mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
				throw new CustomException("FEE_NOT_AVAILABLE", "Community Hall Fee not available.");
			}

			JSONArray jsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName())
					.get(getCalculationTypeMasterName());

			JsonNode rootNode = null;
			try {
				rootNode = mapper.readTree(jsonArray.toJSONString());
			} catch (JsonProcessingException e) {
				log.error("Error parsing CalculationType JSON: ", e);
			}

			if (rootNode != null) {
				try {
					calculationTypes = mapper.readValue(rootNode.toString(),
							mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
					log.info("calculationTypes : {}", calculationTypes);
					if (!CollectionUtils.isEmpty(calculationTypes)) {
						feeTypeCache = calculationTypes.stream()
								.collect(Collectors.groupingBy(CalculationType::getCommunityHallCode, // Key: Community Hall code
										Collectors.toList() // Value: List of CalculationType objects
								));
					}

				} catch (JsonProcessingException e) {
					log.error("Error converting calculation types: ", e);
				
				}
			}
			log.info("Loaded calculation type data for all hall codes : " + feeTypeCache);
		}

		log.info("Calculation type for hall code : {} is : {}", hallCode, feeTypeCache.get(hallCode));

		return feeTypeCache.get(hallCode);

	}

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName,
			String communityHallCode) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(getCalculationTypeMasterName());
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(moduleName);
		List<ModuleDetail> moduleDetailList = new ArrayList<>();
		moduleDetailList.add(moduleDetail);

		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setTenantId(tenantId);
		mdmsCriteria.setModuleDetails(moduleDetailList);

		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);

		return mdmsCriteriaReq;
	}

	private String getCalculationTypeMasterName() {
		return CommunityHallBookingConstants.CHB_CALCULATION_TYPE;
	}

}