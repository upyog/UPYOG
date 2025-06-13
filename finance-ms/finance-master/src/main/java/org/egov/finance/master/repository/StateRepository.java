package org.egov.finance.master.repository;

import org.egov.finance.master.workflow.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
}
