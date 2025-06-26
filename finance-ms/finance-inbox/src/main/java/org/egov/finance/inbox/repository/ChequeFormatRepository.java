package org.egov.finance.inbox.repository;

import java.util.Optional;

import org.egov.finance.inbox.entity.ChequeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChequeFormatRepository extends JpaRepository<ChequeFormat, Long> {
    Optional<ChequeFormat> findByChequeName(String chequeName);
}
