package org.egov.finance.master.repository;

import org.egov.finance.master.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CChartOfAccountDetailRepository extends JpaRepository<CChartOfAccountDetail, Long> {
}
