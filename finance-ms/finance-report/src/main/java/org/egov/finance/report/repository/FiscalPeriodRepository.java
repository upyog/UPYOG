package org.egov.finance.report.repository;

import java.util.List;

import org.egov.finance.report.entity.FiscalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FiscalPeriodRepository
		extends JpaRepository<FiscalPeriod, Long>, JpaSpecificationExecutor<FiscalPeriod> {

	List<FiscalPeriod> findByActive(Boolean active);

	List<FiscalPeriod> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
