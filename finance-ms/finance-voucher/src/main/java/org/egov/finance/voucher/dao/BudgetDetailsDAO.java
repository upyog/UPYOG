package org.egov.finance.voucher.dao;

import java.util.Map;

import org.egov.finance.voucher.exception.ValidationException;

public interface BudgetDetailsDAO {
	
	public boolean budgetaryCheck(Map<String, Object> paramMap) throws ValidationException;

}
