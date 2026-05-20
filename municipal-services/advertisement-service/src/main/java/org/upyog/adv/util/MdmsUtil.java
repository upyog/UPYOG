package org.upyog.adv.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.upyog.adv.web.models.AdditionalFeeRate;
import org.upyog.adv.web.models.Advertisements;
import org.upyog.adv.web.models.CalculationType;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private static final Map<String, Object> mdmsMapByTenant = new ConcurrentHashMap<>();
	private static final Map<String, List<TaxHeadMaster>> headMastersByTenant = new ConcurrentHashMap<>();
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
		Object result = mdmsMapByTenant.get(tenantId);
		if (result == null) {
			result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
			setMDMSDataMap(tenantId, result);
		}

		// Object result = mdmsClient.getMDMSData(mdmsCriteriaReq);
		// log.info("Master data fetched from MDMSfrom feign client : " + result);

		return result;
	}

	/**
	 * prepares the mdms request object specifically for AutoEscalation master
	 * @param requestInfo
	 * @param tenantId
	 * @return MdmsCriteriaReq
	 */
	public MdmsCriteriaReq getMDMSRequestForAutoEscalationData(RequestInfo requestInfo, String tenantId) {

		// Filter to only active AutoEscalation rows for this module and businessService
		final String filterCode = "$.[?(@.active=='true' && @.module=='" + "advandhoarding-services" + "' && @.businessService=='" + "ADV" + "' )]";

		ModuleDetail moduleDtls = ModuleDetail.builder()
				.masterDetails(Arrays.asList(MasterDetail.builder().name("AutoEscalation").filter(filterCode).build()))
				.moduleName("Workflow").build();

		List<ModuleDetail> moduleRequest = Arrays.asList(moduleDtls);

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();

		return mdmsCriteriaReq;
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
		List<ModuleDetail> moduleRequest = getADVModuleRequest(null);
//		List<ModuleDetail> moduleRequest = getADVModuleRequest("$.[?(@.available==true)].id");

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
	 * @param filter
	 * @return request to search ApplicationType and etc from MDMS
	 */
	public List<ModuleDetail> getADVModuleRequest(String filterCode) {

		// master details for CHB module
		List<MasterDetail> advMasterDtls = new ArrayList<>();

		// filter to only get code field from master data
//		if(filterCode==null) {
//			filterCode = "$.[?(@.active==true)].code";
//		}
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.ADD_TYPE).build());

		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.LOCATION).build());
//		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.FACE_AREA).filter(filterCode).build());
//
//		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.DOCUMENTS).filter(filterCode).build());

		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.ADV_TAX_AMOUNT).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.ADVERTISEMENT).build());

		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(advMasterDtls)
				.moduleName(config.getModuleName()).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(BookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);

	}



	public static void setMDMSDataMap(String tenantId, Object mdmsDataMap) {
		mdmsMapByTenant.put(tenantId, mdmsDataMap);
	}

	public static Object getMDMSDataMap(String tenantId) {
		return mdmsMapByTenant.get(tenantId);
	}

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 */
	public List<TaxHeadMaster> getTaxHeadMasterList(RequestInfo requestInfo, String tenantId, String moduleName) {
		if (headMastersByTenant.containsKey(tenantId)) {
			log.info("Returning cached value of tax head masters");
			return headMastersByTenant.get(tenantId);
		}
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		String filter = "$.[?(@.service=='adv-services')]";

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName,
				"TaxHeadMaster", filter);

		try {
			MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
					MdmsResponse.class);

			JSONArray jsonArray = mdmsResponse.getMdmsRes().get("BillingService").get("TaxHeadMaster");

			List<TaxHeadMaster> headMasters = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, TaxHeadMaster.class));
			headMastersByTenant.put(tenantId, headMasters);
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting tax haead master list : " + e);
			headMastersByTenant.put(tenantId, new ArrayList<>());
		}

		return headMastersByTenant.getOrDefault(tenantId, new ArrayList<>());
	}


	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 *
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public List<Advertisements> getAdvertisements(RequestInfo requestInfo, String tenantId, String moduleName,
												  CartDetail cartDetail) throws JsonProcessingException {
// Chnage in this method to get dayata from advertisement ID
		List<Advertisements> advertisements = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

//		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestAdvertisements(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
				MdmsResponse.class);

		// Check if the response for CalculationType is available
		if (mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Advertisement booking Fee not available.");
		}

		JSONArray objects = mdmsResponse.getMdmsRes().get(config.getModuleName()).get(getAdvertisementsMasterName());

		ObjectMapper mapper = new ObjectMapper();

		List<Advertisements> list1 = mapper.readValue(
				objects.toJSONString(),
				mapper.getTypeFactory().constructCollectionType(List.class, Advertisements.class)
		);
		System.out.println("Using TypeReference: " + list1);

		return list1;
	}

	/**
	 * Fetch advertisements data from MDMS for slot search enrichment
	 * @param requestInfo
	 * @param tenantId
	 * @return List of advertisements
	 */
	public List<Advertisements> fetchAdvertisementsData(RequestInfo requestInfo, String tenantId) {
		try {
			return getAdvertisements(requestInfo, tenantId, config.getModuleName(), null);
		} catch (JsonProcessingException e) {
			log.error("Error fetching advertisements data from MDMS", e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetch tax rates from MDMS
	 */
	public List<CalculationType> getTaxRatesMasterList(RequestInfo requestInfo, String tenantId, String moduleName,
													   CartDetail cartDetail) {
		List<CalculationType> taxRates = null;
		String taxRatesMasterName = "TaxRates";

		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName,
				taxRatesMasterName, null);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
				MdmsResponse.class);
		if (mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
			throw new CustomException("TAX_NOT_AVAILABLE", "Advertisement Tax Rates not available.");
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

	/**
	 * Generic method to construct MDMS request for master data
	 */
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

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 *
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<MasterDetail> masterDetailList = new ArrayList<>();

		MasterDetail calculationTypeMasterDetail = new MasterDetail();
		calculationTypeMasterDetail.setName(getCalculationTypeMasterName());
		masterDetailList.add(calculationTypeMasterDetail);

		// Add MasterDetail for TaxAmount
		MasterDetail taxAmountMasterDetail = new MasterDetail();
		taxAmountMasterDetail.setName(getTaxAmountMasterName());
		masterDetailList.add(taxAmountMasterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setModuleName(moduleName);
		moduleDetail.setMasterDetails(masterDetailList);

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


	private MdmsCriteriaReq getMdmsRequestAdvertisements(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<MasterDetail> masterDetailList = new ArrayList<>();

		MasterDetail advertisementsMasterDetail = new MasterDetail();
		advertisementsMasterDetail.setName(getAdvertisementsMasterName());
		masterDetailList.add(advertisementsMasterDetail);

		// Add MasterDetail for TaxAmount
		MasterDetail taxAmountMasterDetail = new MasterDetail();
		taxAmountMasterDetail.setName(getTaxAmountMasterName());
		masterDetailList.add(taxAmountMasterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setModuleName(moduleName);
		moduleDetail.setMasterDetails(masterDetailList);

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
	 * Fetch ServiceCharge configuration from MDMS
	 */
	public List<AdditionalFeeRate> getServiceCharges(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeRates(requestInfo, tenantId, moduleName, "ServiceCharge");
	}

	/**
	 * Fetch PenaltyFee configuration from MDMS
	 */
	public List<AdditionalFeeRate> getPenaltyFees(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeRates(requestInfo, tenantId, moduleName, "PenaltyFee");
	}

	/**
	 * Fetch InterestAmount configuration from MDMS
	 */
	public List<AdditionalFeeRate> getInterestAmounts(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeRates(requestInfo, tenantId, moduleName, "InterestAmount");
	}

	/**
	 * Fetch SecurityDeposit configuration from MDMS
	 */
	public List<AdditionalFeeRate> getSecurityDeposits(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeRates(requestInfo, tenantId, moduleName, "SecurityDeposit");
	}

	/**
	 * Generic method to fetch additional fee rates from MDMS
	 */
	private List<AdditionalFeeRate> getAdditionalFeeRates(RequestInfo requestInfo, String tenantId,
														  String moduleName, String masterName) {
		List<AdditionalFeeRate> additionalFeeRates = null;

		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName, masterName, null);

		try {
			MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
					MdmsResponse.class);

			if (mdmsResponse.getMdmsRes().get(moduleName) == null) {
				log.warn("{} configuration not available in MDMS for tenant: {}", masterName, tenantId);
				return new ArrayList<>();
			}

			JSONArray jsonArray = mdmsResponse.getMdmsRes().get(moduleName).get(masterName);

			additionalFeeRates = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, AdditionalFeeRate.class));

			log.info("Retrieved {} configurations: {}", masterName, additionalFeeRates);

		} catch (JsonProcessingException e) {
			log.error("Exception occurred while converting {} list: {}", masterName, e.getMessage());
			return new ArrayList<>();
		} catch (Exception e) {
			log.error("Exception occurred while fetching {} from MDMS: {}", masterName, e.getMessage());
			return new ArrayList<>();
		}

		return additionalFeeRates != null ? additionalFeeRates : new ArrayList<>();
	}

	//
	// Returns the Master Name Calculation Type
	private String getCalculationTypeMasterName() {
		return BookingConstants.ADV_CALCULATION_TYPE;
	}

	private String getTaxAmountMasterName() {
		return BookingConstants.ADV_TAX_AMOUNT;
	}

	private String getAdvertisementsMasterName() {
		return BookingConstants.ADVERTISEMENT;
	}

}