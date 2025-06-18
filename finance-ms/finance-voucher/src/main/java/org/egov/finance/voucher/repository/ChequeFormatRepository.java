package org.egov.finance.voucher.repository;

import java.util.Optional;

import org.egov.finance.voucher.entity.ChequeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChequeFormatRepository extends JpaRepository<ChequeFormat, Long> {
    Optional<ChequeFormat> findByChequeName(String chequeName);
}
