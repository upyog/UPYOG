package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.User;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.UserSearchResponse;
import org.egov.garbageservice.service.UserService;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.DemandDetail;
import org.egov.garbageservice.contract.bill.DemandRepository;
import org.egov.garbageservice.contract.bill.DemandResponse;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class DemandService {

	@Autowired
	private GrbgConstants grbgConfig;

	@Autowired
	private DemandRepository demandRepository;
	
	@Autowired
	private UserService userService;

	public List<Demand> generateDemand(RequestInfo requestInfo, GarbageAccount garbageAccount, String businessService,
			BigDecimal taxAmount, GenerateBillRequest generateBillRequest) {

		Long taxPeriodFrom = null != generateBillRequest ? generateBillRequest.getFromDate().getTime()
				: new Date().getTime();
		Long taxPeriodTo = null != generateBillRequest ? generateBillRequest.getToDate().getTime()
				: new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime();
		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(GrbgConstants.BILLING_TAX_HEAD_MASTER_CODE)
				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(grbgConfig.getGrbgBillExpiryAfter()));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		
		Demand demandOne = Demand.builder().consumerCode(garbageAccount.getGrbgApplicationNumber())
				.demandDetails(Arrays.asList(demandDetail)).minimumAmountPayable(taxAmount)
				.tenantId(garbageAccount.getTenantId()).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
				.fixedBillExpiryDate(cal.getTimeInMillis()).consumerType(GrbgConstants.WORKFLOW_MODULE_NAME)
				.payer(User.builder().uuid(garbageAccount.getUserUuid()).build())
				.businessService(businessService).build();

		List<Demand> demands = Arrays.asList(demandOne);

		List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);

		return savedDemands;
	}

	List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo,
			String businessService) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);

		if (CollectionUtils.isEmpty(response.getDemands()))
			return null;

		else
			return response.getDemands();

	}

}
