package org.egov.finance.voucher.repository;

import java.util.Date;
import java.util.Optional;

import org.egov.finance.voucher.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialYearRepository
		extends JpaRepository<FinancialYear, Long>, JpaSpecificationExecutor<FinancialYear> {

//    @Query("FROM FinancialYear fy WHERE :date BETWEEN fy.startingDate AND fy.endingDate")
//    FinancialYear getFinancialYearByDate(@Param("date") Date date);
//    
	@Query("SELECT fy FROM FinancialYear fy WHERE :date BETWEEN fy.startingDate AND fy.endingDate")
	Optional<FinancialYear> findByDateInRange(@Param("date") Date date);

	@Query("""
			    FROM FinancialYear fy
			    WHERE :date >= fy.startingDate
			      AND :date <= fy.endingDate
			      AND fy.isClosed = false
			      AND fy.isActiveForPosting = true
			""")
	FinancialYear getFinancialYearByDate(@Param("date") Date date);

}
