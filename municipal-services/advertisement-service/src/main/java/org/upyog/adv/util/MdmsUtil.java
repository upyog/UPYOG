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
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.web.models.CalculationType;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
/**
 * Utility class for interacting with the MDMS (Master Data Management System) in the Advertisement Booking Service.
 */
@Slf4j
@Component
public class MdmsUtil {

	private final BookingConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;

	private Object mdmsMap;
	private List<TaxHeadMaster> headMasters;

	public MdmsUtil(BookingConfiguration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	/**
	 * Makes an MDMS call with the given criteria and returns MDMS data.
	 *
	 * @param requestInfo request metadata
	 * @param tenantId tenant identifier
	 * @return MDMS response payload
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		if (mdmsMap != null) {
			return mdmsMap;
		}
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		mdmsMap = result;
		return result;
	}

	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsPath());
	}

	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getADVModuleRequest();
		log.info("Module details data needs to be fetched from MDMS : " + moduleRequest);

		List<ModuleDetail> moduleDetails = new LinkedList<>(moduleRequest);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
	}

	public List<ModuleDetail> getADVModuleRequest() {
		List<MasterDetail> advMasterDtls = new ArrayList<>();
		final String filterCode = "$.[?(@.active==true)].code";

		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.ADD_TYPE).filter(filterCode).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.LOCATION).filter(filterCode).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.FACE_AREA).filter(filterCode).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.DOCUMENTS).filter(filterCode).build());
		advMasterDtls.add(MasterDetail.builder().name(BookingConstants.ADV_TAX_AMOUNT).build());

		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(advMasterDtls)
				.moduleName(config.getModuleName()).build();

		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(BookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);
	}

	public List<TaxHeadMaster> getTaxHeadMasterList(RequestInfo requestInfo, String tenantId, String moduleName) {
		if (headMasters != null) {
			log.info("Returning cached value of tax head masters");
			return headMasters;
		}
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHeadMaster(requestInfo, tenantId, moduleName);

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

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName,
			CartDetail cartDetail) {

		List<CalculationType> calculationTypes = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq),
				MdmsResponse.class);

		if (mdmsResponse.getMdmsRes().get(config.getModuleName()) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Advertisement booking Fee not available.");
		}

		calculationTypes.addAll(loadLocationBasedCalculationTypes(mdmsResponse, cartDetail));
		calculationTypes.addAll(loadTaxAmountCalculationTypes(mdmsResponse));

		return calculationTypes;
	}

	private List<CalculationType> loadLocationBasedCalculationTypes(MdmsResponse mdmsResponse, CartDetail cartDetail) {
		List<CalculationType> calculationTypes = new ArrayList<>();
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName())
				.get(getCalculationTypeMasterName());
		try {
			JsonNode rootNode = mapper.readTree(jsonArray.toJSONString());
			for (JsonNode locationNode : rootNode) {
				if (locationNode.get("location").asText().equalsIgnoreCase(cartDetail.getLocation())) {
					JsonNode faceAreaNode = locationNode.get("CalculationType_" + cartDetail.getFaceArea());
					if (faceAreaNode != null) {
						calculationTypes.addAll(mapper.readValue(faceAreaNode.toString(),
								mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class)));
					}
				}
			}
		} catch (JsonProcessingException e) {
			log.error("Error parsing CalculationType JSON: ", e);
		}
		return calculationTypes;
	}

	private List<CalculationType> loadTaxAmountCalculationTypes(MdmsResponse mdmsResponse) {
		JSONArray taxAmountJsonArray = mdmsResponse.getMdmsRes().get(config.getModuleName())
				.get(getTaxAmountMasterName());
		try {
			return mapper.readValue(taxAmountJsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.error("Error converting tax calculation types: ", e);
			return List.of();
		}
	}

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

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {
		List<MasterDetail> masterDetailList = new ArrayList<>();

		MasterDetail calculationTypeMasterDetail = new MasterDetail();
		calculationTypeMasterDetail.setName(getCalculationTypeMasterName());
		masterDetailList.add(calculationTypeMasterDetail);

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

	private String getCalculationTypeMasterName() {
		return BookingConstants.ADV_CALCULATION_TYPE;
	}

	private String getTaxAmountMasterName() {
		return BookingConstants.ADV_TAX_AMOUNT;
	}

}
