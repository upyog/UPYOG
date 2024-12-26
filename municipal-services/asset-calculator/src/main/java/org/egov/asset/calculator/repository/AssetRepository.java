package org.egov.asset.calculator.repository;

import org.egov.asset.calculator.web.models.contract.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query(
            "SELECT COUNT(a) " +
                    "FROM Asset a " +
                    "WHERE (:tenantId = 'pg' AND a.tenantId LIKE CONCAT(:tenantId, '%')) " +
                    "   OR (:tenantId != 'pg' AND a.tenantId = :tenantId) " +
                    "   AND a.isLegacyData = true " +
                    "   AND (:assetId IS NULL OR a.id = :assetId)"
    )
    int countLegacyAssets(
            @Param("tenantId") String tenantId,
            @Param("assetId") String assetId
    );

    @Query(
            "SELECT COUNT(a) " +
                    "FROM Asset a " +
                    "WHERE " +
                    "    ((:tenantId = 'pg' AND a.tenantId LIKE CONCAT(:tenantId, '%')) " +
                    "     OR (:tenantId != 'pg' AND a.tenantId = :tenantId)) " +
                    "  AND a.isLegacyData = false " +
                    "  AND (:assetId IS NULL OR a.id = :assetId) " +
                    "  AND TO_CHAR(TO_TIMESTAMP(a.purchaseDate / 1000), 'MM-dd') = :currentDate"
    )
    int countNonLegacyAssets(
            @Param("tenantId") String tenantId,
            @Param("assetId") String assetId,
            @Param("currentDate") String currentDate
    );


    @Query("SELECT a " +
            "FROM Asset a " +
            "WHERE " +
            "    ((:tenantId = 'pg' AND a.tenantId LIKE CONCAT(:tenantId, '%')) " +
            "     OR (:tenantId != 'pg' AND a.tenantId = :tenantId)) " +
            "  AND (:assetId IS NULL OR a.id = :assetId) " +
            "  AND (a.isLegacyData = :legacyData " +
            "       OR (a.isLegacyData = false " +
            "           AND TO_CHAR(TO_TIMESTAMP(a.purchaseDate / 1000), 'MM-dd') = :currentDate)) " +
            "ORDER BY a.id")
    List<Asset> findAssetsForDepreciation(@Param("tenantId") String tenantId,
                                          @Param("assetId") String assetId,
                                          @Param("legacyData") boolean legacyData,
                                          @Param("currentDate") String currentDate,
                                          Pageable pageable);


}
