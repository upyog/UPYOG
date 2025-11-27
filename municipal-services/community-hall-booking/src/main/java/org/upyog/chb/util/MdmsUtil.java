package org.upyog.chb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.AdditionalFeeRate;
import org.upyog.chb.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	// Per-tenant MDMS cache. Keyed by full tenantId (eg: pb.nangal)
	private static Map<String, Object> mdmsMap = new ConcurrentHashMap<>();

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
		// Try per-tenant cache first
		Object result = mdmsMap.get(tenantId);
		if (result == null) {
			result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
			if (result != null) {
				mdmsMap.put(tenantId, result);
			}
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
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_HALL_CODES)
				.filter("$.[?(@.active==true)].HallCode").build());
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

    public List<AdditionalFeeRate> getCowCessForHall(RequestInfo requestInfo, String tenantId, String moduleName,
                                                     CommunityHallBookingDetail bookingDetail) {

        String masterName = "CowCess";
        List<AdditionalFeeRate> cowCessRates = new ArrayList<>();

        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost()).append(config.getMdmsPath());

        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName, masterName,
                null);

        try {
            MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
                    MdmsResponse.class);

            if (mdmsResponse.getMdmsRes().get(moduleName) == null) {
                log.info("CowCess configuration not available in MDMS for tenant: {}", tenantId);
                return cowCessRates;
            }

            JSONArray jsonArray = mdmsResponse.getMdmsRes().get(moduleName).get(masterName);

            com.fasterxml.jackson.databind.JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(jsonArray.toJSONString());
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("Error parsing CowCess JSON: ", e);
            }

            if (rootNode != null) {
                String hallCode = bookingDetail.getCommunityHallCode();
                for (com.fasterxml.jackson.databind.JsonNode hallNode : rootNode) {
                    com.fasterxml.jackson.databind.JsonNode faceAreaNode = hallNode.get(hallCode);
                    if (faceAreaNode != null) {
                        try {
                            cowCessRates = mapper.readValue(faceAreaNode.toString(),
                                    mapper.getTypeFactory().constructCollectionType(List.class, AdditionalFeeRate.class));
                        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                            log.error("Error converting CowCess rates: ", e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Exception occurred while fetching CowCess from MDMS: {}", e.getMessage());
        }

        return cowCessRates != null ? cowCessRates : new ArrayList<>();
    }

    // Utility accessors (if needed elsewhere) can remain, but prefer direct map usage
	public static void setMDMSDataForTenant(String tenantId, Object mdmsData) {
		mdmsMap.put(tenantId, mdmsData);
	}

	public static Object getMDMSDataForTenant(String tenantId) {
		return mdmsMap.get(tenantId);
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

	public List<CalculationType> getTaxRatesMasterList(RequestInfo requestInfo, String tenantId, String moduleName,
			CommunityHallBookingDetail bookingDetail) {
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

}