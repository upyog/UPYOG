/**
 * 
 */
package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.Scheme;
import org.egov.finance.voucher.entity.SubScheme;
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
	
	SubScheme findByCode(String code);

	SubScheme findByName(String name);
	
	boolean existsByCodeAndScheme(String code, Scheme scheme);
}
