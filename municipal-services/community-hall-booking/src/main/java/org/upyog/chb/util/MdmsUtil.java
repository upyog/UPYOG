package org.upyog.chb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private ObjectMapper mapper;


	private static Object mdmsMap = null;

	/*
	 * @Autowired private MDMSClient mdmsClient;
	 */

	@Autowired
	public MdmsUtil(CommunityHallBookingConfiguration config, ServiceRequestRepository serviceRequestRepository) {
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
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		
		if(mdmsMap == null) {
			setMDMSDataMap(result);// = ;
		}
		
		
		log.info("Calculation data for CHB booking :" + 
				JsonPath.read(result,
						CommunityHallBookingConstants.CHB_JSONPATH_CODE +  "." + CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));

		// Object result = mdmsClient.getMDMSData(mdmsCriteriaReq);
		// log.info("Master data fetched from MDMSfrom feign client : " + result);
		
		return result;
	}

	
	
	/**
	 * Fetch data from mdms for mdms criteria request {@code mdmsCriteriaReq}
	 */
	public Object mDMSCall(MdmsCriteriaReq mdmsCriteriaReq) {
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
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
		List<ModuleDetail> moduleRequest = getCHBModuleRequest();

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
	public List<ModuleDetail> getCHBModuleRequest() {

		// master details for CHB module
		List<MasterDetail> chbMasterDtls = new ArrayList<>();

		// filter to only get code field from master data
		final String filterCode = "$.[?(@.active==true)].code";

		chbMasterDtls
				.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_PURPOSE).filter(filterCode).build());

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_SPECIAL_CATEGORY)
				.filter(filterCode).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_CALCULATION_TYPE).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_COMMNUITY_HALLS)
				.filter(filterCode).build());
		chbMasterDtls.add(
				MasterDetail.builder().name(CommunityHallBookingConstants.CHB_HALL_CODES).filter("$.[?(@.active==true)].HallCode").build());
		chbMasterDtls.add(
				MasterDetail.builder().name(CommunityHallBookingConstants.CHB_DOCUMENTS).filter(filterCode).build());

		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(chbMasterDtls)
				.moduleName(config.getModuleName()).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(CommunityHallBookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);

	}

	public static void setMDMSDataMap(Object mdmsDataMap) {
		log.info("Calculkation data for CHB booking :" + 
				JsonPath.read(mdmsDataMap,
						CommunityHallBookingConstants.CHB_JSONPATH_CODE +  "." + CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));
		mdmsMap = mdmsDataMap;
	}

	public static Object getMDMSDataMap() {
		return mdmsMap;
	}
	
	
	public List<TaxHeadMaster> getTaxHeadMasterList(RequestInfo requestInfo, String tenantId, String moduleName) {
		List<TaxHeadMaster> taxHeadMasters = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName);
		//Can create filter as string using this
		//Filter masterDataFilter = filter(where(CommunityHallBookingConstants.SERVICE).is(config.getBusinessServiceName()));

		try {
			Object response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
			org.upyog.chb.web.models.TaxHeadMasterResponse res = mapper.convertValue(response,
					org.upyog.chb.web.models.TaxHeadMasterResponse.class);
			log.info("getTaxHeadMasterList : " +JsonPath.parse(response).read("$.MdmsRes.BillingService.TaxHeadMaster"));
			
			//taxHeadMasters =  JsonPath.parse(response).read("$.MdmsRes.BillingService.TaxHeadMaster");
			Map<String, Object> map=   JsonPath.parse(response).read("$.MdmsRes.BillingService");
			
		//	JSONArray jsonArray = (JSONArray) map.get("TaxHeadMaster");
			
			taxHeadMasters = mapper.readValue((JsonParser) map.get("TaxHeadMaster"),
					mapper.getTypeFactory().constructCollectionType(List.class, TaxHeadMaster.class));
			log.info("taxHeadMasters : " + map);
		} catch (Exception e) {
			log.error("Exception while fetching workflow states to ignore: ", e);
		}

		return taxHeadMasters;
	}

	private MdmsCriteriaReq getMdmsRequestTaxHeadMaster(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName("TaxHeadMaster");
		masterDetail.setFilter("$.[?(@.service=='chb-services')]");
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(CommunityHallBookingConstants.BILLING_SERVICE);
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

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String billingService) {
		List<CalculationType> calculationTypes = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, config.getModuleName());
		//Can create filter as string using this
		//Filter masterDataFilter = filter(where(CommunityHallBookingConstants.SERVICE).is(config.getBusinessServiceName()));

		try {
			Object response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
			log.info("getTaxHeadMasterList : " +JsonPath.parse(response).read("$.MdmsRes.CHB.CalculationType"));
			
			calculationTypes = JsonPath.parse(response).read("$.MdmsRes.CHB.CalculationType");
		//	taxHeadMasters = mapper.readValue(array,
		//			mapper.getTypeFactory().constructCollectionType(List.class, TaxHeadMaster.class));
			log.info("CalculationType : " + calculationTypes);
		} catch (Exception e) {
			log.error("Exception while fetching workflow states to ignore: ", e);
		}

		return calculationTypes;
		
	}
	
	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CommunityHallBookingConstants.CHB_CALCULATION_TYPE);
	//	masterDetail.setFilter("$.[?(@.service=='chb-services')]");
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