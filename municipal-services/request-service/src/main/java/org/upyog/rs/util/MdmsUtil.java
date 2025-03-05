package org.upyog.rs.util;

import java.util.ArrayList;
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
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.web.models.billing.CalculationType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RequestServiceConfiguration config;

	public List<CalculationType> getCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(restRepo.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);

		if (mdmsResponse.getMdmsRes().get(RequestServiceConstants.MDMS_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Water Tank fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(RequestServiceConstants.MDMS_MODULE_NAME)
				.get(RequestServiceConstants.MDMS_TANKER_CALCULATION_TYPE);

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  for tanker request: " + e);
		}

		return calculationTypes;

	}

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(RequestServiceConstants.MDMS_TANKER_CALCULATION_TYPE);
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
	
	public List<CalculationType> getMTCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMTMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(restRepo.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);

		if (mdmsResponse.getMdmsRes().get(RequestServiceConstants.MDMS_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Mobile Toilet fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(RequestServiceConstants.MDMS_MODULE_NAME)
				.get(RequestServiceConstants.MDMS_MOBILE_TOILET_CALCULATION_TYPE);

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  for tanker request: " + e);
		}

		return calculationTypes;

	}

	private MdmsCriteriaReq getMTMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(RequestServiceConstants.MDMS_MOBILE_TOILET_CALCULATION_TYPE);
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

	/*********************** MDMS Utitlity Methods *****************************/

	/**
	 * Fetches all the values of particular attribute as map of fieldname to list
	 *
	 * @param tenantId    tenantId from water tanker request
	 * @param names       List of String containing the names of all masterdata
	 *                    whose code has to be extracted
	 * @param requestInfo RequestInfo of the received water tanker request
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	public Map<String, List<Map<String, Object>>> getAttributeValues(String tenantId, String moduleName,
			List<String> names, String filter, String jsonpath, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder(config.getMdmsHost()).append(config.getMdmsEndpoint());
		MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
		Object response = restRepo.fetchResult(uri, criteriaReq);

		try {
			if (response != null) {
				return JsonPath.read(response, jsonpath);
			}
		} catch (Exception e) {
			throw new CustomException(RequestServiceConstants.INVALID_TENANT_ID_MDMS_KEY,
					RequestServiceConstants.INVALID_TENANT_ID_MDMS_MSG);
		}

		return null;
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {

		List<MasterDetail> masterDetails = new ArrayList<>();

		names.forEach(name -> {
			masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
		});

		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

}