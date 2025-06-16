package org.egov.finance.voucher.repository;

import java.util.List;

import org.egov.finance.voucher.entity.AccountDetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailKeyRepository extends JpaRepository<AccountDetailKey, Long>, JpaSpecificationExecutor<AccountDetailKey> {
    List<AccountDetailKey> findByAccountDetailTypeId(Long accountDetailTypeId);
}
