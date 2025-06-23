/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.entity.Budget;
import org.egov.finance.inbox.entity.BudgetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, 
JpaSpecificationExecutor<Budget>{
	
	@Query("SELECT b FROM Budget b WHERE b.financialYear.id = :financialYearId " +
		       "AND b.isbere = 'RE' AND b.isActiveBudget = true AND b.parent IS NULL " +
		       "AND b.isPrimaryBudget = true AND b.status.code = 'Approved'")
		List<Budget> findApprovedReBudgetsByFinancialYear(@Param("financialYearId") Long financialYearId);
}

