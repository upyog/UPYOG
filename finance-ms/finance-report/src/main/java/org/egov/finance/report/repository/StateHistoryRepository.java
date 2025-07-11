package org.egov.finance.report.repository;

import org.egov.finance.report.workflow.entity.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateHistoryRepository extends JpaRepository<StateHistory, Long> {
}
