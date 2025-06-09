/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.repository;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionRepository extends JpaRepository<Fund, Long> , JpaSpecificationExecutor<Function>{

}

