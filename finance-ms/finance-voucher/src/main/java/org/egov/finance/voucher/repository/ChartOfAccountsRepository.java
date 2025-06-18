package org.egov.finance.voucher.repository;

import java.util.Optional;

import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountsRepository
		extends JpaRepository<CChartOfAccounts, Long>, JpaSpecificationExecutor<CChartOfAccounts> {

	Optional<CChartOfAccounts> findByGlcode(String glcode);
}
