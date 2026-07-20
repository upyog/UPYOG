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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.VenueBookingDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

/**
 * Caches calculation types fetched from MDMS for community halls.
 */
@Component
@Slf4j
public class CalculationTypeCache {

	private final CommunityHallBookingConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;
	private final Map<String, List<CalculationType>> feeTypeCache = new HashMap<>();

	public CalculationTypeCache(CommunityHallBookingConfiguration config,
			ServiceRequestRepository serviceRequestRepository, ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName,
			VenueBookingDetail bookingDetail) {

		String hallCode = bookingDetail.getVenueCode();

		if (feeTypeCache.isEmpty() || !feeTypeCache.containsKey(hallCode)) {
			StringBuilder uri = new StringBuilder();
			uri.append(config.getMdmsHost()).append(config.getMdmsPath());

			MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
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
					List<CalculationType> calculationTypes = mapper.readValue(rootNode.toString(),
							mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
					log.info("calculationTypes : {}", calculationTypes);
					if (!CollectionUtils.isEmpty(calculationTypes)) {
						feeTypeCache.putAll(calculationTypes.stream()
								.collect(Collectors.groupingBy(CalculationType::getCommunityHallCode)));
					}
				} catch (JsonProcessingException e) {
					log.error("Error converting calculation types: ", e);
				}
			}
			log.info("Loaded calculation type data for all hall codes : {}", feeTypeCache);
		}

		log.info("Calculation type for hall code : {} is : {}", hallCode, feeTypeCache.get(hallCode));
		return feeTypeCache.get(hallCode);
	}

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId,
			String moduleName) {
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
