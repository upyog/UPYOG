package org.egov.asset.util;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class AssetUtil {

    private final AssetRepository assetRepository;

    public AssetUtil(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Method to return auditDetails for create/update flows.
     *
     * @param by       The user performing the operation.
     * @param isCreate Whether the operation is create or update.
     * @return AuditDetails object with relevant fields set.
     */
    public AuditDetails getAuditDetails(String by, boolean isCreate) {
        Long time = System.currentTimeMillis();
        if (isCreate) {
            return AuditDetails.builder()
                    .createdBy(by)
                    .lastModifiedBy(by)
                    .createdTime(time)
                    .lastModifiedTime(time)
                    .build();
        }
        return AuditDetails.builder()
                .lastModifiedBy(by)
                .lastModifiedTime(time)
                .build();
    }

    /**
     * Improves the asset ID by replacing a specific character based on the asset parent category.
     *
     * @param assetId The original asset ID.
     * @param asset   The AssetRequest object containing asset details.
     * @return The improved asset ID.
     */
    public static String improveAssetID(String assetId, AssetRequest asset) {
        if (asset == null || assetId == null || assetId.isEmpty()) {
            return null;
        }

        String assetParentCategory = asset.getAsset().getAssetParentCategory();
        if (assetParentCategory != null && !assetParentCategory.trim().isEmpty()) {
            char firstLetter = assetParentCategory.trim().charAt(0);
            assetId = assetId.replace("A", Character.toString(firstLetter));
            log.info(assetId);
        }
        return assetId;
    }

    /**
     * Validates the disposal request.
     *
     * @param request The AssetDisposalRequest to validate.
     */
    public void validateDisposalRequest(AssetDisposalRequest request) {
        if (request.getAssetDisposal() == null) {
            throw new IllegalArgumentException("Invalid disposal data");
        }
    }

    /**
     * Extracts the tenant ID from the AssetDisposalRequest.
     *
     * @param request The AssetDisposalRequest containing tenant information.
     * @return The extracted tenant ID.
     */
    public String extractTenantId(AssetDisposalRequest request) {
        return request.getRequestInfo().getUserInfo().getTenantId().split("\\.")[0];
    }

    /**
     * Fetches an asset by its ID and tenant ID.
     *
     * @param assetId  The ID of the asset to fetch.
     * @param tenantId The tenant ID for the asset.
     * @return The fetched Asset object.
     */
    public Asset fetchAssetById(String assetId, String tenantId) {
        AssetSearchCriteria searchCriteria = AssetSearchCriteria.builder()
                .tenantId(tenantId)
                .ids(Collections.singletonList(assetId))
                .build();

        List<Asset> assets = assetRepository.getAssetData(searchCriteria);
        if (assets == null || assets.isEmpty()) {
            throw new IllegalArgumentException("Asset not found for ID: " + assetId);
        }
        return assets.get(0);
    }

    /**
     * Updates the asset's status and usage based on the provided status or disposal condition.
     *
     * @param asset                The Asset object to update.
     * @param isDisposedInFacility A boolean indicating if the asset is disposed in a facility.
     * @param status               A custom status to set for the asset (optional).
     */
    public void updateAssetStatusAndUsage(Asset asset, Boolean isDisposedInFacility, String status) {
        if (status != null && !status.trim().isEmpty()) {
            switch (status) {
                case AssetConstants.ASSET_USAGE_DISPOSED:
                    asset.setAssetStatus(AssetConstants.ASSET_STATUS_DISPOSED);
                    asset.setAssetUsage(AssetConstants.ASSET_USAGE_DISPOSED);
                    break;
                case AssetConstants.ASSET_USAGE_DISPOSED_AND_SOLD:
                    asset.setAssetStatus(AssetConstants.ASSET_STATUS_DISPOSED_AND_SOLD);
                    asset.setAssetUsage(AssetConstants.ASSET_USAGE_DISPOSED_AND_SOLD);
                    break;
                case AssetConstants.ASSET_USAGE_ASSET_STATUS_REPAIRED:
                    asset.setAssetStatus(AssetConstants.ASSET_STATUS_REPAIRED);
                    asset.setAssetUsage(AssetConstants.ASSET_USAGE_ASSET_STATUS_REPAIRED);
                    break;
                default:
                    asset.setAssetStatus(status);
            }
        } else if (Boolean.TRUE.equals(isDisposedInFacility)) {
            asset.setAssetStatus(AssetConstants.ASSET_STATUS_DISPOSED);
            asset.setAssetUsage(AssetConstants.ASSET_USAGE_DISPOSED);
        } else {
            asset.setAssetStatus(AssetConstants.ASSET_STATUS_DISPOSED_AND_SOLD);
            asset.setAssetUsage(AssetConstants.ASSET_USAGE_DISPOSED_AND_SOLD);
        }
    }
}
