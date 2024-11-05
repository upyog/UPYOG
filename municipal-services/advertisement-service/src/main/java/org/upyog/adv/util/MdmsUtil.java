package org.upyog.adv.util;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.web.models.CalculationType;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private ObjectMapper mapper;


	private static Object mdmsMap = null;
	private static List<TaxHeadMaster> headMasters = null;

	

	/*
	 * @Autowired private MDMSClient mdmsClient;
	 */

	@Autowired
	public MdmsUtil(BookingConfiguration config, ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		Object result = null;
		if(mdmsMap == null) {
			result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
			setMDMSDataMap(result);
		} else {
			result = getMDMSDataMap();
		}

		// Object result = mdmsClient.getMDMSData(mdmsCriteriaReq);
		// log.info("Master data fetched from MDMSfrom feign client : " + result);
		
		return result;
	}

	
	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsPath());
	}

	/**
	 * prepares the mdms request object
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getADVModuleRequest();

		log.info("Module details data needs to be fetched from MDMS : " + moduleRequest);

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		return mdmsCriteriaReq;
	}

	/**
	 * Creates request to search ApplicationType and etc from MDMS
	 * 
	 * @param requestInfo The requestInfo of the request
	 * @param tenantId    The tenantId of the CHB
	 * @return request to search ApplicationType and etc from MDMS
	 */
	public List<ModuleDetail> getADVModuleRequest() {

		// master details for CHB module
		List<MasterDetail> advMasterDtls = new ArrayList<>();

		// filter to only get code field from master data
		final String filterCode = "$.[?(@.active==true)].code";

		advMasterDtls
				.add(MasterDetail.builder().name(BookingConstants.ADD_TYPE).filter(filterCode).build());

		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.LOCATION)
				.filter(filterCode).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.FACE_AREA)
				.filter(filterCode).build());
		
		advMasterDtls.add(
				MasterDetail.builder().name(BookingConstants.DOCUMENTS).filter(filterCode).build());

		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(advMasterDtls)
				.moduleName(config.getModuleName()).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(BookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);

	}

	public static void setMDMSDataMap(Object mdmsDataMap) {
		mdmsMap = mdmsDataMap;
	}

	public static Object getMDMSDataMap() {
		return mdmsMap;
	}
	
	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 */
	
	public List<TaxHeadMaster> getTaxHeadMasterList(RequestInfo requestInfo, String tenantId, String moduleName) {
		if(headMasters != null) {
			log.info("Returning cached value of tax head masters");
			return headMasters;
		}
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName);

		try {
			MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);
			
			JSONArray jsonArray = mdmsResponse.getMdmsRes().get("BillingService").get("TaxHeadMaster");
			
			headMasters = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, TaxHeadMaster.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting tax haead master list : " + e);
		} 

		return headMasters;
	}

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName,CartDetail cartDetail) {
		String faceArea = "";
		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);
		if(mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Advertisement booking Fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName()).get(getCalculationTypeMasterName());
		
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(jsonArray.toJSONString());
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		faceArea = cartDetail.getFaceArea();
		faceArea = faceArea.replace(" ", "_");
		JsonNode nestedArray = rootNode.get(0).get("CalculationType_" + faceArea);
       
		try {
		    // Convert nested array to List<CalculationType>
		    calculationTypes = mapper.readValue(nestedArray.toString(),
		            mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
		    log.info("Exception occurred while converting calculation type: " + e);
		}


		return calculationTypes;
		
	}
	
	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private MdmsCriteriaReq getMdmsRequestTaxHeadMaster(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName("TaxHeadMaster");
		masterDetail.setFilter("$.[?(@.service=='adv-services')]");
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
	
	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

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
	
	//Returns the Master Name Calculation Type
	private String getCalculationTypeMasterName() {
		return BookingConstants.ADV_CALCULATION_TYPE;
	}
	
	}