package org.upyog.chb.util;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	private static List<TaxHeadMaster> headMasters = null;

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
		mdmsMap = mdmsDataMap;
	}

	public static Object getMDMSDataMap() {
		return mdmsMap;
	}
	
	
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

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName, CommunityHallBookingDetail bookingDetail) {
		
		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName, bookingDetail.getCommunityHallCode());
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName()).get(getCalculationTypeMasterName(bookingDetail.getCommunityHallCode()));
		
		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type : " + e);
		} 

		return calculationTypes;
		
	}
	
	private MdmsCriteriaReq getMdmsRequestTaxHeadMaster(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName("TaxHeadMaster");
		masterDetail.setFilter("$.[?(@.service=='chb-services')]");
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
	
	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName, String communityHallCode) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(getCalculationTypeMasterName(communityHallCode));
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
	
	private String getCalculationTypeMasterName(String communityHallCode) {
		return CommunityHallBookingConstants.CHB_CALCULATION_TYPE + '_' + communityHallCode;
	}
}