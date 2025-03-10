package org.upyog.cdwm.calculator.util;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.repository.ServiceRequestRepository;
import org.upyog.cdwm.calculator.web.models.CalculationType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CalculatorConfig config;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
	@Autowired
	private ObjectMapper mapper;


	public List<CalculationType> getCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, "pg", moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);

		if (mdmsResponse.getMdmsRes().get(CalculatorConstants.MDMS_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Cnd vehicle fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(CalculatorConstants.MDMS_MODULE_NAME)
				.get(CalculatorConstants.MDMS_VEHICLE_TYPE);

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  forcnd request: " + e);
		}

		return calculationTypes;

	}

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CalculatorConstants.MDMS_VEHICLE_TYPE);
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
	

}