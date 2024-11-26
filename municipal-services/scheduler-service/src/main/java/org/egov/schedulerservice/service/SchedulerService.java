package org.egov.schedulerservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.contract.bill.Demand;
import org.egov.schedulerservice.contract.bill.DemandDetail;
import org.egov.schedulerservice.contract.bill.DemandRepository;
import org.egov.schedulerservice.contract.bill.DemandRequest;
import org.egov.schedulerservice.contract.garbage.GarbageAccountResponse;
import org.egov.schedulerservice.contract.garbage.SearchCriteriaGarbageAccountRequest;
import org.egov.schedulerservice.dto.SchedularMasterRequest;
import org.egov.schedulerservice.dto.SchedulerMasterBody;
import org.egov.schedulerservice.producer.Producer;
import org.egov.schedulerservice.util.SchedulerConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SchedulerService {

	@Autowired
	private DemandService demandService;

	@Autowired
	private GarbageService garbageService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private Producer producer;

	@Autowired
	private BillService billService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MdmsService mdmsService;

	public Object runService(SchedularMasterRequest schedularMasterRequest) {

		// validate
		validateRequest(schedularMasterRequest);

		// enrich

		// process
		process(schedularMasterRequest);

		return null;
	}

	private void process(SchedularMasterRequest schedularMasterRequest) {

		schedularMasterRequest.getSchedulerMasterBodies().parallelStream().forEach(request -> {

			Long slotSize = request.getSlotSize();
			Long start = 0L;
			Object mdmsResponse = mdmsService.fetchMdms(request, schedularMasterRequest.getRequestInfo());

			while (start <= request.getMaxCount()) {

				// fetch application AND generate demand request
				List<Demand> demands = fetchApplicationsAndCreateDemandRequest(request,
						schedularMasterRequest.getRequestInfo(), mdmsResponse, start, (start + slotSize));

				start = start + slotSize + 1;
			}

		});

	}

	private List<Demand> fetchApplicationsAndCreateDemandRequest(SchedulerMasterBody request, RequestInfo requestInfo,
			Object mdmsResponse, Long start, Long endId) {

		List<Demand> demands = new ArrayList<>();
		String businessService = null;

		if (StringUtils.equalsIgnoreCase(request.getBusinesService(), "GB")) {
			businessService = "GB";
			SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = garbageService
					.createSearchAccountsRequest(request, requestInfo, businessService, start, endId);
			
			// search garbage accounts
			GarbageAccountResponse garbageAccountResponse = garbageService.searchAccounts(searchCriteriaGarbageAccountRequest);
			if (CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
				throw new CustomException("NO_APPLICATIONS_FOUND", "No Applications found to generate bills.");
			}

			// generate demand request
			demands = generateDemandRequestsForGarbageAccounts(garbageAccountResponse, mdmsResponse, businessService);
			

		}
		
		
		DemandRequest demandRequest = new DemandRequest(requestInfo, demands);
		producer.push("test_save_demand", demandRequest);
		
		return demands;
	}

	private List<Demand> generateDemandRequestsForGarbageAccounts(GarbageAccountResponse garbageAccountResponse,
			Object mdmsResponse, String businessService) {

		List<Demand> demands = new ArrayList<>();

		garbageAccountResponse.getGarbageAccounts().stream().forEach(garbageAccount -> {

			String tenantId = garbageAccount.getTenantId();
			
			// calculate fees from mdms response
			BigDecimal taxAmount = mdmsService.fetchGarbageAmountFromMDMSResponse(mdmsResponse, garbageAccount);
			String consumerCode = garbageAccount.getGrbgApplication().getApplicationNo();
			Integer days = 30;

			demands.add(createDemandRequest(taxAmount, consumerCode, consumerCode, consumerCode, days));

		});

		return demands;
	}

	private Demand createDemandRequest(BigDecimal taxAmount, String consumerCode, String tenantId,
			String businessService, Integer days) {

		DemandDetail demandDetail = DemandDetail.builder()
				.taxHeadMasterCode(SchedulerConstants.BILLING_TAX_HEAD_MASTER_CODE).taxAmount(taxAmount)
				.collectionAmount(BigDecimal.ZERO).build();

		Calendar calendar = Calendar.getInstance();
		Long currentTimeMillis = calendar.getTimeInMillis();
		calendar.add(Calendar.DAY_OF_MONTH, days); // days = 30 or 31
		Long taxPeriodTo = calendar.getTimeInMillis();

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(Arrays.asList(demandDetail))
				.minimumAmountPayable(taxAmount).tenantId(tenantId).taxPeriodFrom(currentTimeMillis)
				.taxPeriodTo(taxPeriodTo).consumerType(businessService).businessService(businessService).build();

		return demand;
	}

	private void validateRequest(SchedularMasterRequest schedularMasterRequest) {

		if (null != schedularMasterRequest
				&& !CollectionUtils.isEmpty(schedularMasterRequest.getSchedulerMasterBodies())) {

			schedularMasterRequest.getSchedulerMasterBodies().stream().forEach(body -> {
				if (null == body.getMaxCount() || null == body.getSlotSize() || null == body.getType()
						|| null == body.getBusinesService()) {
					throw new CustomException("PROVIDE_INPUT", "Mendatory fields are missing.");
				}
			});
		} else {
			throw new CustomException("PROVIDE_INPUT", "Mendatory fields are missing.");
		}

	}

}
