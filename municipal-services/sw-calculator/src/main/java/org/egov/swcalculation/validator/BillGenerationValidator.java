package org.egov.swcalculation.validator;

import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.repository.BillGeneratorDao;
import org.egov.swcalculation.util.CalculatorUtils;
import org.egov.swcalculation.web.models.BillGenerationRequest;
import org.egov.swcalculation.web.models.BillScheduler;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillGenerationValidator {

	@Autowired
	private CalculatorUtils calculatorUtils;

	@Autowired
	private BillGeneratorDao billGeneratorDao;

	public boolean validateBillingCycleDates(BillGenerationRequest billGenerationReq, RequestInfo requestInfo) {
		 boolean checkBillingStatus = false;
		Map<String, Object> billingMasterData = calculatorUtils.loadBillingFrequencyMasterData(requestInfo,
				billGenerationReq.getBillScheduler().getTenantId());

		if (billingMasterData.get("taxPeriodFrom") == null || billingMasterData.get("taxPeriodTo") == null) {
			throw new CustomException(SWCalculationConstant.SW_NO_BILLING_PERIOD_MSG,
					SWCalculationConstant.SW_NO_BILLING_PERIOD_MSG);
		}
		long taxPeriodFrom = (long) billingMasterData.get("taxPeriodFrom");
		long taxPeriodTo = (long) billingMasterData.get("taxPeriodTo");

		billGenerationReq.getBillScheduler().setBillingcycleStartdate(taxPeriodFrom);
		billGenerationReq.getBillScheduler().setBillingcycleEnddate(taxPeriodTo);
		checkBillingStatus = validateExistingScheduledBillStatus(billGenerationReq);
		return checkBillingStatus;
	}

	private boolean validateExistingScheduledBillStatus(BillGenerationRequest billGenerationReq) {
		 boolean checkBillingStatus = false;
		BillScheduler billScheduler = billGenerationReq.getBillScheduler();
		List<String> status = billGeneratorDao.fetchExistingBillSchedularStatusForLocality(billScheduler.getLocality(),
				billScheduler.getBillingcycleStartdate(), billScheduler.getBillingcycleEnddate(),
				billScheduler.getTenantId());

		if (status.contains("INITIATED") || status.contains("INPROGRESS")) {
			checkBillingStatus = true;
			throw new CustomException(SWCalculationConstant.SW_DUPLICATE_BILL_SCHEDULER,
					SWCalculationConstant.SW_DUPLICATE_BILL_SCHEDULER_MSG + billScheduler.getLocality());
		}
		return checkBillingStatus;
	}

}
