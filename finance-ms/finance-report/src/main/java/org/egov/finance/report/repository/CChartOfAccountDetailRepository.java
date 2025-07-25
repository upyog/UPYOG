package org.egov.finance.report.repository;

import org.egov.finance.report.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CChartOfAccountDetailRepository extends JpaRepository<CChartOfAccountDetail, Long> {
}
