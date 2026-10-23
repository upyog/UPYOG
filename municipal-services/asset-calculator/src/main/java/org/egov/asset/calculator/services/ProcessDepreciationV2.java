package org.egov.asset.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.kafka.broker.Producer;
import org.egov.asset.calculator.repository.AssetRepository;
import org.egov.asset.calculator.repository.DepreciationDetailRepository;
import org.egov.asset.calculator.repository.MdmsDataRepository;
import org.egov.asset.calculator.utils.AssetCalculatorUtil;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.egov.asset.calculator.utils.dto.DepreciationRateDTO;
import org.egov.asset.calculator.web.models.AuditDetails;
import org.egov.asset.calculator.web.models.DepreciationDetail;
import org.egov.asset.calculator.web.models.DepreciationReq;
import org.egov.asset.calculator.web.models.contract.Asset;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * ProcessDepreciationV2 - Enhanced depreciation processing service
 *
 * This service handles two distinct depreciation calculation modes:
 * 1. Legacy Mode: Complete historical depreciation calculation from purchase date
 * 2. Regular Mode: Anniversary-based annual depreciation calculation
 *
 * Key Features:
 * - Batch processing for large datasets
 * - Support for both SLM (Straight Line Method) and DBM (Declining Balance Method)
 * - Kafka integration for audit trail
 * - Comprehensive error handling and logging
 */
@Service
@Slf4j
public class ProcessDepreciationV2 {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    private final AssetRepository assetRepository;
    private final DepreciationDetailRepository depreciationDetailRepository;
    private final MdmsDataRepository mdmsDataRepository;
    private final Producer producer;
    private final AssetCalculatorUtil assetCalculatorUtil;
    private final int batchSize;
    private final DepreciationReq depreciationReq;
    private AuditDetails auditDetails;
    private String uuid;

    @Autowired
    public ProcessDepreciationV2(AssetRepository assetRepository,
                                 DepreciationDetailRepository depreciationDetailRepository,
                                 MdmsDataRepository mdmsDataRepository, Producer producer,
                                 EnrichmentService enrichmentService, AssetCalculatorUtil assetCalculatorUtil,
                                 CalculatorConfig config) {
        this.assetRepository = assetRepository;
        this.depreciationDetailRepository = depreciationDetailRepository;
        this.mdmsDataRepository = mdmsDataRepository;
        this.producer = producer;
        this.assetCalculatorUtil = assetCalculatorUtil;
        this.batchSize = config.getBatchSize();
        this.depreciationReq = new DepreciationReq();
        this.auditDetails = new AuditDetails();
    }

    /**
     * Main entry point for depreciation calculation.
     * Handles both legacy data (historical catch-up) and regular anniversary-based processing.
     *
     * @param tenantId - The tenant/city identifier
     * @param assetId - Specific asset ID (if null, processes all assets)
     * @param legacyData - Flag indicating if this is legacy data processing
     * @param uuid - Unique identifier for audit trail
     * @return Success message after processing
     */
    @Transactional
    public String calculateDepreciation(String tenantId, String assetId, boolean legacyData, String uuid) {
        LocalDate currentDate = LocalDate.now(SYSTEM_ZONE);
        int totalAssets;
        this.uuid = uuid;

        // If specific assetId is provided, treat it as legacy data for complete recalculation
        if(assetId != null ){
            legacyData = true;
            // Mark asset as legacy to trigger full depreciation history calculation
            assetRepository.updateIsLegacyDataFlag(assetId);
        }

        // Count total assets to determine batch processing scope
        if (legacyData) {
            // Legacy: Process all assets that need historical depreciation calculation
            totalAssets = assetRepository.countLegacyAssets(tenantId, assetId);
        } else {
            // Regular: Process only assets whose anniversary date is today
            totalAssets = assetRepository.countNonLegacyAssets(tenantId, assetId);
        }

        log.info("Total assets to be processed: {}", totalAssets);

        int pageIndex = 0;

        // Process assets in batches to avoid memory issues with large datasets
        while (pageIndex * batchSize < totalAssets) {
            // Safety check to prevent infinite loops in case of data inconsistency
            if (pageIndex > totalAssets / batchSize + 1) {
                log.error("Potential infinite loop detected in batch processing. Terminating.");
                break;
            }

            // Create pagination object for current batch
            Pageable pageable = PageRequest.of(pageIndex, batchSize);

            // Fetch current batch of assets from database
            List<Asset> allAssets = assetRepository.findAssetsForDepreciation(
                    tenantId, assetId, legacyData, pageable);

            // Apply business logic filtering based on processing type
            List<Asset> assetsToProcess;
            if (!legacyData) {
                // For regular processing: Only process assets whose purchase anniversary is today
                // This ensures depreciation is calculated exactly once per year on the anniversary
                assetsToProcess = filterAssetsForAnniversary(allAssets, currentDate);
                log.info("Filtered {} assets out of {} for anniversary processing",
                    assetsToProcess.size(), allAssets.size());
            } else {
                // For legacy processing: Process all assets in the batch
                assetsToProcess = allAssets;
            }

            log.info("Processing batch {} with {} assets", pageIndex + 1, assetsToProcess.size());

            // Process each asset in the current batch
            for (Asset asset : assetsToProcess) {
                processAssetDepreciation(asset, currentDate, legacyData);
            }

            pageIndex++;
        }

        return CalculatorConstants.SUCCESS_MESSAGE;
    }

    /**
     * Filters assets to only include those whose purchase anniversary is today.
     * This ensures depreciation is calculated exactly once per year on the correct date.
     *
     * @param assets - List of assets to filter
     * @param currentDate - Today's date
     * @return Filtered list of assets whose anniversary is today
     */
    private List<Asset> filterAssetsForAnniversary(List<Asset> assets, LocalDate currentDate) {
        return assets.stream()
                .filter(asset -> isAnniversaryDate(asset, currentDate))
                .toList();
    }

    /**
     * Determines if today is the anniversary of the asset's purchase date.
     * Anniversary logic: Same month and day as purchase, but different year.
     *
     * Example: Asset purchased on 2020-03-15, today is 2024-03-15 → true
     *          Asset purchased on 2020-03-15, today is 2024-03-16 → false
     *          Asset purchased on 2024-03-15, today is 2024-03-15 → false (same year)
     *
     * @param asset - The asset to check
     * @param currentDate - Today's date
     * @return true if today is the anniversary and not the same year as purchase
     */
    private boolean isAnniversaryDate(Asset asset, LocalDate currentDate) {
        if (asset.getPurchaseDate() == null) {
            log.warn("Asset {} has null purchase date", asset.getId());
            return false;
        }

        try {
            // Convert stored epoch milliseconds to LocalDate for comparison
            LocalDate purchaseDate = Instant.ofEpochMilli(asset.getPurchaseDate())
                    .atZone(SYSTEM_ZONE)
                    .toLocalDate();

            // Check if month and day match (anniversary condition)
            boolean isAnniversary = purchaseDate.getMonthValue() == currentDate.getMonthValue() &&
                                   purchaseDate.getDayOfMonth() == currentDate.getDayOfMonth();

            // Exclude same year to prevent processing on purchase date itself
            boolean isSameYear = purchaseDate.getYear() == currentDate.getYear();

            log.debug("Asset {}: purchase={}, current={}, anniversary={}, sameYear={}",
                asset.getId(), purchaseDate, currentDate, isAnniversary, isSameYear);

            // Return true only if it's anniversary date but not the same year
            return isAnniversary && !isSameYear;

        } catch (Exception e) {
            log.error("Error processing date for asset {}: {}", asset.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Core method to process depreciation for a single asset.
     * Handles two distinct processing modes:
     * 1. Legacy Mode: Calculates complete depreciation history from purchase date to current date
     * 2. Regular Mode: Calculates depreciation for current anniversary year only
     *
     * @param asset - The asset to process
     * @param currentDate - Current processing date
     * @param legacyData - Flag indicating processing mode
     */
    private void processAssetDepreciation(Asset asset, LocalDate currentDate, boolean legacyData) {
        if (legacyData) {
            processLegacyAssetDepreciation(asset, currentDate);
        } else {
            processRegularAssetDepreciation(asset, currentDate);
        }
    }

    private void processLegacyAssetDepreciation(Asset asset, LocalDate currentDate) {
        log.info("Processing legacy Data, legacyData Flag = true");

        // Reset book value to original for complete recalculation
        asset.setBookValue(BigDecimal.valueOf(asset.getOriginalBookValue()).doubleValue());

        LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / CalculatorConstants.SECONDS_IN_A_DAY);
        if (asset.getLifeOfAsset() == null) {
            log.warn("Skipping asset due to missing mandatory fields: {}", asset.getId());
            return;
        }

        int totalLife = Integer.parseInt(asset.getLifeOfAsset());
        LocalDate lifeEndDate = purchaseDate.plusYears(totalLife);

        if (lifeEndDate.isBefore(purchaseDate)) {
            log.warn("Invalid asset life for asset: {}, purchaseDate: {}, lifeEndDate: {}",
                asset.getId(), purchaseDate, lifeEndDate);
            return;
        }

        if (currentDate.isAfter(lifeEndDate)) {
            log.info("Asset life expired. Calculating depreciation only until {}", lifeEndDate);
        }

        processLegacyYearByYear(asset, purchaseDate, currentDate, lifeEndDate);

        asset.setIsLegacyData(false);
        assetRepository.save(asset);
    }

    private void processLegacyYearByYear(Asset asset, LocalDate purchaseDate, LocalDate currentDate,
                                        LocalDate lifeEndDate) {
        LocalDate startDate = purchaseDate;
        LocalDate endDate = startDate.plusYears(1);
        boolean continueProcessing = true;

        while ((endDate.isBefore(currentDate) || endDate.isEqual(currentDate)) && continueProcessing) {
            continueProcessing = processLegacyDepreciationYear(asset, startDate, endDate, lifeEndDate);
            if (continueProcessing) {
                startDate = endDate;
                endDate = startDate.plusYears(1);
            }
        }
    }

    private boolean processLegacyDepreciationYear(Asset asset, LocalDate startDate, LocalDate endDate,
                                                  LocalDate lifeEndDate) {
        if (startDate.isAfter(lifeEndDate) || endDate.isAfter(lifeEndDate)) {
            return false;
        }

        DepreciationRateDTO depreciationRateDTO = fetchDepreciationRateAndMethod(
            asset.getAssetCategory(), asset.getPurchaseDate());

        if (depreciationRateDTO == null) {
            log.warn("Skipping depreciation due to missing rate/method for asset: {}", asset.getId());
            return false;
        }

        BigDecimal depreciation = calculateDepreciationForMethod(asset, depreciationRateDTO);
        if (depreciation == null) {
            return false;
        }

        BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
        asset.setBookValue(currentBookValue.doubleValue());

        saveDepreciationDetail(asset, startDate, endDate, depreciation, depreciationRateDTO.getRate(),
            true, depreciationRateDTO.getMethod());
        return true;
    }

    private void processRegularAssetDepreciation(Asset asset, LocalDate currentDate) {
        log.info("Processing Non legacy Data, legacyData Flag = false");

        LocalDate purchaseDate = Instant.ofEpochMilli(asset.getPurchaseDate())
                .atZone(SYSTEM_ZONE)
                .toLocalDate();

        LocalDate anniversaryDate = LocalDate.of(currentDate.getYear(),
            purchaseDate.getMonth(), purchaseDate.getDayOfMonth());

        if (!anniversaryDate.equals(currentDate) || purchaseDate.equals(currentDate)) {
            log.warn("Anniversary date / purchase date not applicable for assetId: {} Skipping processing.",
                asset.getId());
            return;
        }

        DepreciationRateDTO depreciationRateDTO = fetchDepreciationRateAndMethod(
            asset.getAssetCategory(), asset.getPurchaseDate());
        BigDecimal depreciationRate = depreciationRateDTO.getRate();

        if (depreciationRate == null || depreciationRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Depreciation not processed for assetId : {} as rate is Zero", asset.getId());
            return;
        }

        BigDecimal depreciation = calculateDepreciationForMethod(asset, depreciationRateDTO);
        if (depreciation == null) {
            return;
        }

        BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
        asset.setBookValue(currentBookValue.doubleValue());

        saveDepreciationDetail(asset, purchaseDate, anniversaryDate, depreciation,
            depreciationRate, false, depreciationRateDTO.getMethod());
        assetRepository.save(asset);
    }

    private BigDecimal calculateDepreciationForMethod(Asset asset, DepreciationRateDTO depreciationRateDTO) {
        String depreciationMethod = depreciationRateDTO.getMethod();
        BigDecimal depreciationRate = depreciationRateDTO.getRate();

        if (CalculatorConstants.SLM.equals(depreciationMethod)) {
            return calculateDepreciationValueSLM(asset, depreciationRate);
        }
        if (CalculatorConstants.DBM.equals(depreciationMethod)) {
            return calculateDepreciationValueDBM(asset, depreciationRate);
        }

        log.warn("Unknown depreciation method: {}", depreciationMethod);
        return null;
    }

    /**
     * Fetches depreciation rate and method from master data based on asset category and purchase date.
     * The method queries MDMS (Master Data Management Service) for applicable rates.
     *
     * @param category - Asset category (e.g., "Vehicle", "Building")
     * @param purchaseDate - Asset purchase date in epoch milliseconds
     * @return DepreciationRateDTO containing rate and method, or null if not found
     */
    private DepreciationRateDTO fetchDepreciationRateAndMethod(String category, Long purchaseDate) {
        BigDecimal depreciationRate = null;
        String depreciationMethod = null;

        Object[] result = mdmsDataRepository.findDepreciationRateByCategoryAndPurchaseDate(
            category.trim(), purchaseDate * CalculatorConstants.MILLISECONDS_IN_A_SECOND);
        try {
            log.debug("Fetched depreciation rate for rate, method: {}", result[0]);

            if (result.length > 0 && result[0] instanceof Object[] innerArray) {
                depreciationRate = innerArray[0] instanceof BigDecimal bigdecimal ? bigdecimal : null;
                depreciationMethod = innerArray[1] instanceof String string ? string : null;

                if (depreciationRate == null || depreciationMethod == null) {
                    throw new IllegalArgumentException("Depreciation details are incomplete.");
                }
                log.info("Depreciation Rate is : {} and method is :{} ", depreciationRate, depreciationMethod);
                return new DepreciationRateDTO(depreciationRate, depreciationMethod);
            }
        }
        catch (Exception e) {
            log.error("Unexpected error while fetching depreciation rate: {}", e.getMessage(), e);
            throw new CustomException(CalculatorConstants.DEPRECIATION_DATA_ERROR_KEY,
                    CalculatorConstants.DEPRECIATION_DATA_ERROR_MSG);
        }

        log.info("Depreciation Rate is : {} and method is :{} ", depreciationRate, depreciationMethod);
        return new DepreciationRateDTO(depreciationRate, depreciationMethod);
    }

    /**
     * Calculates depreciation using Straight Line Method (SLM).
     * SLM Formula: (Original Value × Rate%) / 100
     * The depreciation amount remains constant each year.
     *
     * @param asset - Asset object containing values
     * @param depreciationRate - Annual depreciation rate as percentage
     * @return Calculated depreciation amount, capped at maximum allowable depreciation
     */
    private BigDecimal calculateDepreciationValueSLM(Asset asset, BigDecimal depreciationRate) {
        if (depreciationRate == null || BigDecimal.ZERO.compareTo(depreciationRate) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal bookValue = BigDecimal.valueOf(asset.getBookValue());
        BigDecimal minimumValue = BigDecimal.valueOf(asset.getMinimumValue());
        BigDecimal originalBookValue = BigDecimal.valueOf(asset.getOriginalBookValue());

        // Maximum depreciation cannot reduce book value below minimum value
        BigDecimal maxDepreciation = bookValue.subtract(minimumValue);

        // SLM: Fixed percentage of original value
        BigDecimal calculatedDepreciation = originalBookValue.multiply(depreciationRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Return the lesser of calculated depreciation or maximum allowable
        return calculatedDepreciation.min(maxDepreciation);
    }

    /**
     * Calculates depreciation using Declining Balance Method (DBM).
     * DBM Formula: (Current Book Value × Rate%) / 100
     * The depreciation amount decreases each year as book value decreases.
     *
     * @param asset - Asset object containing values
     * @param depreciationRate - Annual depreciation rate as percentage
     * @return Calculated depreciation amount, capped at maximum allowable depreciation
     */
    private BigDecimal calculateDepreciationValueDBM(Asset asset, BigDecimal depreciationRate) {
        if (depreciationRate == null || BigDecimal.ZERO.compareTo(depreciationRate) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal bookValue = BigDecimal.valueOf(asset.getBookValue());
        BigDecimal minimumValue = BigDecimal.valueOf(asset.getMinimumValue());

        // Maximum depreciation cannot reduce book value below minimum value
        BigDecimal maxDepreciation = bookValue.subtract(minimumValue);

        // DBM: Percentage of current book value
        BigDecimal calculatedDepreciation = bookValue.multiply(depreciationRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Return the lesser of calculated depreciation or maximum allowable
        return calculatedDepreciation.min(maxDepreciation);
    }

    /**
     * Saves or updates depreciation detail record in the database via Kafka.
     * Handles both new records and updates to existing records for legacy processing.
     *
     * @param asset - The asset being processed
     * @param startDate - Depreciation period start date
     * @param endDate - Depreciation period end date
     * @param depreciation - Calculated depreciation amount
     * @param depreciationRate - Applied depreciation rate
     * @param legacyData - Flag indicating if this is legacy processing
     * @param depreciationMethod - Method used (SLM/DBM)
     */
    private void saveDepreciationDetail(Asset asset, LocalDate startDate, LocalDate endDate,
                                       BigDecimal depreciation, BigDecimal depreciationRate,
                                       boolean legacyData, String depreciationMethod) {
        Optional<DepreciationDetail> existingDetail = depreciationDetailRepository
            .findByAssetIdAndFromDateAndToDate(asset.getId(), startDate, endDate);

        if (existingDetail.isPresent()) {
            if (legacyData) {
                auditDetails = assetCalculatorUtil.getAuditDetails(uuid, false);
                DepreciationDetail detail = existingDetail.get();
                detail.setDepreciationValue(depreciation.doubleValue());
                detail.setBookValue(asset.getBookValue());
                detail.setRate(depreciationRate.doubleValue());
                detail.setDepreciationMethod(depreciationMethod);
                detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());
                detail.setUpdatedAt(auditDetails.getLastModifiedTime());
                detail.setUpdatedBy(auditDetails.getLastModifiedBy());
                depreciationReq.setDepreciation(detail);

                log.info("Pushing message to Kafka: topic={}, data={}", "update-depreciation", detail);
                producer.push("update-depreciation", depreciationReq);
            } else {
                log.warn("Duplicate depreciation record found for asset {} from {} to {}. Skipping save as legacyData is false.",
                        asset.getId(), startDate, endDate);
            }
        } else {
            auditDetails = assetCalculatorUtil.getAuditDetails(uuid, true);
            DepreciationDetail detail = new DepreciationDetail();
            detail.setAssetId(asset.getId());
            detail.setFromDate(startDate);
            detail.setToDate(endDate);
            detail.setDepreciationValue(depreciation.doubleValue());
            detail.setBookValue(asset.getBookValue());
            detail.setCreatedAt(auditDetails.getCreatedTime());
            detail.setCreatedBy(auditDetails.getCreatedBy());
            detail.setRate(depreciationRate.doubleValue());
            detail.setDepreciationMethod(depreciationMethod);
            detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());

            depreciationReq.setDepreciation(detail);

            log.info("Pushing message to Kafka: Create topic={}, data={}", "save-depreciation", detail);
            producer.push("save-depreciation", depreciationReq);
        }
    }
}
