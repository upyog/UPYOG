package org.egov.finance.master.repository;

/**
 * SchemeRepository.java
 * 
 * @author mmavuluri
 * @date 9 Jun 2025
 * @version 1.0
 */
import java.util.List;

import org.egov.finance.master.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SchemeRepository extends JpaRepository<Scheme, Long>, JpaSpecificationExecutor<Scheme> {

}
