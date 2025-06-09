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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerRequest;
import org.egov.garbageservice.model.GrbgBillTrackerResponse;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageAccountSchedulerService {

	@Autowired
	private GarbageAccountService garbageAccountService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private BillService billService;

	@Autowired
	private MdmsService mdmsService;

	@Autowired
	private NotificationService notificationService;

	public GrbgBillTrackerResponse generateBill(GenerateBillRequest generateBillRequest) {

		List<GrbgBillTracker> grbgBillTrackers = new ArrayList<>();
		setFromDateToDate(generateBillRequest);

		List<GarbageAccount> garbageAccounts = getGarbageAccounts(generateBillRequest);
		garbageAccounts = removeAlreadyBillCalculatedGarbageAccounts(garbageAccounts, generateBillRequest);

		// create demand and bill for every account
		if (null != garbageAccounts && !CollectionUtils.isEmpty(garbageAccounts)) {
			garbageAccounts.stream().forEach(garbageAccount -> {
				if(null != garbageAccount.getUserUuid()) {
					Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(generateBillRequest.getRequestInfo(),
							garbageAccount.getTenantId());
					// calculate fees from mdms response
					BigDecimal billAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse, garbageAccount);

					BillResponse billResponse = generateDemandAndBill(generateBillRequest, garbageAccount, billAmount);

					if (null != billResponse && !CollectionUtils.isEmpty(billResponse.getBill())) {
						GrbgBillTrackerRequest grbgBillTrackerRequest = garbageAccountService
								.enrichGrbgBillTrackerCreateRequest(garbageAccount, generateBillRequest, billAmount,billResponse.getBill().get(0));
						// add to garbage bill tracker
						GrbgBillTracker grbgBillTracker = garbageAccountService
								.saveToGarbageBillTracker(grbgBillTrackerRequest);
						grbgBillTrackers.add(grbgBillTracker);

						// triggerNotifications
						notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),
								generateBillRequest.getRequestInfo());
					}
				}
			});
		}

		return GrbgBillTrackerResponse.builder().grbgBillTrackers(grbgBillTrackers).build();
	}

	private List<GarbageAccount> removeAlreadyBillCalculatedGarbageAccounts(List<GarbageAccount> garbageAccounts,
			GenerateBillRequest generateBillRequest) {
		Set<String> grbgApplicationIds = garbageAccounts.stream().map(GarbageAccount::getGrbgApplicationNumber)
				.collect(Collectors.toSet());

		GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.grbgApplicationIds(grbgApplicationIds).build();

		List<GrbgBillTracker> grbgBillTrackers = garbageAccountService
				.getBillCalculatedGarbageAccounts(grbgBillTrackerSearchCriteria);

		Map<String, List<GrbgBillTracker>> grbgBillTrackerMap = grbgBillTrackers.stream()
				.collect(Collectors.groupingBy(GrbgBillTracker::getGrbgApplicationId));

		garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> {
			List<GrbgBillTracker> trackers = grbgBillTrackerMap.get(garbageAccount.getGrbgApplicationNumber());
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
						.status(Collections.singletonList("APPROVED")).isActiveAccount(true).isActiveSubAccount(true)
						.build())
				.isSchedulerCall(true).build();
		GarbageAccountResponse garbageAccountResponse = garbageAccountService
				.searchGarbageAccounts(searchCriteriaGarbageAccountRequest);

		if (null != garbageAccountResponse && !CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
			garbageAccounts = garbageAccountResponse.getGarbageAccounts();
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

	private BillResponse generateDemandAndBill(GenerateBillRequest generateBillRequest, GarbageAccount garbageAccount,
			BigDecimal billAmount) {
		try {
			List<Demand> savedDemands = new ArrayList<>();
			
			// generate demand
			
			savedDemands = demandService.generateDemand(generateBillRequest.getRequestInfo(), garbageAccount,
					"GB", billAmount, generateBillRequest);

			if (CollectionUtils.isEmpty(savedDemands)) {
				throw new CustomException("INVALID_CONSUMERCODE",
						"Bill not generated due to no Demand found for the given consumerCode");
			}

			// fetch/create bill
			GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(garbageAccount.getTenantId())
					.businessService("GB")
					.consumerCode(garbageAccount.getGrbgApplicationNumber())
					.mobileNumber(garbageAccount.getMobileNumber())
//					.email(garbageAccount.getEmailId())
					.build();
			BillResponse billResponse = billService.generateBill(generateBillRequest.getRequestInfo(), billCriteria);

			return billResponse;
		} catch (Exception e) {
			log.error(e.getMessage());
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

}
