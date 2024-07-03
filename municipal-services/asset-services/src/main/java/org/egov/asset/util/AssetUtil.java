package org.egov.asset.util;

import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AuditDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class AssetUtil {

	/**
	 * Method to return auditDetails for create/update flows
	 *
	 * @param by
	 * @param isCreate
	 * @return AuditDetails
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = System.currentTimeMillis();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}
	
	public static String improveAssetID(String assetId, AssetRequest asset) {
        if (asset == null || assetId == null || assetId.isEmpty()) {
            return null;
        }

        String assetParentCategory = asset.getAsset().getAssetParentCategory();
        if (assetParentCategory != null && !assetParentCategory.trim().isEmpty()) {
            // Trim the assetParentCategory and extract the first letter
            char firstLetter = assetParentCategory.trim().charAt(0);
            // Replace 'A' with the first letter of assetParentCategory in the asset ID
            assetId = assetId.replace("A", Character.toString(firstLetter));
            log.info(assetId);
        
        }
        return assetId;
	}
}
