package org.egov.finance.voucher.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<CVoucherHeader, Long> {

	long countByVoucherNumberAndVoucherDate(String voucherNumber, Date voucherDate);

	Optional<CVoucherHeader> findById(Long id);

	@Query("SELECT vh FROM CVoucherHeader vh WHERE vh.voucherNumber = :vcNum AND vh.voucherDate BETWEEN :startDate AND :endDate AND vh.status != 4")
	List<CVoucherHeader> findDuplicateVouchers(@Param("vcNum") String vcNum, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT vh FROM CVoucherHeader vh JOIN vh.vouchermis vmis "
			+ "WHERE (:serviceName IS NULL OR vmis.serviceName = :serviceName) "
			+ "AND vmis.referenceDocument = :referenceDocument " + "ORDER BY vh.createdDate DESC")
	List<CVoucherHeader> findRecentVouchers(@Param("serviceName") String serviceName,
			@Param("referenceDocument") String referenceDocument);

}
