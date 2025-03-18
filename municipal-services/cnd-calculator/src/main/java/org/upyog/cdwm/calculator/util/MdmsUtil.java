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

/**
 * Utility class for fetching calculation types from MDMS.
 */

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

	/**
     * Fetches the list of CalculationType from MDMS based on the givens module name and tenant ID.
     * 
     * @param requestInfo The request information containing metadata.
     * @param tenantId The tenant ID for which the calculation types are required.
     * @param moduleName The module name under which calculation types are defined.
     * @return List of CalculationType retrieved from MDMS.
     */

	public List<CalculationType> getCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, "pg", moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);

		if (mdmsResponse.getMdmsRes().get(CalculatorConstants.MDMS_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Cnd Ton fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(CalculatorConstants.MDMS_MODULE_NAME)
				.get(CalculatorConstants.MDMS_CALCULATION_TYPE);

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  forcnd request: " + e);
		}

		return calculationTypes;

	}
	
	/**
     * Constructs an MDMS request payload for fetching calculation types.
     * 
     * @param requestInfo The request information.
     * @param tenantId The tenant ID.
     * @param moduleName The module name.
     * @return An instance of MdmsCriteriaReq containing the MDMS request details.
     */

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CalculatorConstants.MDMS_CALCULATION_TYPE);
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