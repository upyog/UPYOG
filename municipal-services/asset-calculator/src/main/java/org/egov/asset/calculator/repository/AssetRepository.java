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
                    "WHERE (:tenantId IS NOT NULL AND a.tenantId = :tenantId) " +
                    "   OR (:tenantId IS NULL AND a.tenantId LIKE 'pg%') " +
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
                    "WHERE (:tenantId IS NOT NULL AND a.tenantId = :tenantId) " +
                    "  OR (:tenantId IS NULL AND a.tenantId LIKE 'pg%') " +
                    "  AND a.isLegacyData = false " +
                    "  AND (:assetId IS NULL OR a.id = :assetId) " +
                    "  AND FUNCTION('to_char', FUNCTION('to_timestamp', a.purchaseDate / 1000), 'MM-dd') = :currentDate"
    )
    int countNonLegacyAssets(
            @Param("tenantId") String tenantId,
            @Param("assetId") String assetId,
            @Param("currentDate") String currentDate
    );


    @Query("SELECT a FROM Asset a WHERE a.tenantId = :tenantId AND (:assetId IS NULL OR a.id = :assetId) AND ((a.isLegacyData = :legacyData) OR (a.isLegacyData = false AND FUNCTION('TO_CHAR', a.purchaseDate, 'MM-dd') = :currentDate)) ORDER BY a.id")
    List<Asset> findAssetsForDepreciation(@Param("tenantId") String tenantId, @Param("assetId") String assetId,
                                          @Param("legacyData") boolean legacyData, @Param("currentDate") String currentDate,
                                          Pageable pageable);
}
