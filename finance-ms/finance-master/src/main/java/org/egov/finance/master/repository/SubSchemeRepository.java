/**
 * 
 */
package org.egov.finance.master.repository;

import org.egov.finance.master.entity.SubScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * SubSchemeRepository.java
 * 
 * @author bpattanayak
 * @date 10 Jun 2025
 * @version 1.0
 */

public interface SubSchemeRepository extends JpaRepository<SubScheme, Long>,JpaSpecificationExecutor<SubScheme>{
	

}
