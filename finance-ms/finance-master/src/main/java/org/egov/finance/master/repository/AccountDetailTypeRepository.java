package org.egov.finance.master.repository;

import org.egov.finance.master.entity.AccountDetailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailTypeRepository extends JpaRepository<AccountDetailType, Long>, JpaSpecificationExecutor<AccountDetailType> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}