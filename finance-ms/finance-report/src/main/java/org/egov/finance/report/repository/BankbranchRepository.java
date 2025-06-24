package org.egov.finance.report.repository;

import java.util.Optional;

import org.egov.finance.report.entity.Bankbranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankbranchRepository extends JpaRepository<Bankbranch, Integer>, JpaSpecificationExecutor<Bankbranch> {

	Optional<Bankbranch> findByBranchMICR(String branchMICR);
}