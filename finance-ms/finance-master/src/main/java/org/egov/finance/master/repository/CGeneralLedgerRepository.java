package org.egov.finance.master.repository;

import java.util.List;

import org.egov.finance.master.entity.CGeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerRepository extends JpaRepository<CGeneralLedger, Long> {
	//List<CGeneralLedger> findByVoucherHeaderId(Long voucherHeaderId);
}
