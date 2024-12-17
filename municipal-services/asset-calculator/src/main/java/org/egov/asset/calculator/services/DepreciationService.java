package org.egov.asset.calculator.services;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.egov.asset.calculator.web.models.DepreciationDetail;
import org.egov.asset.calculator.repository.DepreciationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DepreciationService {

    private  DepreciationDetailRepository depreciationDetailRepository;
    private  RestTemplate restTemplate;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    public void calculateDepreciationForSingleAsset(Long assetId) {
        Map<String, Object> asset = fetchAssetDetails(assetId);

        if (!(Boolean) asset.get("isDepreciationApplicable") || !(Boolean) asset.get("isActive")) {
            return; // Skip non-eligible assets
        }

        double bookValue = (Double) asset.get("bookValue");
        double purchaseValue = (Double) asset.get("purchaseValue");
        int assetLife = (Integer) asset.get("assetLife");
        LocalDate purchaseDate = LocalDate.parse((String) asset.get("purchaseDate"));
        double minValue = (Double) asset.get("minimumValue");
        double depreciationRate = 100.0 / assetLife;

        // Remaining life calculation
        long elapsedYears = ChronoUnit.YEARS.between(purchaseDate, LocalDate.now());
        int remainingLife = assetLife - (int) elapsedYears;

        if (remainingLife <= 0 || bookValue <= minValue) return;

        double depreciationValue = Math.min((bookValue * depreciationRate / 100), bookValue - minValue);

        // Upsert logic
        LocalDate fromDate = LocalDate.now().withDayOfYear(1);
        LocalDate toDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        DepreciationDetail detail = depreciationDetailRepository
                .findByAssetIdAndFromDate(assetId, fromDate)
                .orElse(new DepreciationDetail());

        detail.setAssetId(assetId);
        detail.setFromDate(fromDate);
        detail.setToDate(toDate);
        detail.setDepreciationValue(depreciationValue);
        detail.setBookValue(bookValue - depreciationValue);
        detail.setUpdatedAt(LocalDate.now());

        depreciationDetailRepository.save(detail);
    }

    public void calculateBulkDepreciation() {
        List<Map<String, Object>> assets = fetchEligibleAssets();
        assets.forEach(asset -> calculateDepreciationForSingleAsset((Long) asset.get("id")));
    }

    private Map<String, Object> fetchAssetDetails(Long assetId) {
        return restTemplate.getForObject(assetServiceUrl + "/api/assets/" + assetId, Map.class);
    }

    private List<Map<String, Object>> fetchEligibleAssets() {
        return restTemplate.getForObject(assetServiceUrl + "/api/assets/eligible", List.class);
    }
}
