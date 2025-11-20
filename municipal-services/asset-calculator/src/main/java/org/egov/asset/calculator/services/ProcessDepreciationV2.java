package org.egov.asset.calculator.services;

import lombok.AllArgsConstructor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
@AllArgsConstructor
@Slf4j
public class ProcessDepreciationV2 {

    private final AssetRepository assetRepository;
    private final DepreciationDetailRepository depreciationDetailRepository;
    private final MdmsDataRepository mdmsDataRepository;
    private final Producer producer;
    private final AssetCalculatorUtil assetCalculatorUtil;
    private DepreciationReq depreciationReq ;

    private static int BATCH_SIZE;
    private AuditDetails auditDetails ;
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
        depreciationReq = new DepreciationReq();
        auditDetails = new AuditDetails();

        BATCH_SIZE = config.getBatchSize();
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
        LocalDate currentDate = LocalDate.now();
        int totalAssets;
        String message;
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
        while (pageIndex * BATCH_SIZE < totalAssets) {
            // Safety check to prevent infinite loops in case of data inconsistency
            if (pageIndex > totalAssets / BATCH_SIZE + 1) {
                log.error("Potential infinite loop detected in batch processing. Terminating.");
                break;
            }

            // Create pagination object for current batch
            Pageable pageable = PageRequest.of(pageIndex, BATCH_SIZE);

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

        message = CalculatorConstants.SUCCESS_MESSAGE;
        return message;
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
                .collect(Collectors.toList());
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
                    .atZone(ZoneId.systemDefault())
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
        int yearsElapsed;
        int remainingLife;

        if (legacyData) {
            log.info("Processing legacy Data, legacyData Flag = {}", legacyData );

            // Reset book value to original for complete recalculation
            // This ensures accurate historical depreciation calculation
            asset.setBookValue(BigDecimal.valueOf(asset.getOriginalBookValue()).doubleValue());

            // Convert stored epoch milliseconds to LocalDate for date calculations
            LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / CalculatorConstants.SECONDS_IN_A_DAY);
            if (purchaseDate == null || asset.getLifeOfAsset() == null) {
                log.warn("Skipping asset due to missing mandatory fields: {}", asset.getId());
                return;
            }

            // Calculate when the asset's useful life ends
            int totalLife = Integer.parseInt(asset.getLifeOfAsset());
            LocalDate lifeEndDate = purchaseDate.plusYears(totalLife);

            // Validate asset life calculation
            if (lifeEndDate.isBefore(purchaseDate)) {
                log.warn("Invalid asset life for asset: {}, purchaseDate: {}, lifeEndDate: {}",
                    asset.getId(), purchaseDate, lifeEndDate);
                return;
            }

            // Initialize year-by-year depreciation calculation
            // Each iteration represents one year of depreciation
            LocalDate startDate = purchaseDate;
            LocalDate endDate = startDate.plusYears(1);
            yearsElapsed = currentDate.getYear() - purchaseDate.getYear();

            // Stop depreciation calculation if asset life has expired
            if (currentDate.isAfter(lifeEndDate)) {
                log.info("Asset life expired. Calculating depreciation only until {}", lifeEndDate);
            }

            // Process depreciation year by year from purchase date to current date
            while (endDate.isBefore(currentDate) || endDate.isEqual(currentDate)) {
                // Stop if we've reached the end of asset's useful life
                if (startDate.isAfter(lifeEndDate) || endDate.isAfter(lifeEndDate)) {
                    break;
                }

                // Fetch depreciation rate and method from master data
                DepreciationRateDTO depreciationRateDTO = fetchDepreciationRateAndMethod(
                    asset.getAssetCategory(), asset.getPurchaseDate());

                if (depreciationRateDTO == null) {
                    log.warn("Skipping depreciation due to missing rate/method for asset: {}", asset.getId());
                    break;
                }

                BigDecimal depreciationRate = depreciationRateDTO.getRate();
                BigDecimal depreciation = null;

                // Apply appropriate depreciation calculation method
                String depreciationMethod = depreciationRateDTO.getMethod();
                if(depreciationMethod.equals(CalculatorConstants.SLM)){
                    // Straight Line Method: Fixed percentage of original value
                    depreciation = calculateDepreciationValueSLM(asset, depreciationRate);
                }
                else if (CalculatorConstants.DBM.equals(depreciationMethod)) {
                    // Declining Balance Method: Percentage of current book value
                    depreciation = calculateDepreciationValueDBM(asset, depreciationRate);
                } else {
                    log.warn("Unknown depreciation method: {}", depreciationMethod);
                    break;
                }

                // Update asset's book value after depreciation
                BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
                asset.setBookValue(currentBookValue.doubleValue());

                // Save depreciation record for this year
                saveDepreciationDetail(asset, startDate, endDate, depreciation, depreciationRate, true, depreciationMethod);

                // Move to next year
                startDate = endDate;
                endDate = startDate.plusYears(1);
            }

            // Mark asset as processed (no longer legacy)
            asset.setIsLegacyData(false);
            assetRepository.save(asset);

        } else {
            // Regular processing mode: Calculate depreciation for current anniversary year only
            log.info("Processing Non legacy Data, legacyData Flag = {}", legacyData);

            // Convert purchase date from milliseconds to LocalDate
            LocalDate purchaseDate = Instant.ofEpochMilli(asset.getPurchaseDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (purchaseDate == null) {
                log.warn("Skipping asset with null purchase date: {}", asset.getId());
                return;
            }

            // Calculate this year's anniversary date
            LocalDate anniversaryDate = LocalDate.of(currentDate.getYear(),
                purchaseDate.getMonth(), purchaseDate.getDayOfMonth());

            // Additional validation (this should already be filtered, but keeping for safety)
            if (!anniversaryDate.equals(currentDate) || purchaseDate.equals(currentDate)) {
                log.warn("Anniversary date / purchase date not applicable for assetId: {} Skipping processing.",
                    asset.getId());
                return;
            }

            // Fetch current depreciation rate and method
            DepreciationRateDTO depreciationRateDTO = fetchDepreciationRateAndMethod(
                asset.getAssetCategory(), asset.getPurchaseDate());
            BigDecimal depreciationRate = depreciationRateDTO.getRate();
            BigDecimal depreciation = null;

            // Calculate depreciation if rate is valid
            String depreciationMethod = depreciationRateDTO.getMethod();
            if (depreciationRate != null && depreciationRate.compareTo(BigDecimal.ZERO) > 0) {
                if (depreciationMethod.equals(CalculatorConstants.SLM)) {
                    depreciation = calculateDepreciationValueSLM(asset, depreciationRate);
                } else {
                    depreciation = calculateDepreciationValueDBM(asset, depreciationRate);
                }

                // Update asset's book value
                BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
                asset.setBookValue(currentBookValue.doubleValue());

                // Save depreciation record for this anniversary year
                saveDepreciationDetail(asset, purchaseDate, anniversaryDate, depreciation,
                    depreciationRate, false, depreciationMethod);
                assetRepository.save(asset);
            }
            else {
                log.info("Depreciation not processed for assetId : {} as rate is Zero", asset.getId());
            }
        }
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

        // Query master data repository for depreciation configuration
        // Convert milliseconds to seconds for database query
        Object[] result = mdmsDataRepository.findDepreciationRateByCategoryAndPurchaseDate(
            category.trim(), purchaseDate*CalculatorConstants.MILLISECONDS_IN_A_SECOND);
        try {
            log.debug("Fetched depreciation rate for rate, method: {}", result[0]);

            // Parse the complex result structure from database
            if (((Object[]) result).length > 0 && ((Object[]) result)[0] instanceof Object[]) {
                Object[] innerArray = (Object[]) ((Object[]) result)[0];
                depreciationRate = (innerArray[0] instanceof BigDecimal) ? (BigDecimal) innerArray[0] : null;
                depreciationMethod = (innerArray[1] instanceof String) ? (String) innerArray[1] : null;

                if (depreciationRate == null || depreciationMethod == null) {
                    throw new IllegalArgumentException("Depreciation details are incomplete.");
                }
                log.info("Depreciation Rate is : {} and method is :{} ", depreciationRate, depreciationMethod);
                return new DepreciationRateDTO(depreciationRate, depreciationMethod);
            }
        }
        catch (Exception e) {
            log.error("Unexpected error while fetching depreciation rate: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while processing depreciation data.", e);
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
        // Check if depreciation record already exists for this period
        Optional<DepreciationDetail> existingDetail = depreciationDetailRepository
            .findByAssetIdAndFromDateAndToDate(asset.getId(), startDate, endDate);

        if (existingDetail.isPresent()) {
            // Record exists - update only if legacy processing
            if (legacyData) {
                auditDetails = assetCalculatorUtil.getAuditDetails(uuid, false);
                DepreciationDetail detail = existingDetail.get();
                detail.setDepreciationValue(depreciation.doubleValue());
                detail.setBookValue(asset.getBookValue());
                detail.setRate(depreciationRate.doubleValue());
                detail.setDepreciationMethod(depreciationMethod);
                // Calculate old book value (before depreciation)
                detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());
                detail.setUpdatedAt(auditDetails.getLastModifiedTime());
                detail.setUpdatedBy(auditDetails.getLastModifiedBy());
                depreciationReq.setDepreciation(detail);

                // Send update message to Kafka for persistence
                log.info("Pushing message to Kafka: topic={}, data={}", "update-depreciation",detail);
                producer.push("update-depreciation",depreciationReq);
            } else {
                // For regular processing, skip if record already exists to prevent duplicates
                log.warn("Duplicate depreciation record found for asset {} from {} to {}. Skipping save as legacyData is false.",
                        asset.getId(), startDate, endDate);
            }
        } else {
            // Create new depreciation record
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
            // Calculate old book value (before depreciation)
            detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());

            depreciationReq.setDepreciation(detail);

            // Send create message to Kafka for persistence
            log.info("Pushing message to Kafka: Create topic={}, data={}", "save-depreciation",detail);
            producer.push("save-depreciation",depreciationReq);
        }
    }
}