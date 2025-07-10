package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.workflow.entity.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateHistoryRepository extends JpaRepository<StateHistory, Long> {
}
