package org.egov.asset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.calcontract.CalculationReq;
import org.egov.asset.web.models.calcontract.CalculationRes;
import org.egov.asset.web.models.calcontract.CalulationCriteria;
import org.egov.asset.web.models.calcontract.DepreciationRes;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AssetCalculationClient {

    private final ServiceRequestRepository apiClient;
    private final AssetConfiguration config;
    private final ObjectMapper objectMapper;

    public AssetCalculationClient(ServiceRequestRepository apiClient, AssetConfiguration config,
                                  ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public CalculationRes triggerDepreciationCalculation(AssetRequest assetRequest) {
        StringBuilder uri = new StringBuilder(config.getAssetCalculatorServiceHost() + config.getAssetCalculatorDepreciationApi());
        log.info("URI to calculate depreciation is : {}", uri);
        CalculationReq calculationReq = new CalculationReq();
        calculationReq.setRequestInfo(assetRequest.getRequestInfo());
        calculationReq.setCalulationCriteria(new CalulationCriteria());
        calculationReq.getCalulationCriteria().setTenantId(assetRequest.getRequestInfo().getUserInfo().getTenantId());
        calculationReq.getCalulationCriteria().setAssetId(assetRequest.getAsset().getId());

        Object rawResponse = apiClient.fetchResult(uri, calculationReq);
        return objectMapper.convertValue(rawResponse, CalculationRes.class);
    }

    public DepreciationRes getAssetDepreciationList(String assetId) {
        String assetCalculatorHost = config.getAssetCalculatorServiceHost();
        String depreciationDetailsApi = config.getAssetCalculatorDepreciationListApi();
        StringBuilder uri = new StringBuilder(assetCalculatorHost + depreciationDetailsApi);
        log.info("URI to fetch list is {}", uri);
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("assetId", assetId);
        return apiClient.fetchResultWithPathParams(uri, pathParams, DepreciationRes.class);
    }
}
