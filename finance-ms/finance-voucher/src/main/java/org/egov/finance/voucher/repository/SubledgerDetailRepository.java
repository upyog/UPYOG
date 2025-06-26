package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.SubledgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubledgerDetailRepository extends JpaRepository<SubledgerDetail, Long>, JpaSpecificationExecutor<SubledgerDetail> {
}
