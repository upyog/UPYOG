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

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
                                 MdmsDataRepository mdmsDataRepository, Producer producer, EnrichmentService enrichmentService, AssetCalculatorUtil assetCalculatorUtil,
                                 CalculatorConfig config) {
        this.assetRepository = assetRepository;
        this.depreciationDetailRepository = depreciationDetailRepository;
        this.mdmsDataRepository = mdmsDataRepository;
        this.producer = producer;
        this.assetCalculatorUtil = assetCalculatorUtil;
        depreciationReq = new DepreciationReq();
        auditDetails = new AuditDetails();

        // Initialize the static field for batch size from configuration
        BATCH_SIZE = config.getBatchSize();
    }

    /**
     * Calculates depreciation for assets based on legacy or non-legacy data.
     *
     * @param tenantId   Tenant identifier.
     * @param assetId    Asset identifier (can be null for bulk processing).
     * @param legacyData Flag indicating if legacy data should be processed.
     * @param uuid
     * @return Success message.
     */
    @Transactional
    public String calculateDepreciation(String tenantId, String assetId, boolean legacyData, String uuid) {
        LocalDate currentDate = LocalDate.now();
        int totalAssets;
        String message;
        String formattedDate = "";
        this.uuid = uuid;

        if(assetId != null ){
            legacyData = true;
            assetRepository.updateIsLegacyDataFlag(assetId);
        }
        // Determine total assets to process based on @legacyData param (legacy or non-legacy)
        if (legacyData) {
            totalAssets = assetRepository.countLegacyAssets(tenantId, assetId);
        } else {
            formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MM-dd"));
            totalAssets = assetRepository.countNonLegacyAssets(tenantId, assetId, formattedDate);
        }

        log.info("Total assets to be processed : {} and formated date : {}", totalAssets, formattedDate);

        int pageIndex = 0;

        // Process assets in batches with termination condition and additional logging
        while (pageIndex * BATCH_SIZE < totalAssets) {
            if (pageIndex > totalAssets / BATCH_SIZE + 1) {
                log.error("Potential infinite loop detected in batch processing. Terminating.");
                break;
            }
            Pageable pageable = PageRequest.of(pageIndex, BATCH_SIZE);
            List<Asset> assets = assetRepository.findAssetsForDepreciation(
                    tenantId, assetId, legacyData, currentDate.format(DateTimeFormatter.ofPattern("MM-dd")), pageable);
            log.info("Second time count Check : Total Assets fetched for processing is : {}",assets.size() );
            log.info("Processing batch {}", pageIndex + 1);
            for (Asset asset : assets) {
                processAssetDepreciation(asset, currentDate, legacyData);
            }

            pageIndex++;
        }
        message = CalculatorConstants.SUCCESS_MESSAGE;
        return message;
    }

    /**
     * Processes depreciation for a single asset.
     * @param asset Asset to process.
     * @param currentDate Current date.
     * @param legacyData Flag indicating if legacy data should be processed.
     */
    private void processAssetDepreciation(Asset asset, LocalDate currentDate, boolean legacyData) {
        int yearsElapsed;
        int remainingLife;

        if (legacyData) {
            log.info("Processing legacy Data, legacyData Flag = {}", legacyData );
            asset.setBookValue(BigDecimal.valueOf(asset.getOriginalBookValue()).doubleValue());
            //LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate());
            // Convert purchase date from milliseconds to LocalDate
            LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / CalculatorConstants.SECONDS_IN_A_DAY); // Convert milliseconds to days
            if (purchaseDate == null || asset.getLifeOfAsset() == null) {
                log.warn("Skipping asset due to missing mandatory fields: {}", asset.getId());
                return;
            }

            // Calculate asset life details
            int totalLife = Integer.parseInt(asset.getLifeOfAsset());
            LocalDate lifeEndDate = purchaseDate.plusYears(totalLife); // Last date of asset life

            if (lifeEndDate.isBefore(purchaseDate)) {
                log.warn("Invalid asset life for asset: {}, purchaseDate: {}, lifeEndDate: {}", asset.getId(), purchaseDate, lifeEndDate);
                return; // Skip processing for invalid life of asset
            }

            // Calculate depreciation for each year until current date
            LocalDate startDate = purchaseDate;
            LocalDate endDate = startDate.plusYears(1);
            yearsElapsed = currentDate.getYear() - purchaseDate.getYear();
            //remainingLife = Integer.parseInt(asset.getLifeOfAsset()) - yearsElapsed;

            if (currentDate.isAfter(lifeEndDate)) {
                log.info("Asset life expired. Calculating depreciation only until {}", lifeEndDate);
            }

            while (endDate.isBefore(currentDate) || endDate.isEqual(currentDate)) {
                // Ensure that `endDate` stays within both `currentDate` and `lifeEndDate`
                if (startDate.isAfter(lifeEndDate) || endDate.isAfter(lifeEndDate)) { break; }

                DepreciationRateDTO  depreciationRateDTO = fetchDepreciationRateAndMethod(asset.getAssetCategory(), asset.getPurchaseDate());

                if (depreciationRateDTO == null) {
                    log.warn("Skipping depreciation due to missing rate/method for asset: {}", asset.getId());
                    break; // Exit if no valid depreciation method is found
                }

                BigDecimal depreciationRate = depreciationRateDTO.getRate();
                BigDecimal depreciation = null;

                String depreciationMethod = depreciationRateDTO.getMethod();
                if(depreciationMethod.equals(CalculatorConstants.SLM)){
                    depreciation = calculateDepreciationValueSLM(asset, depreciationRate);
                }
                else if (CalculatorConstants.DBM.equals(depreciationMethod)) {
                    depreciation = calculateDepreciationValueDBM(asset, depreciationRate);
                } else {
                    log.warn("Unknown depreciation method: {}", depreciationMethod);
                    break;
                }

                BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
                asset.setBookValue(currentBookValue.doubleValue());
                saveDepreciationDetail(asset, startDate, endDate, depreciation, depreciationRate, true, depreciationMethod);

                startDate = endDate;
                endDate = startDate.plusYears(1);
            }

            asset.setIsLegacyData(false);
            assetRepository.save(asset);
        } else {

            log.info("Processing Non legacy Data, legacyData Flag = {}", legacyData );
            // Validate purchase date and anniversary date
            LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / CalculatorConstants.SECONDS_IN_A_DAY); // Convert milliseconds to days
            if (purchaseDate == null) {
                log.warn("Skipping asset with null purchase date: {}", asset.getId());
                return;
            }

            LocalDate anniversaryDate = LocalDate.of(currentDate.getYear(), purchaseDate.getMonth(), purchaseDate.getDayOfMonth());

            if (!anniversaryDate.equals(currentDate) || purchaseDate.equals(currentDate)) {
                log.warn("Anniversary date / purchase date not applicable for assetId: {} Skipping processing.", asset.getId());
                return; // Skip if not the anniversary date
            }
            DepreciationRateDTO  depreciationRateDTO = fetchDepreciationRateAndMethod(asset.getAssetCategory(), asset.getPurchaseDate());
            BigDecimal depreciationRate = depreciationRateDTO.getRate();
            BigDecimal depreciation = null;

            String depreciationMethod = depreciationRateDTO.getMethod();
            if (depreciationRate != null && depreciationRate.compareTo(BigDecimal.ZERO) > 0) {
                if (depreciationMethod.equals(CalculatorConstants.SLM)) {
                    depreciation = calculateDepreciationValueSLM(asset, depreciationRate);
                } else {
                    depreciation = calculateDepreciationValueDBM(asset, depreciationRate);
                }

                BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
                //currentBookValue = asset.setBookValue(BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation).doubleValue());
                asset.setBookValue(currentBookValue.doubleValue());

                saveDepreciationDetail(asset, purchaseDate, anniversaryDate, depreciation, depreciationRate, false, depreciationMethod);
                assetRepository.save(asset);
            }
            else {
                log.info("Depreciation not processed for assetId : {} as rate is Zero", asset.getId());
            }
        }
    }

    /**
     * Fetches depreciation rate for a given asset category.
     *
     * @param category     Asset category.
     * @param purchaseDate
     * @return Depreciation rate.
     */
    private DepreciationRateDTO fetchDepreciationRateAndMethod(String category, Long purchaseDate) {

        BigDecimal depreciationRate = null; // set static value for testing
        String depreciationMethod = null;

        Object[] result = mdmsDataRepository.findDepreciationRateByCategoryAndPurchaseDate(category.trim(), purchaseDate*CalculatorConstants.MILLISECONDS_IN_A_SECOND);
        try {
            log.debug("Fetched depreciation rate for rate, method: {}", result[0]);

            // Check if result is a nested array
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
     * Straight-Line Depreciation Method
     * Calculates depreciation value based on the rate and asset's original book value.
     * Ensures that depreciation does not reduce the asset's value below the minimum value.
     *
     * @param asset            The asset for which depreciation is being calculated.
     * @param depreciationRate The rate of depreciation (as a percentage).
     * @return The calculated depreciation value.
     */
    private BigDecimal calculateDepreciationValueSLM(Asset asset, BigDecimal depreciationRate) {

        // If the depreciation rate is null or 0, skip the calculation and return 0
        if (depreciationRate == null || BigDecimal.ZERO.compareTo(depreciationRate) == 0) {
            return BigDecimal.ZERO;
        }

        // Fetch the current book value of the asset
        BigDecimal bookValue = BigDecimal.valueOf(asset.getBookValue());

        // Fetch the minimum allowed book value for the asset
        BigDecimal minimumValue = BigDecimal.valueOf(asset.getMinimumValue());

        // Fetch the original book value of the asset (used for straight-line calculation)
        BigDecimal originalBookValue = BigDecimal.valueOf(asset.getOriginalBookValue());

        // Calculate the maximum depreciation allowed to ensure the book value does not drop below the minimum value
        BigDecimal maxDepreciation = bookValue.subtract(minimumValue);

        // Calculate the depreciation value based on the original book value and depreciation rate
        BigDecimal calculatedDepreciation = originalBookValue.multiply(depreciationRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Return the lesser of the calculated depreciation or the maximum allowable depreciation
        return calculatedDepreciation.min(maxDepreciation);
    }


    /**
     * Calculates depreciation value using the Declining Balance Method.
     * Depreciation is based on the current book value of the asset.
     * Ensures the book value does not drop below the minimum value.
     *
     * @param asset            The asset for which depreciation is being calculated.
     * @param depreciationRate The rate of depreciation (as a percentage).
     * @return The calculated depreciation value.
     */
    private BigDecimal calculateDepreciationValueDBM(Asset asset, BigDecimal depreciationRate) {

        // If the depreciation rate is null or 0, skip the calculation and return 0
        if (depreciationRate == null || BigDecimal.ZERO.compareTo(depreciationRate) == 0) {
            return BigDecimal.ZERO;
        }

        // Fetch the current book value of the asset
        BigDecimal bookValue = BigDecimal.valueOf(asset.getBookValue());

        // Fetch the minimum allowed book value for the asset
        BigDecimal minimumValue = BigDecimal.valueOf(asset.getMinimumValue());

        // Determine the maximum allowable depreciation to ensure the book value doesn't fall below the minimum value
        BigDecimal maxDepreciation = bookValue.subtract(minimumValue);

        // Calculate the depreciation for the current year
        BigDecimal calculatedDepreciation = bookValue.multiply(depreciationRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Return the smaller of the calculated depreciation and the maximum allowable depreciation
        return calculatedDepreciation.min(maxDepreciation);
    }

    /**
     * Saves depreciation details to the repository or updates existing records.
     * @param asset Asset to save details for.
     * @param startDate Depreciation start date.
     * @param endDate Depreciation end date.
     * @param depreciation Depreciation value.
     * @param depreciationRate Depreciation rate.
     * @param legacyData Flag indicating legacy data.
     */
    private void saveDepreciationDetail(Asset asset, LocalDate startDate, LocalDate endDate, BigDecimal depreciation, BigDecimal depreciationRate, boolean legacyData, String depreciationMethod) {
        // Check if a record already exists
        Optional<DepreciationDetail> existingDetail = depreciationDetailRepository.findByAssetIdAndFromDateAndToDate(
                asset.getId(), startDate, endDate);


        if (existingDetail.isPresent()) {
            if (legacyData) {
                // Overwrite the existing record for legacy data
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

                log.info("Pushing message to Kafka: topic={}, data={}", "update-depreciation",detail);
                producer.push("update-depreciation",depreciationReq);
                //depreciationDetailRepository.save(detail);
            } else {
                // Skip saving and log or throw an exception
                log.warn("Duplicate depreciation record found for asset {} from {} to {}. Skipping save as legacyData is false.",
                        asset.getId(), startDate, endDate);
            }
        } else {
            // Create a new record
            auditDetails = assetCalculatorUtil.getAuditDetails(uuid, true);
            DepreciationDetail detail = new DepreciationDetail();
            detail.setAssetId(asset.getId());
            detail.setFromDate(startDate);
            detail.setToDate(endDate);
            detail.setDepreciationValue(depreciation.doubleValue());
            detail.setBookValue(asset.getBookValue());
            detail.setCreatedAt(auditDetails.getCreatedTime());; // Assuming updatedAt is LocalDateTime
            detail.setCreatedBy(auditDetails.getCreatedBy());
            detail.setRate(depreciationRate.doubleValue());
            detail.setDepreciationMethod(depreciationMethod);
            detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());

            depreciationReq.setDepreciation(detail);
            log.info("Pushing message to Kafka: Create topic={}, data={}", "save-depreciation",detail);
            producer.push("save-depreciation",depreciationReq);
            //depreciationDetailRepository.save(detail);
        }
    }

}
