package org.egov.rentlease.service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rentlease.contract.bill.Demand;
import org.egov.rentlease.contract.bill.DemandDetail;
import org.egov.rentlease.contract.bill.DemandRepository;
import org.egov.rentlease.model.RentLease;
import org.egov.rentlease.util.RentConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemandService {

	@Autowired
	private RentConstants constants;
	
	@Autowired
	private DemandRepository demandRepository;

	public List<Demand> generateDemand(RequestInfo requestInfo, RentLease rent, String rentLeaseConstant) {

		BigDecimal taxAmount = new BigDecimal("100.00");
		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(constants.BILLING_TAX_HEAD_MASTER_CODE)
				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();

		Demand demandOne = Demand.builder().consumerCode(rent.getApplicationNo())
				.demandDetails(Arrays.asList(demandDetail)).minimumAmountPayable(taxAmount).tenantId(rent.getTenantId())
				.taxPeriodFrom(new Date().getTime())
				.taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000))
						.getTime())
				.consumerType(rent.getApplicationNo()).businessService(constants.RENT_LEASE_CONSTANT).build();
		List<Demand> demands = Arrays.asList(demandOne);
		List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);

		return savedDemands;

	}

}
