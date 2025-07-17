package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialYearRepository
		extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {

}
