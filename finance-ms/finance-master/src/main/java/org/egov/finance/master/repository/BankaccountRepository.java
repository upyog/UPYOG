package org.egov.finance.master.repository;

import java.util.Optional;

import org.egov.finance.master.entity.Bankaccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankaccountRepository extends JpaRepository<Bankaccount, Long>, JpaSpecificationExecutor<Bankaccount> {

	Optional<Bankaccount> findByAccountnumber(String accountnumber);
}
