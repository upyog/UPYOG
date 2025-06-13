package org.egov.finance.master.repository;

import org.egov.finance.master.entity.Vouchermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VouchermisRepository extends JpaRepository<Vouchermis, Long> {
}
