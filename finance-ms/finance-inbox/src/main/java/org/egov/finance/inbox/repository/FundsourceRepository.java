package org.egov.finance.inbox.repository;

import java.util.Optional;

import org.egov.finance.inbox.entity.Fundsource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundsourceRepository extends JpaRepository<Fundsource, Long> {
    Optional<Fundsource> findByCodeAndName(String code, String name);
}
