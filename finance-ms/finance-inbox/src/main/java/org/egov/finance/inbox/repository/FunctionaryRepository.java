package org.egov.finance.inbox.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.egov.finance.inbox.entity.Functionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionaryRepository extends JpaRepository<Functionary, Integer> {
    Optional<Functionary> findByCode(BigDecimal code);
}