package org.egov.finance.report.repository;

import java.util.Optional;

import org.egov.finance.report.entity.CChartOfAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountsRepository
		extends JpaRepository<CChartOfAccounts, Long>, JpaSpecificationExecutor<CChartOfAccounts> {

	Optional<CChartOfAccounts> findByGlcode(String glcode);
	
	@Query("SELECT c.majorCode FROM CChartOfAccounts c WHERE c.purposeId = :purposeId")
    Optional<String> findMajorCodeByPurposeId(@Param("purposeId") Integer purposeId);
}
