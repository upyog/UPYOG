package org.egov.demand.service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.demand.model.BillSearchCriteria;
import org.egov.demand.model.BillV2;
import org.egov.demand.model.BillV2.BillStatus;
import org.egov.demand.web.contract.BillResponseV2;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillSchedulerService {

	@Autowired
	private BillServicev2 billService;

	public void expireEligibleBill(@Valid RequestInfoWrapper requestInfoWrapper) {

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder().build();

		BillResponseV2 billResponseV2 = billService.searchBill(billSearchCriteria, requestInfoWrapper.getRequestInfo());

		if (null != billResponseV2 && !CollectionUtils.isEmpty(billResponseV2.getBill())) {
			Set<String> billIds = billResponseV2.getBill().stream()
					.filter(bill -> bill.getStatus().equals(BillStatus.ACTIVE)
							&& Instant.now().isAfter(Instant.ofEpochMilli(bill.getMaxExpiryDate())))
					.map(BillV2::getId).collect(Collectors.toSet());

			if (!CollectionUtils.isEmpty(billIds)) {
				billService.updateBillStatusToExpire(billIds);
			}
		}
	}

}
