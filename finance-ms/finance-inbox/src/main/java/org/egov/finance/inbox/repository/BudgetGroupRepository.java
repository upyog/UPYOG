/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.entity.BudgetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetGroupRepository extends JpaRepository<BudgetGroup, Long>, 
JpaSpecificationExecutor<BudgetGroup>{
	
	@Query("from BudgetGroup bg where bg.minCode.glcode <= :glcode and bg.maxCode.glcode >= :glcode and bg in (select bd.budgetGroup from BudgetDetail bd) and bg.isActive = true")
	List<BudgetGroup> findEligibleBudgetGroups(@Param("glcode") String glcode);
	
	@Query("SELECT bg FROM BudgetGroup bg " +
	           "WHERE bg.majorCode.glcode = :glcode " +
	           "AND bg.isActive = true " +
	           "AND bg IN (SELECT bd.budgetGroup FROM BudgetDetail bd)")
	    List<BudgetGroup> findByMajorCodeGlcode(@Param("glcode") String glcode);
}

