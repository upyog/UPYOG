package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.BudgetControlType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetControlTypeRepository extends JpaRepository<BudgetControlType,java.lang.Long>{

}
