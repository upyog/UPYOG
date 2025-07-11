package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.CGeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerRepository extends JpaRepository<CGeneralLedger, Long> {

//	void persist(CGeneralLedgerModel gLedger);
//	//List<CGeneralLedger> findByVoucherHeaderId(Long voucherHeaderId);
}
