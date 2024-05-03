package org.egov.swcalculation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.egov.swcalculation.repository.BillGeneratorDao;
import org.egov.swcalculation.web.models.AuditDetails;
import org.egov.swcalculation.web.models.BillGenerationRequest;
import org.egov.swcalculation.web.models.BillGenerationSearchCriteria;
import org.egov.swcalculation.web.models.BillScheduler;
import org.egov.swcalculation.web.models.BillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.egov.swcalculation.constants.SWCalculationConstant;

@Service
public class BillGeneratorService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private BillGeneratorDao billGeneratorDao;

	public List<BillScheduler> saveBillGenerationDetails(BillGenerationRequest billRequest) {
		List<BillScheduler> billSchedulers = new ArrayList<>();
		AuditDetails auditDetails = enrichmentService
				.getAuditDetails(billRequest.getRequestInfo().getUserInfo().getUuid(), true);

		billRequest.getBillScheduler().setId(UUID.randomUUID().toString());
		billRequest.getBillScheduler().setAuditDetails(auditDetails);
		billRequest.getBillScheduler().setStatus(BillStatus.INITIATED);
		billRequest.getBillScheduler().setTransactionType(SWCalculationConstant.SW_BILL_SCHEDULER_TRANSACTION);
		billGeneratorDao.saveBillGenertaionDetails(billRequest);
		billSchedulers.add(billRequest.getBillScheduler());
		return billSchedulers;
	}

	public List<BillScheduler> getBillGenerationDetails(BillGenerationSearchCriteria criteria) {

		return billGeneratorDao.getBillGenerationDetails(criteria);
	}
}
