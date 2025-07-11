/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class BalanceSheetReportService {
	
	@Autowired
	BalanceSheetService balanceSheetService;
	

	public void populateDataSource(Statement balanceSheet) {
		validateMandatoryFields(balanceSheet);
		balanceSheetService.setRelatedEntitiesOn();

		if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null) {
			final List<Fund> selFund = new ArrayList<Fund>();
			selFund.add(balanceSheet.getFund());
			balanceSheet.setFundList(selFund);
		} else
			balanceSheet.setFundList(balanceSheetService.getFunds());
		balanceSheetService.populateBalanceSheet(balanceSheet);
	}
	
	private void validateMandatoryFields(Statement balanceSheet) {
		Map<String, String> errors=new HashMap<String, String>();
		if (StringUtils.isEmpty(balanceSheet.getPeriod()) || "Select".equals(balanceSheet.getPeriod())) {
			errors.put("STATEMENT_PERIOD_ERROR", balanceSheetService.getMessage("msg.please.select.period"));
			
		} else {
			if (!"Date".equals(balanceSheet.getPeriod()) && (balanceSheet.getFinancialYear() == null
					|| balanceSheet.getFinancialYear().getId() == null || balanceSheet.getFinancialYear().getId() == 0))
				errors.put("STATEMENT_PERIOD_ERROR", balanceSheetService.getMessage("msg.please.select.financial.year"));
			if ("Date".equals(balanceSheet.getPeriod()) && balanceSheet.getAsOndate() == null) {
				errors.put("STATEMENT_PERIOD_ERROR", balanceSheetService.getMessage("msg.please.enter.as.onDate"));
			}
		}
		if (StringUtils.isEmpty(balanceSheet.getCurrency())) {
			errors.put("STATEMENT_CURRENCY_ERROR", balanceSheetService.getMessage("msg.please.select.currency"));
		}
		
		if(!CollectionUtils.isEmpty(errors))
			throw new ReportServiceException(errors);
	}
}
