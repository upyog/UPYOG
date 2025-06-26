package org.egov.finance.inbox.repository;

import java.util.Optional;

import org.egov.finance.inbox.entity.CChartOfAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountsRepository
		extends JpaRepository<CChartOfAccounts, Long>, JpaSpecificationExecutor<CChartOfAccounts> {

	Optional<CChartOfAccounts> findByGlcode(String glcode);
}
