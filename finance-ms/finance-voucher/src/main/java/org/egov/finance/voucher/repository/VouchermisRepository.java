package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.Vouchermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VouchermisRepository extends JpaRepository<Vouchermis, Long> {
}
