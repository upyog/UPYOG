package org.egov.finance.master.repository;

import org.egov.finance.master.workflow.entity.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateHistoryRepository extends JpaRepository<StateHistory, Long> {
}
