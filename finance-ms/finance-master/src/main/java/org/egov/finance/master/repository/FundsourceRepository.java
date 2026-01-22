package org.egov.finance.master.repository;

import java.util.Optional;

import org.egov.finance.master.entity.Fundsource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundsourceRepository extends JpaRepository<Fundsource, Long> {
    Optional<Fundsource> findByCodeAndName(String code, String name);
}
