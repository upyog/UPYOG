package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.GrbgBillFailure;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerRequest;
import org.egov.garbageservice.model.GrbgBillTrackerResponse;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.producer.GarbageProducer;
import org.egov.garbageservice.model.OnDemandBillRequest;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.model.UserSearchRequest;
import org.egov.garbageservice.model.contract.OwnerInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.garbageservice.util.GrbgConstants;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageAccountSchedulerService {

	@Autowired
	private GarbageAccountService garbageAccountService;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DemandService demandService;
	
	@Autowired
	private UserService userService ;

	@Autowired
	private BillService billService;

	@Autowired
	private MdmsService mdmsService;

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private GarbageProducer producer;
	
	@Autowired
	private GrbgConstants properties;

	public GrbgBillTrackerResponse generateBill(GenerateBillRequest generateBillRequest) {

		List<GrbgBillTracker> grbgBillTrackers = new ArrayList<>();
		setFromDateToDate(generateBillRequest);
		String message = null;
		List<GarbageAccount> garbageAccounts = getGarbageAccounts(generateBillRequest);

		garbageAccounts = removeAlreadyBillCalculatedGarbageAccounts(garbageAccounts, generateBillRequest);

		// create demand and bill for every account
		if (null != garbageAccounts && !CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccounts.stream().forEach(garbageAccount -> {
				List<String> errorList = new ArrayList<>();
				ObjectNode  calculationBreakdown = objectMapper.createObjectNode();
				if(null != garbageAccount.getUserUuid()) {
					Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(generateBillRequest.getRequestInfo(),
							garbageAccount.getTenantId());
					// calculate fees from mdms response
					BigDecimal billAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse, garbageAccount,errorList,calculationBreakdown);

					if (billAmount != null && billAmount.compareTo(BigDecimal.ZERO) > 0 && errorList.isEmpty()) {
					
						BillResponse billResponse = generateDemandAndBill(generateBillRequest, garbageAccount, billAmount,"MONTHLY");
	
						if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
							GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService
									.enrichGrbgBillTrackerCreateRequest(garbageAccount, generateBillRequest, billAmount,billResponse.getBill().get(0),calculationBreakdown);
							// add to garbage bill tracker
							GrbgBillTracker grbgBillTracker = garbageAccountService
									.saveToGarbageBillTracker(grbgBillTrackerRequest);
							grbgBillTrackers.add(grbgBillTracker);
							
							//remove bill if failure exists
//							GrbgBillFailure grbgBillFailure	= garbageAccountService.enrichGrbgBillFailure(garbageAccount, generateBillRequest,billResponse,errorList);
//							garbageAccountService.removeGarbageBillFailure(grbgBillFailure);
//							triggerNotifications
							notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),
								generateBillRequest.getRequestInfo(),grbgBillTracker);
						}else {
							errorList.add("Issues In Bill Generation Probably Demand Already Exists");
							createFailureLog(garbageAccount, generateBillRequest,billResponse,errorList);
						}
					}else {
						
						errorList.add("Amount could not be calculated");
						createFailureLog(garbageAccount, generateBillRequest,null,errorList);
					}
				}
				else {
					errorList.add("Mobile number user not mapped");
					createFailureLog(garbageAccount, generateBillRequest,null,errorList);
				}
			});
		}else {
			message = "Garbage Acc Not Found";
		}

		if(!grbgBillTrackers.isEmpty())
			message = "Bills Generated Successfully";
		
		sanatizeFailureLog(generateBillRequest);
		
		return GrbgBillTrackerResponse.builder().grbgBillTrackers(grbgBillTrackers).message(message).build();

	}
	
	private void sanatizeFailureLog(GenerateBillRequest generateBillRequest) {
		producer.push(properties.getSanatizeLogger(),generateBillRequest.getUlbNames().get(0));
	}
	
//	private void testKafka() {
//		producer.push(properties.getTestKafka(),generateBillRequest.getUlbNames().get(0));
//	}
	
	
	private void createFailureLog(GarbageAccount garbageAccount,GenerateBillRequest generateBillRequest, BillResponse billResponse,List<String> errorMap) {
		GrbgBillFailure grbgBillFailure	= garbageAccountService.enrichGrbgBillFailure(garbageAccount, generateBillRequest,billResponse,errorMap);
		garbageAccountService.saveToGarbageBillFailure(grbgBillFailure);
	}

	private List<GarbageAccount> removeAlreadyBillCalculatedGarbageAccounts(List<GarbageAccount> garbageAccounts,
			GenerateBillRequest generateBillRequest) {
		Set<String> grbgApplicationIds = garbageAccounts.stream().map(GarbageAccount::getGrbgApplicationNumber)
				.collect(Collectors.toSet());

		GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.grbgApplicationIds(grbgApplicationIds).type("MONTHLY").status(new HashSet<>(Arrays.asList("ACTIVE", "PAID"))).build();

		List<GrbgBillTracker> grbgBillTrackers = garbageAccountService
				.getBillCalculatedGarbageAccounts(grbgBillTrackerSearchCriteria);

		Map<String, List<GrbgBillTracker>> grbgBillTrackerMap = grbgBillTrackers.stream()
				.collect(Collectors.groupingBy(GrbgBillTracker::getGrbgApplicationId));

		garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> {
			List<GrbgBillTracker> trackers = grbgBillTrackerMap.get(garbageAccount.getGrbgApplicationNumber());
			log.info("tracker {}",trackers);
			if (trackers == null) {
				// If no trackers found for the garbage account, we add the garbage account.
				return true;
			}
			// Check if the garbage account matches the conditions
			return trackers.stream().noneMatch(grbgBillTracker -> garbageAccount.getGrbgApplicationNumber()
					.equalsIgnoreCase(garbageAccount.getGrbgApplicationNumber())
					&& (purseToDate(grbgBillTracker.getFromDate()).after(generateBillRequest.getFromDate())
							|| purseToDate(grbgBillTracker.getFromDate()).equals(generateBillRequest.getFromDate()))
					&& purseToDate(grbgBillTracker.getToDate()).before(generateBillRequest.getToDate())
					|| purseToDate(grbgBillTracker.getToDate()).equals(generateBillRequest.getToDate()));
		}).collect(Collectors.toList());

		return garbageAccounts;
	}

	private void setFromDateToDate(GenerateBillRequest generateBillRequest) {
		LocalDate lastMonthDate;
		LocalDate startOfMonth;
		
		LocalDate endOfMonth;

		if (!StringUtils.isEmpty(generateBillRequest.getMonth())
				&& !StringUtils.isEmpty(generateBillRequest.getYear())) {
			LocalDate inputDate = LocalDate.of(Integer.valueOf(generateBillRequest.getYear()),
					Month.valueOf(generateBillRequest.getMonth().toUpperCase()), 1);
			lastMonthDate = inputDate;
			startOfMonth = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth());
			endOfMonth = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth());
		} else {
			LocalDate currentDate = LocalDate.now();
			lastMonthDate = currentDate.minusMonths(1);
			startOfMonth = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth());
			endOfMonth = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth());
			generateBillRequest.setMonth(lastMonthDate.getMonth().name());
			generateBillRequest.setYear(String.valueOf(lastMonthDate.getYear()));
		}
		generateBillRequest.setFromDate(Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		generateBillRequest.setToDate(Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	private List<GarbageAccount> getGarbageAccounts(GenerateBillRequest generateBillRequest) {
		List<GarbageAccount> garbageAccounts = new ArrayList<>();
		List<String> ulbNames = generateBillRequest.getUlbNames();
		List<String> wardNumbers = generateBillRequest.getWardNumbers();

		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().requestInfo(generateBillRequest.getRequestInfo())
				.searchCriteriaGarbageAccount(SearchCriteriaGarbageAccount.builder()
						.applicationNumber(generateBillRequest.getGrbgApplicationNumbers())
						.mobileNumber(generateBillRequest.getMobileNumbers())
						.isMonthlyBilling(true)
						.tenantId("hp." +ulbNames.get(0))
						.wardNames(wardNumbers)
						.status(Collections.singletonList("APPROVED")).isActiveAccount(true).isActiveSubAccount(true)
						.build())
				.isSchedulerCall(true).build();
		GarbageAccountResponse garbageAccountResponse = garbageAccountService
				.searchGarbageAccounts(searchCriteriaGarbageAccountRequest,false);

		if (null != garbageAccountResponse && !CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			garbageAccounts = garbageAccountResponse.getGarbageAccounts();
			if(generateBillRequest.getGrbgApplicationNumbers() != null) {
				if(!garbageAccountResponse.getGarbageAccounts().get(0).getChildGarbageAccounts().isEmpty())
					garbageAccounts.addAll(garbageAccountResponse.getGarbageAccounts().get(0).getChildGarbageAccounts());
			}
		}
		
		if (!CollectionUtils.isEmpty(ulbNames)) {
			garbageAccounts = garbageAccounts.stream()
					.filter(garbageAccount -> !CollectionUtils.isEmpty(garbageAccount.getAddresses())
							&& ulbNames.contains(garbageAccount.getAddresses().get(0).getUlbName()))
					.collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(ulbNames) && !CollectionUtils.isEmpty(wardNumbers)) {
			garbageAccounts = garbageAccounts.stream()
					.filter(garbageAccount -> !CollectionUtils.isEmpty(garbageAccount.getAddresses())
							&& ulbNames.contains(garbageAccount.getAddresses().get(0).getUlbName())
							&& wardNumbers.contains(garbageAccount.getAddresses().get(0).getWardName()))
					.collect(Collectors.toList());
		}

		return garbageAccounts;
	}
	
	private Boolean checkUuidNumber(GarbageAccount garbageAccount) {
		UserSearchRequest userSearchRequest = UserSearchRequest.builder().active(true).userType("CITIZEN").tenantId("hp").mobileNumber(garbageAccount.getMobileNumber()).build();
		List<OwnerInfo> users = userService.userSearch(userSearchRequest);
		if (users != null && !users.isEmpty()) {
			if(users.get(0).getUuid().equals(garbageAccount.getUserUuid())) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	private BillResponse generateDemandAndBill(GenerateBillRequest generateBillRequest, GarbageAccount garbageAccount,
			BigDecimal billAmount,String Type) {
		try {
			if(!checkUuidNumber(garbageAccount))
				return null;
			List<Demand> savedDemands = new ArrayList<>();
			
			// generate demand
			Map<String, Object> additionalDetails = (Map<String, Object>) generateBillRequest.getAdditionalDetail();

			// if null, initialize
			if (additionalDetails == null) {
			    additionalDetails = new HashMap<>();
			}
			
//			Map<String, Object> additionalDetails = new HashMap<>();
		    additionalDetails.put("name", garbageAccount.getName());
		    additionalDetails.put("mobileNumber", garbageAccount.getMobileNumber());
		    additionalDetails.put("ward", garbageAccount.getAddresses().get(0).getWardName());
		    additionalDetails.put("category", garbageAccount.getGrbgCollectionUnits().get(0).getCategory());
		    additionalDetails.put("SubCategoryType", garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType());
		    additionalDetails.put("application_no", garbageAccount.getGrbgApplicationNumber());
		    additionalDetails.put("MONTH", generateBillRequest.getMonth());
		    additionalDetails.put("type", Type);
		    additionalDetails.put("oldGarbageId", 
		    	    garbageAccount.getGrbgOldDetails() != null 
		    	        ? garbageAccount.getGrbgOldDetails().getOldGarbageId() 
		    	        : null
		    	);
			generateBillRequest.setAdditionalDetail(additionalDetails);
			String service = Type.equals("MONTHLY")?"GB":"GB_BULK";
			savedDemands = demandService.generateDemand(generateBillRequest.getRequestInfo(), garbageAccount,service, billAmount, generateBillRequest);

			if (CollectionUtils.isEmpty(savedDemands)) {
				throw new CustomException("INVALID_CONSUMERCODE",
						"Bill not generated due to no Demand found for the given consumerCode");
			}

			// fetch/create bill
			GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(garbageAccount.getTenantId())
					.businessService(service)
					.consumerCode(savedDemands.get(0).getConsumerCode())
//					.demandId(savedDemands.get(0).getId())
					.mobileNumber(garbageAccount.getMobileNumber())
//					.email(garbageAccount.getEmailId())
					.build();
			BillResponse billResponse = billService.generateBill(generateBillRequest.getRequestInfo(), billCriteria);

			return billResponse;
		} catch (ServiceCallException e) {
		    String actualMessage = "Server Error";
			
			if(null != e.getError()) {
				 try {
				        ObjectMapper mapper = new ObjectMapper();
				        JsonNode root = mapper.readTree(e.getError());
				        actualMessage = root.path("Errors").get(0).path("message").asText();
				    } catch (Exception parseEx) {
				        log.error("Failed to parse backend error JSON: {}", parseEx.getMessage());
				    }
			}
			if(null != generateBillRequest.getGrbgApplicationNumbers() || null != generateBillRequest.getMobileNumbers()) {
				throw new CustomException("INVALID_CONSUMERCODE",actualMessage);
			}else {
		        log.error("Failed to parse backend error JSON: {}", e.getError());
			}
		}
		return null;
	}

	private Date purseToDate(String dateString) {
		// Specify the date format (assuming "dd-MM-yyyy")
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (!StringUtils.isEmpty(dateString)) {
			try {
				// Parse the date string to a Date object
				Date date = dateFormat.parse(dateString);
				return date;
			} catch (Exception e) {
				e.printStackTrace(); // Handle parsing errors
			}
		}
		return null;
	}

	public GrbgBillTrackerResponse generateBillOnDemand(OnDemandBillRequest onDemandBillRequest) {
		
		List<GrbgBillTracker> grbgBillTrackers = new ArrayList<>();
		onDemandBillRequest.getGenerateBillRequest().setRequestInfo(onDemandBillRequest.getRequestInfo());
		
		validateOnDemandRequest(onDemandBillRequest);
		
		BigDecimal billAmount = onDemandBillRequest.getBillAmount();
		
		GarbageAccount garbageAccount = null;
		String message = null;
		List<String> ulbNames = onDemandBillRequest.getGenerateBillRequest().getUlbNames();
		List<String> wardNumbers = onDemandBillRequest.getGenerateBillRequest().getWardNumbers();
		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().requestInfo(onDemandBillRequest.getRequestInfo())
				.searchCriteriaGarbageAccount(SearchCriteriaGarbageAccount.builder()
				.applicationNumber(onDemandBillRequest.getGenerateBillRequest().getGrbgApplicationNumbers())
				.mobileNumber(onDemandBillRequest.getGenerateBillRequest().getMobileNumbers())
				.status(Collections.singletonList("APPROVED")).isActiveAccount(true).isActiveSubAccount(true)
				.build())
				.isSchedulerCall(true).build();
		GarbageAccountResponse garbageAccountResponse = garbageAccountService.searchGarbageAccounts(searchCriteriaGarbageAccountRequest,false);
		if(!CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts()))
		{
			if(!CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts().get(0).getAddresses())) {
				if (!CollectionUtils.isEmpty(ulbNames)) {
					 if(ulbNames.contains(garbageAccountResponse.getGarbageAccounts().get(0).getAddresses().get(0).getUlbName())) {
						 garbageAccount = garbageAccountResponse.getGarbageAccounts().get(0);
					 }
				}
				if (!CollectionUtils.isEmpty(ulbNames) && !CollectionUtils.isEmpty(wardNumbers)) {
					 if(ulbNames.contains(garbageAccountResponse.getGarbageAccounts().get(0).getAddresses().get(0).getUlbName())
							 &&  wardNumbers.contains(garbageAccountResponse.getGarbageAccounts().get(0).getAddresses().get(0).getWardName())) {
						 garbageAccount = garbageAccountResponse.getGarbageAccounts().get(0);
					 }
				}
			}
		}
		else
			throw new CustomException("INVALID_GARBAGE_ACCOUNT_DETAILS", "Provide a valid garbage account details.");

		if(garbageAccount !=null) {
			if (billAmount != null && billAmount.compareTo(BigDecimal.ZERO) > 0) {
				
				BillResponse billResponse = generateDemandAndBill(onDemandBillRequest.getGenerateBillRequest(), garbageAccount, billAmount,"ON-DEMAND");
				ObjectMapper mapper = new ObjectMapper();
		        ObjectNode additionalDetails = mapper.convertValue(onDemandBillRequest.getGenerateBillRequest().getAdditionalDetail(), ObjectNode.class);
				if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
					GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService.enrichGrbgBillTrackerCreateRequest(garbageAccount, onDemandBillRequest.getGenerateBillRequest(), billAmount,billResponse.getBill().get(0),additionalDetails);
					// add to garbage bill tracker
					GrbgBillTracker grbgBillTracker = garbageAccountService.saveToGarbageBillTracker(grbgBillTrackerRequest);
					grbgBillTrackers.add(grbgBillTracker);
					
	//				triggerNotifications
//					notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),generateBillRequest.getRequestInfo(),grbgBillTracker);
					message = "Bill Generated";
				}else {
					message = "Bill Could Not Be Generated";
				}
			}else {
				message = "Bill Amount Zero";
			}
		}
		else {
			message = "Garbage Id Not Found";
		}
		return GrbgBillTrackerResponse.builder().grbgBillTrackers(grbgBillTrackers).message(message).build();
	}
	
	private void validateOnDemandRequest(OnDemandBillRequest onDemandBillRequest) {
		if (null == onDemandBillRequest 
				|| null == onDemandBillRequest.getGenerateBillRequest()  ) {
			throw new CustomException("INVALID_ON_DEMAND_BILL_PAYLOAD", "Provide valid bill request details.");
		}
		
		if( null == onDemandBillRequest.getBillAmount() || onDemandBillRequest.getBillAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new CustomException("INVALID_BILL_AMOUNT", "bill amount not valid.");
		}
	}
}
