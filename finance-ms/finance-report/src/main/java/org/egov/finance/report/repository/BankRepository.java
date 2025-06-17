package org.egov.finance.report.repository;

import java.util.Optional;

import org.egov.finance.report.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer>, JpaSpecificationExecutor<Bank> {

	Optional<Bank> findByCode(String code);

	Optional<Bank> findByName(String name);

}