package org.egov.ptr.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.AdditionalFeeRate;
import org.egov.ptr.models.BreedType;
import org.egov.ptr.models.collection.GetBillCriteria;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.util.CollectionUtils;

import static org.egov.ptr.util.PTRConstants.*;

@Component
@Slf4j
public class PetUtil extends CommonUtils {

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetConfiguration config;


	public List<BreedType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<BreedType> calculationTypes = new ArrayList<BreedType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());


		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		Object o = restRepo.fetchResult(uri, mdmsCriteriaReq);
		MdmsResponse mdmsResponse = mapper.convertValue(o , MdmsResponse.class);
		if (mdmsResponse.getMdmsRes().get(PET_MASTER_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Pet registration fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(PET_MASTER_MODULE_NAME).get("BreedType");

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, BreedType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  for pet registration: " + e);
		}

		return calculationTypes;

	}

	/**
	 * Fetches ServiceCharge configuration from MDMS
	 */
	public List<AdditionalFeeRate> getServiceChargeConfig(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeConfig(requestInfo, tenantId, moduleName, "ServiceCharge");
	}

	/**
	 * Fetches PenaltyFee configuration from MDMS
	 */
	public List<AdditionalFeeRate> getPenaltyFeeConfig(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeConfig(requestInfo, tenantId, moduleName, "PenaltyFee");
	}

	/**
	 * Fetches InterestAmount configuration from MDMS
	 */
	public List<AdditionalFeeRate> getInterestAmountConfig(RequestInfo requestInfo, String tenantId, String moduleName) {
		return getAdditionalFeeConfig(requestInfo, tenantId, moduleName, "InterestAmount");
	}

	/**
	 * Generic method to fetch any fee configuration from MDMS
	 * This method provides flexibility for future fee types
	 */
	public List<AdditionalFeeRate> getFeeConfig(RequestInfo requestInfo, String tenantId, String moduleName, String feeType) {
		return getAdditionalFeeConfig(requestInfo, tenantId, moduleName, feeType);
	}

	/**
	 * Generic method to fetch additional fee configurations from MDMS
	 * Flexible method that handles various MDMS configurations and formats
	 */
	private List<AdditionalFeeRate> getAdditionalFeeConfig(RequestInfo requestInfo, String tenantId, String moduleName, String configType) {
		List<AdditionalFeeRate> feeConfigs = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForAdditionalFees(requestInfo, tenantId, moduleName, configType);
		Object o = restRepo.fetchResult(uri, mdmsCriteriaReq);
		MdmsResponse mdmsResponse = mapper.convertValue(o, MdmsResponse.class);
		
		if (mdmsResponse.getMdmsRes() == null || mdmsResponse.getMdmsRes().get(moduleName) == null) {
			return feeConfigs;
		}
		
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(moduleName).get(configType);
		if (jsonArray != null && !jsonArray.isEmpty()) {
			try {
				feeConfigs = mapper.readValue(jsonArray.toJSONString(),
						mapper.getTypeFactory().constructCollectionType(List.class, AdditionalFeeRate.class));
				
				// Process and validate each configuration
				feeConfigs = processFeeConfigurations(feeConfigs, configType);
			} catch (JsonProcessingException e) {
				log.error("Exception occurred while converting {} configuration: {}", configType, e);
			}
		}

		return feeConfigs;
	}

	/**
	 * Processes and validates fee configurations
	 */
	private List<AdditionalFeeRate> processFeeConfigurations(List<AdditionalFeeRate> feeConfigs, String configType) {
		List<AdditionalFeeRate> processedConfigs = new ArrayList<>();
		
		for (AdditionalFeeRate feeConfig : feeConfigs) {
			// Set feeType if not present
			if (feeConfig.getFeeType() == null || feeConfig.getFeeType().trim().isEmpty()) {
				feeConfig.setFeeType(configType);
			}
			
			// Handle active field - flexible conversion
			if (feeConfig.getActive() == null) {
				feeConfig.setActive("true"); // Default to active
			} else {
				// Convert boolean to string if needed
				String activeValue = feeConfig.getActive().toString().toLowerCase();
				if ("true".equals(activeValue) || "1".equals(activeValue) || "yes".equals(activeValue)) {
					feeConfig.setActive("true");
				} else {
					feeConfig.setActive("false");
				}
			}
			
			// Validate configuration
			if (isValidFeeConfiguration(feeConfig)) {
				processedConfigs.add(feeConfig);
			} else {
				log.warn("Invalid fee configuration skipped: {}", feeConfig);
			}
		}
		
		return processedConfigs;
	}

	/**
	 * Validates fee configuration
	 */
	private boolean isValidFeeConfiguration(AdditionalFeeRate feeConfig) {
		// Must have at least one amount calculation method
		boolean hasAmount = feeConfig.getFlatAmount() != null && feeConfig.getFlatAmount().compareTo(BigDecimal.ZERO) > 0;
		boolean hasRate = feeConfig.getRate() != null && feeConfig.getRate().compareTo(BigDecimal.ZERO) > 0;
		boolean hasAmountField = feeConfig.getAmount() != null && feeConfig.getAmount().compareTo(BigDecimal.ZERO) > 0;
		
		if (!hasAmount && !hasRate && !hasAmountField) {
			log.warn("Fee configuration has no valid amount calculation method: {}", feeConfig);
			return false;
		}
		
		// Validate rate if present
		if (feeConfig.getRate() != null && feeConfig.getRate().compareTo(new BigDecimal("100")) > 0) {
			// Rate seems unusually high, but continue processing
		}
		
		return true;
	}

	/**
	 * Creates MDMS request for additional fee configurations
	 */
	private MdmsCriteriaReq getMdmsRequestForAdditionalFees(RequestInfo requestInfo, String tenantId, String moduleName, String configType) {
		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(configType);
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

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
//		masterDetail.setName(CALCULATION_TYPE);
		masterDetail.setName("BreedType");
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
	public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
		StringBuilder builder = new StringBuilder();
		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
			builder = builder.append(config.getBillingHost())
					.append(config.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
					.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(SEPARATER)
					.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getApplicationNumber())
					.append(SEPARATER)
					.append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);
		}
		else {

			builder = builder.append(config.getBillingHost())
					.append(config.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
					.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(SEPARATER)
					.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
					.append(SEPARATER)
					.append(paymentcompleted)
					.append(SEPARATER)
					.append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);

		}
		if (getBillCriteria.getFromDate() != null && getBillCriteria.getToDate() != null)
			builder = builder.append(DEMAND_START_DATE_PARAM).append(getBillCriteria.getFromDate())
					.append(SEPARATER)
					.append(DEMAND_END_DATE_PARAM).append(getBillCriteria.getToDate())
					.append(SEPARATER);

		return builder;
	}


	public StringBuilder getUpdateDemandUrl() {
		return new StringBuilder().append(config.getBillingHost()).append(config.getDemandUpdateEndpoint());
	}

}
