package org.upyog.chb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.BookingPurpose;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Component
@Slf4j
public class CalculationTypeCache {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	// Cache keyed by tenantId:hallCode to avoid cross-tenant collisions
	private static Map<String, List<CalculationType>> feeTypeCache = new java.util.concurrent.ConcurrentHashMap<>();

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName,
			CommunityHallBookingDetail bookingDetail) {

	String hallCode = bookingDetail.getCommunityHallCode();
	// Derive functionType from the booking's purpose via MDMS Purpose.type
	// Falls back to null if purpose is not set or has no type — backward compatible
		BookingPurpose bookingPurpose = bookingDetail.getPurpose();
	String purposeCode = bookingPurpose.getPurpose().toString();
	String functionType = resolveFunctionType(requestInfo, tenantId, moduleName, purposeCode);

	String cacheKey = tenantId + ":" + hallCode + (functionType != null ? ":" + functionType : "");

	if (feeTypeCache.isEmpty() || !feeTypeCache.containsKey(cacheKey)) {

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
				for (JsonNode hallNode : rootNode) {
					JsonNode faceAreaNode = hallNode.get(hallCode);
					if (faceAreaNode != null) {
						try {
							List<CalculationType> allTypes = mapper.readValue(faceAreaNode.toString(),
									mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));

							// Filter by functionType when present — backward compatible:
							// if functionType is null OR none of the entries have functionType set,
							// return all entries unchanged.
							if (functionType != null && allTypes.stream().anyMatch(t -> t.getFunctionType() != null)) {
								final String ft = functionType;
								calculationTypes = allTypes.stream()
										.filter(t -> ft.equalsIgnoreCase(t.getFunctionType()))
										.collect(java.util.stream.Collectors.toList());
								log.info("Filtered CalculationType entries by functionType={}: {}", ft, calculationTypes);
							} else {
								calculationTypes = allTypes;
							}

							feeTypeCache.put(cacheKey, calculationTypes);
						} catch (JsonProcessingException e) {
							log.error("Error converting calculation types: ", e);
						}
					}
				}
			}
			log.info("Loaded calculation type data for tenant/hall/functionType [{}]: {}", cacheKey, feeTypeCache.get(cacheKey));
		}

		log.info("Calculation type for key [{}] is: {}", cacheKey, feeTypeCache.get(cacheKey));

		return feeTypeCache.get(cacheKey);

	}

	/**
	 * Fetches the Purpose master from MDMS and returns the 'type' (HAPPY/SAD)
	 * for the given purposeCode. Returns null if purpose has no type or isn't found
	 * — enabling full backward compatibility with tenants that don't use this feature.
	 */
	private String resolveFunctionType(RequestInfo requestInfo, String tenantId,
			String moduleName, String purposeCode) {
		if (purposeCode == null || purposeCode.isEmpty()) return null;
		try {
			StringBuilder uri = new StringBuilder()
					.append(config.getMdmsHost()).append(config.getMdmsPath());

			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName("Purpose");
			List<MasterDetail> masterDetailList = new ArrayList<>();
			masterDetailList.add(masterDetail);

			ModuleDetail moduleDetail = new ModuleDetail();
			moduleDetail.setMasterDetails(masterDetailList);
			moduleDetail.setModuleName(moduleName);

			MdmsCriteria mdmsCriteria = new MdmsCriteria();
			mdmsCriteria.setTenantId(tenantId);
			mdmsCriteria.setModuleDetails(java.util.Arrays.asList(moduleDetail));

			MdmsCriteriaReq req = new MdmsCriteriaReq();
			req.setMdmsCriteria(mdmsCriteria);
			req.setRequestInfo(requestInfo);

			MdmsResponse mdmsResponse = mapper.convertValue(
					serviceRequestRepository.fetchResult(uri, req), MdmsResponse.class);

			if (mdmsResponse == null || mdmsResponse.getMdmsRes() == null
					|| mdmsResponse.getMdmsRes().get(moduleName) == null) return null;

			JSONArray purposeArray = mdmsResponse.getMdmsRes().get(moduleName).get("Purpose");
			if (purposeArray == null) return null;

			JsonNode purposeNode = mapper.readTree(purposeArray.toJSONString());
			for (JsonNode p : purposeNode) {
				// The frontend sends purpose by name (e.g. "Marriage Function"), not code.
				// Match against the 'name' field accordingly.
				JsonNode nameNode = p.get("name");
				JsonNode typeNode = p.get("type");
				if (nameNode != null && purposeCode.equals(nameNode.asText()) && typeNode != null) {
					log.info("Resolved functionType '{}' for purpose '{}'", typeNode.asText(), purposeCode);
					return typeNode.asText();
				}
			}
		} catch (Exception e) {
			log.warn("Could not resolve functionType for purpose '{}': {}", purposeCode, e.getMessage());
		}
		return null;
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
