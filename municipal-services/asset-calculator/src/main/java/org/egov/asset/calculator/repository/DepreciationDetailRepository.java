package org.egov.asset.calculator.repository;


import org.egov.asset.calculator.web.models.DepreciationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepreciationDetailRepository extends JpaRepository<DepreciationDetail, Long> {

    List<DepreciationDetail> findByAssetId(String assetId);
    Optional<DepreciationDetail> findByAssetIdAndFromDate(Long assetId, LocalDate fromDate);

    Optional<DepreciationDetail> findByAssetIdAndFromDateAndToDate(String id, LocalDate startDate, LocalDate endDate);
}
