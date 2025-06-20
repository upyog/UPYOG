package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CChartOfAccountDetailRepository extends JpaRepository<CChartOfAccountDetail, Long> {
}
