package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.model.EgRemittanceGldtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgRemittanceGldtlRepository extends JpaRepository<EgRemittanceGldtl, Long> {

	//void persist(EgRemittanceGldtl egRemitGldtl);
}
