package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.entity.CGeneralLedgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerDetailRepository  extends JpaRepository<CGeneralLedgerDetail, Long> {
    //List<CGeneralLedgerDetail> findByGeneralLedgerId(Long generalLedgerId);
}