package org.egov.asset.calculator.repository;

import org.egov.asset.calculator.web.models.contract.MdmsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface MdmsDataRepository extends JpaRepository<MdmsData, String> {

    @Query(value = "SELECT CAST(data->>'rate' AS DECIMAL) FROM eg_mdms_data WHERE schemacode = 'ASSET.DepreciationRates' AND uniqueidentifier = :category", nativeQuery = true)
    BigDecimal findDepreciationRateByCategory(@Param("category") String category);
}