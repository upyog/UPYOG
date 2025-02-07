package org.egov.pt.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.DemandDetail;
import org.egov.pt.models.bill.DemandResponse;
import org.egov.pt.repository.DemandRepository;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DemandService {

	@Autowired
	private DemandRepository demandRepository;

	public List<Demand> generateDemand(RequestInfo requestInfo, Property property, String businessService,
			BigDecimal taxAmount) {

		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(PTConstants.PROPERTY_TAX_HEAD_MASTER_CODE)
				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(15));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);

		Demand demandOne = Demand.builder().consumerCode(property.getPropertyId())
				.demandDetails(Arrays.asList(demandDetail)).minimumAmountPayable(taxAmount)
				.tenantId(property.getTenantId()).taxPeriodFrom(new Date().getTime())
				.taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)) // TODO
																													// 30days
						.getTime())
				.fixedBillExpiryDate(cal.getTimeInMillis()).consumerType(PTConstants.MODULE_PROPERTY)
				.businessService(businessService).build();

		List<Demand> demands = Arrays.asList(demandOne);

		List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);

		return savedDemands;
	}

	List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo,
			String businessService) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);

		if (CollectionUtils.isEmpty(response.getDemands())) {
			return null;
		} else {
			return response.getDemands();
		}
	}

}
