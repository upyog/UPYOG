package org.egov.finance.report.repository;

import org.egov.finance.report.entity.Vouchermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VouchermisRepository extends JpaRepository<Vouchermis, Long> {
}
