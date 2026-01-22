/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.EgBillregistermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgBillregistermisRepository extends JpaRepository<EgBillregistermis, Long>, JpaSpecificationExecutor<EgBillregistermis> {
	
}

