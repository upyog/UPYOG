package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccountRequest;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
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

	public void generateBill(RequestInfoWrapper requestInfoWrapper) {

		SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
				.builder().requestInfo(requestInfoWrapper.getRequestInfo())
				.searchCriteriaGarbageAccount(
						SearchCriteriaGarbageAccount.builder().status(Collections.singletonList("APPROVED")).build())
				.isSchedulerCall(true).build();
		GarbageAccountResponse garbageAccountResponse = garbageAccountService
				.searchGarbageAccounts(searchCriteriaGarbageAccountRequest);

		// create demand and bill for every account
		if (null != garbageAccountResponse && !CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {

			garbageAccountResponse.getGarbageAccounts().stream().forEach(garbageAccount -> {

				garbageAccount.setGrbgApplicationNumber(garbageAccount.getGrbgApplication().getApplicationNo());

				Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(requestInfoWrapper.getRequestInfo(),
						garbageAccount.getTenantId());

				// calculate fees from mdms response
				BigDecimal taxAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse, garbageAccount);

				List<Demand> savedDemands = new ArrayList<>();
				// generate demand
				savedDemands = demandService.generateDemand(requestInfoWrapper.getRequestInfo(), garbageAccount,
						garbageAccount.getBusinessService(), taxAmount);

				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}

				// fetch/create bill
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
						.tenantId(garbageAccount.getTenantId()).businessService(garbageAccount.getBusinessService())
						.consumerCode(garbageAccount.getGrbgApplicationNumber()).build();
				BillResponse billResponse = billService.generateBill(requestInfoWrapper.getRequestInfo(), billCriteria);

				// triggerNotifications
				notificationService.triggerNotificationsGenerateBill(garbageAccount, billResponse.getBill().get(0),
						requestInfoWrapper);

			});
		}

	}

}
