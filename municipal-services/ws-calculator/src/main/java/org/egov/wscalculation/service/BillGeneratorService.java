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
	
	public List<BillScheduler> bulkbillgeneration(BillGenerationReq billGenerationReq) {

		List<BillScheduler> billDetails = new ArrayList<BillScheduler>();
	       
    	if(billGenerationReq.getBillScheduler().getIsBatch())
    	{		
		List<String> listOfLocalities = waterCalculatorDao.getLocalityList(billGenerationReq.getBillScheduler().getTenantId(),billGenerationReq.getBillScheduler().getLocality());
		for(String localityName : listOfLocalities)
		{		
			billGenerationReq.getBillScheduler().setLocality(localityName);			
			boolean localityStatus = billGenerationValidator.checkBillingCycleDates(billGenerationReq, billGenerationReq.getRequestInfo());
			if(!localityStatus) 
			{
			billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
			}
			//billDetails1.addAll(billDetails);
		}
    	}
		else if (!billGenerationReq.getBillScheduler().getGroup().isEmpty())
		{
			
			
		
				List<String> temp=billGenerationReq.getBillScheduler().getGroup();
				billGenerationReq.getBillScheduler().setGroup(null);
				for(String grup:temp)
				{
					billGenerationReq.getBillScheduler().setGrup(grup);
					 Boolean Check=billGenerationValidator.checkBillingCycleDates(billGenerationReq, billGenerationReq.getRequestInfo());
					
					 if (!Check)
						 billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
					 else 
						 log.info("Bills Are Already In Initieated Or InProgress For Group--> "+ billGenerationReq.getBillScheduler().getGrup());
				}
			
			

		}
    	
    	else {
				billGenerationValidator.validateBillingCycleDates(billGenerationReq, billGenerationReq.getRequestInfo());
				billDetails = billGeneratorService.saveBillGenerationDetails(billGenerationReq);
			   // billDetails1.addAll(billDetails);
	}
    	
    	return billDetails;
	}
		
	
	
	
	
	
}
