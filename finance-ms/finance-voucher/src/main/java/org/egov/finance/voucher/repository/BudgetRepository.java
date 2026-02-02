package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

	@Query("SELECT b FROM Budget b WHERE b.financialYear.id = :financialYearId "
			+ "AND b.isbere = 'RE' AND b.isActiveBudget = true AND b.parent IS NULL "
			+ "AND b.isPrimaryBudget = true AND b.status.code = 'Approved'")
	Budget findApprovedReBudgetForYear(@Param("financialYearId") Long financialYearId);
}
