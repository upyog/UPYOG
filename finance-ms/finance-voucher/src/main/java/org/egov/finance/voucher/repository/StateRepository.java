package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.workflow.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
}
