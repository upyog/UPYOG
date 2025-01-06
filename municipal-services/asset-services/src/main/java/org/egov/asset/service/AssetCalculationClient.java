package org.egov.asset.service;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.asset.web.models.calcontract.CalculationReq;
import org.egov.asset.web.models.calcontract.DepreciationRes;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AssetCalculationClient {

    private final ServiceRequestRepository apiClient;

    private final AssetConfiguration config;

    public AssetCalculationClient(ServiceRequestRepository apiClient, AssetConfiguration config) {
        this.apiClient = apiClient;
        this.config = config;
    }

    public Object triggerDepreciationCalculation(String tenantId, String assetId) {
        StringBuilder uri = new StringBuilder(config.getAssetCalculatorServiceHost()+config.getAssetCalculatorDepreciationApi());

        // Prepare request payload
        CalculationReq calculationReq = new CalculationReq();
        calculationReq.getCalulationCriteria().setTenantId(tenantId);
        calculationReq.getCalulationCriteria().setAssetId(assetId);

        // Call API and get response
        return apiClient.fetchResult(uri, calculationReq);
    }

    public DepreciationRes getAssetDepreciationList(String tenantId, String assetId) {
        String assetCalculatorHost = config.getAssetCalculatorServiceHost();
        String depreciationDetailsApi = config.getAssetCalculatorDepreciationListApi();
        StringBuilder uri = new StringBuilder(assetCalculatorHost + depreciationDetailsApi);

        // Define path parameters using HashMap
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("assetId", assetId);
        // Call API and get response
        return apiClient.fetchResultWithPathParams(uri, pathParams, DepreciationRes.class);

    }
}
