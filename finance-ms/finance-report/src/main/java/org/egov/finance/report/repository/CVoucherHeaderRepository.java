package org.egov.finance.report.repository;

import java.util.List;
import java.util.Optional;

import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CVoucherHeaderRepository extends JpaRepository<CVoucherHeader, Long>, JpaSpecificationExecutor<CVoucherHeader> {

    Optional<CVoucherHeader> findByVoucherNumber(String voucherNumber);

    List<CVoucherHeader> findByTypeIgnoreCase(String type);
    
    @Query("SELECT DISTINCT v.fundId FROM CVoucherHeader v WHERE v.fundId IS NOT NULL")
    List<Fund> findDistinctFundsUsedInVouchers();
}
