package org.egov.rl.calculator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.calculator.repository.DemandRepository;
import org.egov.rl.calculator.repository.Repository;
import org.egov.rl.calculator.util.Configurations;
import org.egov.rl.calculator.util.NotificationUtil;
import org.egov.rl.calculator.util.PropertyUtil;
import org.egov.rl.calculator.util.RLConstants;
import org.egov.rl.calculator.web.models.*;
import org.egov.rl.calculator.web.models.demand.*;
import org.egov.rl.calculator.web.models.demand.Status;
import org.egov.rl.calculator.web.models.property.AuditDetails;
import org.egov.rl.calculator.web.models.property.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemandService {

	@Autowired
	MasterDataService masterDataService;

	@Autowired
	private Configurations config;

	@Autowired
	private PropertyUtil propertyutil;

	@Autowired
	private Repository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private PropertyUtil utill;

	@Autowired
	private CalculationService calculationService;

	@Autowired
	DaysCycleCalculationService daysCycleCalculationService;

	@Autowired
	NotificationUtil notificationUtil;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SchedulerService schedulerService;

	@Autowired
	private BatchDemanService batchDemanService;

	public DemandResponse createDemand(CalculationReq calculationReq) {
		CalculationCriteria firstCriteria = calculationReq.getCalculationCriteria().get(0);
		boolean isLegacyApplication = isLegacyApplication(firstCriteria);

		if (firstCriteria.isSatelment()) {
			return createSatelmentDemand(calculationReq);
	        } else if (firstCriteria.isLegacyArrear() || isLegacyApplication) {
            return createLegacyArrearDemand(calculationReq);
		} else {

			boolean isSecurityDeposite = firstCriteria.isSecurityDeposite();
			List<Demand> demands = new ArrayList<>();
			RequestInfo requestInfo = calculationReq.getRequestInfo();
			String tenantId = calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getAllotment().get(0)
					.getTenantId();

			for (CalculationCriteria criteria : calculationReq.getCalculationCriteria()) {

				AllotmentRequest allotmentRequest = criteria.getAllotmentRequest();
				AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);

//            String tenantId = allotmentRequest.getAllotment().get(0).getTenantId();
				String consumerCode = allotmentRequest.getAllotment().get(0).getApplicationNumber();

				OwnerInfo ownerInfo = allotmentRequest.getAllotment().get(0).getOwnerInfo().get(0);
				Owner payerUser = Owner.builder().name(ownerInfo.getName()).emailId(ownerInfo.getEmailId())
						.uuid(ownerInfo.getUserUuid()).mobileNumber(ownerInfo.getMobileNo())
						.tenantId(ownerInfo.getTenantId()).build();
				List<DemandDetail> demandDetails = calculationService.calculateDemand(isSecurityDeposite,
						allotmentRequest);
				BigDecimal amountPayable = new BigDecimal(0);
				String applicationType = allotmentRequest.getAllotment().get(0).getApplicationType();

				JsonNode additionalDetails = allotmentDetails.getAdditionalDetails();
				String cycle = additionalDetails.path("propertyDetails").get(0).path("feesPeriodCycle").asText();

				List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(requestInfo, tenantId);
				BillingPeriod billingPeriod = billingPeriods.stream()
						.filter(b -> b.getBillingCycle().equalsIgnoreCase(cycle)).findFirst().orElse(null); // Assuming
				if (billingPeriod != null) {
					long startDay = billingPeriod.getTaxPeriodFrom() <= allotmentDetails.getStartDate()
							? allotmentDetails.getStartDate()
							: billingPeriod.getTaxPeriodFrom();

					long endDay = billingPeriod.getTaxPeriodTo() <= allotmentDetails.getEndDate()
							? billingPeriod.getTaxPeriodTo()
							: allotmentDetails.getEndDate();

//					long exparyDate = billingPeriod.getDemandExpiryDate();

					amountPayable = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
							BigDecimal::add);
//					amountPayable = calculationService.calculatePaybleAmount(startDay, endDay, amountPayable, cycle);

					Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails)
							.payer(payerUser).minimumAmountPayable(amountPayable).tenantId(tenantId)
							.taxPeriodFrom(startDay).taxPeriodTo(endDay).consumerType(applicationType)
							.businessService(RLConstants.RL_SERVICE_NAME).additionalDetails(null).build();
					demands.add(demand);
				}
			}

			List<Demand> demands1 = demandRepository.saveDemand(
					calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getRequestInfo(), demands);
			return DemandResponse.builder().demands(demands1).build();
		}
	}

	private boolean isLegacyApplication(CalculationCriteria criteria) {
		if (criteria == null || criteria.getAllotmentRequest() == null
				|| CollectionUtils.isEmpty(criteria.getAllotmentRequest().getAllotment())) {
			return false;
		}

		String applicationType = criteria.getAllotmentRequest().getAllotment().get(0).getApplicationType();
		return RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(applicationType);
	}
    /**
	 * Creates a single combined demand for legacy applications.
	 * Legacy workflow should persist one demand containing RL fee, arrear and no security deposit.
     */
    public DemandResponse createLegacyArrearDemand(CalculationReq calculationReq) {
		log.info("Creating legacy combined demand - START");
        List<Demand> demands = new ArrayList<>();
        RequestInfo requestInfo = calculationReq.getRequestInfo();
		for (CalculationCriteria criteria : calculationReq.getCalculationCriteria()) {
			Demand demand = buildLegacyCombinedDemand(criteria);
			if (demand != null) {
				demands.add(demand);
			}
		}

		if (CollectionUtils.isEmpty(demands)) {
			log.warn("No legacy demand could be built for the request");
			return DemandResponse.builder().demands(Collections.emptyList()).build();
		}

		log.info("Saving legacy combined demand(s) to billing service. Count: {}", demands.size());
		List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);
		log.info("Legacy combined demand created successfully. Count: {}", savedDemands.size());
		return DemandResponse.builder().demands(savedDemands).build();
    }

	private Demand buildLegacyCombinedDemand(CalculationCriteria criteria) {
		if (criteria == null || criteria.getAllotmentRequest() == null || CollectionUtils.isEmpty(criteria.getAllotmentRequest().getAllotment())) {
			return null;
		}

		AllotmentRequest allotmentRequest = criteria.getAllotmentRequest();
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		String tenantId = allotmentDetails.getTenantId();
		String consumerCode = allotmentDetails.getApplicationNumber();
		BigDecimal arrearAmount = criteria.getArrearAmount() == null ? BigDecimal.ZERO : criteria.getArrearAmount();

		Demand demand = calculationService.buildDemand(allotmentRequest, false);
		if (demand == null) {
			log.warn("Unable to build base legacy demand for consumerCode: {}, tenantId: {}", consumerCode, tenantId);
			return null;
		}

		if (demand.getDemandDetails() == null) {
			demand.setDemandDetails(new ArrayList<>());
		}
		demand.getDemandDetails().removeIf(detail -> detail.getTaxHeadMasterCode() != null
				&& RLConstants.ROUND_OFF_RL_APPLICATION.equalsIgnoreCase(detail.getTaxHeadMasterCode()));

		log.info("Adding legacy arrear to combined demand. consumerCode: {}, arrearAmount: {}", consumerCode,
				arrearAmount);
		demand.getDemandDetails().add(DemandDetail.builder()
				.taxAmount(arrearAmount)
				.taxHeadMasterCode(RLConstants.RL_ARREAR_FEE)
				.tenantId(tenantId)
				.build());

		calculationService.addRoundOffTaxHead(tenantId, demand.getDemandDetails());
		BigDecimal amountPayable = demand.getDemandDetails().stream().map(DemandDetail::getTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		demand.setMinimumAmountPayable(amountPayable);
		return demand;
	}

	public DemandResponse createSatelmentDemand(CalculationReq calculationReq) {

		AllotmentRequest allotmentRequest = calculationReq.getCalculationCriteria().get(0).getAllotmentRequest();
		List<Demand> demands = new ArrayList<>();
		RequestInfo requestInfo = calculationReq.getRequestInfo();
		String tenantId = calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getAllotment().get(0)
				.getTenantId();

		JsonNode additionalDetails = allotmentRequest.getAllotment().get(0).getAdditionalDetails();
		String cycle = additionalDetails.path("propertyDetails").get(0).path("feesPeriodCycle").asText();

		List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(requestInfo, tenantId);
		BillingPeriod billingPeriod = billingPeriods.stream().filter(b -> b.getBillingCycle().equalsIgnoreCase(cycle))
				.findFirst().orElse(null); // Assuming
		if (billingPeriod != null) {
			String consumerCode = allotmentRequest.getAllotment().get(0).getApplicationNumber();

			OwnerInfo ownerInfo = allotmentRequest.getAllotment().get(0).getOwnerInfo().get(0);
			Owner payerUser = Owner.builder().name(ownerInfo.getName()).emailId(ownerInfo.getEmailId())
					.uuid(ownerInfo.getUserUuid()).mobileNumber(ownerInfo.getMobileNo())
					.tenantId(ownerInfo.getTenantId()).build();

			List<DemandDetail> demandDetails = calculationService.calculateSatelmentDemand(allotmentRequest);
			BigDecimal amountPayable = new BigDecimal(0);
			String applicationType = allotmentRequest.getAllotment().get(0).getApplicationType();
			amountPayable = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(payerUser)
					.minimumAmountPayable(amountPayable).tenantId(tenantId)
					.taxPeriodFrom(billingPeriod.getTaxPeriodFrom())
					.taxPeriodTo(daysCycleCalculationService.minus5Days(billingPeriod.getTaxPeriodTo()))
					.billExpiryTime(billingPeriod.getDemandExpiryDate()).consumerType(applicationType)
					.businessService(RLConstants.RL_SERVICE_NAME).additionalDetails(null).build();
			demands.add(demand);
		}
		List<Demand> demands1 = demandRepository.saveDemand(
				calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getRequestInfo(), demands);
		return DemandResponse.builder().demands(demands1).build();
	}

	public DemandResponse estimate(boolean isSecurityDeposite, CalculationReq calculationReq) {

		List<Demand> demands = new ArrayList<>();
		RequestInfo requestInfo = calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getRequestInfo();
		String tenantId = calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getAllotment().get(0)
				.getTenantId();

		List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(requestInfo, tenantId);
		BillingPeriod billingPeriod = billingPeriods.get(0); // Assuming that each ulb will follow only one type of
																// billing

		for (CalculationCriteria criteria : calculationReq.getCalculationCriteria()) {

			AllotmentRequest allotmentRequest = criteria.getAllotmentRequest();
			String consumerCode = allotmentRequest.getAllotment().get(0).getApplicationNumber();

			OwnerInfo ownerInfo = allotmentRequest.getAllotment().get(0).getOwnerInfo().get(0);
			Owner payerUser = Owner.builder().name(ownerInfo.getName()).emailId(ownerInfo.getEmailId())
					.uuid(ownerInfo.getUserUuid()).mobileNumber(ownerInfo.getMobileNo())
					.tenantId(ownerInfo.getTenantId()).build();
			List<DemandDetail> demandDetails = calculationService.calculateDemand(isSecurityDeposite, allotmentRequest);
			BigDecimal amountPayable = new BigDecimal(0);
			String applicationType = allotmentRequest.getAllotment().get(0).getApplicationType();

			amountPayable = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(payerUser)
					.minimumAmountPayable(amountPayable).tenantId(tenantId)
					.taxPeriodFrom(billingPeriod.getTaxPeriodFrom()).taxPeriodTo(billingPeriod.getTaxPeriodTo())
					.billExpiryTime(billingPeriod.getDemandEndDateMillis()).consumerType(applicationType)
					.businessService(RLConstants.RL_SERVICE_NAME).additionalDetails(null).build();

			demands.add(demand);
		}

//        List<Demand> demands1 = demandRepository.saveDemand(calculationReq.getCalculationCriteria().get(0).getAllotmentRequest().getRequestInfo(), demands);
		return DemandResponse.builder().demands(demands).build();

	}

	public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

		if (getBillCriteria.getAmountExpected() == null)
			getBillCriteria.setAmountExpected(BigDecimal.ZERO);
		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();

		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getApplicationNumber()));
		}

		DemandResponse res = mapper.convertValue(
				serviceRequestRepository.fetchResult(utill.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);

		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(RLConstants.EMPTY_DEMAND_ERROR_CODE, RLConstants.EMPTY_DEMAND_ERROR_MESSAGE);
			throw new CustomException(map);
		}

		List<Demand> demands = res.getDemands().stream()
				.filter(d -> d.getStatus() == null
						|| !d.getStatus().toString().equalsIgnoreCase(RLConstants.DEMAND_CANCELLED_STATUS))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(demands)) {
			return DemandResponse.builder().demands(Collections.emptyList()).build();
		}

		List<Demand> demandsToBeUpdated = new LinkedList<>();
		String tenantId = getBillCriteria.getTenantId();
		List<TaxPeriod> taxPeriods = masterDataService.getTaxPeriodList(requestInfo, tenantId,
				RLConstants.RL_SERVICE_NAME);
		List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(requestInfo, tenantId);
		List<Penalty> penaltySlabs = masterDataService.getPenaltySlabs(requestInfo, tenantId);

		for (Demand demand : demands) {
			BigDecimal totalTax = demand.getDemandDetails().stream().map(DemandDetail::getTaxAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal totalCollection = demand.getDemandDetails().stream().map(DemandDetail::getCollectionAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			if (totalTax.compareTo(totalCollection) > 0) {
				applyTimeBasedApplicables(demand, requestInfoWrapper, taxPeriods, billingPeriods, penaltySlabs);
			}

//			calculationService.addRoundOffTaxHead(demand.getTenantId(), demand.getDemandDetails());
			demandsToBeUpdated.add(demand);
		}

		demandRepository.updateDemand(requestInfo, demandsToBeUpdated);
		return DemandResponse.builder().demands(demandsToBeUpdated).build();
	}

	private void applyTimeBasedApplicables(Demand demand, RequestInfoWrapper requestInfoWrapper,
			List<TaxPeriod> taxPeriods, List<BillingPeriod> billingPeriods, List<Penalty> penaltySlabs) {
		log.info("Applying time based applicables for demand: {}", demand.getId());

		if (CollectionUtils.isEmpty(penaltySlabs)) {
			log.info("No penalty slabs found for tenant: {}", demand.getTenantId());
			return;
		}
		log.info("Found {} penalty slabs.", penaltySlabs.size());

		Long demandCreationTime = demand.getAuditDetails() != null ? demand.getAuditDetails().getCreatedTime() : null;
		Long expiryDurationMillis = demand.getBillExpiryTime();
		log.info("Demand ID: {}. Creation Time: {}. Expiry Days: {}", demand.getId(), demandCreationTime,
				expiryDurationMillis);

		if (expiryDurationMillis == null || demandCreationTime == null) {
			log.error("Cannot apply penalty. Demand creation time or expiry days is null for demand: {}",
					demand.getId());
			return;
		}

		long expiryTimeMillis = demandCreationTime + expiryDurationMillis;
		log.info("Demand ID: {}. Calculated Expiry Timestamp: {}. Current Time: {}", demand.getId(), expiryTimeMillis,
				System.currentTimeMillis());

		if (System.currentTimeMillis() < expiryTimeMillis) {
			log.info("Demand is not yet overdue. Skipping penalty calculation for demand: {}", demand.getId());
			return;
		}

		boolean penaltyAlreadyApplied = demand.getDemandDetails().stream()
				.anyMatch(detail -> detail.getTaxHeadMasterCode().equalsIgnoreCase(RLConstants.PENALTY_TAXHEAD_CODE));

		if (penaltyAlreadyApplied) {
			log.info("Penalty already applied for demand: {}", demand.getId());
			return;
		}

		long daysPastExpiry = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - expiryTimeMillis);
		log.info("Demand ID: {}. Days Past Expiry: {}", demand.getId(), daysPastExpiry);

		BigDecimal principalAmount = demand.getDemandDetails().stream().filter(
				detail -> detail.getTaxHeadMasterCode().equalsIgnoreCase(RLConstants.RENT_LEASE_FEE_RL_APPLICATION))
				.map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		log.info("Demand ID: {}. Principal amount for penalty calculation: {}", demand.getId(), principalAmount);

		if (principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			log.info("Principal amount is zero or less for demand: {}. Skipping penalty.", demand.getId());
			return;
		}

		Penalty penaltySlab = penaltySlabs.get(0);
		log.info("Demand ID: {}. Using Penalty Slab: Applicable After {} days.", demand.getId(),
				penaltySlab.getApplicableAfterDays());

		if (penaltySlab.getApplicableAfterDays() != null && daysPastExpiry > penaltySlab.getApplicableAfterDays()) {
			log.info("Applying penalty for demand: {}", demand.getId());

			BigDecimal penaltyAmount = BigDecimal.ZERO;

			if (penaltySlab.getRate() != null && penaltySlab.getRate().compareTo(BigDecimal.ZERO) > 0) {
				penaltyAmount = principalAmount.multiply(penaltySlab.getRate()).divide(new BigDecimal(100), 2,
						RoundingMode.HALF_UP);
			} else if (penaltySlab.getFlatAmount() != null
					&& penaltySlab.getFlatAmount().compareTo(BigDecimal.ZERO) > 0) {
				penaltyAmount = penaltySlab.getFlatAmount();
			}

			if (penaltySlab.getMinAmount() != null && penaltyAmount.compareTo(penaltySlab.getMinAmount()) < 0) {
				penaltyAmount = penaltySlab.getMinAmount();
			}

			if (penaltySlab.getMaxAmount() != null && penaltyAmount.compareTo(penaltySlab.getMaxAmount()) > 0) {
				penaltyAmount = penaltySlab.getMaxAmount();
			}

			if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				DemandDetail penaltyDetail = DemandDetail.builder().taxAmount(penaltyAmount)
						.taxHeadMasterCode(RLConstants.PENALTY_TAXHEAD_CODE).tenantId(demand.getTenantId())
						.collectionAmount(BigDecimal.ZERO).demandId(demand.getId()).build();
				demand.getDemandDetails().add(penaltyDetail);
				log.info("Penalty of {} applied for demand: {}", penaltyAmount, demand.getId());
			} else {
				log.warn("Calculated penalty amount is zero or less for demand: {}. No penalty applied.",
						demand.getId());
			}
		} else {
			log.info("Penalty grace period not over for demand: {}", demand.getId());
		}
	}

	private List<AllotmentDetails> fetchApprovedAllotmentApplications(String tenantId, RequestInfo requestInfo,
			String consumerCode) {
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		String baseHost = config.getRlServiceHost();
		String basePath = config.getRlSearchEndpoint();

		Set<Status> statusSet = new HashSet<>(Arrays.asList(Status.APPROVED, Status.FORWARD_FOT_SETLEMENT, // verify
																											// spelling
				Status.PENDING_FOR_PAYMENT, Status.REQUEST_FOR_DISCONNECTION));
		StringJoiner joiner = new StringJoiner(",");
		statusSet.stream().filter(Objects::nonNull).map(Status::name).forEach(joiner::add);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseHost).path(basePath).queryParam("tenantId",
				tenantId);
		builder.queryParam("status", joiner.toString());
		builder.queryParam("isExpaireFlag", false);
		if (consumerCode != null) {
			builder.queryParam("applicationNumbers", consumerCode);
		}

		String url = builder.build().toUriString();

		log.info("ALLOTMENT SEARCH URI :" + url);
		try {
			Object result = serviceRequestRepository.fetchResult(new StringBuilder(url), requestInfoWrapper);
			AllotmentSearchResponse response = mapper.convertValue(result, AllotmentSearchResponse.class);
			return response.getAllotment();
		} catch (Exception e) {
			log.error("Error while fetching approved allotment applications for tenant: {}", tenantId, e);
			throw new CustomException("RL_APP_SEARCH_ERROR", "Failed to fetch approved allotment applications");
		}
	}

	public void generateBatchDemand(RequestInfo requestInfo, String tenantCode, String consumerCode) {
		LocalDate currentDate = LocalDate.now(); // today

		List<String> tenantIds = (tenantCode == null) ? demandRepository.getDistinctTenantIds()
				: Arrays.asList(tenantCode);
		log.info("Starting demand generation job for tenants: {}", tenantIds);

		for (String tenantId : tenantIds) {
			log.info("Generating demands for tenant: {}", tenantId);
			Runnable task = new Runnable() {

				@Override
				public void run() {
					try {

						List<AllotmentDetails> list = fetchApprovedAllotmentApplications(tenantId, requestInfo,
								consumerCode);
						List<Demand> demandList = new ArrayList<>();
						int batchSize = 10;
						list.forEach(d -> {
							JsonNode additionalDetails = d.getAdditionalDetails();
							String cycle = additionalDetails.path("propertyDetails").get(0).path("feesPeriodCycle").asText();

							List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(requestInfo,
									tenantId);
							BillingPeriod billingPeriod = billingPeriods.stream()
									.filter(b -> b.getBillingCycle().equalsIgnoreCase(cycle))
									.collect(Collectors.toList()).get(0); // Assuming
							if (billingPeriod != null) {
								long startDay = billingPeriod.getTaxPeriodFrom() <= d.getStartDate() ? d.getStartDate()
										: billingPeriod.getTaxPeriodFrom();

								long endDay = billingPeriod.getTaxPeriodTo() <= d.getEndDate()
										? billingPeriod.getTaxPeriodTo()
										: d.getEndDate();

								long exparyDate = billingPeriod.getDemandExpiryDate();

								Demand demand = schedulerService.billGenerateByCycle(startDay, endDay, exparyDate, d,
										requestInfo, cycle);
								if (demand != null)
									demandList.add(demand);
							}
						});
//						log.info
						System.out.println("------::List of consummercode which have to generate bulk demand::-----");
						if (demandList.isEmpty()) {
							System.out.println("------::All demand alreday has been generated::-----");
						}
						demandList.stream().forEach(d -> {
//							log.info("{} Demand consummerCode :{} ",currentDate,d.getConsumerCode());
							System.out.println(currentDate + " Demand consummerCode : " + d.getConsumerCode());

						});

						batchDemanService.batchRun(demandList, batchSize, requestInfo);

					} catch (Exception e) {
						log.error("Error while generating demands for tenant: " + tenantId, e);
					}
				}
			};

			Thread t = new Thread(task);
			t.start();

		}
		log.info("Finished demand generation job.");

	}

	public void sendNotificationAndUpdateDemand(RequestInfo requestInfo, String tenantCode, String consumerCode) {

		List<String> tenantIds = (tenantCode == null) ? demandRepository.getDistinctTenantIds()
				: Arrays.asList(tenantCode);
		log.info("Starting Notification job for tenants: {}", tenantIds);
		// requestInfo.getUserInfo().getTenantId());
		log.info("Starting Notification job for tenants: {}", tenantIds);

		for (String tenantId : tenantIds) {
			log.info("Notification for tenant: {}", tenantId);
			Runnable task = new Runnable() {

				@Override
				public void run() {
					try {
						sendNotificationUpdateDemand(tenantId, requestInfo, consumerCode);
					} catch (Exception e) {
						log.error("Error while Notification for tenant: " + tenantId, e);
					}
				}
			};

			Thread t = new Thread(task);
			t.start();

		}
		log.info("Finished Notification job.");

	}

	public void sendNotificationUpdateDemand(String tenantId, RequestInfo requestInfo, String consumerCode) {

		List<AllotmentDetails> allotmentDetails = fetchApprovedAllotmentApplications(tenantId, requestInfo,
				consumerCode);
		allotmentDetails.stream().forEach(alt -> {
			List<Demand> dmdlist = demandRepository
					.getDemandsNotiByConsumerCode(Arrays.asList(alt.getApplicationNumber()));
			dmdlist = dmdlist.stream().map(d -> {
				d.setDemandDetails(demandRepository.getDemandsDetailsByDemandId(Arrays.asList(d.getId())));
				return d;
			}).collect(Collectors.toList());

			dmdlist.stream().forEach(d -> {
				Instant expireDate = Instant.ofEpochMilli(d.getBillExpiryTime());
				Instant now = Instant.now();
				if (expireDate.isBefore(now)) {
					System.out.println("----Panelty has been added successfully-----");
					DemandDetail baseAmount = d.getDemandDetails().stream()
							.filter(dt -> dt.getTaxHeadMasterCode().equals(RLConstants.RENT_LEASE_FEE_RL_APPLICATION))
							.findFirst().get();
					updatePenalty(baseAmount.getTaxAmount(), d, requestInfo);
				} else {
					System.out.println("----Prepare for send notification-----");
					notificationService.sendNotificationSMS(
							AllotmentRequest.builder().allotment(Arrays.asList(alt)).requestInfo(requestInfo).build());
				}
			});
		});

	}

	public void updatePenalty(BigDecimal basicAmount, Demand demand, RequestInfo requestInfo) {
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		List<Penalty> panelty = masterDataService.getPenaltySlabs(requestInfo, demand.getTenantId());
		BigDecimal paneltyAmount = basicAmount.multiply(panelty.get(0).getRate()).divide(new BigDecimal(100));
		long now = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long exparyDate = daysCycleCalculationService.addAfterPenaltyDays(now, requestInfo, demand.getTenantId());
		List<DemandDetail> dataList = demand.getDemandDetails();
		DemandDetail demandDetail = DemandDetail.builder().demandId(demand.getId()).tenantId(demand.getTenantId())
				.taxHeadMasterCode(RLConstants.PENALTY_FEE_RL_APPLICATION).auditDetails(auditDetails)
				.taxAmount(paneltyAmount).collectionAmount(paneltyAmount).build();
		dataList.add(demandDetail);
		demand.setPayer(demand.getPayer());
		demand.setMinimumAmountPayable(demand.getMinimumAmountPayable().add(demandDetail.getTaxAmount()));
		demand.setDemandDetails(dataList);
		demand.setBillExpiryTime(exparyDate);
		demand.setFixedbillexpirydate(exparyDate);
		// addRoundOffTaxHead(demand.getTenantId(), dataList);
		demandRepository.updateDemand(requestInfo, Arrays.asList(demand));
	}

	/**
	 * Fetches bills from billing service for saved demands.
	 * Called after demand generation to materialize bills.
	 */
	public void fetchBillForDemands(List<Demand> demands, RequestInfo requestInfo) {
		for (Demand demand : demands) {
			try {
				StringBuilder fetchBillURL = utill.getFetchBillURL(demand.getTenantId(), demand.getConsumerCode());
				Object result = serviceRequestRepository.fetchResult(fetchBillURL,
						RequestInfoWrapper.builder().requestInfo(requestInfo).build());
				BillResponse billResponse = mapper.convertValue(result, BillResponse.class);
				if (billResponse.getBill() != null && !billResponse.getBill().isEmpty()) {
					log.info("Bill fetched successfully for consumerCode: {}", demand.getConsumerCode());
				} else {
					log.warn("No bill generated for consumerCode: {}", demand.getConsumerCode());
				}
			} catch (Exception ex) {
				log.error("Error fetching bill for consumerCode: {}", demand.getConsumerCode(), ex);
			}
		}
	}

	public Demand createSingleDemand(long expireDate, AllotmentDetails allotmentDetails, RequestInfo requestInfo,
			String cycle) {
		List<Demand> demands = new ArrayList<>();

		String consumerCode = allotmentDetails.getApplicationNumber();

		OwnerInfo ownerInfo = allotmentDetails.getOwnerInfo().get(0);
		Owner payerUser = Owner.builder().name(ownerInfo.getName()).emailId(ownerInfo.getEmailId())
				.uuid(ownerInfo.getUserUuid()).mobileNumber(ownerInfo.getMobileNo()).tenantId(ownerInfo.getTenantId())
				.build();
		List<DemandDetail> demandDetails = calculationService.calculateDemand(false,
				AllotmentRequest.builder().allotment(Arrays.asList(allotmentDetails)).requestInfo(requestInfo).build());
		BigDecimal amountPayable = new BigDecimal(0);
		String applicationType = allotmentDetails.getApplicationType();

		amountPayable = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		amountPayable = calculationService.calculatePaybleAmount(allotmentDetails.getStartDate(),
				allotmentDetails.getEndDate(), amountPayable, cycle);

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(payerUser)
				.minimumAmountPayable(amountPayable).tenantId(allotmentDetails.getTenantId())
				.taxPeriodFrom(allotmentDetails.getStartDate()).taxPeriodTo(allotmentDetails.getEndDate())
				.billExpiryTime(expireDate).fixedbillexpirydate(expireDate).consumerType(applicationType)
				.businessService(RLConstants.RL_SERVICE_NAME).additionalDetails(null).build();
		demands.add(demand);
		return demand;
	}
}
