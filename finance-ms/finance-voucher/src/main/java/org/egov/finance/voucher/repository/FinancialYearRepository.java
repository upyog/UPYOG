package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialYearRepository
		extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {

}
