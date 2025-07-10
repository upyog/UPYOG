package org.egov.finance.voucher.repository;

import java.util.Date;
import java.util.List;

import org.egov.finance.voucher.entity.FiscalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FiscalPeriodRepository
		extends JpaRepository<FiscalPeriod, Long>, JpaSpecificationExecutor<FiscalPeriod> {

	List<FiscalPeriod> findByIsActive(Boolean isactive);

	List<FiscalPeriod> findByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

	// FiscalPeriod getFiscalPeriodByDate(Date voucherDate);

	@Query("SELECT fp FROM FiscalPeriod fp WHERE :voucherDate BETWEEN fp.startingDate AND fp.endingDate")
	FiscalPeriod findByVoucherDateBetween(Date voucherDate);

}
