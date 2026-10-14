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
import org.egov.garbageservice.model.DdpPrintingUlbWard;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.GrbgBillFailure;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerRequest;
import org.egov.garbageservice.model.GrbgBillTrackerResponse;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.model.MarkReadyForPrintingRequest;
import org.egov.garbageservice.model.MarkReadyForPrintingResponse;
import org.egov.garbageservice.model.UlbWardPrintingResult;
import org.egov.garbageservice.producer.GarbageProducer;
import org.egov.garbageservice.model.OnDemandBillRequest;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.model.UserSearchRequest;
import org.egov.garbageservice.model.contract.OwnerInfo;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.ResponseInfoFactory;
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
import org.egov.garbageservice.model.GenerateBillPreviewResponse;
import java.util.concurrent.atomic.AtomicInteger;


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
	
	@Autowired
	private RestCallRepository restCallRepository;
	
	@Autowired
	private GarbageBillTrackerRepository garbageBillTrackerRepository;

	@Autowired
	private GarbageAccountRepository garbageAccountRepository;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Value("${garbage.rebate.grace.days:15}")
	private Integer rebateGraceDays;
	
	@Value("${egov.sms.host}")
	private String smsHost;

	@Value("${egov.sms.tracker.create.endpoint}")
	private String smsTrackerCreateEndpoint;

	public GrbgBillTrackerResponse generateBill(GenerateBillRequest generateBillRequest) {

		List<GrbgBillTracker> grbgBillTrackers = new ArrayList<>();
		AtomicInteger skippedCount = new AtomicInteger(0);
		setFromDateToDate(generateBillRequest);
		String message = null;
		List<GarbageAccount> garbageAccounts = getGarbageAccounts(generateBillRequest);

		// garbageAccounts = removeAlreadyBillCalculatedGarbageAccounts(garbageAccounts,
		// generateBillRequest);
		// create demand and bill for every account
		if (null != garbageAccounts && !CollectionUtils.isEmpty(garbageAccounts)) {

			Set<String> applicationIds = garbageAccounts.stream().map(GarbageAccount::getGrbgApplicationNumber)
					.collect(Collectors.toSet());

			GrbgBillTrackerSearchCriteria criteria = GrbgBillTrackerSearchCriteria.builder()
					.grbgApplicationIds(applicationIds).build();

			List<GrbgBillTracker> existingTrackers = garbageAccountService.getBillCalculatedGarbageAccounts(criteria);

			Map<String, List<GrbgBillTracker>> trackerMap = existingTrackers.stream()
					.collect(Collectors.groupingBy(GrbgBillTracker::getGrbgApplicationId));

			// Fee structure and rebate rate are identical for every account sharing a
			// tenantId within this batch, so each is resolved from MDMS once per
			// tenantId instead of once per account.
			Map<String, Object> mdmsFeeResponseByTenant = new HashMap<>();
			Map<String, BigDecimal> rebateRateByTenant = new HashMap<>();

			// Demand existence for this exact period is checked once per batch (in
			// chunks, to stay within URL length limits) instead of once per account.
			Set<String> applicationsWithExistingDemand = fetchApplicationsWithExistingDemandForPeriod(
					generateBillRequest, garbageAccounts);

			garbageAccounts.stream().forEach(garbageAccount -> {
				List<String> errorList = new ArrayList<>();
				try {
					ObjectNode calculationBreakdown = objectMapper.createObjectNode();
					int numberOfMonths = 0;
					if (!Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
						calculationBreakdown.putPOJO("months", generateBillRequest.getMonths());
						calculationBreakdown.put("month",
								generateBillRequest.getMonths().get(generateBillRequest.getMonths().size() - 1));
					}

					if (null != garbageAccount.getUserUuid()) {

						if (validateExistingBillOverlap(generateBillRequest, garbageAccount, trackerMap)) {
							skippedCount.incrementAndGet();
							errorList.add("Bill already exists for application "
									+ garbageAccount.getGrbgApplicationNumber() + " for the selected period");
							if (applicationsWithExistingDemand.contains(garbageAccount.getGrbgApplicationNumber())) {
								errorList.clear();
								errorList.add("Demand already exists for the selected period");
								createFailureLog(garbageAccount, generateBillRequest, null, errorList);
							}
							return;
						}

						Object mdmsResponse = mdmsFeeResponseByTenant.computeIfAbsent(garbageAccount.getTenantId(),
								tenantId -> mdmsService.fetchGarbageFeeFromMdms(generateBillRequest.getRequestInfo(),
										tenantId));
						// calculate fees from mdms response
						BigDecimal monthlyAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse,
								garbageAccount, errorList, calculationBreakdown);

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
								LocalDate fromDate = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault())
										.toLocalDate();
								LocalDate toDate = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDate();

								numberOfMonths = (int) ChronoUnit.MONTHS.between(fromDate.withDayOfMonth(1),
										toDate.withDayOfMonth(1)) + 1;
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
							rebatePercentage = rebateRateByTenant.computeIfAbsent(garbageAccount.getTenantId(),
									tenantId -> mdmsService.fetchGarbageRebateRate(generateBillRequest.getRequestInfo(),
											tenantId));

							if (rebatePercentage.compareTo(BigDecimal.ZERO) > 0) {
								rebateAmount = billAmount.multiply(rebatePercentage).divide(BigDecimal.valueOf(100))
										.setScale(2, RoundingMode.HALF_UP);

								finalBillAmount = billAmount.subtract(rebateAmount);
							}
						}

						calculationBreakdown.put("baseAmount", billAmount);
						calculationBreakdown.put("rebatePercentage", rebatePercentage);
						calculationBreakdown.put("rebateAmount", rebateAmount);
						calculationBreakdown.put("finalAmount", finalBillAmount);

						if (billAmount != null && billAmount.compareTo(BigDecimal.ZERO) > 0 && errorList.isEmpty()) {

							String billType = Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())
									|| generateBillRequest.getMonths().size() > 1 ? "MULTI_MONTH" : "MONTHLY";
							if (applicationsWithExistingDemand.contains(garbageAccount.getGrbgApplicationNumber())) {
								errorList.add("Demand already exists for the selected period");
								createFailureLog(garbageAccount, generateBillRequest, null, errorList);
								return;
							}

							AtomicReference<String> demandId = new AtomicReference<>(null);
							BillResponse billResponse;
							try {
								billResponse = generateDemandAndBill(generateBillRequest, garbageAccount,
										finalBillAmount, billType, demandId, numberOfMonths);
							} catch (Exception e) {
								log.error("Failed to generate garbage bill for application {}",
										garbageAccount.getGrbgApplicationNumber(), e);
								errorList.add("Bill generation failed: " + e.getMessage());
								createFailureLog(garbageAccount, generateBillRequest, null, errorList);
								return;
							}

							if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
								GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService
										.enrichGrbgBillTrackerCreateRequest(garbageAccount, generateBillRequest, billAmount,
												billResponse.getBill().get(0), calculationBreakdown);
								grbgBillTrackerRequest.getGrbgBillTracker().setDemandId(demandId.get());
								// add to garbage bill tracker
								GrbgBillTracker tracker = grbgBillTrackerRequest.getGrbgBillTracker();
								tracker.setGarbageBillWithoutRebate(billAmount);
								tracker.setRebateAmount(rebateAmount);
								tracker.setGrbgBillAmount(finalBillAmount);

								GrbgBillTracker grbgBillTracker = garbageAccountService
										.saveToGarbageBillTracker(grbgBillTrackerRequest);
								grbgBillTrackers.add(grbgBillTracker);
								// Status comes from the bill we just fetched above - no need to search
								// billing-service again just to re-read it (that extra search also
								// triggers billing-service's per-bill callback into this service's
								// /extract-tracker endpoint for every GB bill it returns).
								syncTrackerWithBillStatus(grbgBillTracker, billResponse.getBill().get(0));

								GrbgBillTrackerSearchCriteria prevCriteria = GrbgBillTrackerSearchCriteria.builder()
										.grbgApplicationIds(Collections.singleton(String.valueOf(garbageAccount.getGrbgApplicationNumber())))
										.status(new HashSet<>(Arrays.asList("ACTIVE", "ADVANCE_ADJUSTED")))
										.tenantId(garbageAccount.getTenantId())
										.build();

								List<GrbgBillTracker> prevTrackers = garbageBillTrackerRepository
										.getBillTracker(prevCriteria);

								if (!CollectionUtils.isEmpty(prevTrackers)) {
									for (GrbgBillTracker prev : prevTrackers) {
										if (prev.getUuid().equals(grbgBillTracker.getUuid())
												|| "PAID".equals(prev.getStatus())) {
											continue;
										}

										prev.setStatus("EXPIRED");
										garbageBillTrackerRepository.updateStatusBillTracker(prev);
									}
								}
								// remove bill if failure exists
//							GrbgBillFailure grbgBillFailure	= garbageAccountService.enrichGrbgBillFailure(garbageAccount, generateBillRequest,billResponse,errorList);
//							garbageAccountService.removeGarbageBillFailure(grbgBillFailure);
//							triggerNotifications
								notificationService.triggerNotificationsGenerateBill(garbageAccount,
										billResponse.getBill().get(0), generateBillRequest.getRequestInfo(),
										grbgBillTracker);
								// getting
								// calling sms_TRACKER call to make a push to sms_tracker
								StringBuilder smsTrackerUri = new StringBuilder();
								smsTrackerUri.append(smsHost).append(smsTrackerCreateEndpoint);

								ObjectNode smsRequestJson = notificationService.buildGenerateBillSmsRequest(garbageAccount,
										billResponse.getBill().get(0), grbgBillTracker);

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
								smsTrackerRequest.put("monthCount",
										Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth()) ? numberOfMonths
												: generateBillRequest.getMonths().size());
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
								smsTrackerRequest.put("smsRequest", smsRequestJson);
								smsTrackerRequest.put("smsResponse", null);

								try {
									restCallRepository.fetchResult(smsTrackerUri, smsTrackerRequest);
									log.info("SMS tracker entry created for billId {}",
											billResponse.getBill().get(0).getId());
								} catch (Exception e) {
									log.error("Failed to create SMS tracker entry for billId {}",
											billResponse.getBill().get(0).getId(), e);
								}
							} else {
								errorList.add("Issues In Bill Generation Probably Demand Already Exists");
								createFailureLog(garbageAccount, generateBillRequest, billResponse, errorList);
							}
						} else {

							errorList.add("Amount could not be calculated");
							createFailureLog(garbageAccount, generateBillRequest, null, errorList);
						}
					} else {
						errorList.add("Mobile number user not mapped");
						createFailureLog(garbageAccount, generateBillRequest, null, errorList);
					}
				} catch (Exception e) {
					log.error("Unexpected failure while generating bill for application {}",
							garbageAccount.getGrbgApplicationNumber(), e);
					errorList.add("Unexpected error: " + e.getMessage());
					createFailureLog(garbageAccount, generateBillRequest, null, errorList);
				}
			});
		} else {
			message = "Garbage Acc Not Found";
		}
		int generatedCount = grbgBillTrackers.size();
		int skipped = skippedCount.get();

		if (generatedCount > 0 && skipped > 0) {
			message = "Bills generated successfully for " + generatedCount + " Application Id(s). Failed for " + skipped
					+ " Application Id(s) due to overlapping bill periods.";
		} else if (generatedCount > 0) {
			message = "Bills generated successfully for " + generatedCount + " Application Id(s).";
		} else if (skipped > 0) {
			message = "No bills generated. " + skipped + " Application Id(s) failed due to overlapping bill periods.";
		}

		sanatizeFailureLog(generateBillRequest);

		return GrbgBillTrackerResponse.builder().grbgBillTrackers(grbgBillTrackers).message(message).build();

	}

	private boolean validateExistingBillOverlap(GenerateBillRequest generateBillRequest, GarbageAccount garbageAccount,
			Map<String, List<GrbgBillTracker>> trackerMap) {

		List<GrbgBillTracker> trackers = trackerMap.get(garbageAccount.getGrbgApplicationNumber());

		if (CollectionUtils.isEmpty(trackers)) {
			return false;
		}

		Date newFrom = generateBillRequest.getFromDate();
		Date newTo = generateBillRequest.getToDate();

		for (GrbgBillTracker tracker : trackers) {

			if ("CANCELLED".equalsIgnoreCase(tracker.getStatus())) {
				continue;
			}

			Date existingFrom = purseToDate(tracker.getFromDate());
			Date existingTo = purseToDate(tracker.getToDate());

			if (existingFrom == null || existingTo == null) {
				continue;
			}

			boolean overlap = !(existingTo.before(newFrom) || existingFrom.after(newTo));

			if (overlap) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the application numbers in {@code garbageAccounts} that already
	 * have an ACTIVE demand for the exact billing period in
	 * {@code generateBillRequest}. Billing-service's demand search already
	 * accepts a comma-joined set of consumer codes in one call, so this checks
	 * the whole batch in chunks (grouped by tenantId, since the search is
	 * tenant-scoped) instead of issuing one search per account.
	 */
	private Set<String> fetchApplicationsWithExistingDemandForPeriod(GenerateBillRequest generateBillRequest,
			List<GarbageAccount> garbageAccounts) {

		Map<String, List<String>> applicationNumbersByTenant = garbageAccounts.stream()
				.collect(Collectors.groupingBy(GarbageAccount::getTenantId,
						Collectors.mapping(GarbageAccount::getGrbgApplicationNumber, Collectors.toList())));

		long fromDate = generateBillRequest.getFromDate().getTime();
		long toDate = generateBillRequest.getToDate().getTime();
		int chunkSize = 150;
		Set<String> applicationsWithExistingDemand = new HashSet<>();

		applicationNumbersByTenant.forEach((tenantId, applicationNumbers) -> {
			for (int i = 0; i < applicationNumbers.size(); i += chunkSize) {
				List<String> chunk = applicationNumbers.subList(i, Math.min(i + chunkSize, applicationNumbers.size()));

				List<Demand> demands = demandService.searchDemand(tenantId, new HashSet<>(chunk),
						generateBillRequest.getRequestInfo(), "GB");

				if (CollectionUtils.isEmpty(demands)) {
					continue;
				}

				demands.stream()
						.filter(demand -> Demand.StatusEnum.ACTIVE.equals(demand.getStatus())
								&& demand.getTaxPeriodFrom() != null && demand.getTaxPeriodTo() != null
								&& demand.getTaxPeriodFrom() == fromDate && demand.getTaxPeriodTo() == toDate)
						.forEach(demand -> applicationsWithExistingDemand.add(demand.getConsumerCode()));
			}
		});

		return applicationsWithExistingDemand;
	}
	
	public List<GenerateBillPreviewResponse> generateBillPreview(GenerateBillRequest generateBillRequest) {

		List<GenerateBillPreviewResponse> previewResponses = new ArrayList<>();
		setFromDateToDate(generateBillRequest);
		List<GarbageAccount> garbageAccounts = getGarbageAccounts(generateBillRequest);
		garbageAccounts = removeAlreadyBillCalculatedGarbageAccounts(garbageAccounts, generateBillRequest);

		if (garbageAccounts != null && !CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccounts.forEach(garbageAccount -> {
				List<String> errorList = new ArrayList<>();
				ObjectNode calculationBreakdown = objectMapper.createObjectNode();
				int numberOfMonths = 0;

				if (!Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
					calculationBreakdown.putPOJO("months", generateBillRequest.getMonths());
					calculationBreakdown.put("month",
							generateBillRequest.getMonths().get(generateBillRequest.getMonths().size() - 1));
				}

				if (garbageAccount.getUserUuid() == null) {
					errorList.add("Mobile number user not mapped");
					previewResponses.add(GenerateBillPreviewResponse.builder()
							.applicationNo(garbageAccount.getGrbgApplicationNumber())
							.tenantId(garbageAccount.getTenantId()).ownerName(garbageAccount.getName())
							.mobileNumber(garbageAccount.getMobileNumber())
							.build());
					return;
				}

				Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(generateBillRequest.getRequestInfo(),
						garbageAccount.getTenantId());

				BigDecimal monthlyAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse, garbageAccount,
						errorList, calculationBreakdown);

				if (monthlyAmount == null) {
					errorList.add("Monthly amount not found from MDMS");
					previewResponses.add(GenerateBillPreviewResponse.builder()
							.applicationNo(garbageAccount.getGrbgApplicationNumber())
							.tenantId(garbageAccount.getTenantId()).ownerName(garbageAccount.getName())
							.mobileNumber(garbageAccount.getMobileNumber())
							.build());
					return;
				}

				if (Boolean.TRUE.equals(generateBillRequest.getIsMultiMonth())) {
					Long from = generateBillRequest.getFromDateTimestamp();
					Long to = generateBillRequest.getToDateTimestamp();

					if (from != null && to != null) {
						LocalDate fromDate = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate toDate = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDate();
						numberOfMonths = (int) ChronoUnit.MONTHS.between(fromDate.withDayOfMonth(1),
								toDate.withDayOfMonth(1)) + 1;
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
					rebatePercentage = mdmsService.fetchGarbageRebateRate(generateBillRequest.getRequestInfo(),
							garbageAccount.getTenantId());

					if (rebatePercentage.compareTo(BigDecimal.ZERO) > 0) {
						rebateAmount = billAmount.multiply(rebatePercentage).divide(BigDecimal.valueOf(100), 2,
								RoundingMode.HALF_UP);
						finalBillAmount = billAmount.subtract(rebateAmount);
					}
				}

				calculationBreakdown.put("baseAmount", billAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());

				calculationBreakdown.put("rebatePercentage",
						rebatePercentage.setScale(2, RoundingMode.HALF_UP).toPlainString());

				calculationBreakdown.put("rebateAmount",
						rebateAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());

				calculationBreakdown.put("finalAmount",
						finalBillAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());

				if (billAmount == null || billAmount.compareTo(BigDecimal.ZERO) <= 0) {
					errorList.add("Amount could not be calculated");
				}

				Object address = objectMapper.convertValue(garbageAccount.getAddresses(), Object.class);

				previewResponses.add(GenerateBillPreviewResponse.builder()
						.applicationNo(garbageAccount.getGrbgApplicationNumber()).tenantId(garbageAccount.getTenantId())
						.ownerName(garbageAccount.getName()).mobileNumber(garbageAccount.getMobileNumber())
						.monthlyAmount(monthlyAmount.setScale(2, RoundingMode.HALF_UP)).monthCount(numberOfMonths)
						.garbageBillWithoutRebate(billAmount.setScale(2, RoundingMode.HALF_UP))
						.rebatePercentage(rebatePercentage.setScale(2, RoundingMode.HALF_UP))
						.rebateAmount(rebateAmount.setScale(2, RoundingMode.HALF_UP))
						.finalBillAmount(finalBillAmount.setScale(2, RoundingMode.HALF_UP))
						.calculationDetails(calculationBreakdown).address(address)
						.build());
			});
		}

		return previewResponses;
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

				Long createdTime = tracker.getAuditDetails().getCreatedDate();

				if (createdTime == null) {
					continue;
				}

				LocalDate createdDate = Instant.ofEpochMilli(createdTime).atZone(ZoneId.systemDefault()).toLocalDate();
				long diffDays = ChronoUnit.DAYS.between(createdDate, LocalDate.now());
				long monthsOverdue = diffDays / 30;

				if (monthsOverdue <= 0) {
					continue;
				}
	
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
	
	/**
	 * Syncs the tracker's status from the bill that was just created/fetched for
	 * it. Takes the {@link Bill} directly rather than searching billing-service
	 * again for it: that bill already carries its current status fresh off the
	 * {@code _fetchbill} call, and a second {@code _search} call here was both a
	 * wasted round trip and the trigger for billing-service's per-bill callback
	 * into this service's {@code /extract-tracker} endpoint for every GB bill it
	 * returns.
	 */
	private void syncTrackerWithBillStatus(GrbgBillTracker tracker, Bill currentBill) {

		if (tracker == null || tracker.getBillId() == null || currentBill == null) {
			return;
		}

		if (Bill.StatusEnum.ADVANCE_ADJUSTED.equals(currentBill.getStatus())) {

			ObjectNode additionalDetails;

			if (tracker.getAdditionaldetail() != null && !tracker.getAdditionaldetail().isNull()) {
				additionalDetails = (ObjectNode) tracker.getAdditionaldetail().deepCopy();
			} else {
				additionalDetails = objectMapper.createObjectNode();
			}

			additionalDetails.put("advanceAdjusted", true);
			tracker.setAdditionaldetail(additionalDetails);
			garbageBillTrackerRepository.updateTrackerAdditionalDetails(tracker);
		}

		tracker.setStatus(currentBill.getStatus().name());
		garbageBillTrackerRepository.updateStatusBillTracker(tracker);
	}

	/**
	 * For every ULB/ward added today to the ULBS.DdpPrinting MDMS master
	 * (per the record's {@code auditDetails.createdTime}), finds
	 * ddpVerified, approved, active garbage accounts in that ULB/ward and
	 * marks them ready for printing, in id batches per ULB/ward so a single
	 * UPDATE doesn't have to cover every matching row across the whole
	 * tenant at once.
	 */
	public MarkReadyForPrintingResponse markReadyForPrinting(MarkReadyForPrintingRequest request) {

		if (StringUtils.isEmpty(request.getTenantId())) {
			throw new CustomException("INVALID_REQUEST",
					"TenantId is mandatory to mark accounts ready for printing.");
		}

		RequestInfo requestInfo = request.getRequestInfo();
		String userUuid = null != requestInfo && null != requestInfo.getUserInfo()
				? requestInfo.getUserInfo().getUuid()
				: "system";
		int batchSize = null != request.getBatchSize() && request.getBatchSize() > 0 ? request.getBatchSize() : 500;
		Long now = System.currentTimeMillis();

		List<DdpPrintingUlbWard> ulbWards = mdmsService.fetchDdpPrintingUlbWards(requestInfo, request.getTenantId());

		List<UlbWardPrintingResult> results = new ArrayList<>();
		int totalMarked = 0;

		for (DdpPrintingUlbWard ulbWard : ulbWards) {

			List<GarbageAccount> accounts = searchDdpVerifiedAccounts(requestInfo, request.getTenantId(), ulbWard);

			List<Long> idsToMark = accounts.stream()
					.filter(account -> !Boolean.TRUE.equals(account.getIsReadyForPrinting()))
					.map(GarbageAccount::getId).filter(Objects::nonNull).collect(Collectors.toList());

			int marked = 0;
			for (int i = 0; i < idsToMark.size(); i += batchSize) {
				List<Long> batch = idsToMark.subList(i, Math.min(i + batchSize, idsToMark.size()));
				marked += garbageAccountRepository.markReadyForPrinting(batch, userUuid, now);
			}

			totalMarked += marked;
			results.add(UlbWardPrintingResult.builder().ulbName(ulbWard.getUlbName())
					.wardName(ulbWard.getWardName()).accountsMarkedReady(marked).build());

			log.info("[DdpPrinting] ulb={}, ward={}, ddpVerifiedAccountsFound={}, markedReady={}",
					ulbWard.getUlbName(), ulbWard.getWardName(), accounts.size(), marked);
		}

		return MarkReadyForPrintingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true))
				.totalUlbWardsProcessed(ulbWards.size()).totalAccountsMarkedReady(totalMarked).results(results)
				.build();
	}

	/**
	 * Searches ddpVerified, approved, active accounts for one ULB/ward,
	 * following the same tenantId="{tenantId}.{ulbName}" + wardNames search
	 * convention used by {@link #getGarbageAccounts}, with an in-memory
	 * ulb/ward re-check on the address since wardNames alone can't
	 * disambiguate identically-named wards across different ULBs.
	 */
	private List<GarbageAccount> searchDdpVerifiedAccounts(RequestInfo requestInfo, String tenantId,
			DdpPrintingUlbWard ulbWard) {

		SearchCriteriaGarbageAccountRequest searchRequest = SearchCriteriaGarbageAccountRequest.builder()
				.requestInfo(requestInfo)
				.searchCriteriaGarbageAccount(SearchCriteriaGarbageAccount.builder()
						.tenantId(tenantId + "." + ulbWard.getUlbName())
						.wardNames(Collections.singletonList(ulbWard.getWardName())).isDdpVerified(true)
						.status(Collections.singletonList("APPROVED")).isActiveAccount(true).isActiveSubAccount(true)
						.build())
				.isSchedulerCall(true).build();

		GarbageAccountResponse response = garbageAccountService.searchGarbageAccounts(searchRequest, false);

		if (null == response || CollectionUtils.isEmpty(response.getGarbageAccounts())) {
			return Collections.emptyList();
		}

		return response.getGarbageAccounts().stream()
				.filter(account -> !CollectionUtils.isEmpty(account.getAddresses())
						&& ulbWard.getUlbName().equals(account.getAddresses().get(0).getUlbName())
						&& ulbWard.getWardName().equals(account.getAddresses().get(0).getWardName()))
				.collect(Collectors.toList());
	}

}
