package org.egov.finance.master.repository;

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

}
