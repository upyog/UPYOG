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
import java.util.Objects;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;


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
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.mdms.model.MdmsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.contract.bill.DemandDetail;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.egov.garbageservice.util.RequestInfoWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.garbageservice.model.BillV2;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.model.contract.User;
import org.egov.garbageservice.contract.bill.BillDetail;
import org.egov.garbageservice.contract.bill.BillAccountDetail;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.egov.garbageservice.model.BillIdRequest;


import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled and on-demand jobs for bulk garbage bill generation, penalty, and rebate processing.
 * Orchestrates demand/bill creation via DemandService and BillService, MDMS fee lookup, and
 * GrbgBillTracker updates; exposed through GarbageAccountSchedulerController endpoints.
 */
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
	
	@Autowired
	private RestCallRepository restCallRepository;
	
	@Autowired
	private GarbageBillTrackerRepository garbageBillTrackerRepository;
	
	@Value("${garbage.rebate.grace.days:15}")
	private Integer rebateGraceDays;
	
	@Value("${egov.sms.host}")
	private String smsHost;

	@Value("${egov.sms.tracker.create.endpoint}")
	private String smsTrackerCreateEndpoint;

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
				ObjectNode calculationBreakdown = objectMapper.createObjectNode();
				int numberOfMonths = 0;
				if (!Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
				    calculationBreakdown.putPOJO("months", generateBillRequest.getMonths());
				    calculationBreakdown.put("month",
				        generateBillRequest.getMonths()
				            .get(generateBillRequest.getMonths().size() - 1));
				}
				    
				if(null != garbageAccount.getUserUuid()) {
					Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(generateBillRequest.getRequestInfo(),
							garbageAccount.getTenantId());
					// calculate fees from mdms response
					BigDecimal monthlyAmount =
						    mdmsService.fetchGarbageAmountFromMDMSResponse(
						        mdmsResponse, garbageAccount, errorList, calculationBreakdown
						    );
					
					if (monthlyAmount == null) {
					    log.warn("Monthly amount is null for account {}", garbageAccount.getGrbgApplicationNumber());
					    errorList.add("Monthly amount not found from MDMS");
					    createFailureLog(garbageAccount, generateBillRequest, null, errorList);
					    return;
					}

					if (Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
					    Long from = generateBillRequest.getFromDateTimestamp();
					    Long to = generateBillRequest.getToDateTimestamp();

					    if (from != null && to != null) {
					        LocalDate fromDate = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDate();
					        LocalDate toDate = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDate();
					        
					        numberOfMonths = (int) ChronoUnit.MONTHS.between(
					                fromDate.withDayOfMonth(1),
					                toDate.withDayOfMonth(1)
					        ) + 1;
					    }
					} else {
					    numberOfMonths = generateBillRequest.getMonths().size();
					}
					calculationBreakdown.put("monthCount", numberOfMonths);
	
					BigDecimal billAmount = monthlyAmount.multiply(BigDecimal.valueOf(numberOfMonths));
					BigDecimal rebatePercentage = BigDecimal.ZERO;
					BigDecimal rebateAmount = BigDecimal.ZERO;
					BigDecimal finalBillAmount = billAmount;
					
					if (Boolean.TRUE.equals(generateBillRequest.getIsRebate())) {
						rebatePercentage =
					            mdmsService.fetchGarbageRebateRate(
					                    generateBillRequest.getRequestInfo(),
					                    garbageAccount.getTenantId()
					            );
						
						if (rebatePercentage.compareTo(BigDecimal.ZERO) > 0) {
						    rebateAmount = billAmount
						            .multiply(rebatePercentage)
						            .divide(BigDecimal.valueOf(100))
						            .setScale(2, RoundingMode.HALF_UP);

						    finalBillAmount = billAmount.subtract(rebateAmount);
						}
					}

					calculationBreakdown.put("baseAmount", billAmount);
					calculationBreakdown.put("rebatePercentage", rebatePercentage);
					calculationBreakdown.put("rebateAmount", rebateAmount);
					calculationBreakdown.put("finalAmount", finalBillAmount);
					
					if (billAmount != null && billAmount.compareTo(BigDecimal.ZERO) > 0 && errorList.isEmpty()) {
					
						String billType =
								Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth()) ||
								generateBillRequest.getMonths().size() > 1
						        ? "MULTI_MONTH"
						        : "MONTHLY";
						        
						        AtomicReference<String> demandId = new AtomicReference<>(null);
						        
						        BillResponse billResponse =
						                generateDemandAndBill(generateBillRequest, garbageAccount, finalBillAmount, billType, demandId, numberOfMonths);

	
						if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
							GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService
									.enrichGrbgBillTrackerCreateRequest(garbageAccount, generateBillRequest, billAmount,billResponse.getBill().get(0),calculationBreakdown);
							grbgBillTrackerRequest.getGrbgBillTracker().setDemandId(demandId.get());
							// add to garbage bill tracker
							GrbgBillTracker tracker = grbgBillTrackerRequest.getGrbgBillTracker();
							tracker.setGarbageBillWithoutRebate(billAmount);
							tracker.setRebateAmount(rebateAmount);
							tracker.setGrbgBillAmount(finalBillAmount);
							
							GrbgBillTracker grbgBillTracker = garbageAccountService
									.saveToGarbageBillTracker(grbgBillTrackerRequest);
							grbgBillTrackers.add(grbgBillTracker);
							
							GrbgBillTrackerSearchCriteria prevCriteria = GrbgBillTrackerSearchCriteria.builder()
								    .grbgApplicationIds(Collections.singleton(String.valueOf(garbageAccount.getGrbgApplicationNumber())))
								    .status(Collections.singleton("ACTIVE"))
								    .tenantId(garbageAccount.getTenantId())
								    .build();

							List<GrbgBillTracker> prevTrackers = garbageBillTrackerRepository.getBillTracker(prevCriteria);

							if (!CollectionUtils.isEmpty(prevTrackers)) {
							    for (GrbgBillTracker prev : prevTrackers) {
							        if (prev.getUuid().equals(grbgBillTracker.getUuid()) || 
							            "PAID".equals(prev.getStatus())) {
							            continue;
							        }
							        
							        prev.setStatus("EXPIRED");
							        garbageBillTrackerRepository.updateStatusBillTracker(prev);					        
							    }
							}
							//remove bill if failure exists
//							GrbgBillFailure grbgBillFailure	= garbageAccountService.enrichGrbgBillFailure(garbageAccount, generateBillRequest,billResponse,errorList);
//							garbageAccountService.removeGarbageBillFailure(grbgBillFailure);
//							triggerNotifications
							notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),
								generateBillRequest.getRequestInfo(),grbgBillTracker);
							//getting 
							//calling sms_TRACKER call to make a push to sms_tracker
							StringBuilder smsTrackerUri = new StringBuilder();
							smsTrackerUri.append(smsHost).append(smsTrackerCreateEndpoint);
							
							
							
							ObjectNode smsRequestJson =
							        notificationService.buildGenerateBillSmsRequest(
							                garbageAccount,
							                billResponse.getBill().get(0),
							                grbgBillTracker
							        );
							
							SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
							String fromDateStr = formatter.format(generateBillRequest.getFromDate());
							String toDateStr = formatter.format(generateBillRequest.getToDate());
							
							Map<String, Object> smsTrackerRequest = new HashMap<>();
							smsTrackerRequest.put("uuid", java.util.UUID.randomUUID().toString());
							smsTrackerRequest.put("amount", billAmount);
							smsTrackerRequest.put("applicationNo", garbageAccount.getGrbgApplicationNumber());
							smsTrackerRequest.put("tenantId", garbageAccount.getTenantId());
							smsTrackerRequest.put("service", "GARBAGE");
							if (generateBillRequest.getMonths() != null) {
							    smsTrackerRequest.put("months", generateBillRequest.getMonths());
							}
							smsTrackerRequest.put("monthCount",Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())
								        ? numberOfMonths
								        : generateBillRequest.getMonths().size()
								);
							smsTrackerRequest.put("year", generateBillRequest.getYear());
//							smsTrackerRequest.put("financialYear", generateBillRequest.getFinancialYear());
							smsTrackerRequest.put("fromDate", fromDateStr);
							smsTrackerRequest.put("toDate", toDateStr);
							smsTrackerRequest.put("createdBy", "system");
							smsTrackerRequest.put("createdTime", System.currentTimeMillis());
							smsTrackerRequest.put("ward", garbageAccount.getAddresses().get(0).getWardName());
							smsTrackerRequest.put("billId", billResponse.getBill().get(0).getId());
							smsTrackerRequest.put("smsStatus", false);
							smsTrackerRequest.put("additionalDetail", calculationBreakdown);
							
							smsTrackerRequest.put("ownerMobileNo", garbageAccount.getMobileNumber());
							smsTrackerRequest.put("ownerName", garbageAccount.getName());
							smsTrackerRequest.put("smsRequest",smsRequestJson);  
							smsTrackerRequest.put("smsResponse", null); 
							
							
							try {
							    restCallRepository.fetchResult(smsTrackerUri, smsTrackerRequest);
							    log.info("SMS tracker entry created for billId {}", billResponse.getBill().get(0).getId());
							} catch (Exception e) {
							    log.error("Failed to create SMS tracker entry for billId {}", billResponse.getBill().get(0).getId(), e);
							}
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
				.grbgApplicationIds(grbgApplicationIds)
				.type(Collections.singleton("MONTHLY"))
				.status(new HashSet<>(Arrays.asList("ACTIVE", "PAID"))).build();
		
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
			return trackers.stream().noneMatch(tracker -> {

			    Date existingFrom = purseToDate(tracker.getFromDate());
			    Date existingTo   = purseToDate(tracker.getToDate());

			    Date newFrom = generateBillRequest.getFromDate();
			    Date newTo   = generateBillRequest.getToDate();
			    boolean overlap =
			            !(existingTo.before(newFrom) || existingFrom.after(newTo));

			    return overlap;
			});

		}).collect(Collectors.toList());

		return garbageAccounts;
	}

	private void setFromDateToDate(GenerateBillRequest request) {
	
		if (Boolean.TRUE.equals(request.getIsMultiMonth())) {
		    if (request.getFromDateTimestamp() == null || request.getToDateTimestamp() == null) {
		        throw new CustomException("INVALID_BILL_PERIOD", "fromDateTimestamp and toDateTimestamp are mandatory");
		    }
		    request.setFromDate(new Date(request.getFromDateTimestamp()));
		    request.setToDate(new Date(request.getToDateTimestamp()));
		    return;
		}
		
		if (CollectionUtils.isEmpty(request.getMonths()) || StringUtils.isEmpty(request.getYear())) {
		    throw new CustomException("INVALID_BILL_PERIOD", "Months and year are mandatory");
		}
		
	    int year = Integer.parseInt(request.getYear());
	
	    List<Month> months = request.getMonths().stream()
	            .map(m -> Month.valueOf(m.toUpperCase()))
	            .sorted()
	            .collect(Collectors.toList());
	
	    LocalDate start = LocalDate.of(year, months.get(0), 1)
	            .with(TemporalAdjusters.firstDayOfMonth());
	
	    LocalDate end = LocalDate.of(year, months.get(months.size() - 1), 1)
	            .with(TemporalAdjusters.lastDayOfMonth());
	
	    request.setFromDate(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	    request.setToDate(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
			BigDecimal billAmount,String Type,AtomicReference<String> demandId, int numberOfMonths) {
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
		    additionalDetails.put("bulkType","MULTI_MONTH".equals(Type) ? "MONTH_RANGE" : "SINGLE_MONTH");
		    additionalDetails.put("BILLING_PERIOD",generateBillRequest.getFromDate() + " - " + generateBillRequest.getToDate());
		    additionalDetails.put("type", Type);
		    additionalDetails.put("oldGarbageId", 
		    	    garbageAccount.getGrbgOldDetails() != null 
		    	        ? garbageAccount.getGrbgOldDetails().getOldGarbageId() 
		    	        : null
		    	);
		    if (Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
		        additionalDetails.put("MONTH_COUNT", numberOfMonths);
		    } else {
		    	if (generateBillRequest.getMonths() != null) {
		    	    additionalDetails.put("MONTHS", generateBillRequest.getMonths());
		    	}
		    }
			generateBillRequest.setAdditionalDetail(additionalDetails);
			String service = Type.equals("ON-DEMAND")?"GB_BULK":"GB";
			savedDemands = demandService.generateDemand(generateBillRequest.getRequestInfo(), garbageAccount,service, billAmount, generateBillRequest);

			if (CollectionUtils.isEmpty(savedDemands)) {
				throw new CustomException("INVALID_CONSUMERCODE",
						"Bill not generated due to no Demand found for the given consumerCode");
			}

			// fetch/create bill
			demandId.set(savedDemands.get(0).getId());
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
				
				AtomicReference<String> demandId = new AtomicReference<>(null);
				BillResponse billResponse = generateDemandAndBill(onDemandBillRequest.getGenerateBillRequest(), garbageAccount, billAmount,"ON-DEMAND",demandId, 1);				
				ObjectMapper mapper = new ObjectMapper();
		        ObjectNode additionalDetails = mapper.convertValue(onDemandBillRequest.getGenerateBillRequest().getAdditionalDetail(), ObjectNode.class);
				if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
					GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService.enrichGrbgBillTrackerCreateRequest(garbageAccount, onDemandBillRequest.getGenerateBillRequest(), billAmount,billResponse.getBill().get(0),additionalDetails);
					// add to garbage bill tracker
					grbgBillTrackerRequest.getGrbgBillTracker().setDemandId(demandId.get());
					GrbgBillTracker grbgBillTracker = garbageAccountService.saveToGarbageBillTracker(grbgBillTrackerRequest);
					grbgBillTrackers.add(grbgBillTracker);
					
	//				triggerNotifications
					notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),onDemandBillRequest.getRequestInfo(),grbgBillTracker);
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
	
	public void processGarbagePenalty(RequestInfo requestInfo) {
	
	    List<GrbgBillTracker> trackers = garbageAccountService.fetchExpiredUnpaidBills(requestInfo);
	
	    for (GrbgBillTracker tracker : trackers) {
	        try {
	            log.info("Processing tracker for consumerCode {}", tracker.getGrbgApplicationId());
	
	            // fetch tenantId and ULB from tracker
	            String tenantId = tracker.getTenantId();
	
	            log.info(tenantId); //hp.Shimla
	
	            BigDecimal penaltyRate = mdmsService.fetchGarbagePenaltyRate(requestInfo, tenantId);
	            
	            log.info("[Penalty] Penalty rate resolved = {}", penaltyRate);
	
	            if (penaltyRate.compareTo(BigDecimal.ZERO) <= 0) {
	                continue;
	            }
	
	            String service =
	                    "MULTI_MONTH".equals(tracker.getType()) ? "GB_BULK" : "GB";
	
	            Demand demand = demandService.searchDemand(
	                tracker.getTenantId(),
	                Collections.singleton(tracker.getGrbgApplicationId()),
	                requestInfo,
	                service
	            ).get(0);
	
	            BigDecimal baseAmount = demand.getDemandDetails().stream()
	                .filter(d -> GrbgConstants.BILLING_TAX_HEAD_MASTER_CODE.equals(d.getTaxHeadMasterCode()))
	                .map(DemandDetail::getTaxAmount)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	            Long expiry = tracker.getExpiryDate();
	            if (expiry == null || expiry >= System.currentTimeMillis()) {
	                continue; 
	            }
	
	            LocalDate expiryDate = Instant.ofEpochMilli(expiry)
	                                          .atZone(ZoneId.systemDefault())
	                                          .toLocalDate();
	
	            long monthsOverdue = ChronoUnit.MONTHS.between(expiryDate, LocalDate.now());
	            if (monthsOverdue <= 0) monthsOverdue = 1;
	
	            BigDecimal penalty = baseAmount
	                    .multiply(penaltyRate)
	                    .divide(BigDecimal.valueOf(100))
	                    .multiply(BigDecimal.valueOf(monthsOverdue))
	                    .setScale(2, RoundingMode.HALF_UP);
	            
	            if (tracker.getGrbgBillWithoutPenalty() == null) {
	                tracker.setGrbgBillWithoutPenalty(baseAmount);
	            }
	            
	            garbageAccountService.applyPenalty(tracker, demand, penalty, requestInfo);
	            log.info("Base={}, Rate={}, Months={}", baseAmount, penaltyRate, monthsOverdue);
	
	        } catch (Exception e) {
	            log.error("Penalty failed for consumerCode {}", tracker.getGrbgApplicationId(), e);
	        }
	    }
	}
	
	public List<GrbgBillTracker> fetchRebateEligibleTrackers() {

	    List<GrbgBillTracker> trackers =
	        garbageBillTrackerRepository.fetchRebateEligibleTrackers();

	    long now = System.currentTimeMillis();

	    return trackers.stream()
	        .filter(t -> {
	            long createdTime = t.getAuditDetails().getCreatedDate();
	            long graceMillis = rebateGraceDays * 24L * 60L * 60L * 1000L;
	            return now - createdTime > graceMillis;
	        })
	        .collect(Collectors.toList());
	}
	
	public List<GrbgBillTracker> reverseGarbageRebate(RequestInfoWrapper wrapper) {
	
	    RequestInfo requestInfo = wrapper.getRequestInfo();
	    List<GrbgBillTracker> trackers = fetchRebateEligibleTrackers();
	
	    for (GrbgBillTracker tracker : trackers) {
	        try {
	            if (tracker.getRebateAmount() == null
	                    || tracker.getRebateAmount().compareTo(BigDecimal.ZERO) <= 0) {
	                continue;
	            }
	
	            String service =
	                    "MULTI_MONTH".equals(tracker.getType()) ? "GB_BULK" : "GB";
	
	            BigDecimal originalAmount = tracker.getGarbageBillWithoutRebate();
	
	            List<Demand> demands = demandService.searchDemand(
	                    tracker.getTenantId(),
	                    Collections.singleton(tracker.getGrbgApplicationId()),
	                    requestInfo,
	                    service
	            );
	
	            if (CollectionUtils.isEmpty(demands)) {
	                throw new CustomException(
	                        "DEMAND_NOT_FOUND",
	                        "No demand found for rebate reversal"
	                );
	            }
	
	            Demand demand = demands.get(0);
	            demand.setMinimumAmountPayable(originalAmount);
	
	            if (demand.getDemandDetails() != null) {
	                for (DemandDetail dd : demand.getDemandDetails()) {
	                    dd.setTaxAmount(originalAmount);
	                    dd.setCollectionAmount(BigDecimal.ZERO);
	                }
	            }
	
	            demandService.updateDemand(
	                    requestInfo,
	                    Collections.singletonList(demand)
	            );
	
	            BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
	                    .tenantId(tracker.getTenantId())
	                    .consumerCode(Collections.singleton(tracker.getGrbgApplicationId()))
	                    .build();
	
	            List<Bill> bills = billService
	                    .searchBill(billSearchCriteria, requestInfo)
	                    .getBill();
	
	            if (CollectionUtils.isEmpty(bills)) {
	                throw new CustomException(
	                        "BILL_NOT_FOUND",
	                        "No bill found for rebate reversal"
	                );
	            }
	
	            Bill bill = bills.get(0);
	
	            for (BillDetail billDetail : bill.getBillDetails()) {
	
	                billDetail.setAmount(originalAmount);
	                billDetail.setAmountPaid(BigDecimal.ZERO);
	
	                if (billDetail.getBillAccountDetails() != null) {
	                    billDetail.getBillAccountDetails().forEach(bad -> {
	                        bad.setAmount(originalAmount);
	                        bad.setAdjustedAmount(BigDecimal.ZERO);
	                    });
	                }
	            }
	
	            bill.setTotalAmount(originalAmount);
	
	            ObjectNode billAdditionalDetails;
	            if (bill.getAdditionalDetails() != null && bill.getAdditionalDetails().isObject()) {
	                billAdditionalDetails = (ObjectNode) bill.getAdditionalDetails();
	            } else {
	                billAdditionalDetails = JsonNodeFactory.instance.objectNode();
	            }

	            billAdditionalDetails.put("rebateReversed", true);
	
	            bill.setAdditionalDetails(billAdditionalDetails);
	            
	            bill.setStatus(Bill.StatusEnum.ACTIVE);

	         if (bill.getAmountPaid() == null) {
	             bill.setAmountPaid(BigDecimal.ZERO);
	         }

	         if (bill.getBillDate() == null) {
	             bill.setBillDate(System.currentTimeMillis());
	         }
	
	            billService.updateBill(
	                    requestInfo,
	                    Collections.singletonList(bill)
	            );
	            JsonNode additionalDetailNode = tracker.getAdditionaldetail();
	            ObjectNode trackerAdditionalDetail;
	
	            if (additionalDetailNode != null && additionalDetailNode.isObject()) {
	                trackerAdditionalDetail = (ObjectNode) additionalDetailNode;
	            } else {
	                trackerAdditionalDetail = JsonNodeFactory.instance.objectNode();
	            }
	
	            trackerAdditionalDetail.put("rebateAmount", 0);
	            trackerAdditionalDetail.put("rebatePercentage", 0);
	            trackerAdditionalDetail.put("finalAmount", originalAmount);
	            trackerAdditionalDetail.put("rebateReversed", true);
	
	            tracker.setAdditionaldetail(trackerAdditionalDetail);
	            tracker.setRebateAmount(BigDecimal.ZERO);
	            tracker.setGrbgBillAmount(originalAmount);
	            tracker.setGrbgBillWithoutPenalty(originalAmount);
	
	            garbageBillTrackerRepository.updateRebateReversal(tracker);
	
	        } catch (Exception e) {
	            log.error(
	                    "Garbage rebate reversal failed for applicationId {}",
	                    tracker.getGrbgApplicationId(),
	                    e
	            );
	        }
	    }
	
	    return trackers;
	}

	public GrbgBillTracker getTrackerByBillId(BillIdRequest request) {

		if (request.getBillId() == null) {
			throw new CustomException("INVALID_REQUEST", "billId is required");
		}
		GrbgBillTrackerSearchCriteria criteria = GrbgBillTrackerSearchCriteria.builder()
				.billIds(Collections.singleton(request.getBillId()))
				.build();

		List<GrbgBillTracker> trackers = garbageBillTrackerRepository.extractTrackers(criteria);

		if (CollectionUtils.isEmpty(trackers)) {
			throw new CustomException("NOT_FOUND", "No active tracker found for given billId");
		}
		return trackers.get(0);
	}

}
