package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.entity.CGeneralLedger;
import org.egov.finance.inbox.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerRepository extends JpaRepository<CGeneralLedger, Long>, JpaSpecificationExecutor<CGeneralLedger> {
	//List<CGeneralLedger> findByVoucherHeaderId(Long voucherHeaderId);
}
