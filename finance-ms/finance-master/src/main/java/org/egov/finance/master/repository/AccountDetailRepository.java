package org.egov.finance.master.repository;

import org.egov.finance.master.entity.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long>, JpaSpecificationExecutor<AccountDetail> {
}
