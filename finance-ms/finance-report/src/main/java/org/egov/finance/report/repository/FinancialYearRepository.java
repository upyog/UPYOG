package org.egov.finance.report.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.egov.finance.report.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialYearRepository
		extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {
	
	@Query("SELECT fy.id FROM FinancialYear fy WHERE :date BETWEEN fy.startingDate AND fy.endingDate")
    Long findFinancialYearIdByDate(@Param("date") Date date);
	
	@Query("SELECT fy FROM FinancialYear fy WHERE :date BETWEEN fy.startingDate AND fy.endingDate")
    Optional<FinancialYear> findByDateWithinRange(@Param("date") Date date);

}
