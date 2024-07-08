package org.egov.swcalculation.service;

import static org.egov.swcalculation.constants.SWCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.swcalculation.config.SWCalculationConfiguration;
import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.producer.SWCalculationProducer;
import org.egov.swcalculation.repository.DemandRepository;
import org.egov.swcalculation.repository.ServiceRequestRepository;
import org.egov.swcalculation.repository.SewerageCalculatorDao;
import org.egov.swcalculation.repository.SewerageConnectionRepository;
import org.egov.swcalculation.util.CalculatorUtils;
import org.egov.swcalculation.util.SWCalculationUtil;
import org.egov.swcalculation.validator.SWCalculationWorkflowValidator;
import org.egov.swcalculation.web.models.BulkBillCriteria;
import org.egov.swcalculation.web.models.BillResponseV2;
import org.egov.swcalculation.web.models.BillV2;
import org.egov.swcalculation.web.models.Calculation;
import org.egov.swcalculation.web.models.CalculationCriteria;
import org.egov.swcalculation.web.models.CalculationReq;
import org.egov.swcalculation.web.models.Demand;
import org.egov.swcalculation.web.models.Demand.StatusEnum;
import org.egov.swcalculation.web.models.DemandDetail;
import org.egov.swcalculation.web.models.DemandDetailAndCollection;
import org.egov.swcalculation.web.models.DemandNotificationObj;
import org.egov.swcalculation.web.models.DemandRequest;
import org.egov.swcalculation.web.models.DemandResponse;
import org.egov.swcalculation.web.models.GetBillCriteria;
import org.egov.swcalculation.web.models.MigrationCount;
import org.egov.swcalculation.web.models.Property;
import org.egov.swcalculation.web.models.RequestInfoWrapper;
import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.web.models.SewerageConnectionRequest;
import org.egov.swcalculation.web.models.SewerageDetails;
import org.egov.common.contract.request.Role;
import org.egov.swcalculation.web.models.TaxHeadEstimate;
import org.egov.swcalculation.web.models.TaxPeriod;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.swcalculation.web.models.SingleDemand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private SWCalculationUtil utils;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private SWCalculationConfiguration configs;
	
	@Autowired
	private SWCalculationProducer swCalculationProducer;
	
	@Autowired
	private ServiceRequestRepository repository;


	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private PayService payService;
	
    @Autowired
    private CalculatorUtils calculatorUtils;
    
    @Autowired
    private SewerageCalculatorDao sewerageCalculatorDao;
    
    @Autowired
    private EstimationService estimationService;
    
    @Autowired
    private SWCalculationProducer producer;
    
    @Autowired
    private KafkaTemplate kafkaTemplate;
    
    @Autowired
    private SWCalculationUtil sWCalculationUtil;
    
    @Autowired
	private SWCalculationWorkflowValidator swCalulationWorkflowValidator;

	@Autowired
	private PaymentNotificationService paymentNotificationService;
	
	@Autowired
	private EnrichmentService enrichmentService;

	private Object calculationReq;

	@Autowired
	private SewerageConnectionRepository sewerageConnectionRepository;



	/**
	 * Creates or updates Demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation request
	 * @param calculations The Calculation Objects for which demand has to be generated or updated
	 */
	public List<Demand> generateDemand(CalculationReq request, List<Calculation> calculations,
			Map<String, Object> masterMap, boolean isForConnectionNo) {
		@SuppressWarnings("unchecked")
		Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
				.get(SWCalculationConstant.BILLING_PERIOD);
		Long fromDate = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
		Long toDate = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);
		
		// List that will contain Calculation for old demands
		List<Calculation> updateCalculations = new LinkedList<>();
		List<Calculation> createCalculations = new LinkedList<>();
		if (!CollectionUtils.isEmpty(calculations)) {
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

			List<Demand> demands = searchDemand(tenantId, consumerCodes, fromDateSearch, toDateSearch, request.getRequestInfo(), null,
					request.getIsDisconnectionRequest(),request.getIsReconnectionRequest());
			Set<String> connectionNumbersFromDemands = new HashSet<>();
			if (!CollectionUtils.isEmpty(demands))
				connectionNumbersFromDemands = demands.stream().map(Demand::getConsumerCode)
						.collect(Collectors.toSet());
			// List that will contain Calculation for new demands
			// If demand already exists add it updateCalculations else
			// createCalculations
			for (Calculation calculation : calculations) {
				if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest()) {
					demands = searchDemandForDisconnectionRequest(calculation.getTenantId(), consumerCodes, null,
							toDateSearch, request.getRequestInfo(), null, request.getIsDisconnectionRequest());
					if (!CollectionUtils.isEmpty(demands) &&
							!(demands.get(0).getDemandDetails().get(0).getCollectionAmount().doubleValue() == 0.0)) {
						createCalculations.add(calculation);
					} else if (CollectionUtils.isEmpty(demands)) {
						calculation.getApplicationNO();
						createCalculations.add(calculation);
					} else {
						updateCalculations.add(calculation);
					}
				} else {
					if (!connectionNumbersFromDemands.contains(isForConnectionNo ? calculation.getConnectionNo() : calculation.getApplicationNO())) {
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
			createdDemands = updateDemandForCalculation(request, updateCalculations, fromDate, toDate, isForConnectionNo,
					request.getIsDisconnectionRequest(),request.getIsReconnectionRequest());
		return createdDemands;
	}

	/**
	 * Creates demand for the given list of calculations
	 * 
	 * @param requestInfo
	 *            The RequestInfo of the calculation request
	 * @param calculations
	 *            List of calculation object
	 * @return Demands that are created
	 */
	private List<Demand> createDemand(CalculationReq calculationReq, List<Calculation> calculations,
			Map<String, Object> masterMap,boolean isForConnectionNO) {
		List<Demand> demands = new LinkedList<>();
		Set<String> sewerageConnectionIds = new HashSet<>();
		for (Calculation calculation : calculations) {

			SewerageConnection connection = calculation.getSewerageConnection();

			if (connection == null)
				throw new CustomException("EG_SW_INVALID_SEWERAGE_CONNECTION",
						"Demand cannot be generated for "
								+ (isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO())
								+ " Sewerage Connection with this number does not exist ");
			
			SewerageConnectionRequest sewerageConnectionRequest = SewerageConnectionRequest.builder()
					.sewerageConnection(connection).requestInfo(calculationReq.getRequestInfo()).build();
			
			Property property = sWCalculationUtil.getProperty(sewerageConnectionRequest);
			String tenantId = calculation.getTenantId();
			String consumerCode = isForConnectionNO ?  calculation.getConnectionNo() : calculation.getApplicationNO();
			sewerageConnectionIds.add(consumerCode);
			User owner = property.getOwners().get(0).toCommonUser();
			if (!CollectionUtils.isEmpty(sewerageConnectionRequest.getSewerageConnection().getConnectionHolders())) {
				owner = sewerageConnectionRequest.getSewerageConnection().getConnectionHolders().get(0).toCommonUser();
			}
			owner = getPlainOwnerDetails(calculationReq.getRequestInfo(),owner.getUuid(), tenantId);
			List<DemandDetail> demandDetails = new LinkedList<>();
			
			calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
				demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
						.tenantId(calculation.getTenantId()).build());
			});
			
			@SuppressWarnings("unchecked")
			Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
					.get(SWCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);
			Long expiryDate = (Long) financialYearMaster.get(SWCalculationConstant.Demand_Expiry_Date_String);
			BigDecimal minimumPayableAmount = isForConnectionNO ? configs.getMinimumPayableAmount() : calculation.getTotalAmount();
			String businessService = isForConnectionNO ? configs.getBusinessService() : ONE_TIME_FEE_SERVICE_FIELD;
			if(calculationReq.getIsReconnectionRequest())
				businessService="SWReconnection";
			addRoundOffTaxHead(calculation.getTenantId(), demandDetails);
			Map<String, String> additionalDetailsMap = new HashMap<>();
			additionalDetailsMap.put("propertyId", property.getPropertyId());
			demands.add(Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
					.minimumAmountPayable(minimumPayableAmount).tenantId(calculation.getTenantId()).taxPeriodFrom(fromDate)
					.taxPeriodTo(toDate).consumerType("sewerageConnection").businessService(businessService)
					.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDate).additionalDetails(additionalDetailsMap).build());
		}

		String billingcycle = calculatorUtils.getBillingCycle(masterMap);
		DemandNotificationObj notificationObj = DemandNotificationObj.builder()
				.requestInfo(calculationReq.getRequestInfo())
				.tenantId(calculations.get(0).getTenantId())
				.sewerageConnetionIds(sewerageConnectionIds)
				.billingCycle(billingcycle)
				.build();
		List<Demand> demandRes = demandRepository.saveDemand(calculationReq.getRequestInfo(), demands,notificationObj);
		if(calculationReq.getIsReconnectionRequest())
			fetchBillForReconnect(demandRes, calculationReq.getRequestInfo(), masterMap);
		else if(isForConnectionNO && !calculationReq.getIsReconnectionRequest())
			fetchBill(demandRes, calculationReq.getRequestInfo(),masterMap);
		return demandRes;
	}

	private User getPlainOwnerDetails(RequestInfo requestInfo, String uuid, String tenantId){
		User userInfoCopy = requestInfo.getUserInfo();
		StringBuilder uri = new StringBuilder();
		uri.append(configs.getUserHost()).append(configs.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		tenantId = tenantId.split("\\.")[0];

		Role role = Role.builder()
				.name("Internal Microservice Role").code("INTERNAL_MICROSERVICE_ROLE")
				.tenantId(tenantId).build();

		User userInfo = User.builder()
				.uuid(configs.getEgovInternalMicroserviceUserUuid())
				.type("SYSTEM")
				.roles(Collections.singletonList(role)).id(0L).build();

		requestInfo.setUserInfo(userInfo);

		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("uuid",Collections.singletonList(uuid));
		User user = null;
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(uri, userSearchRequest);

			List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
			String dobFormat = "yyyy-MM-dd";
			utils.parseResponse(responseMap,dobFormat);
			user = 	mapper.convertValue(users.get(0), User.class);

		} catch (Exception e) {
			throw new CustomException("EG_USER_SEARCH_ERROR", "Service returned null while fetching user");
		}
		requestInfo.setUserInfo(userInfoCopy);
		return user;
	}
	
	
	public boolean fetchBill(List<Demand> demandResponse, RequestInfo requestInfo,Map<String, Object> masterMap) {
		boolean notificationSent = false;
		List<Demand> errorMap = new ArrayList<>();
		int successCount=0;
		for (Demand demand : demandResponse) {
			try {
				Object result = serviceRequestRepository.fetchResult(calculatorUtils.getFetchBillURL(demand.getTenantId(), demand.getConsumerCode()),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				HashMap<String, Object> billResponse = new HashMap<>();
				billResponse.put("requestInfo", requestInfo);
				billResponse.put("billResponse", result);
				producer.push(configs.getPayTriggers(), billResponse);
				notificationSent = true;
				successCount++;

			} catch (Exception ex) {
				log.error("EG_SW Fetch Bill Error", ex);
				errorMap.add(demand);
			}
		}
		String uuid = demandResponse.get(0).getAuditDetails().getCreatedBy();
		if(errorMap.size() == demandResponse.size())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,false);
		}
		else
		{if(!errorMap.isEmpty())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid, demandResponse.get(0).getTenantId(), masterMap,false);
		}
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,true);
		}
		return notificationSent;
	}

	public boolean fetchBillForReconnect(List<Demand> demandResponse, RequestInfo requestInfo,Map<String, Object> masterMap) {
		boolean notificationSent = false;
		List<Demand> errorMap = new ArrayList<>();
		int successCount=0;
		for (Demand demand : demandResponse) {
			try {
				Object result = serviceRequestRepository.fetchResult(calculatorUtils.getFetchBillURL(demand.getTenantId(), demand.getConsumerCode()),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				HashMap<String, Object> billResponse = new HashMap<>();
				billResponse.put("requestInfo", requestInfo);
				billResponse.put("billResponse", result);
				producer.push(configs.getPayTriggers(), billResponse);
				notificationSent = true;
				successCount++;

			} catch (Exception ex) {
				log.error("EG_SW Fetch Bill Error", ex);
				errorMap.add(demand);
			}
		}
		String uuid = demandResponse.get(0).getAuditDetails().getCreatedBy();
		if(errorMap.size() == demandResponse.size())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,false);
		}
		else
		{if(!errorMap.isEmpty())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid, demandResponse.get(0).getTenantId(), masterMap,false);
		}
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,true);
		}
		return notificationSent;
	}

	public boolean fetchBillforReconnect(List<Demand> demandResponse, RequestInfo requestInfo,Map<String, Object> masterMap) {
		boolean notificationSent = false;
		List<Demand> errorMap = new ArrayList<>();
		int successCount=0;
		for (Demand demand : demandResponse) {
			try {
				Object result = serviceRequestRepository.fetchResult(calculatorUtils.getFetchBillURLForReconnection(demand.getTenantId(), demand.getConsumerCode()),
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				HashMap<String, Object> billResponse = new HashMap<>();
				billResponse.put("requestInfo", requestInfo);
				billResponse.put("billResponse", result);
				producer.push(configs.getPayTriggers(), billResponse);
				notificationSent = true;
				successCount++;

			} catch (Exception ex) {
				log.error("EG_SW Fetch Bill Error", ex);
				errorMap.add(demand);
			}
		}
		String uuid = demandResponse.get(0).getAuditDetails().getCreatedBy();
		if(errorMap.size() == demandResponse.size())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,false);
		}
		else
		{if(!errorMap.isEmpty())
		{
			paymentNotificationService.sendBillNotification(requestInfo,uuid, demandResponse.get(0).getTenantId(), masterMap,false);
		}
			paymentNotificationService.sendBillNotification(requestInfo,uuid,demandResponse.get(0).getTenantId(),masterMap,true);
		}
		return notificationSent;
	}

	/**
	 * Adds roundOff taxHead if decimal values exists
	 * 
	 * @param tenantId
	 *            The tenantId of the demand
	 * @param demandDetails
	 *            The list of demandDetail
	 */
	public void addRoundOffTaxHead(String tenantId, List<DemandDetail> demandDetails) {
		BigDecimal totalTax = BigDecimal.ZERO;
		
		BigDecimal previousRoundOff = BigDecimal.ZERO;
		/*
		 * Sum all taxHeads except RoundOff as new roundOff will be calculated
		 */
		for (DemandDetail demandDetail : demandDetails) {
			if (!demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(SWCalculationConstant.MDMS_ROUNDOFF_TAXHEAD))
				totalTax = totalTax.add(demandDetail.getTaxAmount());
			else
				previousRoundOff = previousRoundOff.add(demandDetail.getTaxAmount());
		}

		BigDecimal decimalValue = totalTax.remainder(BigDecimal.ONE);
		BigDecimal midVal = BigDecimal.valueOf(0.5);
		BigDecimal roundOff = BigDecimal.ZERO;

		/*
		 * If the decimal amount is greater than 0.5 we subtract it from 1 and
		 * put it as roundOff taxHead so as to nullify the decimal eg: If the
		 * tax is 12.64 we will add extra tax roundOff taxHead of 0.36 so that
		 * the total becomes 13
		 */
		if (decimalValue.compareTo(midVal) >= 0)
			roundOff = BigDecimal.ONE.subtract(decimalValue);

		/*
		 * If the decimal amount is less than 0.5 we put negative of it as
		 * roundOff taxHead so as to nullify the decimal eg: If the tax is 12.36
		 * we will add extra tax roundOff taxHead of -0.36 so that the total
		 * becomes 12
		 */
		if (decimalValue.compareTo(midVal) < 0)
			roundOff = decimalValue.negate();

		/*
		 * If roundOff already exists in previous demand create a new roundOff
		 * taxHead with roundOff amount equal to difference between them so that
		 * it will be balanced when bill is generated. eg: If the previous
		 * roundOff amount was of -0.36 and the new roundOff excluding the
		 * previous roundOff is 0.2 then the new roundOff will be created with
		 * 0.2 so that the net roundOff will be 0.2 -(-0.36)
		 */
		if (previousRoundOff.compareTo(BigDecimal.ZERO) != 0) {
			roundOff = roundOff.subtract(previousRoundOff);
		}

		if (roundOff.compareTo(BigDecimal.ZERO) != 0) {
			demandDetails.add(DemandDetail.builder().taxAmount(roundOff)
					.taxHeadMasterCode(SWCalculationConstant.MDMS_ROUNDOFF_TAXHEAD).tenantId(tenantId)
					.collectionAmount(BigDecimal.ZERO).build());
		}
	}

	/**
	 * Searches demand for the given consumerCode and tenantIDd
	 * 
	 * @param tenantId
	 *            The tenantId of the tradeLicense
	 * @param consumerCodes
	 *            The set of consumerCode of the demands
	 * @param requestInfo
	 *            The RequestInfo of the incoming request
	 * @return List of demands for the given consumerCode
	 */
	public List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, Long taxPeriodFrom, Long taxPeriodTo,
									 RequestInfo requestInfo, Boolean isDemandPaid, Boolean isDisconnectionRequest ,Boolean isReconnectionRequest) {
		Object result = serviceRequestRepository.fetchResult(getDemandSearchURL(tenantId, consumerCodes, taxPeriodFrom, taxPeriodTo, isDemandPaid,
						isDisconnectionRequest,isReconnectionRequest),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		DemandResponse response;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
			if (CollectionUtils.isEmpty(response.getDemands()))
				return Collections.emptyList();
			return response.getDemands();
		} catch (IllegalArgumentException e) {
			throw new CustomException("EG_SW_PARSING_ERROR", "Failed to parse response from Demand Search");
		}

	}
	
	/**
	 * Creates demand Search url based on tenantId,businessService, period from, period to and
	 * ConsumerCode 
	 * 
	 * @return demand search url
	 */
	public StringBuilder getDemandSearchURL(String tenantId, Set<String> consumerCodes, Long taxPeriodFrom, Long taxPeriodTo, Boolean isDemandPaid,
											Boolean isDisconnectionRequest, Boolean isReconnectionRequest) {

		StringBuilder url = new StringBuilder(configs.getBillingServiceHost());
		String businessService = taxPeriodFrom == null && !isDisconnectionRequest ? ONE_TIME_FEE_SERVICE_FIELD : configs.getBusinessService();
		if(isReconnectionRequest)
			businessService="SWReconnection";
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
	 * Updates demand for the given list of calculations
	 * 
	 * @param requestInfo
	 *            The RequestInfo of the calculation request
	 * @param calculations
	 *            List of calculation object
	 * @return Demands that are updated
	 */
	private List<Demand> updateDemandForCalculation(CalculationReq request, List<Calculation> calculations,
			Long fromDate, Long toDate, boolean isForConnectionNo, Boolean isDisconnectionRequest, Boolean isReconnectionRequest) {

		List<Demand> demands = new LinkedList<>();
		Long fromDateSearch = isForConnectionNo ? fromDate : null;
		Long toDateSearch = isForConnectionNo ? toDate : null;

		for (Calculation calculation : calculations) {
			Set<String> consumerCodes = new HashSet<>();
			consumerCodes = isForConnectionNo
					? Collections.singleton(calculation.getSewerageConnection().getConnectionNo())
					: Collections.singleton(calculation.getSewerageConnection().getApplicationNo());
			List<Demand> searchResult = new ArrayList<>();
			if (isDisconnectionRequest)
				searchResult = searchDemandForDisconnectionRequest(calculation.getTenantId(), consumerCodes, null, toDateSearch,
						request.getRequestInfo(), null, isDisconnectionRequest);
			else
				searchResult = searchDemand(calculation.getTenantId(), consumerCodes, fromDateSearch, toDateSearch, request.getRequestInfo(),
						null, isDisconnectionRequest,isReconnectionRequest);

			if (CollectionUtils.isEmpty(searchResult))
				throw new CustomException("EG_SW_INVALID_DEMAND_UPDATE", "No demand exists for Number: "
						+ consumerCodes.toString());
			Demand demand = searchResult.get(0);
			String tenantId = calculation.getTenantId();
			List<DemandDetail> demandDetails = demand.getDemandDetails();
			List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(calculation, demandDetails, isDisconnectionRequest);
			demand.setDemandDetails(updatedDemandDetails);

			if(isForConnectionNo){
				SewerageConnection connection = calculation.getSewerageConnection();
				if (connection == null) {
					List<SewerageConnection> sewerageConnectionList = calculatorUtils.getSewerageConnection(request.getRequestInfo(),
							calculation.getConnectionNo(),calculation.getTenantId());
					int size = sewerageConnectionList.size();
					connection = sewerageConnectionList.get(size-1);

				}

				if(connection.getApplicationType().equalsIgnoreCase("MODIFY_SEWERAGE_CONNECTION")){
					SewerageConnectionRequest sewerageConnectionRequest = SewerageConnectionRequest.builder().sewerageConnection(connection)
							.requestInfo(request.getRequestInfo()).build();
					Property property = sWCalculationUtil.getProperty(sewerageConnectionRequest);
					User owner = property.getOwners().get(0).toCommonUser();
					if (!CollectionUtils.isEmpty(sewerageConnectionRequest.getSewerageConnection().getConnectionHolders())) {
						owner = sewerageConnectionRequest.getSewerageConnection().getConnectionHolders().get(0).toCommonUser();
					}
					owner = getPlainOwnerDetails(request.getRequestInfo(),owner.getUuid(), tenantId);
					if(!(demand.getPayer().getUuid().equalsIgnoreCase(owner.getUuid())))
						demand.setPayer(owner);
				}
			}
			demands.add(demand);
		}

		log.info("Updated Demand Details " + demands.toString());
		return demandRepository.updateDemand(request.getRequestInfo(), demands);
	}

	/**
	 * Returns the list of new DemandDetail to be added for updating the demand
	 * 
	 * @param calculation
	 *            The calculation object for the update request
	 * @param demandDetails
	 *            The list of demandDetails from the existing demand
	 * @return The list of new DemandDetails
	 */
	private List<DemandDetail> getUpdatedDemandDetails(Calculation calculation, List<DemandDetail> demandDetails, Boolean isDisconnectionRequest) {

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
				if (taxHeadEstimate.getTaxHeadCode().equalsIgnoreCase(SWCalculationConstant.SW_Round_Off))
					continue;
				total = taxHeadEstimate.getEstimateAmount();
				if (total.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(total)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(calculation.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
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
	 * 
	 * @param getBillCriteria Bill Criteria
	 * @param requestInfoWrapper contains request info wrapper
	 * @return updated demand response
	 */
	public List<Demand> updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper, Boolean isCallFromBulkGen) {

		if (getBillCriteria.getAmountExpected() == null)
			getBillCriteria.setAmountExpected(BigDecimal.ZERO);
		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		Map<String, JSONArray> billingSlabMaster = new HashMap<>();

		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		masterDataService.setSewerageConnectionMasterValues(requestInfoWrapper.getRequestInfo(), getBillCriteria.getTenantId(), billingSlabMaster,
				timeBasedExemptionMasterMap);

		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getConnectionNumber()));

		DemandResponse res = mapper.convertValue(
				repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);
		if (CollectionUtils.isEmpty(res.getDemands())) {
			return Collections.emptyList();
		}
		List<Demand> demands = res.getDemands();
		Demand oldDemand = demands.get(0);

	
		demands = demands.stream()
				.filter(i -> !SWCalculationConstant.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(i.getStatus().toString()))
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
		
		log.info("-------updateDemands------------oldDemand--------" + oldDemand);
		String tenantId = getBillCriteria.getTenantId();

		List<TaxPeriod> taxPeriods = masterDataService.getTaxPeriodList(requestInfoWrapper.getRequestInfo(), getBillCriteria.getTenantId(), SWCalculationConstant.SERVICE_FIELD_VALUE_SW);
		consumerCodeToDemandMap.forEach((id, demand) ->{
			BigDecimal totalTax = demand.getDemandDetails().stream().map(DemandDetail::getTaxAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal totalCollection = demand.getDemandDetails().stream().map(DemandDetail::getCollectionAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			List<String> taxHeadMasterCodes = demand.getDemandDetails().stream().map(DemandDetail::getTaxHeadMasterCode)
					.collect(Collectors.toList());
			log.info("Demand Id: "+ demand.getId());
			log.info(" taxHeadMasterCodes "+taxHeadMasterCodes );
			
			log.info(" isMigratedCon "+ isMigratedCon);
			log.info(" oldDemand.getId().equalsIgnoreCase(demand.getId()) "+ !oldDemand.getId().equalsIgnoreCase(demand.getId()));
			log.info(" Payment Completed  "+ demand.getIsPaymentCompleted());
			log.info("Total Tax "+ totalTax);
			log.info("Total totalCollection "+ totalCollection);
			Boolean abc=totalTax.compareTo(totalCollection) > 0;
			
			log.info(" tax condition "+ abc);
			log.info(" penalty taxhead code "+	taxHeadMasterCodes.contains(SWCalculationConstant.SW_TIME_PENALTY));
			if (!(isMigratedCon && oldDemand.getId().equalsIgnoreCase(demand.getId()))) {
				log.info("-------updateDemands-----inside if-------demand.getId()--------" + demand.getId()
						+ "-------oldDemand.getId()---------" + oldDemand.getId());
				if (!demand.getIsPaymentCompleted() 
						&& totalTax.compareTo(totalCollection) > 0
						&& !taxHeadMasterCodes.contains(SWCalculationConstant.SW_TIME_PENALTY))
				{
						if (demand.getStatus() != null
								&& SWCalculationConstant.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(demand.getStatus().toString()))
							throw new CustomException(SWCalculationConstant.EG_SW_INVALID_DEMAND_ERROR,
									SWCalculationConstant.EG_SW_INVALID_DEMAND_ERROR_MSG);
						User owner = getPlainOwnerDetails(requestInfo,demand.getPayer().getUuid(), tenantId);
						demand.setPayer(owner);
						applyTimeBasedApplicables(demand, requestInfoWrapper, timeBasedExemptionMasterMap, taxPeriods);
						addRoundOffTaxHead(getBillCriteria.getTenantId(), demand.getDemandDetails());
						demandsToBeUpdated.add(demand);
				}
				else
				{
					addRoundOffTaxHead(getBillCriteria.getTenantId(), demand.getDemandDetails());
					demandsToBeUpdated.add(demand);
				}	
			}
		});
		// Call demand update in bulk to update the interest or penalty
		if(!isCallFromBulkGen)
			repository.fetchResult(utils.getUpdateDemandUrl(),
					DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfoWrapper.getRequestInfo()).build());

		return demandsToBeUpdated;
	}
	private boolean isMigratedConnection(final String connectionNumber, final String tenantId) {

		String connectionAddlDetail = sewerageConnectionRepository.fetchConnectionAdditonalDetails(connectionNumber,
				tenantId);
		log.info("SW connectionAddlDetail-->" + connectionAddlDetail);
		Map<String, Object> result = null;
		try {
			result = mapper.readValue(connectionAddlDetail, HashMap.class);
		} catch (Exception e) {
			log.error("Exception while reading connection migration flag");
		}
		if (result == null)
			return false;
		else if ((boolean) result.getOrDefault("isMigrated", false)) {
			return true;
		}
		return false;

	}
	
	/**
	 * Updates the amount in the latest demandDetail by adding the diff between
	 * new and old amounts to it
	 * 
	 * @param newAmount
	 *            The new tax amount for the taxHead
	 * @param latestDetailInfo
	 *            The latest demandDetail for the particular taxHead
	 */
	private void updateTaxAmount(BigDecimal newAmount, DemandDetailAndCollection latestDetailInfo) {
		BigDecimal diff = newAmount.subtract(latestDetailInfo.getTaxAmountForTaxHead());
		BigDecimal newTaxAmountForLatestDemandDetail = latestDetailInfo.getLatestDemandDetail().getTaxAmount()
				.add(diff);
		latestDetailInfo.getLatestDemandDetail().setTaxAmount(newTaxAmountForLatestDemandDetail);
	}

	
	/**
	 * Applies Penalty/Rebate/Interest to the incoming demands
	 * 
	 * If applied already then the demand details will be updated
	 * 
	 * @param demand - Demand Object
	 * @param requestInfoWrapper - Request Info Object
	 * @param timeBasedExemptionMasterMap - List of Time based exemptions
	 * @param taxPeriods - List of Tax Periods
	 * @return - Returns TRUE or FALSE
	 */
	
	private boolean applyTimeBasedApplicables(Demand demand, RequestInfoWrapper requestInfoWrapper,
											  Map<String, JSONArray> timeBasedExemptionMasterMap, List<TaxPeriod> taxPeriods) {

		TaxPeriod taxPeriod = taxPeriods.stream().filter(t -> demand.getTaxPeriodFrom().compareTo(t.getFromDate()) >= 0
				&& demand.getTaxPeriodTo().compareTo(t.getToDate()) <= 0).findAny().orElse(null);
		if (taxPeriod == null) {
			log.info("Demand Expired!!");
			return false;
		}

		boolean isCurrentDemand = false;
		if (!(taxPeriod.getFromDate() <= System.currentTimeMillis()
				&& taxPeriod.getToDate() >= System.currentTimeMillis()))
			isCurrentDemand = true;
		
		if(demand.getBillExpiryTime() < System.currentTimeMillis()) {
		BigDecimal sewerageChargeApplicable = BigDecimal.ZERO;
		BigDecimal oldPenalty = BigDecimal.ZERO;
		BigDecimal oldInterest = BigDecimal.ZERO;
		BigDecimal oldRebate = BigDecimal.ZERO;


		for (DemandDetail detail : demand.getDemandDetails()) {
			if (SWCalculationConstant.TAX_APPLICABLE.contains(detail.getTaxHeadMasterCode())) {
				sewerageChargeApplicable = sewerageChargeApplicable.add(detail.getTaxAmount());
			}
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(SWCalculationConstant.SW_TIME_PENALTY)) {
				oldPenalty = oldPenalty.add(detail.getTaxAmount());
			}
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(SWCalculationConstant.SW_TIME_INTEREST)) {
				oldInterest = oldInterest.add(detail.getTaxAmount());
			}
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(SWCalculationConstant.SW_TIME_REBATE)) {
				oldRebate = oldRebate.add(detail.getTaxAmount());
			}
		}
		
		boolean isPenaltyUpdated = false;
		boolean isInterestUpdated = false;
		boolean isRebateUpdated = false;

		List<DemandDetail> details = demand.getDemandDetails();

		Map<String, BigDecimal> interestPenaltyRebateEstimates = payService.applyPenaltyRebateAndInterest(
				sewerageChargeApplicable, taxPeriod.getFinancialYear(), timeBasedExemptionMasterMap, demand.getBillExpiryTime(),demand);
		log.info("old penalty amount is " + oldPenalty);
		log.info("old interest amount is " + oldInterest);
		log.info("old rebate amount is " + oldRebate);
	
		BigDecimal penalty  = interestPenaltyRebateEstimates.get(SWCalculationConstant.SW_TIME_PENALTY);
		BigDecimal interest = interestPenaltyRebateEstimates.get(SWCalculationConstant.SW_TIME_INTEREST);
		BigDecimal rebate   = interestPenaltyRebateEstimates.get(SWCalculationConstant.SW_TIME_REBATE);
		log.info("penalty amount after calculation is " + penalty);
		log.info("interest amount after calculation  is " + interest);
		log.info("rebate amount after calculation is " + rebate);
		if(penalty == null)
			penalty = BigDecimal.ZERO;
		if(interest == null)
			interest = BigDecimal.ZERO;
		if(rebate == null)
			rebate = BigDecimal.ZERO;
		DemandDetailAndCollection latestPenaltyDemandDetail, latestInterestDemandDetail,latestRebateDemandDetail;

		if (interest.compareTo(BigDecimal.ZERO) != 0) {
			latestInterestDemandDetail = utils.getLatestDemandDetailByTaxHead(SWCalculationConstant.SW_TIME_INTEREST,
					demand.getDemandDetails());
			if (latestInterestDemandDetail != null) {
				updateTaxAmount(interest, latestInterestDemandDetail);
				isInterestUpdated = true;
			}
		}

		if (penalty.compareTo(BigDecimal.ZERO) != 0) {
			latestPenaltyDemandDetail = utils.getLatestDemandDetailByTaxHead(SWCalculationConstant.SW_TIME_PENALTY,
					demand.getDemandDetails());
			if (latestPenaltyDemandDetail != null) {
				updateTaxAmount(penalty, latestPenaltyDemandDetail);
				isPenaltyUpdated = true;
			}
		}

		if (oldRebate.compareTo(BigDecimal.ZERO) != 0 || rebate.compareTo(BigDecimal.ZERO) != 0) {
			latestRebateDemandDetail = utils.getLatestDemandDetailByTaxHead(SWCalculationConstant.SW_TIME_REBATE,
					details);
			if (latestRebateDemandDetail != null) {
				updateRebate(rebate, latestRebateDemandDetail);
				isRebateUpdated = true;
			}
		}
		if (!isPenaltyUpdated && penalty.compareTo(BigDecimal.ZERO) > 0)
			demand.getDemandDetails().add(
					DemandDetail.builder().taxAmount(penalty.setScale(2, 2)).taxHeadMasterCode(SWCalculationConstant.SW_TIME_PENALTY)
							.demandId(demand.getId()).tenantId(demand.getTenantId()).build());
		if (!isInterestUpdated && interest.compareTo(BigDecimal.ZERO) > 0)
			demand.getDemandDetails().add(
					DemandDetail.builder().taxAmount(interest.setScale(2, 2)).taxHeadMasterCode(SWCalculationConstant.SW_TIME_INTEREST)
							.demandId(demand.getId()).tenantId(demand.getTenantId()).build());
		
		if (!isRebateUpdated && rebate.compareTo(BigDecimal.ZERO) != 0)
			details.add(
					DemandDetail.builder().taxAmount(rebate.setScale(2, 2)).taxHeadMasterCode(SWCalculationConstant.SW_TIME_REBATE)
							.demandId(demand.getId()).tenantId(demand.getTenantId()).build());
		}
		return isCurrentDemand;
	}
	
	private void updateRebate(BigDecimal newAmount, DemandDetailAndCollection latestDetailInfo) {
		BigDecimal diff =BigDecimal.ZERO;
		if(newAmount.compareTo(BigDecimal.ZERO)==0)
			diff=BigDecimal.ZERO;
		else
			diff= newAmount;
		
		log.info("Rebate after calculation is "+ diff);
		
		latestDetailInfo.getLatestDemandDetail().setTaxAmount(diff);
	}
	
	public void SingleDemandGenerate(String tenantId, SingleDemand singledemand) {
		singledemand.getRequestInfo().getUserInfo().setTenantId(tenantId);
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterDatas(singledemand, tenantId);
		long taxPeriodFrom = billingMasterData.get("taxPeriodFrom") == null ? 0l
				: (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = billingMasterData.get("taxPeriodTo") == null ? 0l : (long) billingMasterData.get("taxPeriodTo");
		log.info("generateDemandForTenantId:: "+ tenantId+" taxPeriodFrom:: "+taxPeriodFrom+" taxPeriodTo "+taxPeriodTo);
		if(taxPeriodFrom == 0 || taxPeriodTo == 0) {
			throw new CustomException("NO_BILLING_PERIODS","MDMS Billing Period does not available for tenant: "+ tenantId);
		}

		generateDemandForSingle(billingMasterData, singledemand, tenantId, taxPeriodFrom, taxPeriodTo);
	}
	
	
	public String generateDemandForConsumerCode(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo,  bulkBillCriteria.getTenantId());
		long startDay = (((int) billingMasterData.get(SWCalculationConstant.Demand_Generate_Date_String)) / 86400000);
		if(isCurrentDateIsMatching((String) billingMasterData.get(SWCalculationConstant.Billing_Cycle_String), startDay)) {

			Map<String, Object> masterMap = masterDataService.loadMasterData(requestInfo,  bulkBillCriteria.getTenantId());

			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(SWCalculationConstant.Billing_Period_Master);
			masterDataService.enrichBillingPeriod(null, billingFrequencyMap, masterMap, SWCalculationConstant.nonMeterdConnection);

			Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
					.get(SWCalculationConstant.BILLING_PERIOD);

			// Long fromDate = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
			// Long toDate = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);
			// log.info("from date:: "+ fromDate);
			// log.info("to Date :: "+ toDate);
		Long fromDate= (Long) billingMasterData.get("taxPeriodFrom");
			Long toDate = (Long) billingMasterData.get("taxPeriodTo");
			List<SewerageConnection> connections = sewerageCalculatorDao.getConnection( bulkBillCriteria.getTenantId(),bulkBillCriteria.getConsumerCode(),
							SWCalculationConstant.nonMeterdConnection, fromDate, toDate);
					log.info("Size of the connection list for batch : "+ connections.size());
					connections = enrichmentService.filterConnections(connections);
					log.info("Size of the connection list after filtering "+ connections.size());

					if(connections.size()>0){
						List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();

						for (SewerageConnection connection : connections) {
							CalculationCriteria calculationCriteria = CalculationCriteria.builder().tenantId( bulkBillCriteria.getTenantId())
									.assessmentYear(estimationService.getAssessmentYear()).connectionNo(connection.getConnectionNo())
									.sewerageConnection(connection).build();
							calculationCriteriaList.add(calculationCriteria);
						}

						/*MigrationCount migrationCount = MigrationCount.builder().id(UUID.randomUUID().toString()).offset(Long.valueOf(batchOffset)).limit(Long.valueOf(batchsize)).recordCount(Long.valueOf(connectionNos.size()))
								.tenantid(tenantId).createdTime(System.currentTimeMillis()).businessService("SW").build();*/

						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid( bulkBillCriteria.getTenantId())
								.businessService("SW")
								.limit((long)1.00)
								.id(UUID.randomUUID().toString())
								.offset((long)1.00)								
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connections.size()))
								.build();

						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.taxPeriodFrom(fromDate)
								.taxPeriodTo(toDate)
								.requestInfo(requestInfo)
								.isconnectionCalculation(true)
								.migrationCount(migrationCount).build();
						
						swCalculationProducer.push(configs.getCreateDemand(), calculationReq);
						calculationCriteriaList.clear();
						return "Demand Generated successfully for consumer Code"+bulkBillCriteria.getConsumerCode();
					}
					return "Demand is already generated for consumer code" + bulkBillCriteria.getConsumerCode();
			
		}

		return "Some Error Occured!!";
	}
	
	
	public List<SewerageConnection> getConnectionPendingForDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo, bulkBillCriteria.getTenantId());
		List<SewerageConnection> connections=new ArrayList<SewerageConnection>();

		long startDay = (((int) billingMasterData.get(SWCalculationConstant.Demand_Generate_Date_String)) / 86400000);
		if(isCurrentDateIsMatching((String) billingMasterData.get(SWCalculationConstant.Billing_Cycle_String), startDay)) {

			Map<String, Object> masterMap = masterDataService.loadMasterData(requestInfo, bulkBillCriteria.getTenantId());

			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(SWCalculationConstant.Billing_Period_Master);
			masterDataService.enrichBillingPeriod(null, billingFrequencyMap, masterMap, SWCalculationConstant.nonMeterdConnection);

			Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
					.get(SWCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);

			connections = sewerageCalculatorDao.getConnectionsNoListForDemand(bulkBillCriteria.getTenantId(),
							SWCalculationConstant.nonMeterdConnection, fromDate, toDate);
					log.info("Size of the connection list for batch : "+ connections.size());
					connections = enrichmentService.filterConnections(connections);
					
										}
					return connections;

	}

	
	/**
	 * 
	 * @param tenantId TenantId for getting master data.
	 */
	public void generateDemandForTenantId(String tenantId, RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		requestInfo.getUserInfo().setTenantId(tenantId);
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo, tenantId);
		generateDemandForULB(requestInfo, tenantId, bulkBillCriteria);
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
		long taxPeriodTo = billingMasterData.get("taxPeriodTo") == null ? 0l : (long) billingMasterData.get("taxPeriodTo");
		if(taxPeriodFrom == 0 || taxPeriodTo == 0) {
			throw new CustomException("NO_BILLING_PERIODS","MDMS Billing Period does not available for tenant: "+ tenantId);
		}
		
		generateDemandForULB(billingMasterData, requestInfo, tenantId, taxPeriodFrom, taxPeriodTo);
	}
	
	/**
	 * 
	 * @param master - List of MDMS master data
	 * @param requestInfo - Request Info Object
	 * @param tenantId - Tenant Id
	 * @param bulkBillCriteria - Critera for bulk bill generation
	 */
	@SuppressWarnings("unchecked")
	public void generateDemandForULB(RequestInfo requestInfo, String tenantId, BulkBillCriteria bulkBillCriteria) {

		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo, tenantId);

		long startDay = (((int) billingMasterData.get(SWCalculationConstant.Demand_Generate_Date_String)) / 86400000);
		if(isCurrentDateIsMatching((String) billingMasterData.get(SWCalculationConstant.Billing_Cycle_String), startDay)) {

			Integer batchsize = configs.getBulkbatchSize();
			Integer batchOffset = configs.getBatchOffset();

			if(bulkBillCriteria.getLimit() != null)
				batchsize = Math.toIntExact(bulkBillCriteria.getLimit());

			if(bulkBillCriteria.getOffset() != null)
				batchOffset = Math.toIntExact(bulkBillCriteria.getOffset());

			Map<String, Object> masterMap = masterDataService.loadMasterData(requestInfo, tenantId);

			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(SWCalculationConstant.Billing_Period_Master);
			masterDataService.enrichBillingPeriod(null, billingFrequencyMap, masterMap, SWCalculationConstant.nonMeterdConnection);

			Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
					.get(SWCalculationConstant.BILLING_PERIOD);

			Long fromDate = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
			Long toDate = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);

			long count = sewerageCalculatorDao.getConnectionCount(tenantId, fromDate, toDate);
			
			log.info("Count: "+count);

			if(count > 0) {
				while (batchOffset < count) {
					List<SewerageConnection> connections = sewerageCalculatorDao.getConnectionsNoList(tenantId,
							SWCalculationConstant.nonMeterdConnection, batchOffset, batchsize, fromDate, toDate);
					log.info("Size of the connection list for batch : "+ batchOffset + " is " + connections.size());
					connections = enrichmentService.filterConnections(connections);
					
					if(connections.size()>0){
						List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();

						for (SewerageConnection connection : connections) {
							CalculationCriteria calculationCriteria = CalculationCriteria.builder().tenantId(tenantId)
									.assessmentYear(estimationService.getAssessmentYear()).connectionNo(connection.getConnectionNo())
									.sewerageConnection(connection).build();
							calculationCriteriaList.add(calculationCriteria);
						}

						/*MigrationCount migrationCount = MigrationCount.builder().id(UUID.randomUUID().toString()).offset(Long.valueOf(batchOffset)).limit(Long.valueOf(batchsize)).recordCount(Long.valueOf(connectionNos.size()))
								.tenantid(tenantId).createdTime(System.currentTimeMillis()).businessService("SW").build();*/

						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid(tenantId)
								.businessService("SW")
								.limit(Long.valueOf(batchsize))
								.id(UUID.randomUUID().toString())
								.offset(Long.valueOf(batchOffset))
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connections.size()))
								.build();

						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.requestInfo(requestInfo)
								.isconnectionCalculation(true)
								.migrationCount(migrationCount).build();
						
						kafkaTemplate.send(configs.getCreateDemand(), calculationReq);
						log.info("Bulk bill Gen batch info : " + migrationCount);
						calculationCriteriaList.clear();
					}
					batchOffset = batchOffset + batchsize;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param master      - List of MDMS aster data
	 * @param requestInfo - Request Info Object
	 * @param tenantId    - Tenant Id
	 */
	@SuppressWarnings("unchecked")
	public void generateDemandForULB(Map<String, Object> master, RequestInfo requestInfo, String tenantId,
			Long taxPeriodFrom, Long taxPeriodTo) {
		try {
			List<Role> roles = requestInfo.getUserInfo().getRoles()	!= null ? requestInfo.getUserInfo().getRoles() : new ArrayList<Role>();
			log.info("requestInfo Before removing Anonymous User: {}", mapper.writeValueAsString(requestInfo));
			//Removing the ANONYMOUS role.
			roles.removeIf(role -> role.getCode().equalsIgnoreCase("ANONYMOUS"));
			log.info("requestInfo After removing Anonymous User: {}", mapper.writeValueAsString(requestInfo));

			List<TaxPeriod> taxPeriods = calculatorUtils.getTaxPeriodsFromMDMS(requestInfo, tenantId);
			
			int generateDemandToIndex = IntStream.range(0, taxPeriods.size())
				     .filter(p -> taxPeriodFrom.equals(taxPeriods.get(p).getFromDate()))
				     .findFirst().getAsInt();
			
			log.info("Billing master data values for non metered connection:: {}", master);
			String cone=requestInfo.getKey();
			List<SewerageDetails> connectionNos = sewerageCalculatorDao.getConnectionsNoListsingle(tenantId,SWCalculationConstant.nonMeterdConnection, taxPeriodFrom, taxPeriodTo,cone);


			//Generate bulk demands for connections in below count
			int bulkSaveDemandCount = configs.getBulkSaveDemandCount() != null ? configs.getBulkSaveDemandCount() : 1;
			
			log.info("Total Connections: {} and batch count: {}", connectionNos.size(), bulkSaveDemandCount);

			int connectionNosCount = 0;
			int totalRecordsPushedToKafka = 0;
			int threadSleepCount = 0;
			List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
			for (int connectionNosIndex = 0; connectionNosIndex < connectionNos.size(); connectionNosIndex++) {
				SewerageDetails sewConnDetails = connectionNos.get(connectionNosIndex);
				connectionNosCount++;
				int billingCycleCount = 0;

				try {
					int generateDemandFromIndex = 0;
					Long lastDemandFromDate = sewerageCalculatorDao.searchLastDemandGenFromDate(sewConnDetails.getConnectionNo(), tenantId);
					if(lastDemandFromDate != null) {
						generateDemandFromIndex = IntStream.range(0, taxPeriods.size())
								.filter(p -> lastDemandFromDate.equals(taxPeriods.get(p).getFromDate()))
								.findFirst().getAsInt();
						//Increased one index to generate the next quarter demand
						generateDemandFromIndex++;
					}

					for (int taxPeriodIndex = generateDemandFromIndex; generateDemandFromIndex <= generateDemandToIndex; taxPeriodIndex++) {
						generateDemandFromIndex++;
						billingCycleCount++;
						TaxPeriod taxPeriod = taxPeriods.get(taxPeriodIndex);
//						log.info("FromPeriod: {} and ToPeriod: {}",taxPeriod.getFromDate(),taxPeriod.getToDate());
						log.info("taxPeriodIndex: {} and generateDemandFromIndex: {} and generateDemandToIndex: {}",taxPeriodIndex, generateDemandFromIndex, generateDemandToIndex);

						boolean isValidBillingCycle = isValidBillingCycle(sewConnDetails, taxPeriod.getFromDate(), taxPeriod.getToDate(), tenantId,
								requestInfo);
						if (isValidBillingCycle) {

							CalculationCriteria calculationCriteria = CalculationCriteria.builder()
									.tenantId(tenantId)
									.assessmentYear(taxPeriod.getFinancialYear())
									.from(taxPeriod.getFromDate())
									.to(taxPeriod.getToDate())
									.connectionNo(sewConnDetails.getConnectionNo())
									.build();
							calculationCriteriaList.add(calculationCriteria);
							log.info("connectionNosIndex: {} and connectionNos.size(): {}",connectionNosIndex, connectionNos.size());

						}
						
					}
					if(calculationCriteriaList == null || calculationCriteriaList.isEmpty())
						continue;
					
					if(billingCycleCount > 10 || connectionNosCount == bulkSaveDemandCount) {
						log.info("Controller entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}",connectionNosCount, connectionNos.size());
						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid(tenantId)
								.businessService("SW")
								.limit((long)1.00)
								.id(UUID.randomUUID().toString())
								.offset((long)1.00)								
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connectionNos.size()))
								.build();
						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.requestInfo(requestInfo)
								.isconnectionCalculation(true)
								.build();
						log.info("Pushing calculation req to the kafka topic with bulk data of calculationCriteriaList size: {}", calculationCriteriaList.size());
						kafkaTemplate.send(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka++;
						billingCycleCount=0;
						calculationCriteriaList.clear();
						connectionNosCount=0;
						if(threadSleepCount == 3) {
							Thread.sleep(15000);
							threadSleepCount=0;
						}
						threadSleepCount++;

					} else if(connectionNosIndex == connectionNos.size()-1) {
						log.info("Last connection entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}",connectionNosCount, connectionNos.size());
						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid(tenantId)
								.businessService("SW")
								.limit((long)1.00)
								.id(UUID.randomUUID().toString())
								.offset((long)1.00)								
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connectionNos.size()))
								.build();
						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.requestInfo(requestInfo)
								.isconnectionCalculation(true)
								.build();
						log.info("Pushing calculation last req to the kafka topic with bulk data of calculationCriteriaList size: {}", calculationCriteriaList.size());
						kafkaTemplate.send(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka++;
						calculationCriteriaList.clear();
						connectionNosCount=0;

					}

				}catch (Exception e) {
					log.error("Exception occurred while generating demand for sewerage connectionno: "+sewConnDetails.getConnectionNo() + " tenantId: "+tenantId);
				}
			}
			log.info("totalConnRecordsPushedToKafka: {}", totalRecordsPushedToKafka);
		}catch (Exception e) {
			log.error("Exception occurred while processing the demand generation for tenantId: "+tenantId);
		}
	}

	private boolean isValidBillingCycle(SewerageDetails detail, long taxPeriodFrom, long taxPeriodTo,
			String tenantId, RequestInfo requestInfo) {
		boolean isValidSewerageConnection = true;

		if (detail.getConnectionExecutionDate() > taxPeriodTo) {

			isValidSewerageConnection = false;
		}

		
		/*
		 * if (detail.getConnectionExecutionDate() < taxPeriodFrom) {
		 * 
		 * isValidSewerageConnection = fetchBill(detail, taxPeriodFrom, taxPeriodTo,
		 * tenantId, requestInfo);
		 * 
		 * }
		 */

		return isValidSewerageConnection;
	}
	public String generateDemandForSingle(Map<String, Object> master, SingleDemand singleDemand, String tenantId,
			Long taxPeriodFrom, Long taxPeriodTo) {
		try {
			List<Role> roles = singleDemand.getRequestInfo().getUserInfo().getRoles()	!= null ? singleDemand.getRequestInfo().getUserInfo().getRoles() : new ArrayList<Role>();
			log.info("requestInfo Before removing Anonymous User: {}", mapper.writeValueAsString(singleDemand.getRequestInfo()));
			//Removing the ANONYMOUS role.
			roles.removeIf(role -> role.getCode().equalsIgnoreCase("ANONYMOUS"));
			log.info("requestInfo After removing Anonymous User: {}", mapper.writeValueAsString(singleDemand.getRequestInfo()));

			List<TaxPeriod> taxPeriods = calculatorUtils.getTaxPeriodsFromMDMS(singleDemand.getRequestInfo(), tenantId);
			
			int generateDemandToIndex = IntStream.range(0, taxPeriods.size())
				     .filter(p -> taxPeriodFrom.equals(taxPeriods.get(p).getFromDate()))
				     .findFirst().getAsInt();
			
			log.info("Billing master data values for non metered connection:: {}", master);
		
			List<SewerageDetails> connectionNos = sewerageCalculatorDao.getConnectionsNoListsingle(tenantId,SWCalculationConstant.nonMeterdConnection, taxPeriodFrom, taxPeriodTo,singleDemand.getConsumercode());

			int bulkSaveDemandCount = configs.getBulkSaveDemandCount() != null ? configs.getBulkSaveDemandCount() : 1;
			log.info("Total Connections: {} and batch count: {}", connectionNos.size(), bulkSaveDemandCount);

			int connectionNosCount = 0;
			int totalRecordsPushedToKafka = 0;
			int threadSleepCount = 0;
			List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
			for (int connectionNosIndex = 0; connectionNosIndex < connectionNos.size(); connectionNosIndex++) {
				SewerageDetails sewConnDetails = connectionNos.get(connectionNosIndex);
				connectionNosCount++;
				int billingCycleCount = 0;

				try {
					int generateDemandFromIndex = 0;
					Long lastDemandFromDate = sewerageCalculatorDao.searchLastDemandGenFromDate(sewConnDetails.getConnectionNo(), tenantId);
					if(lastDemandFromDate != null) {
						generateDemandFromIndex = IntStream.range(0, taxPeriods.size())
								.filter(p -> lastDemandFromDate.equals(taxPeriods.get(p).getFromDate()))
								.findFirst().getAsInt();
						//Increased one index to generate the next quarter demand
						generateDemandFromIndex++;
					}

					for (int taxPeriodIndex = generateDemandFromIndex; generateDemandFromIndex <= generateDemandToIndex; taxPeriodIndex++) {
						generateDemandFromIndex++;
						billingCycleCount++;
						TaxPeriod taxPeriod = taxPeriods.get(taxPeriodIndex);
//						log.info("FromPeriod: {} and ToPeriod: {}",taxPeriod.getFromDate(),taxPeriod.getToDate());
						log.info("taxPeriodIndex: {} and generateDemandFromIndex: {} and generateDemandToIndex: {}",taxPeriodIndex, generateDemandFromIndex, generateDemandToIndex);

						boolean isValidBillingCycle = isValidBillingCycle(sewConnDetails, taxPeriod.getFromDate(), taxPeriod.getToDate(), tenantId,
								singleDemand.getRequestInfo());
						if (isValidBillingCycle) {
							MigrationCount migrationCount = MigrationCount.builder()
									.tenantid( singleDemand.getTenantId())
									.businessService("SW")
									.limit((long)1.00)
									.id(UUID.randomUUID().toString())
									.offset((long)1.00)								
									.createdTime(System.currentTimeMillis())
									.recordCount(Long.valueOf(connectionNos.size()))
									.build();
							CalculationCriteria calculationCriteria = CalculationCriteria.builder()
									.tenantId(tenantId)
									.assessmentYear(taxPeriod.getFinancialYear())
									.from(taxPeriod.getFromDate())
									.to(taxPeriod.getToDate())
									.connectionNo(sewConnDetails.getConnectionNo())
									.build();
							calculationCriteriaList.add(calculationCriteria);
							log.info("connectionNosIndex: {} and connectionNos.size(): {}",connectionNosIndex, connectionNos.size());

						}
						
					}
					if(calculationCriteriaList == null || calculationCriteriaList.isEmpty())
						continue;
					
					if(billingCycleCount > 10 || connectionNosCount == bulkSaveDemandCount) {
						log.info("Controller entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}",connectionNosCount, connectionNos.size());
						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid( singleDemand.getTenantId())
								.businessService("SW")
								.limit((long)1.00)
								.id(UUID.randomUUID().toString())
								.offset((long)1.00)								
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connectionNos.size()))
								.build();
						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.requestInfo(singleDemand.getRequestInfo())
								.isconnectionCalculation(true)
								.migrationCount(migrationCount)
								.build();
						log.info("Pushing calculation req to the kafka topic with bulk data of calculationCriteriaList size: {}", calculationCriteriaList.size());
						kafkaTemplate.send(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka++;
						billingCycleCount=0;
						calculationCriteriaList.clear();
						connectionNosCount=0;
						if(threadSleepCount == 3) {
							Thread.sleep(15000);
							threadSleepCount=0;
						}
						threadSleepCount++;

					} else if(connectionNosIndex == connectionNos.size()-1) {
						log.info("Last connection entered into producer logic, connectionNosCount: {} and connectionNos.size(): {}",connectionNosCount, connectionNos.size());
						MigrationCount migrationCount = MigrationCount.builder()
								.tenantid( singleDemand.getTenantId())
								.businessService("SW")
								.limit((long)1.00)
								.id(UUID.randomUUID().toString())
								.offset((long)1.00)								
								.createdTime(System.currentTimeMillis())
								.recordCount(Long.valueOf(connectionNos.size()))
								.build();
						CalculationReq calculationReq = CalculationReq.builder()
								.calculationCriteria(calculationCriteriaList)
								.requestInfo(singleDemand.getRequestInfo())
								.isconnectionCalculation(true)
								.migrationCount(migrationCount).build();
						log.info("Pushing calculation last req to the kafka topic with bulk data of calculationCriteriaList size: {}", calculationCriteriaList.size());
						kafkaTemplate.send(configs.getCreateDemand(), calculationReq);
						totalRecordsPushedToKafka++;
						calculationCriteriaList.clear();
						connectionNosCount=0;

					}

				}catch (Exception e) {
					log.error("Exception occurred while generating demand for sewerage connectionno: "+sewConnDetails.getConnectionNo() + " tenantId: "+tenantId);
				}
			}
			log.info("totalConnRecordsPushedToKafka: {}", totalRecordsPushedToKafka);
		}catch (Exception e) {
			log.error("Exception occurred while processing the demand generation for tenantId: "+tenantId);
		}
		return tenantId;
	}
	
	/**
	 * 
	 * @param billingFrequency - Billing Frequency period
	 * @param dayOfMonth - Day of the Month
	 * @return true if current day is for generation of demand
	 */
	private boolean isCurrentDateIsMatching(String billingFrequency, long dayOfMonth) {
		if (billingFrequency.equalsIgnoreCase(SWCalculationConstant.Monthly_Billing_Period)
				&& (dayOfMonth == LocalDateTime.now().getDayOfMonth())) {
			return true;
		} else if (billingFrequency.equalsIgnoreCase(SWCalculationConstant.Quaterly_Billing_Period)) {
			return false;
		}
		return true;
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
	 * @param tenantId - Tenant ID
	 * @param consumerCode - Connection number
	 * @param requestInfo - Request Info Object
	 * @return List of Demand
	 */
	private List<Demand> searchDemandBasedOnConsumerCode(String tenantId, String consumerCode,
			RequestInfo requestInfo, String businessService) {
		String uri = getDemandSearchURLForDemandId().toString();
		uri = uri.replace("{1}", tenantId);
		uri = uri.replace("{2}", businessService);
		uri = uri.replace("{3}", consumerCode);
		Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		try {
			return mapper.convertValue(result, DemandResponse.class).getDemands();
		} catch (IllegalArgumentException e) {
			throw new CustomException("EG_SW_PARSING_ERROR", "Failed to parse response from Demand Search");
		}
	}
	
	/**
	 * compare and update the demand details
	 * 
	 * @param calculation - Calculation Object
	 * @param demandDetails - List of Demand Details
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
public List<String> fetchBillSchedulerBatch(Set<String> consumerCodes,String tenantId, RequestInfo requestInfo) {
		List<String> consumercodesFromRes = null ;
		try {

			StringBuilder fetchBillURL = calculatorUtils.getFetchBillURL(tenantId, getCommaSeparateStrings(consumerCodes));

			Object result = serviceRequestRepository.fetchResult(fetchBillURL, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			log.info("Bills generated for the consumercodes: {}", fetchBillURL);
			BillResponseV2 billResponse = mapper.convertValue(result, BillResponseV2.class);
			List<BillV2> bills = billResponse.getBill();
			if(bills != null && !bills.isEmpty()) {
				consumercodesFromRes = bills.stream().map(BillV2::getConsumerCode).collect(Collectors.toList());
			}

		} catch (Exception ex) {
			log.error("Fetch Bill Error For tenantId:{} consumercode: {} and Exception is: {}",tenantId,consumerCodes, ex);
			return consumercodesFromRes;
		}
		return consumercodesFromRes;
	}
	
	public List<String> fetchBillSchedulerSingle(Set<String> consumerCodes,String tenantId, RequestInfo requestInfo) {
		List<String> consumercodesFromRes = new ArrayList<>() ;
		for (String consumerCode : consumerCodes) {

			try {
				
				StringBuilder fetchBillURL = calculatorUtils.getFetchBillURL(tenantId, consumerCode);

				Object result = serviceRequestRepository.fetchResult(fetchBillURL, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				log.info("Bills generated for the consumercodes: {}", fetchBillURL);
				BillResponseV2 billResponse = mapper.convertValue(result, BillResponseV2.class);
				List<BillV2> bills = billResponse.getBill();
				if(bills != null && !bills.isEmpty()) {
					consumercodesFromRes.addAll(bills.stream().map(BillV2::getConsumerCode).collect(Collectors.toList()));
					log.info("Bill generated successfully for consumercode: {}, TenantId: {}" ,consumerCode, tenantId);
				}

			} catch (Exception ex) {
				log.error("Fetch Bill Error For tenantId:{} consumercode: {} and Exception is: {}",tenantId,consumerCodes, ex);
			}
		}
		return consumercodesFromRes;
	}
		/**
		 * Search demand based on demand id and updated the tax heads with new adhoc tax heads
		 * 
		 * @param requestInfo - Request Info Object
		 * @param calculations - List of Calculations
		 * @return List of calculation
		 */
		public List<Calculation> updateDemandForAdhocTax(RequestInfo requestInfo, List<Calculation> calculations, String businessService) {
			List<Demand> demands = new LinkedList<>();
			for (Calculation calculation : calculations) {
				String consumerCode = calculation.getConnectionNo();
				List<Demand> searchResult = searchDemandBasedOnConsumerCode(calculation.getTenantId(), consumerCode,
						requestInfo, businessService);
				if (CollectionUtils.isEmpty(searchResult))
					throw new CustomException("EG_SW_INVALID_DEMAND_UPDATE",
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

	/**
	 * Search the latest demand generated for a connection.
	 *
	 * @param tenantId
	 * @param consumerCodes
	 * @param fromDateSearch
	 * @param toDateSearch
	 * @param requestInfo            - Request Info Object
	 * @param isDisconnectionRequest - Boleean value to test if it is a Disconnection request
	 * @return List of calculation
	 */
	List<Demand> searchDemandForDisconnectionRequest(String tenantId, Set<String> consumerCodes,
													 Long fromDateSearch, Long toDateSearch, RequestInfo requestInfo, Boolean isDemandPaid, Boolean isDisconnectionRequest) {
		List<Demand> demandList = searchDemand(tenantId, consumerCodes, null, toDateSearch, requestInfo,
				null, isDisconnectionRequest ,false);
		if (!CollectionUtils.isEmpty(demandList)) {
			//Sorting the demandList in descending order to pick the latest demand generated
			demandList = demandList.stream().sorted(Comparator.comparing(Demand::getTaxPeriodTo)
					.reversed()).collect(Collectors.toList());
		}
		return demandList;
	}
	
	
	
	/**
	 * 
	 * @param requestInfo  RequestInfo
	 * @param calculations List of Calculation
	 * @param masterMap    Master MDMS Data
	 * @return Returns list of demands
	 */
	private Demand createDemandForNonMeteredInBulk(RequestInfo requestInfo, Calculation calculation,
			Map<String, Object> masterMap, boolean isForConnectionNO, long taxPeriodFrom, long taxPeriodTo) {

			SewerageConnection connection = calculation.getSewerageConnection();
			if (connection == null) {
				throw new CustomException("INVALID_WATER_CONNECTION",
						"Demand cannot be generated for "
								+ (isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO())
								+ " Water Connection with this number does not exist ");
			}
			SewerageConnectionRequest sewerageConnectionRequest = SewerageConnectionRequest.builder().sewerageConnection(connection)
					.requestInfo(requestInfo).build();
			
			log.info("SewerageConnectionRequest: {}",sewerageConnectionRequest);
			Property property = utils.getProperty(sewerageConnectionRequest);
			log.info("Property: {}",property);
			
			String tenantId = calculation.getTenantId();
			String consumerCode = isForConnectionNO ? calculation.getConnectionNo() : calculation.getApplicationNO();
			User owner = property.getOwners().get(0).toCommonUser();
			if (!CollectionUtils.isEmpty(sewerageConnectionRequest.getSewerageConnection().getConnectionHolders())) {
				owner = sewerageConnectionRequest.getSewerageConnection().getConnectionHolders().get(0).toCommonUser();
			}
			List<DemandDetail> demandDetails = new LinkedList<>();
			calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
				demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
						.tenantId(tenantId).build());
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> financialYearMaster = (Map<String, Object>) masterMap
					.get(SWCalculationConstant.BILLING_PERIOD);

			if (taxPeriodFrom == 0 && taxPeriodTo == 0) {
				taxPeriodFrom = (Long) financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
				taxPeriodTo = (Long) financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);
			}
			Long expiryDaysInmillies = (Long) financialYearMaster.get(SWCalculationConstant.Demand_Expiry_Date_String);
			//Long expiryDate = System.currentTimeMillis() + expiryDaysInmillies;

			BigDecimal minimumPayableAmount = calculation.getTotalAmount();
			String businessService = isForConnectionNO ? configs.getBusinessService()
					: SWCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;

			addRoundOffTaxHead(calculation.getTenantId(), demandDetails);
			Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
						.minimumAmountPayable(minimumPayableAmount).tenantId(tenantId).taxPeriodFrom(taxPeriodFrom)
						.taxPeriodTo(taxPeriodTo).consumerType("waterConnection").businessService(businessService)
						.status(StatusEnum.valueOf("ACTIVE")).billExpiryTime(expiryDaysInmillies).build();
						
		return demand;
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
		

}
