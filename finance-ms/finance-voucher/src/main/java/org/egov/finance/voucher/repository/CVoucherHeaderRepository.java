package org.egov.finance.voucher.repository;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CVoucherHeaderRepository extends JpaRepository<CVoucherHeader, Long>, JpaSpecificationExecutor<CVoucherHeader> {

    Optional<CVoucherHeader> findByVoucherNumber(String voucherNumber);

    List<CVoucherHeader> findByTypeIgnoreCase(String type);
}
