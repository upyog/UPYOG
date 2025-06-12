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

	Scheme findByCode(String code);

	Scheme findByName(String name);

	List<Scheme> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrIsactive(String name, String code,
			Boolean isactive);

	List<Scheme> findByCodeContainingIgnoreCase(String code);

	List<Scheme> findByIsactive(Boolean isactive);

	List<Scheme> findByNameContainingIgnoreCase(String name);

	List<Scheme> findByNameIgnoreCaseAndFundId(String name, Long fundId);

	List<Scheme> findByNameAndFundId(String name, Long fundId);

	// Uniqueness checks
	boolean existsByNameIgnoreCaseAndFundId(String name, Long fundId);

	// Optimized check for update validation
	boolean existsByNameIgnoreCaseAndFundIdAndIdNot(String name, Long fundId, Long id);
}
