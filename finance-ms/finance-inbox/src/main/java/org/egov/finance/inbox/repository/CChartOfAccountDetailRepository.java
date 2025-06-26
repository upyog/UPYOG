package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CChartOfAccountDetailRepository extends JpaRepository<CChartOfAccountDetail, Long> {
}
