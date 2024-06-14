package org.egov.wscalculation.validator;

import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.repository.BillGeneratorDao;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.web.models.BillGenerationReq;
import org.egov.wscalculation.web.models.BillScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillGenerationValidator {

	@Autowired
	private CalculatorUtil calculatorUtils;

	@Autowired
	private BillGeneratorDao billGeneratorDao;

	public void validateBillingCycleDates(BillGenerationReq billGenerationReq, RequestInfo requestInfo) {

		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo,
				billGenerationReq.getBillScheduler().getTenantId());

		if (billingMasterData.get("taxPeriodFrom") == null || billingMasterData.get("taxPeriodTo") == null) {
			throw new CustomException(WSCalculationConstant.WS_NO_BILLING_PERIOD_MSG,
					WSCalculationConstant.WS_NO_BILLING_PERIOD_MSG);
		}
		long taxPeriodFrom = (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = (long) billingMasterData.get("taxPeriodTo");

		billGenerationReq.getBillScheduler().setBillingcycleStartdate(taxPeriodFrom);
		billGenerationReq.getBillScheduler().setBillingcycleEnddate(taxPeriodTo);
		validateExistingScheduledBillStatus(billGenerationReq);
	}

	private void validateExistingScheduledBillStatus(BillGenerationReq billGenerationReq) {

		BillScheduler billScheduler = billGenerationReq.getBillScheduler();
		List<String> status = billGeneratorDao.fetchExistingBillSchedularStatusForLocality(billScheduler.getLocality(),
				billScheduler.getBillingcycleStartdate(), billScheduler.getBillingcycleEnddate(),
				billScheduler.getTenantId());

		if (status.contains("INITIATED") || status.contains("INPROGRESS")) {

			throw new CustomException(WSCalculationConstant.WS_DUPLICATE_BILL_SCHEDULER,
					WSCalculationConstant.WS_DUPLICATE_BILL_SCHEDULER_MSG + billScheduler.getLocality());
		}
	}

	public boolean checkBillingCycleDates(BillGenerationReq billGenerationReq, RequestInfo requestInfo) {
		 boolean checkBillingStatus = false;
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo,
				billGenerationReq.getBillScheduler().getTenantId());

		if (billingMasterData.get("taxPeriodFrom") == null || billingMasterData.get("taxPeriodTo") == null) {
			throw new CustomException(WSCalculationConstant.WS_NO_BILLING_PERIOD_MSG,
					WSCalculationConstant.WS_NO_BILLING_PERIOD_MSG);
		}
		long taxPeriodFrom = (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = (long) billingMasterData.get("taxPeriodTo");

		billGenerationReq.getBillScheduler().setBillingcycleStartdate(taxPeriodFrom);
		billGenerationReq.getBillScheduler().setBillingcycleEnddate(taxPeriodTo);
		checkBillingStatus = validateExistingScheduledBillStatusForBatch(billGenerationReq);	
		return checkBillingStatus;
	}

	private boolean validateExistingScheduledBillStatusForBatch(BillGenerationReq billGenerationReq) {
		 boolean checkBillingStatus = false;
		BillScheduler billScheduler = billGenerationReq.getBillScheduler();
		List<String> status = billGeneratorDao.fetchExistingBillSchedularStatusForLocality(billScheduler.getLocality(),
				billScheduler.getBillingcycleStartdate(), billScheduler.getBillingcycleEnddate(),
				billScheduler.getTenantId());

		
		if (status.contains("INITIATED") || status.contains("INPROGRESS")) {			
			checkBillingStatus = true;
	
	}
		return checkBillingStatus;
	}

	

}
