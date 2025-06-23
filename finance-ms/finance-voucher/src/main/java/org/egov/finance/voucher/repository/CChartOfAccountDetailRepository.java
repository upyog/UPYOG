package org.egov.finance.voucher.repository;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CChartOfAccountDetailRepository extends JpaRepository<CChartOfAccountDetail, Long> {

	List<CChartOfAccountDetail> findByGlCodeId_Id(Long glcodeId);

	Optional<CChartOfAccountDetail> findByGlCodeId_IdAndDetailTypeId_Id(Long glcodeId, Integer detailTypeId);

	Optional<CChartOfAccountDetail> findByGlCodeId_GlcodeAndDetailTypeId_Id(String glcode, Long long1);

	List<CChartOfAccountDetail> findAll();

	boolean existsByGlCodeIdId(Long glCodeId);

	@Query("SELECT cd FROM CChartOfAccountDetail cd WHERE cd.glCodeId.glcode = :glcode")
	List<CChartOfAccountDetail> findByGlcode(@Param("glcode") String glcode);

	@Query("SELECT cd FROM CChartOfAccountDetail cd WHERE cd.glCodeId.glcode = :glcode AND cd.detailTypeId.id = :detailTypeId")
	List<CChartOfAccountDetail> findByGlcodeAndDetailTypeId(@Param("glcode") String glcode,
			@Param("detailTypeId") Integer detailTypeId);
}
