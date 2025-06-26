package org.egov.finance.voucher.dao;

import java.util.Date;

import org.egov.finance.voucher.entity.FiscalPeriod;

public interface FiscalPeriodDAO {

	public String getFiscalPeriodIds(String financialYearId);

	public FiscalPeriod getFiscalPeriodByDate(Date voucherDate);

}
