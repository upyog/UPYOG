package org.egov.wscalculation.service;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.repository.DemandRepository;
import org.egov.wscalculation.repository.ServiceRequestRepository;
import org.egov.wscalculation.repository.WSCalculationDao;
import org.egov.wscalculation.repository.WaterConnectionRepository;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.util.NotificationUtil;
import org.egov.wscalculation.util.WSCalculationUtil;
import org.egov.wscalculation.validator.WSCalculationWorkflowValidator;
import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.web.models.Demand.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

import static org.egov.wscalculation.constants.WSCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;
import static org.egov.wscalculation.constants.WSCalculationConstant.SERVICE_FIELD_VALUE_WS;
import static org.egov.wscalculation.constants.WSCalculationConstant.MODIFY_WATER_CONNECTION;
import static org.egov.wscalculation.constants.WSCalculationConstant.DISCONNECT_WATER_CONNECTION;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private ServiceRequestRepository repository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PayService payService;

	@Autowired
	private MasterDataService mstrDataService;

	@Autowired
	private WSCalculationUtil utils;

	@Autowired
	private WSCalculationConfiguration configs;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private WSCalculationDao waterCalculatorDao;

	@Autowired
	private CalculatorUtil calculatorUtils;

	@Autowired
	private EstimationService estimationService;

	@Autowired
	private WSCalculationProducer wsCalculationProducer;

	@Autowired
	private WSCalculationUtil wsCalculationUtil;

	@Autowired
	private WaterConnectionRepository waterConnectionRepository;

	@Autowired
	private PaymentNotificationService paymentNotificationService;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private NotificationUtil notificationUtil;

	/**
	 * Creates or updates Demand
	 * 
	 * @param request      The CalculationReq
	 * @param calculations The Calculation Objects for which demand has to be
	 *                     generated or updated
	 */
	public List<Demand> generateDemand(CalculationReq request, List<Calculation> calculations,
			Map<String, Object> masterMap, boolean isForConnectionNo) {
		@SuppressWarnings("unchecked")
		Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
				.get(WSCalculationConstant.BILLING_PERIOD);
		Long fromDate = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
		Long toDate = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);
		if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest()) {
			fromDate = (Long) request.getCalculationCriteria().get(0).getFrom();
			toDate = (Long) request.getCalculationCriteria().get(0).getTo();
		}
		// List that will contain Calculation for new demands
		List<Calculation> createCalculations = new LinkedList<>();
		// List that will contain Calculation for old demands
		List<Calculation> updateCalculations = new LinkedList<>();
		if (!CollectionUtils.isEmpty(calculations) && calculations.get(0) != null) {
			// Collect required parameters for demand search
			String tenantId = calculations.get(0).getTenantId();
			Long fromDateSearch = null;
			Long toDateSearch = null;
			Set<String> consumerCodes;
			if (isForConnectionNo) {
				fromDateSearch = fromDate;
				toDateSearch = toDate;
				consumerCodes = calculations.stream().map(calculation -> calculation.getConnectionNo())
						.collect(Collectors.toSet());
			} else {
				consumerCodes = calculations.stream().map(calculation -> calculation.getApplicationNO())
						.collect(Collectors.toSet());
			}

			List<Demand> demands = new ArrayList<>();
			// If demand already exists add it updateCalculations else
			for (Calculation calculation : calculations) {
				if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest()) {
					demands = searchDemandForDisconnectionRequest(calculation.getTenantId(), consumerCodes, null,
							toDateSearch, request.getRequestInfo(), null, request.getIsDisconnectionRequest());
					if (!CollectionUtils.isEmpty(demands)
							&& !(demands.get(0).getDemandDetails().get(0).getCollectionAmount().doubleValue() == 0.0)) {
						createCalculations.add(calculation);
					} else if (CollectionUtils.isEmpty(demands)) {
						calculation.getApplicationNO();
						createCalculations.add(calculation);
					} else {
						updateCalculations.add(calculation);
					}
				} else {
					demands = searchDemand(tenantId, consumerCodes, fromDateSearch, toDateSearch,
							request.getRequestInfo(), null, request.getIsDisconnectionRequest(),
							request.getIsReconnectionRequest());
					log.info("Demands fetched are:: " + demands);

					Set<String> connectionNumbersFromDemands = new HashSet<>();
					if (!CollectionUtils.isEmpty(demands))
						connectionNumbersFromDemands = demands.stream().map(Demand::getConsumerCode)
								.collect(Collectors.toSet());
					if (!connectionNumbersFromDemands.contains(
							isForConnectionNo ? calculation.getConnectionNo() : calculation.getApplicationNO())) {
						createCalculations.add(calculation);
					} else
						updateCalculations.add(calculation);
				}
			}
		}
		List<Demand> createdDemands = new ArrayList<>();
		if (!CollectionUtils.isEmpty(createCalculations))
			createdDemands = createDemand(request, createCalculations, masterMap, isForConnectionNo);

		if (!CollectionUtils.isEmpty(updateCalculations))
			createdDemands = updateDemandForCalculation(request.getRequestInfo(), updateCalculations, fromDate, toDate,
					isForConnectionNo, request.getIsDisconnectionRequest(), request.getIsReconnectionRequest());
		return createdDemands;
	}

	/**
	 * Creates or updates Demand
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations The Calculation Objects for which demand has to be
	 *                     generated or updated
	 */
	public List<Demand> generateDemandForBillingCycleInBulk(CalculationReq request, List<Calculation> calculations,
			Map<String, Object> masterMap, boolean isForConnectionNo) {

		boolean isDemandAvailable = false;
		List<Demand> createDemands = new ArrayList<>();
		List<Demand> updateDemands = new ArrayList<>();
		List<Demand> demandRes = new ArrayList<>();

		try {
			if (!CollectionUtils.isEmpty(calculations)) {
				for (Calculation calculation : calculations) {
					// Collect required parameters for demand search
					String tenantId = calculation.getTenantId();
					Long fromDateSearch = null;
					Long toDateSearch = null;
					String consumerCodes;
					if (isForConnectionNo) {

						fromDateSearch = calculation.getFrom();
						toDateSearch = calculation.getTo();

						consumerCodes = calculation.getConnectionNo();
					} else {
						consumerCodes = calculation.getApplicationNO();
					}

					isDemandAvailable = waterCalculatorDao.isConnectionDemandAvailableForBillingCycle(tenantId,
							fromDateSearch, toDateSearch, consumerCodes);

					log.info("isDemandAvailable: {} for consumercode: {}, taxperiod from: {} and To: {}",
							isDemandAvailable, consumerCodes, fromDateSearch, toDateSearch);
					// If demand already exists add it updateCalculations else
					if (!isDemandAvailable)
						createDemands.add(createDemandForNonMeteredInBulk(request.getRequestInfo(), calculation,
								masterMap, isForConnectionNo, fromDateSearch, toDateSearch));
					else
						updateDemands.add(createDemandForNonMeteredInBulk(request.getRequestInfo(), calculation,
								masterMap, isForConnectionNo, toDateSearch, toDateSearch));
				}
			}

			// Save the bulk demands for metered connections
			if (!createDemands.isEmpty()) {
				log.info("Creating Non metered Demands list size: {} and Demand Object: {}", createDemands.size(),
						mapper.writeValueAsString(createDemands));
				demandRes.addAll(demandRepository.saveDemand(request.getRequestInfo(), createDemands));

			}
			// Save the bulk demands for non metered connections
			if (!updateDemands.isEmpty()) {
				log.info("Updating Non metered Demands list size: {} and Demand Object: {}", updateDemands.size(),
						mapper.writeValueAsString(updateDemands));
				demandRes.addAll(demandRepository.updateDemand(request.getRequestInfo(), updateDemands));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return demandRes;
	}

	/**
	 * 
	 * @param requestInfo  RequestInfo
	 * @param calculations List of Calculation
	 * @param masterMap    Master MDMS Data
	 * @return Returns list of demands
	 * @throws IOException
	 * @throws JsonMappingException
	 */
	private List<Demand> createDemand(RequestInfo requestInfo, List<Calculation> calculations,
			Map<String, Object> masterMap, boolean isForConnectionNO, long taxPeriodFrom, long taxPeriodTo) {
		List<Demand> demandRes = new LinkedList<>();
		List<Demand> demandReq = new LinkedList<>();
		List<Demand> demandsForMetered = new LinkedList<>();
		String sewConsumerCode = "";
		String businessServices = "";

		for (Calculation calculation : calculations) {
			WaterConnection connection = calculation.getWaterConnection();
			if (connection == null) {
				throw new CustomException("INVALID_WATER_CONNECTION",
						"Demand cannot be generated for "
								+ (isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO())
								+ " Water Connection with this number does not exist ");
			}
			WaterConnectionRequest waterConnectionRequest = WaterConnectionRequest.builder().waterConnection(connection)
					.requestInfo(requestInfo).build();

			log.info("waterConnectionRequest: {}", waterConnectionRequest);
			Property property = wsCalculationUtil.getProperty(waterConnectionRequest);
			log.info("Property: {}", property);

			String tenantId = calculation.getTenantId();
			String consumerCode = isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO();
			User owner = property.getOwners().get(0).toCommonUser();
			if (!CollectionUtils.isEmpty(waterConnectionRequest.getWaterConnection().getConnectionHolders())) {
				owner = waterConnectionRequest.getWaterConnection().getConnectionHolders().get(0).toCommonUser();
			}
			List<DemandDetail> demandDetails = new LinkedList<>();
			List<DemandDetail> demandDetails1 = new LinkedList<>();
			calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
				demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
						.tenantId(tenantId).build());
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
					.get(WSCalculationConstant.BILLING_PERIOD);

			if (taxPeriodFrom == 0 && taxPeriodTo == 0) {
				taxPeriodFrom = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
				taxPeriodTo = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);
			}
			Long expiryDaysInmillies = (Long) financialYearMaster.get(WSCalculationConstant.Demand_Expiry_Date_String);
			// Long expiryDate = System.currentTimeMillis() + expiryDaysInmillies;

			BigDecimal minimumPayableAmount = calculation.getTotalAmount();
			String businessService = isForConnectionNO ? configs.getBusinessService()
					: WSCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;
			ObjectMapper obj = new ObjectMapper();

			String json = "{ \"connectionType\" : \"" + connection.getConnectionType() + "\"}";
			JsonNode additionalDetail = null;
			try {
				additionalDetail = obj.readTree(json);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			addRoundOffTaxHead(calculation.getTenantId(), demandDetails);
			Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
					.minimumAmountPayable(minimumPayableAmount).tenantId(tenantId).taxPeriodFrom(taxPeriodFrom)
					.taxPeriodTo(taxPeriodTo).consumerType("waterConnection").businessService(businessService)
					.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDaysInmillies)
					.additionalDetails(additionalDetail).build();

			// For the metered connections demand has to create one by one
			if (WSCalculationConstant.meteredConnectionType.equalsIgnoreCase(connection.getConnectionType())) {
				demandsForMetered.add(demand);
				if (demand.getTenantId().equalsIgnoreCase("pb.amritsar")
						&& demand.getBusinessService().equalsIgnoreCase("WS_CHARGE")) {
					List<String> usageCategory = waterCalculatorDao.fetchUsageCategory(demand.getConsumerCode());
					if (usageCategory.size() > 0) {
						if (usageCategory.get(0).equals("NONRESIDENTIAL.COMMERCIAL")) {
							List<String> sewConsumerList = waterCalculatorDao
									.fetchSewConnection(demand.getConsumerCode());
							if (sewConsumerList.size() > 0) {
								sewConsumerCode = sewConsumerList.get(0);
							}
						}
					}
					businessServices = "SW";
					for (DemandDetail ddSew : demandDetails) {
						DemandDetail dd1 = new DemandDetail();
						if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_CHARGE")) {
							dd1.setTaxHeadMasterCode("SW_CHARGE");
						} else if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_Round_Off")) {
							dd1.setTaxHeadMasterCode("SW_Round_Off");
						} else if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_ADVANCE_CARRYFORWARD")) {
							dd1.setTaxHeadMasterCode("SW_ADVANCE_CARRYFORWARD");
						} else {
							dd1.setTaxHeadMasterCode("SW_CHARGE");
						}
						dd1.setDemandId(ddSew.getDemandId());
						dd1.setAuditDetails(ddSew.getAuditDetails());
						dd1.setCollectionAmount(ddSew.getCollectionAmount());
						dd1.setId(ddSew.getId());
						dd1.setTaxAmount(ddSew.getTaxAmount());
						dd1.setTenantId(ddSew.getTenantId());
						demandDetails1.add(dd1);
					}

					Demand demand1 = Demand.builder().consumerCode(sewConsumerCode).demandDetails(demandDetails1)
							.payer(owner).minimumAmountPayable(minimumPayableAmount).tenantId(tenantId)
							.taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo).consumerType("sewerageConnection")
							.businessService(businessServices).status(StatusEnum.valueOf("ACTIVE"))
							.billExpiryTime(expiryDaysInmillies).additionalDetails(additionalDetail).build();
					demandsForMetered.add(demand1);
				}
			} else {
				demandReq.add(demand);
			}
			List<String> consumerCodes = new ArrayList<String>();
			consumerCodes.add(sewConsumerCode);
			waterCalculatorDao.updateBillStatus(consumerCodes, "SW", "EXPIRED");

		}
		// Save the bulk demands for metered connections
		if (!demandsForMetered.isEmpty()) {
			log.info("Demands list size: {} and Demand Object" + demandsForMetered.toString(),
					demandsForMetered.size());
			demandRes.addAll(demandRepository.saveDemand(requestInfo, demandsForMetered));
			fetchBill(demandRes, requestInfo, masterMap);

		}
		// Save the bulk demands for non metered connections
		if (!demandReq.isEmpty()) {
			log.info("Non metered Demands list size: {} and Demand Object" + demandReq.toString(), demandReq.size());
			demandRes.addAll(demandRepository.saveDemand(requestInfo, demandReq));

		}

		return demandRes;
	}

	/**
	 * 
	 * @param calculationReq - calculation request object
	 * @param calculations   List of Calculation
	 * @param masterMap      Master MDMS Data
	 * @return Returns list of demands
	 */
	private List<Demand> createDemand(CalculationReq calculationReq, List<Calculation> calculations,
			Map<String, Object> masterMap, boolean isForConnectionNO) {
		List<Demand> demands = new LinkedList<>();
		List<Demand> demandsSw = new LinkedList<>();
		List<Demand> demandReq = new LinkedList<>();
		Set<String> waterConnectionIds = new HashSet<>();
		List<Demand> demandsForMetered = new LinkedList<>();
		RequestInfo requestInfo = calculationReq.getRequestInfo();
		String sewConsumerCode = "";
		String businessServices = "";

		for (Calculation calculation : calculations) {
			WaterConnection connection = calculation.getWaterConnection();
			if (connection == null) {
				throw new CustomException("EG_WS_INVALID_WATER_CONNECTION",
						"Demand cannot be generated for "
								+ (isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO())
								+ " Water Connection with this number does not exist ");
			}
			WaterConnectionRequest waterConnectionRequest = WaterConnectionRequest.builder().waterConnection(connection)
					.requestInfo(requestInfo).build();
			Property property = wsCalculationUtil.getProperty(waterConnectionRequest);
			String tenantId = calculation.getTenantId();
			String consumerCode = isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO();
			waterConnectionIds.add(consumerCode);

			User owner = property.getOwners().get(0).toCommonUser();
			if (!CollectionUtils.isEmpty(waterConnectionRequest.getWaterConnection().getConnectionHolders())) {
				owner = waterConnectionRequest.getWaterConnection().getConnectionHolders().get(0).toCommonUser();
			}
			owner = getPlainOwnerDetails(requestInfo, owner.getUuid(), tenantId);
			List<DemandDetail> demandDetails = new LinkedList<>();
			List<DemandDetail> demandDetails1 = new LinkedList<>();
			calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
				demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
						.tenantId(tenantId).build());
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
					.get(WSCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);

			if (calculationReq.getIsDisconnectionRequest() != null && calculationReq.getIsDisconnectionRequest()
					&& calculationReq.getCalculationCriteria().get(0).getWaterConnection().getConnectionType()
							.equalsIgnoreCase("Metered")) {
				fromDate = (Long) calculationReq.getCalculationCriteria().get(0).getFrom();
				toDate = (Long) calculationReq.getCalculationCriteria().get(0).getTo();
			}

			Long expiryDate = (Long) financialYearMaster.get(WSCalculationConstant.Demand_Expiry_Date_String);
			BigDecimal minimumPayableAmount = isForConnectionNO ? configs.getMinimumPayableAmount()
					: calculation.getTotalAmount();
			String businessService = isForConnectionNO ? configs.getBusinessService() : ONE_TIME_FEE_SERVICE_FIELD;

			log.info("in create Demand  Business Service is" + businessService);

			if (calculationReq.getIsReconnectionRequest())
				businessService = "WSReconnection";
			log.info("in create Demand  Business Service is" + businessService);

			addRoundOffTaxHead(calculation.getTenantId(), demandDetails);

			Map<String, String> additionalDetailsMap = new HashMap<>();
			additionalDetailsMap.put("propertyId", property.getPropertyId());
			demands.add(Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
					.minimumAmountPayable(minimumPayableAmount).tenantId(tenantId).taxPeriodFrom(fromDate)
					.taxPeriodTo(toDate).consumerType("waterConnection").businessService(businessService)
					.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDate)
					.additionalDetails(additionalDetailsMap).build());

			// For the metered connections demand has to create one by one
			if (WSCalculationConstant.meteredConnectionType.equalsIgnoreCase(connection.getConnectionType())) {
				additionalDetailsMap.put("connectionType", connection.getConnectionType());
				demandReq.addAll(demands);
				if (tenantId.equalsIgnoreCase("pb.amritsar")
						&& demands.get(0).getBusinessService().equalsIgnoreCase("WS")) {
					List<String> usageCategory = waterCalculatorDao
							.fetchUsageCategory(demands.get(0).getConsumerCode());
					if (usageCategory.size() > 0) {
						if (usageCategory.get(0).equals("NONRESIDENTIAL.COMMERCIAL")) {
							List<String> sewConsumerList = waterCalculatorDao
									.fetchSewConnection(demands.get(0).getConsumerCode());
							if (sewConsumerList.size() > 0) {
								sewConsumerCode = sewConsumerList.get(0);
							}
						}
					}
					businessServices = "SW";
					for (DemandDetail ddSew : demandDetails) {
						DemandDetail dd1 = new DemandDetail();
						if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_CHARGE")) {
							dd1.setTaxHeadMasterCode("SW_CHARGE");
						} else if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_Round_Off")) {
							dd1.setTaxHeadMasterCode("SW_Round_Off");
						} else if (ddSew.getTaxHeadMasterCode().equalsIgnoreCase("WS_ADVANCE_CARRYFORWARD")) {
							dd1.setTaxHeadMasterCode("SW_ADVANCE_CARRYFORWARD");
						} else {
							dd1.setTaxHeadMasterCode("SW_CHARGE");
						}
						dd1.setDemandId(ddSew.getDemandId());
						dd1.setAuditDetails(ddSew.getAuditDetails());
						dd1.setCollectionAmount(ddSew.getCollectionAmount());
						dd1.setId(ddSew.getId());
						dd1.setTaxAmount(ddSew.getTaxAmount());
						dd1.setTenantId(ddSew.getTenantId());
						demandDetails1.add(dd1);
					}

					demandsSw.add(Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails1).payer(owner)
							.minimumAmountPayable(minimumPayableAmount).tenantId(tenantId).taxPeriodFrom(fromDate)
							.taxPeriodTo(toDate).consumerType("sewerageConnection").businessService(businessService)
							.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDate)
							.additionalDetails(additionalDetailsMap).build());
					demandsSw.get(0).setBusinessService("SW");
					demandReq.addAll(demandsSw);
				}
			} else {
				demandReq.addAll(demands);
			}
			List<String> consumerCodes = new ArrayList<String>();
			consumerCodes.add(sewConsumerCode);
			waterCalculatorDao.updateBillStatus(consumerCodes, "SW", "EXPIRED");
		}

		String billingcycle = calculatorUtils.getBillingCycle(masterMap);
		DemandNotificationObj notificationObj = DemandNotificationObj.builder().requestInfo(requestInfo)
				.tenantId(calculations.get(0).getTenantId()).waterConnectionIds(waterConnectionIds)
				.billingCycle(billingcycle).build();
		log.info("Demand Input for WSREconnection is " + demands);
		List<Demand> demandRes = demandRepository.saveDemand(requestInfo, demandReq, notificationObj);
		log.info("Demand Response for WSREconnection is " + demandRes);

		if (calculationReq.getIsReconnectionRequest())
			fetchBillForReconnect(demandRes, requestInfo, masterMap);
		else if (isForConnectionNO && !calculationReq.getIsReconnectionRequest())
			fetchBill(demandRes, requestInfo, masterMap);

		return demandRes;
	}

	private User getPlainOwnerDetails(RequestInfo requestInfo, String uuid, String tenantId) {
		User userInfoCopy = requestInfo.getUserInfo();
		StringBuilder uri = new StringBuilder();
		uri.append(configs.getUserHost()).append(configs.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		tenantId = tenantId.split("\\.")[0];

		Role role = Role.builder().name("Internal Microservice Role").code("INTERNAL_MICROSERVICE_ROLE")
				.tenantId(tenantId).build();

		User userInfo = User.builder().uuid(configs.getEgovInternalMicroserviceUserUuid()).type("SYSTEM")
				.roles(Collections.singletonList(role)).id(0L).build();

		requestInfo.setUserInfo(userInfo);

		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("uuid", Collections.singletonList(uuid));
		User user = null;
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository
					.fetchResult(uri, userSearchRequest);

			List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
			String dobFormat = "yyyy-MM-dd";
			notificationUtil.parseResponse(responseMap, dobFormat);
			user = mapper.convertValue(users.get(0), User.class);

		} catch (Exception e) {
			throw new CustomException("EG_USER_SEARCH_ERROR", "Service returned null while fetching user");
		}
		requestInfo.setUserInfo(userInfoCopy);
		return user;
	}

	private Demand createDemandForNonMeteredInBulk(RequestInfo requestInfo, Calculation calculation,
			Map<String, Object> masterMap, boolean isForConnectionNO, long taxPeriodFrom, long taxPeriodTo) {

		WaterConnection connection = calculation.getWaterConnection();
		connection.setConnectionHolders(null);
		if (connection == null) {
			throw new CustomException("INVALID_WATER_CONNECTION",
					"Demand cannot be generated for "
							+ (isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO())
							+ " Water Connection with this number does not exist ");
		}
		WaterConnectionRequest waterConnectionRequest = WaterConnectionRequest.builder().waterConnection(connection)
				.requestInfo(requestInfo).build();

		log.info("waterConnectionRequest: {}", waterConnectionRequest);
		Property property = wsCalculationUtil.getProperty(waterConnectionRequest);
		log.info("Property: {}", property);

		String tenantId = calculation.getTenantId();
		String consumerCode = isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO();
		User owner = property.getOwners().get(0).toCommonUser();
		if (!CollectionUtils.isEmpty(waterConnectionRequest.getWaterConnection().getConnectionHolders())) {
			owner = waterConnectionRequest.getWaterConnection().getConnectionHolders().get(0).toCommonUser();
		}
		List<DemandDetail> demandDetails = new LinkedList<>();
		calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
			demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
					.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
					.tenantId(tenantId).build());
		});
		@SuppressWarnings("unchecked")
		Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
				.get(WSCalculationConstant.BILLING_PERIOD);

		if (taxPeriodFrom == 0 && taxPeriodTo == 0) {
			taxPeriodFrom = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
			taxPeriodTo = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);
		}
		Long expiryDaysInmillies = (Long) financialYearMaster.get(WSCalculationConstant.Demand_Expiry_Date_String);
		// Long expiryDate = System.currentTimeMillis() + expiryDaysInmillies;

		BigDecimal minimumPayableAmount = calculation.getTotalAmount();
		String businessService = isForConnectionNO ? configs.getBusinessService()
				: WSCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;

		addRoundOffTaxHead(calculation.getTenantId(), demandDetails);
		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(minimumPayableAmount).tenantId(tenantId).taxPeriodFrom(taxPeriodFrom)
				.taxPeriodTo(taxPeriodTo).consumerType("waterConnection").businessService(businessService)
				.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDaysInmillies).build();

		return demand;
	}

	/**
	 * Returns the list of new DemandDetail to be added for updating the demand
	 * 
	 * @param calculation            The calculation object for the update request
	 * @param demandDetails          The list of demandDetails from the existing
	 *                               demand
	 * @param isDisconnectionRequest
	 * @return The list of new DemandDetails
	 */
	private List<DemandDetail> getUpdatedDemandDetails(Calculation calculation, List<DemandDetail> demandDetails,
			Boolean isDisconnectionRequest) {

		List<DemandDetail> newDemandDetails = new ArrayList<>();
		Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

		demandDetails.forEach(demandDetail -> {
			if (!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())) {
				List<DemandDetail> demandDetailList = new LinkedList<>();
				demandDetailList.add(demandDetail);
				taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(), demandDetailList);
			} else
				taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
		});

		BigDecimal diffInTaxAmount;
		List<DemandDetail> demandDetailList;
		BigDecimal total;

		for (TaxHeadEstimate taxHeadEstimate : calculation.getTaxHeadEstimates()) {
			if (!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()) && !isDisconnectionRequest)
				newDemandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
						.collectionAmount(BigDecimal.ZERO).build());
			else if (isDisconnectionRequest) {
				if (taxHeadEstimate.getTaxHeadCode().equalsIgnoreCase(WSCalculationConstant.WS_Round_Off))
					continue;
				total = taxHeadEstimate.getEstimateAmount();
				if (total.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(
							DemandDetail.builder().taxAmount(total).taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
									.tenantId(calculation.getTenantId()).collectionAmount(BigDecimal.ZERO).build());
				}
			} else {
				demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
				total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
				if (diffInTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(diffInTaxAmount)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
				}
			}
		}
		List<DemandDetail> combinedBillDetails = new LinkedList<>(demandDetails);
		if (!CollectionUtils.isEmpty(combinedBillDetails) && isDisconnectionRequest) {
			combinedBillDetails.clear();
			combinedBillDetails.addAll(newDemandDetails);
		} else
			combinedBillDetails.addAll(newDemandDetails);
		addRoundOffTaxHead(calculation.getTenantId(), combinedBillDetails);
		return combinedBillDetails;
	}

	/**
	 * Adds roundOff taxHead if decimal values exists
	 * 
	 * @param tenantId      The tenantId of the demand
	 * @param demandDetails The list of demandDetail
	 */
	public void addRoundOffTaxHead(String tenantId, List<DemandDetail> demandDetails) {
		BigDecimal totalTax = BigDecimal.ZERO;

		BigDecimal previousRoundOff = BigDecimal.ZERO;

		/*
		 * Sum all taxHeads except RoundOff as new roundOff will be calculated
		 */
		for (DemandDetail demandDetail : demandDetails) {
			if (!demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(WSCalculationConstant.WS_Round_Off))
				totalTax = totalTax.add(demandDetail.getTaxAmount());
			else
				previousRoundOff = previousRoundOff.add(demandDetail.getTaxAmount());
		}

		BigDecimal decimalValue = totalTax.remainder(BigDecimal.ONE);
		BigDecimal midVal = BigDecimal.valueOf(0.5);
		BigDecimal roundOff = BigDecimal.ZERO;

		/*
		 * If the decimal amount is greater than 0.5 we subtract it from 1 and put it as
		 * roundOff taxHead so as to nullify the decimal eg: If the tax is 12.64 we will
		 * add extra tax roundOff taxHead of 0.36 so that the total becomes 13
		 */
		if (decimalValue.compareTo(midVal) >= 0)
			roundOff = BigDecimal.ONE.subtract(decimalValue);

		/*
		 * If the decimal amount is less than 0.5 we put negative of it as roundOff
		 * taxHead so as to nullify the decimal eg: If the tax is 12.36 we will add
		 * extra tax roundOff taxHead of -0.36 so that the total becomes 12
		 */
		if (decimalValue.compareTo(midVal) < 0)
			roundOff = decimalValue.negate();

		/*
		 * If roundOff already exists in previous demand create a new roundOff taxHead
		 * with roundOff amount equal to difference between them so that it will be
		 * balanced when bill is generated. eg: If the previous roundOff amount was of
		 * -0.36 and the new roundOff excluding the previous roundOff is 0.2 then the
		 * new roundOff will be created with 0.2 so that the net roundOff will be 0.2
		 * -(-0.36)
		 */
		if (previousRoundOff.compareTo(BigDecimal.ZERO) != 0) {
			roundOff = roundOff.subtract(previousRoundOff);
		}

		if (roundOff.compareTo(BigDecimal.ZERO) != 0) {
			DemandDetail roundOffDemandDetail = DemandDetail.builder().taxAmount(roundOff)
					.taxHeadMasterCode(WSCalculationConstant.WS_Round_Off).tenantId(tenantId)
					.collectionAmount(BigDecimal.ZERO).build();
			demandDetails.add(roundOffDemandDetail);
		}
	}

	/**
	 * Searches demand for the given consumerCode and tenantIDd
	 * 
	 * @param tenantId      The tenantId of the tradeLicense
	 * @param consumerCodes The set of consumerCode of the demands
	 * @param requestInfo   The RequestInfo of the incoming request
	 * @return Lis to demands for the given consumerCode
	 */
	public List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, Long taxPeriodFrom, Long taxPeriodTo,
			RequestInfo requestInfo, Boolean isDemandPaid, Boolean isDisconnectionRequest,
			Boolean isReconnectionRequest) {
		Object result = serviceRequestRepository.fetchResult(
				getDemandSearchURL(tenantId, consumerCodes, taxPeriodFrom, taxPeriodTo, isDemandPaid,
						isDisconnectionRequest, isReconnectionRequest),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		log.info("Search demand for reconnection is " + result);
		try {
			return mapper.convertValue(result, DemandResponse.class).getDemands();
		} catch (IllegalArgumentException e) {
			throw new CustomException("EG_WS_PARSING_ERROR", "Failed to parse response from Demand Search");
		}

	}

	/**
	 * Creates demand Search url based on tenantId,businessService, and
	 * 
	 * @return demand search url
	 */
	public StringBuilder getDemandSearchURLForDemandId() {
		StringBuilder url = new StringBuilder(configs.getBillingServiceHost());
		url.append(configs.getDemandSearchEndPoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("businessService=");
		url.append("{2}");
		url.append("&");
		url.append("consumerCode=");
		url.append("{3}");
		url.append("&");
		url.append("isPaymentCompleted=false");
		return url;
	}

	/**
	 * 
	 * @param tenantId     TenantId
	 * @param consumerCode Connection number
	 * @param requestInfo  - RequestInfo
	 * @return List of Demand
	 */
	private List<Demand> searchDemandBasedOnConsumerCode(String tenantId, String consumerCode, RequestInfo requestInfo,
			String businessService) {
		String uri = getDemandSearchURLForDemandId().toString();
		uri = uri.replace("{1}", tenantId);
		uri = uri.replace("{2}", businessService);
		uri = uri.replace("{3}", consumerCode);
		Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		try {
			return mapper.convertValue(result, DemandResponse.class).getDemands();
		} catch (IllegalArgumentException e) {
			throw new CustomException("EG_WS_PARSING_ERROR", "Failed to parse response from Demand Search");
		}
	}

	/**
	 * Creates demand Search url based on tenantId,businessService, period from,
	 * period to and ConsumerCode
	 * 
	 * @return demand search url
	 */
	public StringBuilder getDemandSearchURL(String tenantId, Set<String> consumerCodes, Long taxPeriodFrom,
			Long taxPeriodTo, Boolean isDemandPaid, Boolean isDisconnectionRequest, Boolean isReconnectionRequest) {
		StringBuilder url = new StringBuilder(configs.getBillingServiceHost());
		String businessService = taxPeriodFrom == null && !isDisconnectionRequest ? ONE_TIME_FEE_SERVICE_FIELD
				: configs.getBusinessService();
		if (isReconnectionRequest)
			businessService = "WSReconnection";
		url.append(configs.getDemandSearchEndPoint());
		url.append("?");
		url.append("tenantId=");
		url.append(tenantId);
		url.append("&");
		url.append("businessService=");
		url.append(businessService);
		url.append("&");
		url.append("consumerCode=");
		url.append(StringUtils.join(consumerCodes, ','));
		if (taxPeriodFrom != null) {
			url.append("&");
			url.append("periodFrom=");
			url.append(taxPeriodFrom.toString());
		}
		if (taxPeriodTo != null) {
			url.append("&");
			url.append("periodTo=");
			url.append(taxPeriodTo.toString());
		}
		if (isDemandPaid != null) {
			url.append("&");
			url.append("isPaymentCompleted=");
			url.append(isDemandPaid);
		}
		return url;
	}

	/**
	 * 
	 * @param getBillCriteria    Bill Criteria
	 * @param requestInfoWrapper contains request info wrapper
	 * @return updated demand response
	 */
	public List<Demand> updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper,
			Boolean isCallFromBulkGen) {

		if (getBillCriteria.getAmountExpected() == null)
			getBillCriteria.setAmountExpected(BigDecimal.ZERO);
		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		Map<String, JSONArray> billingSlabMaster = new HashMap<>();

		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		mstrDataService.setWaterConnectionMasterValues(requestInfo, getBillCriteria.getTenantId(), billingSlabMaster,
				timeBasedExemptionMasterMap);
		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getConnectionNumber()));

		DemandResponse res = mapper.convertValue(
				repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);

		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(WSCalculationConstant.EMPTY_DEMAND_ERROR_CODE, WSCalculationConstant.EMPTY_DEMAND_ERROR_MESSAGE);
			throw new CustomException(map);
		}
		List<Demand> demands = res.getDemands();
		demands = demands.stream()
				.filter(i -> !WSCalculationConstant.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(i.getStatus().toString()))
				.collect(Collectors.toList());

		log.info("Demands are of size " + res.getDemands().size());

		Map<String, Demand> consumerCodeToDemandMap = demands.stream()
				.collect(Collectors.toMap(Demand::getId, Function.identity()));
		List<Demand> demandsToBeUpdated = new LinkedList<>();
		boolean isMigratedCon = isMigratedConnection(getBillCriteria.getConsumerCodes().get(0),
				getBillCriteria.getTenantId());
		log.info("-------updateDemands------------isMigratedCon--------" + isMigratedCon);

		// Loop through the consumerCodes and re-calculate the time base applicable

		demands.sort((d1, d2) -> d1.getTaxPeriodFrom().compareTo(d2.getTaxPeriodFrom()));
		log.info("-------updateDemands------------demands--------" + demands);
		Demand oldDemand = demands.get(0);
		log.info("-------updateDemands------------oldDemand--------" + oldDemand);
		String tenantId = getBillCriteria.getTenantId();

		List<TaxPeriod> taxPeriods = mstrDataService.getTaxPeriodList(requestInfoWrapper.getRequestInfo(), tenantId,
				WSCalculationConstant.SERVICE_FIELD_VALUE_WS);
		consumerCodeToDemandMap.forEach((id, demand) -> {
			BigDecimal totalTax = demand.getDemandDetails().stream().map(DemandDetail::getTaxAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal totalCollection = demand.getDemandDetails().stream().map(DemandDetail::getCollectionAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			List<String> taxHeadMasterCodes = demand.getDemandDetails().stream().map(DemandDetail::getTaxHeadMasterCode)
					.collect(Collectors.toList());
			;

			log.info("Demand Id: " + demand.getId());
			log.info(" taxHeadMasterCodes " + taxHeadMasterCodes);

			log.info(" isMigratedCon " + isMigratedCon);
			log.info(" oldDemand.getId().equalsIgnoreCase(demand.getId()) "
					+ !oldDemand.getId().equalsIgnoreCase(demand.getId()));
			log.info(" Payment Completed  " + demand.getIsPaymentCompleted());
			log.info("Total Tax " + totalTax);
			log.info("Total totalCollection " + totalCollection);
			Boolean abc = totalTax.compareTo(totalCollection) > 0;

			log.info(" tax condition " + abc);
			log.info(" penalty taxhead code " + taxHeadMasterCodes.contains(WSCalculationConstant.WS_TIME_PENALTY));

			if (!(isMigratedCon && oldDemand.getId().equalsIgnoreCase(demand.getId()))) {
				log.info("-------updateDemands-----inside if-------demand.getId()--------" + demand.getId()
						+ "-------oldDemand.getId()---------" + oldDemand.getId());
				if (!demand.getIsPaymentCompleted() && totalTax.compareTo(totalCollection) > 0
						&& !taxHeadMasterCodes.contains(WSCalculationConstant.WS_TIME_PENALTY)) {
					log.info(" Inside Update if ");

					if (demand.getStatus() != null && WSCalculationConstant.DEMAND_CANCELLED_STATUS
							.equalsIgnoreCase(demand.getStatus().toString()))
						throw new CustomException(WSCalculationConstant.EG_WS_INVALID_DEMAND_ERROR,
								WSCalculationConstant.EG_WS_INVALID_DEMAND_ERROR_MSG);
					applyTimeBasedApplicables(demand, requestInfoWrapper, timeBasedExemptionMasterMap, taxPeriods);
					addRoundOffTaxHead(tenantId, demand.getDemandDetails());
					demandsToBeUpdated.add(demand);
				} else {
					addRoundOffTaxHead(tenantId, demand.getDemandDetails());
					demandsToBeUpdated.add(demand);
				}
			}
		});
		log.info("demand to be update123 " + demandsToBeUpdated);
		// Call demand update in bulk to update the interest or penalty
		DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
		log.info("Is call For Bulk Gen" + isCallFromBulkGen);
		if (!isCallFromBulkGen)
			repository.fetchResult(utils.getUpdateDemandUrl(), request);
		log.info("demand to be update " + demandsToBeUpdated);
		return demandsToBeUpdated;

	}

	private boolean isMigratedConnection(final String connectionNumber, final String tenantId) {
		Boolean isMigrated = false;
		String connectionAddlDetail = waterConnectionRepository.fetchConnectionAdditonalDetails(connectionNumber,
				tenantId);
		log.info("isMigratedConnection-----connectionAddlDetail-->" + connectionAddlDetail);
		Map<String, Object> result = null;
		try {
			result = mapper.readValue(connectionAddlDetail, HashMap.class);
			isMigrated = (Boolean) result.getOrDefault("isMigrated", false);
		} catch (Exception e) {
			log.error("Exception while reading connection migration flag");
		}
		log.info("isMigratedConnection-----isMigrated-->" + isMigrated);
		return isMigrated;

	}

	/**
	 * Updates demand for the given list of calculations
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations List of calculation object
	 * @return Demands that are updated
	 */
	private List<Demand> updateDemandForCalculation(RequestInfo requestInfo, List<Calculation> calculations,
			Long fromDate, Long toDate, boolean isForConnectionNo, Boolean isDisconnectionRequest,
			Boolean isReconnectionRequest) {
		List<Demand> demands = new LinkedList<>();
		Long fromDateSearch = isForConnectionNo ? fromDate : null;
		Long toDateSearch = isForConnectionNo ? toDate : null;

		for (Calculation calculation : calculations) {
			Set<String> consumerCodes = isForConnectionNo
					? Collections.singleton(calculation.getWaterConnection().getConnectionNo())
					: Collections.singleton(calculation.getWaterConnection().getApplicationNo());
			List<Demand> searchResult = new ArrayList<>();
			if (isDisconnectionRequest) {
				searchResult = searchDemandForDisconnectionRequest(calculation.getTenantId(), consumerCodes, null,
						toDateSearch, requestInfo, null, isDisconnectionRequest);
			} else {
				searchResult = searchDemand(calculation.getTenantId(), consumerCodes, fromDateSearch, toDateSearch,
						requestInfo, null, isDisconnectionRequest, isReconnectionRequest);
			}
			if (CollectionUtils.isEmpty(searchResult))
				throw new CustomException("EG_WS_INVALID_DEMAND_UPDATE",
						"No demand exists for Number: " + consumerCodes.toString());
			Demand demand = searchResult.get(0);
			String tenantId = calculation.getTenantId();
			demand.setDemandDetails(
					getUpdatedDemandDetails(calculation, demand.getDemandDetails(), isDisconnectionRequest));

			if (isForConnectionNo) {
				WaterConnection connection = calculation.getWaterConnection();
				if (connection == null) {
					List<WaterConnection> waterConnectionList = calculatorUtils.getWaterConnection(requestInfo,
							calculation.getConnectionNo(), calculation.getTenantId());
					int size = waterConnectionList.size();
					connection = waterConnectionList.get(size - 1);

				}

				if (connection.getApplicationType().equalsIgnoreCase(MODIFY_WATER_CONNECTION)) {
					WaterConnectionRequest waterConnectionRequest = WaterConnectionRequest.builder()
							.waterConnection(connection).requestInfo(requestInfo).build();
					Property property = wsCalculationUtil.getProperty(waterConnectionRequest);
					User owner = property.getOwners().get(0).toCommonUser();
					if (!CollectionUtils.isEmpty(waterConnectionRequest.getWaterConnection().getConnectionHolders())) {
						owner = waterConnectionRequest.getWaterConnection().getConnectionHolders().get(0)
								.toCommonUser();
					}
					owner = getPlainOwnerDetails(requestInfo, owner.getUuid(), tenantId);
					if (!(demand.getPayer().getUuid().equalsIgnoreCase(owner.getUuid())))
						demand.setPayer(owner);
				}

			}

			demands.add(demand);
		}

		log.info("Updated Demand Details " + demands.toString());
		return demandRepository.updateDemand(requestInfo, demands);
	}

	/**
	 * Applies Penalty/Rebate/Interest to the incoming demands
	 * 
	 * If applied already then the demand details will be updated
	 * 
	 * @param demand                      - Demand Object
	 * @param requestInfoWrapper          RequestInfoWrapper Object
	 * @param timeBasedExemptionMasterMap - List of TimeBasedExemption details
	 * @param taxPeriods                  - List of tax periods
	 * @return Returns TRUE if successful, FALSE otherwise
	 */

	private boolean applyTimeBasedApplicables(Demand demand, RequestInfoWrapper requestInfoWrapper,
			Map<String, JSONArray> timeBasedExemptionMasterMap, List<TaxPeriod> taxPeriods) {

		String tenantId = demand.getTenantId();
		String demandId = demand.getId();
		Long expiryDate = demand.getBillExpiryTime();
		TaxPeriod taxPeriod = taxPeriods.stream().filter(t -> demand.getTaxPeriodFrom().compareTo(t.getFromDate()) >= 0
				&& demand.getTaxPeriodTo().compareTo(t.getToDate()) <= 0).findAny().orElse(null);

		log.info("expiry date is ===" + expiryDate);

		log.info("Current time is ---" + System.currentTimeMillis());
		if (taxPeriod == null) {
			log.info("Demand Expired!! ->> Consumer Code " + demand.getConsumerCode() + " Demand Id -->> "
					+ demand.getId());
			return false;
		}
		boolean isCurrentDemand = false;
		if (!(taxPeriod.getFromDate() <= System.currentTimeMillis()
				&& taxPeriod.getToDate() >= System.currentTimeMillis()))
			isCurrentDemand = true;

		if (expiryDate < System.currentTimeMillis()) {
			BigDecimal waterChargeApplicable = BigDecimal.ZERO;
			BigDecimal oldPenalty = BigDecimal.ZERO;
			BigDecimal oldInterest = BigDecimal.ZERO;
			BigDecimal oldRebate = BigDecimal.ZERO;

			for (DemandDetail detail : demand.getDemandDetails()) {
				if (WSCalculationConstant.TAX_APPLICABLE.contains(detail.getTaxHeadMasterCode())) {
					waterChargeApplicable = waterChargeApplicable.add(detail.getTaxAmount());
				}
				if (detail.getTaxHeadMasterCode().equalsIgnoreCase(WSCalculationConstant.WS_TIME_PENALTY)) {
					oldPenalty = oldPenalty.add(detail.getTaxAmount());
				}
				if (detail.getTaxHeadMasterCode().equalsIgnoreCase(WSCalculationConstant.WS_TIME_INTEREST)) {
					oldInterest = oldInterest.add(detail.getTaxAmount());
				}

				if (detail.getTaxHeadMasterCode().equalsIgnoreCase(WSCalculationConstant.WS_TIME_REBATE)) {
					oldRebate = oldRebate.add(detail.getTaxAmount());
				}
			}

			boolean isPenaltyUpdated = false;
			boolean isInterestUpdated = false;
			boolean isRebateUpdated = false;
			List<DemandDetail> details = demand.getDemandDetails();

			Map<String, BigDecimal> interestPenaltyRebateEstimates = payService.applyPenaltyRebateAndInterest(
					waterChargeApplicable, taxPeriod.getFinancialYear(), timeBasedExemptionMasterMap, expiryDate,
					demand);
			log.info("old penalty amount is " + oldPenalty);
			log.info("old interest amount is " + oldInterest);
			log.info("old rebate amount is " + oldRebate);

			BigDecimal penalty = interestPenaltyRebateEstimates.get(WSCalculationConstant.WS_TIME_PENALTY);
			BigDecimal interest = interestPenaltyRebateEstimates.get(WSCalculationConstant.WS_TIME_INTEREST);
			BigDecimal rebate = interestPenaltyRebateEstimates.get(WSCalculationConstant.WS_TIME_REBATE);
			log.info("penalty amount after calculation is " + penalty);
			log.info("interest amount after calculation  is " + interest);
			log.info("rebate amount after calculation is " + rebate);

			if (penalty == null)
				penalty = BigDecimal.ZERO;
			if (interest == null)
				interest = BigDecimal.ZERO;
			if (rebate == null)
				rebate = BigDecimal.ZERO;

			DemandDetailAndCollection latestPenaltyDemandDetail, latestInterestDemandDetail, latestRebateDemandDetail;

			if (interest.compareTo(BigDecimal.ZERO) != 0) {
				latestInterestDemandDetail = utils
						.getLatestDemandDetailByTaxHead(WSCalculationConstant.WS_TIME_INTEREST, details);
				if (latestInterestDemandDetail != null) {
					updateTaxAmount(interest, latestInterestDemandDetail);
					isInterestUpdated = true;
				}
			}

			if (penalty.compareTo(BigDecimal.ZERO) != 0) {
				latestPenaltyDemandDetail = utils.getLatestDemandDetailByTaxHead(WSCalculationConstant.WS_TIME_PENALTY,
						details);
				if (latestPenaltyDemandDetail != null) {
					updateTaxAmount(penalty, latestPenaltyDemandDetail);
					isPenaltyUpdated = true;
				}
			}

			if (oldRebate.compareTo(BigDecimal.ZERO) != 0 || rebate.compareTo(BigDecimal.ZERO) != 0) {
				latestRebateDemandDetail = utils.getLatestDemandDetailByTaxHead(WSCalculationConstant.WS_TIME_REBATE,
						details);
				if (latestRebateDemandDetail != null) {
					updateRebate(rebate, latestRebateDemandDetail);
					isRebateUpdated = true;
				}
			}

			if (!isPenaltyUpdated && penalty.compareTo(BigDecimal.ZERO) > 0)
				details.add(DemandDetail.builder().taxAmount(penalty.setScale(2, 2))
						.taxHeadMasterCode(WSCalculationConstant.WS_TIME_PENALTY).demandId(demandId).tenantId(tenantId)
						.build());
			if (!isInterestUpdated && interest.compareTo(BigDecimal.ZERO) > 0)
				details.add(DemandDetail.builder().taxAmount(interest.setScale(2, 2))
						.taxHeadMasterCode(WSCalculationConstant.WS_TIME_INTEREST).demandId(demandId).tenantId(tenantId)
						.build());

			if (!isRebateUpdated && rebate.compareTo(BigDecimal.ZERO) != 0)
				details.add(DemandDetail.builder().taxAmount(rebate.setScale(2, 2))
						.taxHeadMasterCode(WSCalculationConstant.WS_TIME_REBATE).demandId(demandId).tenantId(tenantId)
						.build());
		}
		log.info("Is current Demand  " + isCurrentDemand);
		return isCurrentDemand;
	}

	/**
	 * Updates the amount in the latest demandDetail by adding the diff between new
	 * and old amounts to it
	 * 
	 * @param newAmount        The new tax amount for the taxHead
	 * @param latestDetailInfo The latest demandDetail for the particular taxHead
	 */
	private void updateTaxAmount(BigDecimal newAmount, DemandDetailAndCollection latestDetailInfo) {
		BigDecimal diff = newAmount.subtract(latestDetailInfo.getTaxAmountForTaxHead());
		BigDecimal newTaxAmountForLatestDemandDetail = latestDetailInfo.getLatestDemandDetail().getTaxAmount()
				.add(diff);
		latestDetailInfo.getLatestDemandDetail().setTaxAmount(newTaxAmountForLatestDemandDetail);
	}

	private void updateRebate(BigDecimal newAmount, DemandDetailAndCollection latestDetailInfo) {
		BigDecimal diff = BigDecimal.ZERO;
		if (newAmount.compareTo(BigDecimal.ZERO) == 0)
			diff = BigDecimal.ZERO;
		else
			diff = newAmount;

		log.info("Rebate after calculation is " + diff);

		latestDetailInfo.getLatestDemandDetail().setTaxAmount(diff);
	}



	/**
	 * 
	 * @param tenantId TenantId for getting master data.
	 */
	public void generateDemandForTenantId(String tenantId, RequestInfo requestInfo) {
		requestInfo.getUserInfo().setTenantId(tenantId);
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo, tenantId);
		long taxPeriodFrom = billingMasterData.get("taxPeriodFrom") == null ? 0l
				: (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = billingMasterData.get("taxPeriodTo") == null ? 0l
				: (long) billingMasterData.get("taxPeriodTo");
		log.info("generateDemandForTenantId:: " + tenantId + " taxPeriodFrom:: " + taxPeriodFrom + " taxPeriodTo "
				+ taxPeriodTo);
		if (taxPeriodFrom == 0 || taxPeriodTo == 0) {
			throw new CustomException("NO_BILLING_PERIODS",
					"MDMS Billing Period does not available for tenant: " + tenantId);
		}

		generateDemandForULB(billingMasterData, requestInfo, tenantId, taxPeriodFrom, taxPeriodTo);
	}

	public String generateDemandForConsumerCode(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo,
				bulkBillCriteria.getTenantId());

		long startDay = (((int) billingMasterData.get(WSCalculationConstant.Demand_Generate_Date_String)) / 86400000);
		if (isCurrentDateIsMatching((String) billingMasterData.get(WSCalculationConstant.Billing_Cycle_String),
				startDay)) {

			Map<String, Object> masterMap = mstrDataService.loadMasterData(requestInfo, bulkBillCriteria.getTenantId());

			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(WSCalculationConstant.Billing_Period_Master);
			mstrDataService.enrichBillingPeriod(null, billingFrequencyMap, masterMap,
					WSCalculationConstant.nonMeterdConnection);

			Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
					.get(WSCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);

			List<WaterConnection> connections = waterCalculatorDao.getConnection(bulkBillCriteria.getTenantId(),
					bulkBillCriteria.getConsumerCode(), WSCalculationConstant.nonMeterdConnection, fromDate, toDate);
			log.info("connection after search in cal" + connections.size() + " with from and to date as" + fromDate
					+ "  " + toDate);
			connections = enrichmentService.filterConnections(connections);
			String assessmentYear = estimationService.getAssessmentYear();
			log.info("Size of the connection list after filter : " + connections.size());

			if (connections.size() > 0) {
				List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
				for (WaterConnection connection : connections) {
					CalculationCriteria calculationCriteria = CalculationCriteria.builder()
							.tenantId(bulkBillCriteria.getTenantId()).assessmentYear(assessmentYear)
							.connectionNo(connection.getConnectionNo()).waterConnection(connection).build();
					calculationCriteriaList.add(calculationCriteria);
				}
				MigrationCount migrationCount = MigrationCount.builder().tenantid(bulkBillCriteria.getTenantId())
						.businessService("WS").limit((long) 1.00).id(UUID.randomUUID().toString()).offset((long) 1.00)
						.createdTime(System.currentTimeMillis()).recordCount(Long.valueOf(connections.size())).build();

				CalculationReq calculationReq = CalculationReq.builder().calculationCriteria(calculationCriteriaList)
						.requestInfo(requestInfo).isconnectionCalculation(true).migrationCount(migrationCount).build();

				wsCalculationProducer.push(configs.getCreateDemand(), calculationReq);

				calculationCriteriaList.clear();
				return "Demand Generated successfully for this consumer Code.";
			} else {
				return "Either connection is Metered or Demand is already generated for last billing cycle for this consumercode";
			}

		}
		return "Some error occured!!";
	}

	public String SingleDemandGenerate(String tenantId, SingleDemand singledemand) {
		singledemand.getRequestInfo().getUserInfo().setTenantId(tenantId);
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterDatas(singledemand, tenantId);
		long taxPeriodFrom = billingMasterData.get("taxPeriodFrom") == null ? 0l
				: (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = billingMasterData.get("taxPeriodTo") == null ? 0l
				: (long) billingMasterData.get("taxPeriodTo");
		log.info("generateDemandForTenantId:: " + tenantId + " taxPeriodFrom:: " + taxPeriodFrom + " taxPeriodTo "
				+ taxPeriodTo);
		if (taxPeriodFrom == 0 || taxPeriodTo == 0) {
			throw new CustomException("NO_BILLING_PERIODS",
					"MDMS Billing Period does not available for tenant: " + tenantId);
		}

		return generateDemandForSingle(billingMasterData, singledemand, tenantId, taxPeriodFrom, taxPeriodTo);
	}

	/*
	 * CANCEL BILL
	 */

	public CancelDemand cancelDemandForConsumer(CancelDemand cancelDemand) {

		for (CancelList CancelList : cancelDemand.getCancelList()) {
			String businessService = CancelList.getBusinessService();
			String consumerCode = CancelList.getConsumerCode();
			String tenantId = cancelDemand.getTenantId();
			Long taxPeriodFrom = cancelDemand.gettaxPeriodFrom();
			Long taxPeriodTo = cancelDemand.gettaxPeriodTo();

			Set<String> consumerCodeset = new HashSet<>();
			consumerCodeset.add(consumerCode);
			CancelList.setConsumerCode(consumerCode);
			consumerCodeset.add(businessService);
			CancelList.setBusinessService(businessService);
			consumerCodeset.add(tenantId);

			Set<Long> consumerCodesets = new HashSet<>();
			consumerCodesets.add(taxPeriodFrom);
			cancelDemand.settaxPeriodFrom(taxPeriodFrom);
			consumerCodesets.add(taxPeriodTo);
			cancelDemand.settaxPeriodFrom(taxPeriodTo);

			List<Canceldemandsearch> demandlist = waterCalculatorDao.getConnectionCancel(businessService, tenantId,
					consumerCode, taxPeriodFrom, taxPeriodTo);
			List<BillSearch> billSearch = waterCalculatorDao.getBill(consumerCode, businessService);

			if (!demandlist.isEmpty()) {
//            for (Canceldemandsearch connectionNo : demandlist) 
				{
					boolean billCancelled = false;
					Boolean Cancel = waterCalculatorDao.getUpdate(demandlist);
					if (Cancel) {
						billCancelled = waterCalculatorDao.getexpiryBill(billSearch);
					}

				}
			}

		}
		return cancelDemand;
	}

	public String generateDemandForSingle(Map<String, Object> master, SingleDemand singleDemand, String tenantId,
			Long taxPeriodFrom, Long taxPeriodTo) {
		RequestInfo requestInfo = singleDemand.getRequestInfo();
		String tempvariable = "";
		log.info("generateDemandForULB:: " + tenantId + " taxPeriodFrom:: " + taxPeriodFrom + " taxPeriodTo "
				+ taxPeriodTo);
		try {
			List<TaxPeriod> taxPeriods = calculatorUtils.getTaxPeriodsFromMDMS(requestInfo, tenantId);

			int generateDemandToIndex = IntStream.range(0, taxPeriods.size())
					.filter(p -> taxPeriodFrom.equals(taxPeriods.get(p).getFromDate())).findFirst().getAsInt();
			String cone = singleDemand.getConsumercode();
			log.info("Billing master data values for non metered connection:: {}", master);
			List<WaterDetails> connectionNos = waterCalculatorDao.getConnectionsNoListforsingledemand(tenantId,
					WSCalculationConstant.nonMeterdConnection, taxPeriodFrom, taxPeriodTo, cone);
			int bulkSaveDemandCount = configs.getBulkSaveDemandCount() != null ? configs.getBulkSaveDemandCount() : 1;
			log.info("Total Connections: {} and batch count: {}", connectionNos.size(), bulkSaveDemandCount);

			if (connectionNos == null || connectionNos.size() <= 0) {
				tempvariable = null;
				throw new IllegalArgumentException("Demand not generated: No connections found");
			}
			List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
			int connectionNosCount = 0;
			int totalRecordsPushedToKafka = 0;
			int threadSleepCount = 0;
			for (int connectionNosIndex = 0; connectionNosIndex < connectionNos.size(); connectionNosIndex++) {
				WaterDetails waterConnection = connectionNos.get(connectionNosIndex);
				connectionNosCount++;
				log.info("Connection Number: {} ", waterConnection.getConnectionNo());

				try {
					int generateDemandFromIndex = 0;
					Long lastDemandFromDate = waterCalculatorDao
							.searchLastDemandGenFromDate(waterConnection.getConnectionNo(), tenantId);

					if (lastDemandFromDate != null) {
						generateDemandFromIndex = IntStream.range(0, taxPeriods.size())
								.filter(p -> lastDemandFromDate.equals(taxPeriods.get(p).getFromDate())).findFirst()
								.getAsInt();
						generateDemandFromIndex++;
					}
					for (int taxPeriodIndex = generateDemandFromIndex; generateDemandFromIndex <= generateDemandToIndex; taxPeriodIndex++) {
						generateDemandFromIndex++;
						TaxPeriod taxPeriod = taxPeriods.get(taxPeriodIndex);

						boolean isConnectionValid = isValidBillingCycle(waterConnection, requestInfo, tenantId,
								taxPeriod.getFromDate(), taxPeriod.getToDate());
						if (isConnectionValid) {
							CalculationCriteria calculationCriteria = CalculationCriteria.builder().tenantId(tenantId)
									.assessmentYear(taxPeriod.getFinancialYear()).from(taxPeriod.getFromDate())
									.to(taxPeriod.getToDate()).connectionNo(waterConnection.getConnectionNo()).build();
							calculationCriteriaList.add(calculationCriteria);
							log.info("connectionNosIndex: {} and connectionNos.size(): {}", connectionNosIndex,
									connectionNos.size());

						} else {
							log.info("Invalid Connection");
							log.info("connectionNosIndex: {} and connectionNos.size(): {}", connectionNosIndex,
									connectionNos.size());
						}

					}
					if (calculationCriteriaList.isEmpty())
						continue;

					if (connectionNosCount == bulkSaveDemandCount) {
						/*
						 * log.info(
						 * "Controller entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}"
						 * , connectionNosCount, connectionNos.size());
						 */

						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList).requestInfo(requestInfo)
								.isconnectionCalculation(true).build();
						log.info(
								"Pushing calculation req to the kafka topic with bulk data of calculationCriteriaList size: {}",
								calculationCriteriaList.size());

						wsCalculationProducer.push(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka = totalRecordsPushedToKafka + calculationCriteriaList.size();
						calculationCriteriaList.clear();
						connectionNosCount = 0;
						if (threadSleepCount == 3) {
							Thread.sleep(15000);
							threadSleepCount = 0;
						}
						threadSleepCount++;

					} else if (connectionNosIndex == connectionNos.size() - 1) {
						/*
						 * log.info(
						 * "Last connection entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}"
						 * , connectionNosCount, connectionNos.size());
						 */
						MigrationCount migrationCount = MigrationCount.builder().tenantid(tenantId)
								.businessService("WS").id(UUID.randomUUID().toString())
								.createdTime(System.currentTimeMillis()).build();

						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList).requestInfo(requestInfo)
								.isconnectionCalculation(true).migrationCount(migrationCount).build();

						log.info(
								"Pushing calculation last req to the kafka topic with bulk data of calculationCriteriaList size: {}",
								calculationCriteriaList.size());

						wsCalculationProducer.push(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka = totalRecordsPushedToKafka + calculationCriteriaList.size();
						calculationCriteriaList.clear();
						connectionNosCount = 0;

					}
					tempvariable = waterConnection.getConnectionNo();
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Exception occurred while generating demand for water connectionno: "
							+ waterConnection.getConnectionNo() + " tenantId: " + tenantId + " Exception msg:"
							+ e.getMessage());
				}

			}
			log.info("totalRecordsPushedToKafka: {}", totalRecordsPushedToKafka);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occurred while processing the demand generation for tenantId: " + tenantId);
		}
		return tempvariable;
	}

	private boolean isValidBillingCycles(WaterDetails waterConnection, RequestInfo requestInfo, String tenantId,
			long taxPeriodFrom, long taxPeriodTo) {
		// TODO Auto-generated method stub

		boolean isConnectionValid = true;

		if (waterConnection.getConnectionExecutionDate() > taxPeriodTo)
			isConnectionValid = false;
		/*
		 * if (waterConnection.getConnectionExecutionDate() < taxPeriodFrom) {
		 * 
		 * isConnectionValid = fetchBill(waterConnection, taxPeriodFrom, taxPeriodTo,
		 * tenantId, requestInfo);
		 * 
		 * }
		 */

		return isConnectionValid;

	}

	public List<WaterConnection> getConnectionPendingForDemand(RequestInfo requestInfo, String tenantId) {
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo, tenantId);
		List<WaterConnection> connections = new ArrayList<WaterConnection>();
		long startDay = (((int) billingMasterData.get(WSCalculationConstant.Demand_Generate_Date_String)) / 86400000);
		if (isCurrentDateIsMatching((String) billingMasterData.get(WSCalculationConstant.Billing_Cycle_String),
				startDay)) {

			Map<String, Object> masterMap = mstrDataService.loadMasterData(requestInfo, tenantId);

			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(WSCalculationConstant.Billing_Period_Master);
			mstrDataService.enrichBillingPeriod(null, billingFrequencyMap, masterMap,
					WSCalculationConstant.nonMeterdConnection);

			Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
					.get(WSCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);

			// long count = waterCalculatorDao.getConnectionCount(tenantId, fromDate,
			// toDate);

			log.info("fromDate:" + fromDate);
			log.info("toDate:" + toDate);
			connections = waterCalculatorDao.getConnectionsNoListForDemand(tenantId,
					WSCalculationConstant.nonMeterdConnection, fromDate, toDate);
			log.info("Connection Count for pending demand: " + connections.size());

			connections = enrichmentService.filterConnections(connections);
		}
		return connections;

	}



	/**
	 * 
	 * @param master      Master MDMS Data
	 * @param requestInfo Request Info
	 * @param tenantId    Tenant Id
	 */
	public void generateDemandForULB(Map<String, Object> master, RequestInfo requestInfo, String tenantId,
                                 Long taxPeriodFrom, Long taxPeriodTo) {
    log.info("generateDemandForULB:: " + tenantId + " taxPeriodFrom:: " + taxPeriodFrom + " taxPeriodTo " + taxPeriodTo);
    try {
        List<TaxPeriod> taxPeriods = calculatorUtils.getTaxPeriodsFromMDMS(requestInfo, tenantId);
        int generateDemandToIndex = IntStream.range(0, taxPeriods.size())
                .filter(p -> taxPeriodFrom.equals(taxPeriods.get(p).getFromDate())).findFirst().getAsInt();
        String cone = requestInfo.getKey();
        log.info("Billing master data values for non metered connection:: {}", master);
        List<WaterDetails> connectionNos = waterCalculatorDao.getConnectionsNoListforsingledemand(tenantId,
                WSCalculationConstant.nonMeterdConnection, taxPeriodFrom, taxPeriodTo, cone);

        // Set batch size to 30
        int bulkSaveDemandCount = configs.getBatchSize();
        log.info("Total Connections: {} and batch count: {}", connectionNos.size(), bulkSaveDemandCount);

        List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
        int totalRecordsPushedToKafka = 0;

        for (int batchStart = 0; batchStart < connectionNos.size(); batchStart += bulkSaveDemandCount) {
            // Fetch sublist for the current batch (30 records)
            List<WaterDetails> currentBatch = connectionNos.subList(batchStart, 
                                    Math.min(batchStart + bulkSaveDemandCount, connectionNos.size()));
            log.info("Processing batch: {} to {}", batchStart, 
                                    Math.min(batchStart + bulkSaveDemandCount, connectionNos.size()));

            for (WaterDetails waterConnection : currentBatch) {
                log.info("Connection Number: {} ", waterConnection.getConnectionNo());

                try {
                    int generateDemandFromIndex = 0;
                    Long lastDemandFromDate = waterCalculatorDao
                            .searchLastDemandGenFromDate(waterConnection.getConnectionNo(), tenantId);

                    if (lastDemandFromDate != null) {
                        generateDemandFromIndex = IntStream.range(0, taxPeriods.size())
                                .filter(p -> lastDemandFromDate.equals(taxPeriods.get(p).getFromDate())).findFirst()
                                .getAsInt();
                        generateDemandFromIndex++; // Move to next quarter demand
                    }

                    log.info("lastDemandFromDate: {} and generateDemandFromIndex: {}", lastDemandFromDate,
                            generateDemandFromIndex);

                    // Loop through each tax period within the valid range
                    for (int taxPeriodIndex = generateDemandFromIndex; taxPeriodIndex <= generateDemandToIndex; taxPeriodIndex++) {
                        TaxPeriod taxPeriod = taxPeriods.get(taxPeriodIndex);
                        log.info("FromPeriod: {} and ToPeriod: {}", taxPeriod.getFromDate(), taxPeriod.getToDate());

                        boolean isConnectionValid = isValidBillingCycle(waterConnection, requestInfo, tenantId,
                                taxPeriod.getFromDate(), taxPeriod.getToDate());

                        if (isConnectionValid) {
                            CalculationCriteria calculationCriteria = CalculationCriteria.builder()
                                    .tenantId(tenantId)
                                    .assessmentYear(taxPeriod.getFinancialYear())
                                    .from(taxPeriod.getFromDate())
                                    .to(taxPeriod.getToDate())
                                    .connectionNo(waterConnection.getConnectionNo())
                                    .build();
                            calculationCriteriaList.add(calculationCriteria);
                        } else {
                            log.info("Invalid Connection");
                        }
                    }

                } catch (Exception e) {
                    log.error("Exception occurred while processing water connectionNo: " 
                        + waterConnection.getConnectionNo() + ", tenantId: " + tenantId, e);
                }
            }

            // Push the batch to Kafka once the sublist is processed
            if (!calculationCriteriaList.isEmpty()) {
                log.info("Pushing calculation request to Kafka with batch size: {}", calculationCriteriaList.size());
                
                CalculationReq calculationReq = CalculationReq.builder()
                        .calculationCriteria(calculationCriteriaList)
                        .requestInfo(requestInfo)
                        .isconnectionCalculation(true)
                        .build();

                wsCalculationProducer.push(configs.getCreateDemand(), calculationReq);
                totalRecordsPushedToKafka += calculationCriteriaList.size();
                calculationCriteriaList.clear();  // Clear list for the next batch
            }

            // Sleep for 15 seconds between batches
            try {
                log.info("Sleeping for 15 seconds before processing the next batch...");
                Thread.sleep(configs.getSleepvalue() );
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                log.error("Sleep interrupted", ie);
            }
        }

        log.info("Total records pushed to Kafka: {}", totalRecordsPushedToKafka);

    } catch (Exception e) {
        log.error("Exception occurred while processing demand generation for tenantId: " + tenantId, e);
    }
}

	private boolean isValidBillingCycle(WaterDetails waterConnection, RequestInfo requestInfo, String tenantId,
			long taxPeriodFrom, long taxPeriodTo) {
		// TODO Auto-generated method stub

		boolean isConnectionValid = true;

		if (waterConnection.getConnectionExecutionDate() > taxPeriodTo)
			isConnectionValid = false;
		/*
		 * if (waterConnection.getConnectionExecutionDate() < taxPeriodFrom) {
		 * 
		 * isConnectionValid = fetchBill(waterConnection, taxPeriodFrom, taxPeriodTo,
		 * tenantId, requestInfo);
		 * 
		 * }
		 */

		return isConnectionValid;

	}

	private boolean fetchBill(WaterDetails waterConnection, long taxPeriodFrom, long taxPeriodTo, String tenantId,
			RequestInfo requestInfo) {

		final boolean[] isConnectionValid = { false };

		Object result = serviceRequestRepository.fetchResult(
				calculatorUtils.getFetchBillURL(tenantId, waterConnection.getConnectionNo()),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		BillResponseV2 billResponse = mapper.convertValue(result, BillResponseV2.class);
		billResponse.getBill().forEach(bill -> {
			bill.getBillDetails().forEach(billDetail -> {

				long previousBillingCycleToDate = taxPeriodFrom - 86400000;
				if (billDetail.getToPeriod() == previousBillingCycleToDate) {
					isConnectionValid[0] = true;
				}

			});

		});

		return isConnectionValid[0];

	}

	/**
	 * 
	 * @param billingFrequency Billing Frequency details
	 * @param dayOfMonth       Day of the given month
	 * @return true if current day is for generation of demand
	 */
	private boolean isCurrentDateIsMatching(String billingFrequency, long dayOfMonth) {
		if (billingFrequency.equalsIgnoreCase(WSCalculationConstant.Monthly_Billing_Period)
				&& (dayOfMonth <= LocalDateTime.now().getDayOfMonth())) {
			return true;
		} else if (billingFrequency.equalsIgnoreCase(WSCalculationConstant.Quaterly_Billing_Period)) {
			return false;
		}
		return true;
	}

	public boolean fetchBillForReconnect(List<Demand> demandResponse, RequestInfo requestInfo,
			Map<String, Object> masterMap) {
		boolean notificationSent = false;
		List<Demand> errorMap = new ArrayList<>();
		int successCount = 0;
		for (Demand demand : demandResponse) {
			try {
				Object result = serviceRequestRepository.fetchResult(
						calculatorUtils.getFetchBillURLForReconnection(demand.getTenantId(), demand.getConsumerCode()),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				log.info("Result of fetchbillReconnect is " + result);

				HashMap<String, Object> billResponse = new HashMap<>();
				billResponse.put("requestInfo", requestInfo);
				billResponse.put("billResponse", result);
//				log.info("Result"+result.toString());
				wsCalculationProducer.push(configs.getPayTriggers(), billResponse);
				notificationSent = true;
				successCount++;
			} catch (Exception ex) {
				log.error("Fetch Bill Error", ex);
				errorMap.add(demand);
			}
		}
		String uuid = demandResponse.get(0).getAuditDetails().getCreatedBy();
		if (errorMap.size() == demandResponse.size()) {
			paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
					masterMap, false);
		} else {
			if (!errorMap.isEmpty()) {
				paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
						masterMap, false);
			}
			paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
					masterMap, true);
		}
		return notificationSent;
	}

	public List<String> fetchBillSchedulerSingle(Set<String> consumerCodes, String tenantId, RequestInfo requestInfo) {
		List<String> consumercodesFromRes = new ArrayList<>();
		for (String consumerCode : consumerCodes) {

			try {

				StringBuilder fetchBillURL = calculatorUtils.getFetchBillURL(tenantId, consumerCode);

				Object result = serviceRequestRepository.fetchResult(fetchBillURL,
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				log.info("Bills generated for the consumercodes: {}", fetchBillURL);
				BillResponseV2 billResponse = mapper.convertValue(result, BillResponseV2.class);
				List<BillV2> bills = billResponse.getBill();
				if (bills != null && !bills.isEmpty()) {
					consumercodesFromRes
							.addAll(bills.stream().map(BillV2::getConsumerCode).collect(Collectors.toList()));
					log.info("Bill generated successfully for consumercode: {}, TenantId: {}", consumerCode, tenantId);
				}

			} catch (Exception ex) {
				log.error("Fetch Bill Error For tenantId:{} consumercode: {} and Exception is: {}", tenantId,
						consumerCodes, ex);
			}
		}
		return consumercodesFromRes;
	}

	public boolean fetchBill(List<Demand> demandResponse, RequestInfo requestInfo, Map<String, Object> masterMap) {
		boolean notificationSent = false;
		List<Demand> errorMap = new ArrayList<>();
		int successCount = 0;
		for (Demand demand : demandResponse) {
			try {
				Object result = serviceRequestRepository.fetchResult(
						calculatorUtils.getFetchBillURL(demand.getTenantId(), demand.getConsumerCode()),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				HashMap<String, Object> billResponse = new HashMap<>();
				billResponse.put("requestInfo", requestInfo);
				billResponse.put("billResponse", result);
//				log.info("Result"+result.toString());
				wsCalculationProducer.push(configs.getPayTriggers(), billResponse);
				notificationSent = true;
				successCount++;
			} catch (Exception ex) {
				log.error("Fetch Bill Error", ex);
				errorMap.add(demand);
			}
		}
		String uuid = demandResponse.get(0).getAuditDetails().getCreatedBy();
		if (errorMap.size() == demandResponse.size()) {
			paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
					masterMap, false);
		} else {
			if (!errorMap.isEmpty()) {
				paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
						masterMap, false);
			}
			paymentNotificationService.sendBillNotification(requestInfo, uuid, demandResponse.get(0).getTenantId(),
					masterMap, true);
		}
		return notificationSent;
	}

	/**
	 * compare and update the demand details
	 * 
	 * @param calculation   - Calculation object
	 * @param demandDetails - List Of Demand Details
	 * @return combined demand details list
	 */
	private List<DemandDetail> getUpdatedAdhocTax(Calculation calculation, List<DemandDetail> demandDetails) {

		List<DemandDetail> newDemandDetails = new ArrayList<>();
		Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

		demandDetails.forEach(demandDetail -> {
			if (!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())) {
				List<DemandDetail> demandDetailList = new LinkedList<>();
				demandDetailList.add(demandDetail);
				taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(), demandDetailList);
			} else
				taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
		});

		BigDecimal diffInTaxAmount;
		List<DemandDetail> demandDetailList;
		BigDecimal total;

		for (TaxHeadEstimate taxHeadEstimate : calculation.getTaxHeadEstimates()) {
			if (!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
				newDemandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
						.collectionAmount(BigDecimal.ZERO).build());
			else {
				demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
				total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
				if (diffInTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(diffInTaxAmount)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
				}
			}
		}
		List<DemandDetail> combinedBillDetails = new LinkedList<>(demandDetails);
		combinedBillDetails.addAll(newDemandDetails);
		addRoundOffTaxHead(calculation.getTenantId(), combinedBillDetails);
		return combinedBillDetails;
	}

	/**
	 * Search demand based on demand id and updated the tax heads with new adhoc tax
	 * heads
	 * 
	 * @param requestInfo  - Request Info Object
	 * @param calculations - List of Calculation to update the Demand
	 * @return List of calculation
	 */
	public List<Calculation> updateDemandForAdhocTax(RequestInfo requestInfo, List<Calculation> calculations,
			String businessService) {
		List<Demand> demands = new LinkedList<>();
		for (Calculation calculation : calculations) {
			String consumerCode = calculation.getConnectionNo();
			List<Demand> searchResult = searchDemandBasedOnConsumerCode(calculation.getTenantId(), consumerCode,
					requestInfo, businessService);
			if (CollectionUtils.isEmpty(searchResult))
				throw new CustomException("EG_WS_INVALID_DEMAND_UPDATE",
						"No demand exists for Number: " + consumerCode);

			Collections.sort(searchResult, new Comparator<Demand>() {
				@Override
				public int compare(Demand d1, Demand d2) {
					return d1.getTaxPeriodFrom().compareTo(d2.getTaxPeriodFrom());
				}
			});

			Demand demand = searchResult.get(0);
			demand.setDemandDetails(getUpdatedAdhocTax(calculation, demand.getDemandDetails()));
			demands.add(demand);
		}

		log.info("Updated Demand Details " + demands.toString());
		demandRepository.updateDemand(requestInfo, demands);
		return calculations;
	}

	public List<String> fetchBillSchedulerBatch(Set<String> consumerCodes, String tenantId, RequestInfo requestInfo) {
		List<String> consumercodesFromRes = null;
		try {

			StringBuilder fetchBillURL = calculatorUtils.getFetchBillURL(tenantId,
					getCommaSeparateStrings(consumerCodes));

			Object result = serviceRequestRepository.fetchResult(fetchBillURL,
					RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			log.info("Bills generated for the consumercodes: {}", fetchBillURL);
			BillResponseV2 billResponse = mapper.convertValue(result, BillResponseV2.class);
			List<BillV2> bills = billResponse.getBill();
			if (bills != null && !bills.isEmpty()) {
				consumercodesFromRes = bills.stream().map(BillV2::getConsumerCode).collect(Collectors.toList());
			}

		} catch (Exception ex) {
			log.error("Fetch Bill Error For tenantId:{} consumercode: {} and Exception is: {}", tenantId, consumerCodes,
					ex);
			return consumercodesFromRes;
		}
		return consumercodesFromRes;
	}

	/**
	 * Creates demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be created
	 * @return The list of demand created
	 */
	public void saveDemand(RequestInfo requestInfo, List<Demand> demands) {
		try {
			DemandRequest request = new DemandRequest(requestInfo, demands);
			wsCalculationProducer.push(configs.getSaveDemand(), request);
		} catch (Exception e) {
			throw new CustomException("PARSING_ERROR", "Failed to push the save demand data to kafka topic");
		}
	}

	private static String getCommaSeparateStrings(Set<String> idList) {

		StringBuilder query = new StringBuilder();
		if (!idList.isEmpty()) {

			String[] list = idList.toArray(new String[idList.size()]);
			query.append(list[0]);
			for (int i = 1; i < idList.size(); i++) {
				query.append("," + list[i]);
			}
		}
		return query.toString();
	}

	/**
	 * Search the latest demand generated for a connection.
	 *
	 * @param tenantId
	 * @param consumerCodes
	 * @param fromDateSearch
	 * @param toDateSearch
	 * @param requestInfo            - Request Info Object
	 * @param isDisconnectionRequest - Boleean value to test if it is a
	 *                               Disconnection request
	 * @return List of calculation
	 */
	List<Demand> searchDemandForDisconnectionRequest(String tenantId, Set<String> consumerCodes, Long fromDateSearch,
			Long toDateSearch, RequestInfo requestInfo, Boolean isDemandPaid, Boolean isDisconnectionRequest) {
		List<Demand> demandList = searchDemand(tenantId, consumerCodes, null, toDateSearch, requestInfo, null,
				isDisconnectionRequest, false);
		if (!CollectionUtils.isEmpty(demandList)) {
			// Sorting the demandList in descending order to pick the latest demand
			// generated
			demandList = demandList.stream().sorted(Comparator.comparing(Demand::getTaxPeriodTo).reversed())
					.collect(Collectors.toList());
		}
		return demandList;
	}

}
