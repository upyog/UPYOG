package org.egov.finance.voucher.repository;

import java.util.List;

import org.egov.finance.voucher.entity.BudgetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetGroupRepository extends JpaRepository<BudgetGroup, Long>, JpaSpecificationExecutor<BudgetGroup> {

	@Query("SELECT bg FROM BudgetGroup bg WHERE bg.minCode.glcode <= :glcode AND bg.maxCode.glcode >= :glcode "
			+ "AND bg IN (SELECT bd.budgetGroup FROM BudgetDetail bd) AND bg.isActive = true")
	List<BudgetGroup> findByDetailCode(@Param("glcode") String glcode);

	@Query("SELECT bg FROM BudgetGroup bg WHERE bg.minCode.glcode <= :glcode AND bg.maxCode.glcode >= :glcode "
			+ "AND bg IN (SELECT bd.budgetGroup FROM BudgetDetail bd) AND bg.isActive = true")
	List<BudgetGroup> findByMinorCode(@Param("glcode") String glcode);

	@Query("SELECT bg FROM BudgetGroup bg WHERE bg.majorCode.glcode = :glcode "
			+ "AND bg IN (SELECT bd.budgetGroup FROM BudgetDetail bd) AND bg.isActive = true")
	List<BudgetGroup> findByMajorCode(@Param("glcode") String glcode);
}
