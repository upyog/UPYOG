package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetDisposeRepository;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class AssetDisposeService {

    private final AssetDisposeRepository assetDisposeRepository;
    private final AssetService assetService;
    private final EnrichmentService enrichmentService;
    private final AssetUtil assetUtil;

    public AssetDisposeService(AssetDisposeRepository assetDisposeRepository, AssetService assetService,
                               EnrichmentService enrichmentService, AssetUtil assetUtil) {
        this.assetDisposeRepository = assetDisposeRepository;
        this.assetService = assetService;
        this.enrichmentService = enrichmentService;
        this.assetUtil = assetUtil;
    }

    /**
     * Create a new asset disposal record.
     *
     * @param request The AssetDisposalRequest containing disposal details.
     * @return The created AssetDisposal object.
     */
    public AssetDisposal createDisposal(AssetDisposalRequest request) {
        log.debug("Asset disposal service method create called");

        assetUtil.validateDisposalRequest(request);

        String tenantId = assetUtil.extractTenantId(request);
        AssetDisposal disposal = request.getAssetDisposal();
        Asset asset = assetUtil.fetchAssetById(disposal.getAssetId(), tenantId);

        enrichmentService.enrichDisposalCreateOperations(request);
        assetDisposeRepository.save(request);

        if (disposal.getDisposalDate() != null) {
            assetUtil.updateAssetStatusAndUsage(asset, disposal.getIsAssetDisposedInFacility(), disposal.getAssetDisposalStatus());
            updateAssetInSystem(request.getRequestInfo(), asset);
        }

        return disposal;
    }

    /**
     * Update an existing asset disposal record.
     *
     * @param request The AssetDisposalRequest containing updated disposal details.
     * @return The updated AssetDisposal object.
     */
    public AssetDisposal updateDisposal(@Valid AssetDisposalRequest request) {
        log.debug("Asset disposal service method update called");

        assetUtil.validateDisposalRequest(request);

        String tenantId = assetUtil.extractTenantId(request);
        AssetDisposal disposal = request.getAssetDisposal();
        Asset asset = assetUtil.fetchAssetById(disposal.getAssetId(), tenantId);

        enrichmentService.enrichDisposalUpdateOperations(request);
        assetDisposeRepository.update(request);

        if (disposal.getDisposalDate() != null) {
            assetUtil.updateAssetStatusAndUsage(asset, disposal.getIsAssetDisposedInFacility(), null);
        }

        return disposal;
    }

    /**
     * Searches for asset disposals based on the given search criteria.
     *
     * @param searchCriteria The criteria used to filter the asset disposals.
     * @param requestInfo    The request information containing user and tenant details.
     * @return A list of AssetDisposal objects that match the search criteria.
     */
    public List<AssetDisposal> searchDisposals(AssetDisposalSearchCriteria searchCriteria, RequestInfo requestInfo) {
        return assetDisposeRepository.search(searchCriteria);
    }

    /**
     * Update the asset in the system using AssetService.
     *
     * @param requestInfo The RequestInfo object.
     * @param asset       The Asset object to update.
     */
    private void updateAssetInSystem(RequestInfo requestInfo, Asset asset) {
        AssetRequest assetRequest = AssetRequest.builder()
                .requestInfo(requestInfo)
                .asset(asset)
                .build();
        assetService.updateAssetInSystem(assetRequest);
        log.info("Updated asset ID: {} with status: {} and usage: {}",
                asset.getId(), asset.getAssetStatus(), asset.getAssetUsage());
    }
}
