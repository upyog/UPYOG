package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.workflow.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
}
