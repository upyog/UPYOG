package upyog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upyog.config.EstateConfiguration;
import upyog.config.ServiceConstants;
import upyog.util.BillingPeriodUtil;
import upyog.util.EstateUtil;
import upyog.util.IdGenUtil;
import upyog.web.models.*;

import java.util.List;

/**
 * Service responsible for enriching estate management requests with generated IDs and audit details.
 * Handles enrichment for both asset and allotment creation requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrichmentService {

    @Autowired
    private IdGenUtil idGenUtil;
    
    @Autowired
    private EstateConfiguration estateConfiguration;

    /**
     * Enriches asset creation request with system-generated IDs and default values.
     * 
     * <p>This method performs the following enrichments:
     * <ul>
     *   <li>Generates a unique asset ID (UUID)</li>
     *   <li>Generates an estate number using the ID generation service</li>
     *   <li>Sets default asset status to 'active'</li>
     *   <li>Validates that asset type is provided</li>
     *   <li>Sets audit details with creation timestamp and user</li>
     * </ul>
     * </p>
     *
     * @param assetRequest The AssetRequest to be enriched
     * @throws CustomException with code ASSET_TYPE_MISSING if asset type is not provided
     */
    public void enrichAssetRequest(AssetRequest assetRequest) {
        Asset asset = assetRequest.getAssets().get(0);
        
        // Validate mandatory fields
        if (asset.getAssetType() == null || asset.getAssetType().isEmpty()) {
            log.error("Asset enrichment failed: Asset type is mandatory");
            throw new CustomException(
                    "ASSET_TYPE_MISSING", 
                    "Asset type is mandatory for asset creation"
            );
        }
        
        // Generate asset ID (UUID)
        String assetId = EstateUtil.getRandomUUID();
        asset.setAssetId(assetId);
        log.debug("Generated Asset ID (UUID): {}", assetId);
        
        // Generate estate number from ID Gen service
        List<String> estateNumbers = getIdList(
                assetRequest.getRequestInfo(),
                asset.getTenantId(),
                estateConfiguration.getEstateAssetIdName(),
                estateConfiguration.getEstateAssetIdFormat(),
                1
        );
        
        String estateNo = estateNumbers.get(0);
        asset.setEstateNo(estateNo);
        log.info("Generated Estate Number: {}", estateNo);
        
        // Set default status
        asset.setAssetStatus("active");
        asset.setAssetAllotmentStatus(ServiceConstants.STATUS_PENDING_FOR_ALLOTMENT);
        
        // Set audit details
        String userUuid = assetRequest.getRequestInfo().getUserInfo().getUuid();
        asset.setAuditDetails(EstateUtil.getAuditDetails(userUuid, true));
        log.debug("Asset enrichment completed for Asset ID: {}, Estate No: {}", assetId, estateNo);
    }

    /**
     * Enriches allotment creation request with system-generated IDs and audit details.
     * 
     * <p>This method performs the following enrichments for each allotment:
     * <ul>
     *   <li>Generates a unique allotment ID (UUID)</li>
     *   <li>Sets audit details with creation timestamp and user</li>
     * </ul>
     * </p>
     *
     * @param request The AllotmentRequest to be enriched
     */
    public void enrichAllotmentRequest(AllotmentRequest request) {
        String userUuid = request.getRequestInfo().getUserInfo().getUuid();
        
        request.getAllotments().forEach(allotment -> {
            // Generate allotment ID using UUID
            String allotmentId = EstateUtil.getRandomUUID();
            allotment.setAllotmentId(allotmentId);
            log.info("Generated Allotment ID: {}", allotmentId);
            
            // Generate allotment number from ID Gen service
            List<String> allotmentNumbers = getIdList(
                    request.getRequestInfo(),
                    allotment.getTenantId(),
                    estateConfiguration.getEstateAllotmentNoName(),
                    estateConfiguration.getEstateAllotmentNoFormat(),
                    1
            );
            String allotmentNo = allotmentNumbers.get(0);
            allotment.setAllotmentNo(allotmentNo);
            log.info("Generated Allotment Number: {}", allotmentNo);
            
            // Set default status to PENDING_FOR_PAYMENT
            allotment.setStatus(ServiceConstants.STATUS_PENDING_FOR_PAYMENT);

            // Calculate and set initial due date based on agreement start date and billing cycle
            if (allotment.getDueDate() == null) {
                try {
                    BillingCycle billingCycle = BillingCycle.valueOf(allotment.getBillingCycle().toUpperCase(java.util.Locale.ROOT));
                    BillingPeriod billingPeriod = BillingPeriodUtil.getBillingPeriod(
                            allotment.getAgreementStartDate(),
                            billingCycle,
                            allotment.getAgreementEndDate()
                    );
                    allotment.setDueDate(billingPeriod.getPeriodTo());
                    log.info("Calculated initial due date: {}", allotment.getDueDate());
                } catch (Exception e) {
                    log.error("Failed to calculate initial due date for allotment: {}", e.getMessage());
                }
            }
            
            // Set audit details
            allotment.setAuditDetails(EstateUtil.getAuditDetails(userUuid, true));
            log.debug("Allotment enrichment completed for Allotment ID: {}", allotmentId);
        });
    }

    /**
     * Fetches a list of generated IDs from the ID Generation service.
     *
     * @param requestInfo The request metadata
     * @param tenantId The tenant identifier
     * @param idKey The ID key configured in ID Gen service
     * @param idFormat The format pattern for ID generation
     * @param count Number of IDs to generate
     * @return List of generated IDs
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, 
                                   String idKey, String idFormat, int count) {
        log.debug("Requesting {} ID(s) from ID Gen service for tenant: {}, key: {}", 
                count, tenantId, idKey);
        
        List<String> idResponses = idGenUtil.getIdList(
                requestInfo, 
                tenantId, 
                idKey, 
                idFormat, 
                count
        );
        
        log.debug("Received {} ID(s) from ID Gen service", idResponses.size());
        return idResponses;
    }
}
