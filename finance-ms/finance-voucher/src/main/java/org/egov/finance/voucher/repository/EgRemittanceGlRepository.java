package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.EgRemittanceGl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgRemittanceGlRepository extends JpaRepository<EgRemittanceGl, Integer> {
    // No additional methods needed for basic save()
}
