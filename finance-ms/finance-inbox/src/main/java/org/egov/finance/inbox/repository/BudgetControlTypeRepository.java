/**
 * Created on Jun 20, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.BudgetControlType;
import org.egov.finance.inbox.entity.BudgetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetControlTypeRepository extends JpaRepository<BudgetControlType, Long>, 
JpaSpecificationExecutor<BudgetControlType>{

}

