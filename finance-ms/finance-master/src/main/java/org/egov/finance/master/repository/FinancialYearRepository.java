package org.egov.finance.master.repository;

import org.egov.finance.master.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialYearRepository
		extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {

}
