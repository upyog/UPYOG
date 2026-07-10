package org.egov.wscalculation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.egov.wscalculation.repository.BillGeneratorDao;
import org.egov.wscalculation.repository.WSCalculationDao;
import org.egov.wscalculation.validator.BillGenerationValidator;
import org.egov.wscalculation.web.controller.BillGeneratorController;
import org.egov.wscalculation.web.models.AuditDetails;
import org.egov.wscalculation.web.models.BillGenerationReq;
import org.egov.wscalculation.web.models.BillGenerationSearchCriteria;
import org.egov.wscalculation.web.models.BillScheduler;
import org.egov.wscalculation.web.models.BillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.constants.WSCalculationConstant;


@Service
@Slf4j
public class BillGeneratorService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private BillGeneratorDao billGeneratorDao;
	
	@Autowired
	private WSCalculationDao waterCalculatorDao;
	
	@Autowired
	private BillGenerationValidator billGenerationValidator;
	

	@Autowired
	private BillGeneratorService billGeneratorService;

	public List<BillScheduler> saveBillGenerationDetails(BillGenerationReq billRequest) {
		List<BillScheduler> billSchedulers = new ArrayList<>();
		AuditDetails auditDetails = enrichmentService
				.getAuditDetails(billRequest.getRequestInfo().getUserInfo().getUuid(), true);

		billRequest.getBillScheduler().setId(UUID.randomUUID().toString());
		billRequest.getBillScheduler().setAuditDetails(auditDetails);
		billRequest.getBillScheduler().setStatus(BillStatus.INITIATED);
		billRequest.getBillScheduler().setTransactionType(WSCalculationConstant.WS_BILL_SCHEDULER_TRANSACTION);
		
		billGeneratorDao.saveBillGenertaionDetails(billRequest);
		billSchedulers.add(billRequest.getBillScheduler());
		return billSchedulers;
	}

	public List<BillScheduler> getBillGenerationDetails(BillGenerationSearchCriteria criteria) {

		return billGeneratorDao.getBillGenerationDetails(criteria);
	}
	
	
	public List<BillScheduler> getBillGenerationGroup(BillGenerationSearchCriteria criteria) {

		return billGeneratorDao.getBillGenerationGroup(criteria);

	}
	
	public List<BillScheduler> getBillGenerationByTenant(BillGenerationSearchCriteria criteria) {

		return billGeneratorDao.getBillGenerationByTenant(criteria);
		
	}
	
	public List<BillScheduler> bulkbillgeneration(BillGenerationReq billGenerationReq) {

		List<BillScheduler> billDetails = new ArrayList<BillScheduler>();
		BillScheduler billScheduler = billGenerationReq.getBillScheduler();

		if (billScheduler.getIsBatch()) {
			List<String> listOfLocalities = waterCalculatorDao.getLocalityList(billScheduler.getTenantId(),
					billScheduler.getLocality());
			for (String localityName : listOfLocalities) {
				billScheduler.setLocality(localityName);
				boolean localityStatus = billGenerationValidator.checkBillingCycleDates(billGenerationReq,
						billGenerationReq.getRequestInfo());
				if (!localityStatus) {
					billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
				}
			}
		} else if (billScheduler.getGroup() != null && !billScheduler.getGroup().isEmpty()) {
			List<String> temp = billScheduler.getGroup();
			billScheduler.setGroup(null);
			for (String grup : temp) {
				billScheduler.setGrup(grup);
				Boolean Check = billGenerationValidator.checkBillingCycleDates(billGenerationReq,
						billGenerationReq.getRequestInfo());
				if (!Check)
					billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
				else
					log.info("Bills Are Already In Initieated Or InProgress For Group--> " + billScheduler.getGrup());
			}
		} else if (billScheduler.getLocality() != null) {
			billGenerationValidator.validateBillingCycleDates(billGenerationReq, billGenerationReq.getRequestInfo());
			billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
		} else if (!billScheduler.getIsBatch()
				&& (billScheduler.getGroup() == null || billScheduler.getGroup().isEmpty())
				&& billScheduler.getGrup() == null && billScheduler.getLocality() == null
				&& billScheduler.getIsTenant()) {
			billGenerationValidator.validateBillingCycleDates(billGenerationReq, billGenerationReq.getRequestInfo());
			billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
			// billDetails1.addAll(billDetails);
		} else {
			throw new CustomException(WSCalculationConstant.WS_BILL_SCHEDULER_TRANSACTION,
					"Invalid bill scheduler configuration. Please provide at least one scheduling criterion: batch, group, locality, or tenant.");
		}
		return billDetails;
	}
		
	
	
	
	
	
}
