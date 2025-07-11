package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountsRepository
		extends JpaRepository<CChartOfAccounts, Long>, JpaSpecificationExecutor<CChartOfAccounts> {

//	CChartOfAccounts findByGlcode(String glcode);
//
//	CChartOfAccounts getCChartOfAccountsByGlCode(String glcode);

	CChartOfAccounts findByGlcode(String glcode); 

	// Optional alias
	CChartOfAccounts getByGlcode(String glcode);
}
