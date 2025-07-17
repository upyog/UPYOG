package org.egov.finance.report.repository;

import org.egov.finance.report.entity.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long>, JpaSpecificationExecutor<AccountDetail> {
}
