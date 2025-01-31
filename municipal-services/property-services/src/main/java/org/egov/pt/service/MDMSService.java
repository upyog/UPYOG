package org.egov.pt.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.util.PTConstants;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MDMSService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PropertyConfiguration propertyConfiguration;

	public MdmsResponse getMdmsMasterData(RequestInfo requestInfo, List<ModuleDetail> moduleDetails) {

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(propertyConfiguration.getStateLevelTenantId())
				.moduleDetails(moduleDetails).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria)
				.build();

		MdmsResponse response = null;

		try {
			response = restTemplate.postForObject(getMdmsSearchUrl().toString(), mdmsCriteriaReq, MdmsResponse.class);
		} catch (HttpClientErrorException e) {
			log.error("External Service threw an Exception: ", e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("Exception while fetching from searcher: ", e);
		}

		return response;
	}

	private StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(propertyConfiguration.getMdmsV2Host())
				.append(propertyConfiguration.getMdmsV2Endpoint());
	}

	public MdmsResponse getULBSMdmsData(RequestInfo requestInfo, String filter) {

		List<ModuleDetail> moduleDetails = getModuleDetails(getULBSMdmsModuleDetails(), filter);
		MdmsResponse mdmsResponse = getMdmsMasterData(requestInfo, moduleDetails);
		return mdmsResponse;
	}

	public List<ModuleDetail> getModuleDetails(Map<String, List<String>> mapOfModulesAndMasters, String filter) {
		List<ModuleDetail> moduleDetails = new ArrayList<>();

		for (String module : mapOfModulesAndMasters.keySet()) {
			ModuleDetail moduleDetail = new ModuleDetail();
			moduleDetail.setModuleName(module);
			List<MasterDetail> masterDetails = new ArrayList<>();
			for (String master : mapOfModulesAndMasters.get(module)) {
				MasterDetail masterDetail = MasterDetail.builder().name(master).filter(filter).build();
				masterDetails.add(masterDetail);
			}
			moduleDetail.setMasterDetails(masterDetails);
			moduleDetails.add(moduleDetail);
		}

		return moduleDetails;
	}

	private Map<String, List<String>> getULBSMdmsModuleDetails() {
		Map<String, List<String>> mapOfModulesAndMasters = new HashMap<>();

		mapOfModulesAndMasters.put(PTConstants.MDMS_MODULE_ULBS, getULBSMdmsMasterDetails());

		return mapOfModulesAndMasters;
	}

	private List<String> getULBSMdmsMasterDetails() {
		return Arrays.asList(PTConstants.MDMS_MASTER_DETAILS_ZONES, PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE,
				PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR,
				PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE, PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE);
	}

}
