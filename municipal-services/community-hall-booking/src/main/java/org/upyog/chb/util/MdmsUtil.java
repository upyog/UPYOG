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
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

/**
 * This utility class provides methods for interacting with the MDMS (Master Data Management System)
 * to fetch and process master data required for the Community Hall Booking module.
 * 
 * Purpose:
 * - To retrieve master data such as calculation types, tax head masters, and other configurations.
 * - To simplify the process of creating MDMS requests and handling responses.
 * 
 * Dependencies:
 * - ServiceRequestRepository: Sends HTTP requests to the MDMS service.
 * - CommunityHallBookingConfiguration: Provides configuration properties for MDMS operations.
 * - ObjectMapper: Serializes and deserializes JSON objects for requests and responses.
 * 
 * Features:
 * - Constructs MDMS requests with the required criteria and module details.
 * - Sends requests to the MDMS service and processes the responses.
 * - Handles exceptions and logs errors for debugging and monitoring purposes.
 * 
 * Fields:
 * - mdmsHost: The base URL of the MDMS service.
 * - mdmsPath: The endpoint path for MDMS requests.
 * 
 * Methods:
 * 1. fetchMdmsData:
 *    - Sends a request to the MDMS service to fetch master data based on the provided criteria.
 *    - Processes the response and returns the required master data.
 * 
 * 2. getCalculationTypes:
 *    - Retrieves calculation types from the MDMS service for use in demand generation.
 * 
 * 3. getTaxHeadMasters:
 *    - Fetches tax head masters from the MDMS service for financial calculations.
 * 
 * Usage:
 * - This class is used throughout the module to fetch and process master data from MDMS.
 * - It ensures consistent and reusable logic for MDMS interactions across the application.
 */
@Slf4j
@Component
public class MdmsUtil {

	private final CommunityHallBookingConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;

	private static Object mdmsMap = null;

	public MdmsUtil(CommunityHallBookingConfiguration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	/**
	 * Makes an MDMS call using the provided request information and tenant ID.
	 * Constructs the criteria request specific to general MDMS data and fetches the results.
	 * * @param requestInfo The authentication and metadata information for the request.
	 * @param tenantId    The unique identifier of the tenant.
	 * @return The MDMS data object fetched from the repository or cache.
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		return mDMSCommonCall(mdmsCriteriaReq);
	}
	
	/**
	 * Makes an MDMS call specifically for Venue Type configurations using the provided 
	 * request information and tenant ID.
	 * * @param requestInfo The authentication and metadata information for the request.
	 * @param tenantId    The unique identifier of the tenant.
	 * @return The MDMS Venue Type data object fetched from the repository or cache.
	 */
	public Object mDMSVenueTypeCall(RequestInfo requestInfo , String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSVenueTypeRequest(requestInfo, tenantId);
		return mDMSCommonCall(mdmsCriteriaReq);
	}
	
	/**
	 * Executes the common MDMS API call logic. It logs the request payload, 
	 * and serves the data from an in-memory map cache if available; 
	 * otherwise, it fetches fresh data from the remote MDMS repository.
	 * * @param mdmsCriteriaReq The prepared MDMS criteria request payload wrapper.
	 * @return The raw Master Data Management System (MDMS) response object.
	 */
	public Object mDMSCommonCall(MdmsCriteriaReq mdmsCriteriaReq) {
		try {
			log.debug("MDMS request payload: {}",
					mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mdmsCriteriaReq));
		} catch (JsonProcessingException e) {
			log.warn("Failed to serialize MDMS request for logging", e);
		}
		Object result;
		if (mdmsMap == null) {
			result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
			setMDMSDataMap(result);
		} else {
			result = getMDMSDataMap();
		}

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
	 * Prepares a common MDMS criteria request object by combining the specified 
	 * module details, tenant ID, and request metadata.
	 * * @param moduleRequest The list of modules and their specific master data details to fetch.
	 * @param tenantId      The unique identifier of the tenant.
	 * @param requestInfo   The authentication and metadata information for the request.
	 * @return A constructed {@link MdmsCriteriaReq} instance populated with the criteria.
	 */
	public MdmsCriteriaReq getCommonMDMSRequest(List<ModuleDetail> moduleRequest,String tenantId,RequestInfo requestInfo) {
		log.info("Module details data needs to be fetched from MDMS : " + moduleRequest);

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
	}

	/**
	 * Constructs the standard MDMS request for general CHB (Community Hall Booking) modules.
	 * It automatically retrieves the default CHB module configurations before building the request.
	 * * @param requestInfo The authentication and metadata information for the request.
	 * @param tenantId    The unique identifier of the tenant.
	 * @return A configured {@link MdmsCriteriaReq} instance for general CHB data.
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getCHBModuleRequest();

		return getCommonMDMSRequest(moduleRequest, tenantId,requestInfo);
	}
	
	/**
	 * Constructs the MDMS request specifically for CHB Venue Type configurations.
	 * It retrieves the dedicated Venue Type module requirements before building the request.
	 * * @param requestInfo The authentication and metadata information for the request.
	 * @param tenantId    The unique identifier of the tenant.
	 * @return A configured {@link MdmsCriteriaReq} instance tailored for Venue Type data.
	 */
	public MdmsCriteriaReq getMDMSVenueTypeRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getCHBVenueTypeModuleRequest();

		return getCommonMDMSRequest(moduleRequest, tenantId,requestInfo);
	}

	/**
	 * Generates the specific list of {@link ModuleDetail} objects required to fetch 
	 * Venue Type data and Common Masters data for the Community Hall Booking system.
	 * * @return A {@link List} of configured {@link ModuleDetail} objects representing the requested masters.
	 */
	public List<ModuleDetail> getCHBVenueTypeModuleRequest() {

		// master details for CHB module
		List<MasterDetail> chbMasterDtls = new ArrayList<>();

		
		chbMasterDtls
				.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_VENUE_TYPE_DATA).build());


		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(chbMasterDtls)
				.moduleName(config.getModuleName()).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(CommunityHallBookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);

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
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_GUEST_HOUSE_CODES)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_GUEST_HOUSES)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_STADIUM_CODES)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_STADIUMS)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_PARK_CODES)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_PARKS)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_CREMATORIUMS)
				.filter(filterCode).build());
		

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_CREMATORIUM_CODES)
				.filter(filterCode).build());
		
		
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_HALL_CODES)
				.filter(filterCode).build());
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
		List<TaxHeadMaster> headMasters = null;

		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		String filter = "$.[?(@.service=='chb-services')]";

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName,
				"TaxHeadMaster", filter);

		try {
			MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
					MdmsResponse.class);

			JSONArray jsonArray = mdmsResponse.getMdmsRes().get("BillingService").get("TaxHeadMaster");

			headMasters = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, TaxHeadMaster.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting tax haead master list : " + e);
		}

		return headMasters;
	}

	public List<CalculationType> getTaxRatesMasterList(RequestInfo requestInfo, String tenantId, String moduleName) {
		List<CalculationType> taxRates = null;
		String taxRatesMasterName = "TaxRates";

		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName,
				taxRatesMasterName, null);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
				MdmsResponse.class);
		if (mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
			throw new CustomException("TAX_NOT_AVAILABLE", "Community Hall Tax Rates not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName()).get(taxRatesMasterName);

		try {
			taxRates = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
			log.info("tax rates : " + taxRates);
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting tax rates : " + e);
		}

		return taxRates;

	}

	private MdmsCriteriaReq getMdmsRequestTaxHeadMaster(RequestInfo requestInfo, String tenantId, String moduleName,
			String masterName, String filter) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(masterName);
		if (null != filter) {
			masterDetail.setFilter(filter);
		}
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