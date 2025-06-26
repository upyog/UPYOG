package org.egov.finance.master.repository;

import java.util.List;

import org.egov.finance.master.entity.CGeneralLedgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CGeneralLedgerDetailRepository  extends JpaRepository<CGeneralLedgerDetail, Long> {
    //List<CGeneralLedgerDetail> findByGeneralLedgerId(Long generalLedgerId);
}