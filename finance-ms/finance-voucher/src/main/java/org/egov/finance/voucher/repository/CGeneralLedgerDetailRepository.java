package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.CGeneralLedgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerDetailRepository extends JpaRepository<CGeneralLedgerDetail, Long> {

	//void persist(CGeneralLedgerDetailModel gLedgerDet);
	// List<CGeneralLedgerDetail> findByGeneralLedgerId(Long generalLedgerId);
}