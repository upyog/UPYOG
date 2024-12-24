package org.egov.asset.calculator.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.repository.AssetRepository;
import org.egov.asset.calculator.repository.DepreciationDetailRepository;
import org.egov.asset.calculator.repository.MdmsDataRepository;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.egov.asset.calculator.web.models.DepreciationDetail;
import org.egov.asset.calculator.web.models.contract.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static int BATCH_SIZE;

    @Autowired
    public ProcessDepreciationV2(AssetRepository assetRepository,
                                 DepreciationDetailRepository depreciationDetailRepository,
                                 MdmsDataRepository mdmsDataRepository,
                                 CalculatorConfig config) {
        this.assetRepository = assetRepository;
        this.depreciationDetailRepository = depreciationDetailRepository;
        this.mdmsDataRepository = mdmsDataRepository;

        // Initialize the static field
        BATCH_SIZE = config.getBatchSize();
    }

    @Transactional
    public String calculateDepreciation(String tenantId, String assetId, boolean legacyData) {
        LocalDate currentDate = LocalDate.now();
        int totalAssets;
        String message;

        // Determine total assets to process
        if (legacyData) {
            totalAssets = assetRepository.countLegacyAssets(tenantId, assetId);
        } else {
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MM-dd"));
            totalAssets = assetRepository.countNonLegacyAssets(tenantId, assetId, formattedDate);
        }

        int pageIndex = 0;

        while (pageIndex * BATCH_SIZE < totalAssets) {
            Pageable pageable = PageRequest.of(pageIndex, BATCH_SIZE);
            List<Asset> assets = assetRepository.findAssetsForDepreciation(
                    tenantId, assetId, legacyData, currentDate.format(DateTimeFormatter.ofPattern("MM-dd")), pageable);

            for (Asset asset : assets) {
                processAssetDepreciation(asset, currentDate, legacyData);
            }

            pageIndex++;
        }
        message = CalculatorConstants.SUCCESS_MESSAGE;
        return message;
    }

    private void processAssetDepreciation(Asset asset, LocalDate currentDate, boolean legacyData) {
        int yearsElapsed;
        int remainingLife;

        if (legacyData) {
            asset.setBookValue(BigDecimal.valueOf(asset.getOriginalBookValue()).doubleValue());
            //LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate());
            LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / 86400000); // Convert milliseconds to days
            if (purchaseDate == null) {
                log.warn("Skipping asset with null purchase date: {}", asset.getId());
                return; // Skip if purchase date is null
            }

            LocalDate startDate = purchaseDate;
            LocalDate endDate = startDate.plusYears(1);
            yearsElapsed = currentDate.getYear() - purchaseDate.getYear();
            remainingLife = Integer.parseInt(asset.getLifeOfAsset()) - yearsElapsed;

            while (endDate.isBefore(currentDate) && remainingLife > 0) {
                BigDecimal depreciationRate = fetchDepreciationRate(asset.getAssetCategory());
                BigDecimal depreciation = calculateDepreciationValue(asset, depreciationRate);
                BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
                //currentBookValue = asset.setBookValue(BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation).doubleValue());
                asset.setBookValue(currentBookValue.doubleValue());
                saveDepreciationDetail(asset, startDate, endDate, depreciation, depreciationRate, true);

                startDate = endDate;
                endDate = startDate.plusYears(1);
            }

            asset.setIsLegacyData(false);
            assetRepository.save(asset);
        } else {
            //LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate());
            LocalDate purchaseDate = LocalDate.ofEpochDay(asset.getPurchaseDate() / 86400000); // Convert milliseconds to days
            if (purchaseDate == null) {
                log.warn("Skipping asset with null purchase date: {}", asset.getId());
                return;
            }

            LocalDate anniversaryDate = LocalDate.of(currentDate.getYear(), purchaseDate.getMonth(), purchaseDate.getDayOfMonth());

            if (!anniversaryDate.equals(currentDate)) {
                return; // Skip if not the anniversary date
            }

            BigDecimal depreciationRate = fetchDepreciationRate(asset.getAssetCategory());
            BigDecimal depreciation = calculateDepreciationValue(asset, depreciationRate);

            BigDecimal currentBookValue = BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation);
            //currentBookValue = asset.setBookValue(BigDecimal.valueOf(asset.getBookValue()).subtract(depreciation).doubleValue());
            asset.setBookValue(currentBookValue.doubleValue());

            saveDepreciationDetail(asset, purchaseDate, anniversaryDate, depreciation, depreciationRate, false);
            assetRepository.save(asset);
        }
    }

    private BigDecimal fetchDepreciationRate(String category) {
        BigDecimal depreciationRate = mdmsDataRepository.findDepreciationRateByCategory(category);
        depreciationRate = BigDecimal.valueOf(10.0); // set static value for testing
        if (depreciationRate == null) {
            throw new IllegalArgumentException("Depreciation rate not found for category: " + category);
        }

        return depreciationRate;
    }

    private BigDecimal calculateDepreciationValue(Asset asset, BigDecimal depreciationRate) {
        BigDecimal bookValue = BigDecimal.valueOf(asset.getBookValue());
        BigDecimal minimumValue = BigDecimal.valueOf(asset.getMinimumValue());
        BigDecimal originalBookValue = BigDecimal.valueOf(asset.getOriginalBookValue());

        BigDecimal maxDepreciation = bookValue.subtract(minimumValue);
        BigDecimal calculatedDepreciation = originalBookValue.multiply(depreciationRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return calculatedDepreciation.min(maxDepreciation);
    }

    private void saveDepreciationDetail(Asset asset, LocalDate startDate, LocalDate endDate, BigDecimal depreciation, BigDecimal depreciationRate, boolean legacyData) {
        // Check if a record already exists
        Optional<DepreciationDetail> existingDetail = depreciationDetailRepository.findByAssetIdAndFromDateAndToDate(
                asset.getId(), startDate, endDate);

        if (existingDetail.isPresent()) {
            if (legacyData) {
                // Overwrite the existing record for legacy data
                DepreciationDetail detail = existingDetail.get();
                detail.setDepreciationValue(depreciation.doubleValue());
                detail.setBookValue(asset.getBookValue());
                detail.setRate(depreciationRate.doubleValue());
                detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());
                detail.setUpdatedAt(LocalDate.from(LocalDateTime.now())); // Assuming updatedAt is LocalDateTime
                depreciationDetailRepository.save(detail);
            } else {
                // Skip saving and log or throw an exception
                log.warn("Duplicate depreciation record found for asset {} from {} to {}. Skipping save as legacyData is false.",
                        asset.getId(), startDate, endDate);
                throw new IllegalStateException("Duplicate depreciation record found for non-legacy data.");
            }
        } else {
            // Create a new record
            DepreciationDetail detail = new DepreciationDetail();
            detail.setAssetId(asset.getId());
            detail.setFromDate(startDate);
            detail.setToDate(endDate);
            detail.setDepreciationValue(depreciation.doubleValue());
            detail.setBookValue(asset.getBookValue());
            detail.setUpdatedAt(LocalDate.from(LocalDateTime.now())); // Assuming updatedAt is LocalDateTime
            detail.setRate(depreciationRate.doubleValue());
            detail.setOldBookValue(BigDecimal.valueOf(asset.getBookValue()).add(depreciation).doubleValue());
            depreciationDetailRepository.save(detail);
        }
    }

}
