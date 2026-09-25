package upyog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upyog.config.EstateConfiguration;
import upyog.repository.ServiceRequestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client to interact with Asset Service APIs
 */
@Component
@Slf4j
public class AssetService {

    @Autowired
    private EstateConfiguration estateConfiguration;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Searches for an asset in the asset service by asset number
     * 
     * @param assetNo The asset number to search for
     * @param tenantId The tenant ID
     * @param requestInfo The request information
     * @return The asset response from the asset service
     */
    public Object searchAssetByAssetNo(String assetNo, String tenantId, RequestInfo requestInfo) {
        log.info("Searching for asset with assetNo: {} and tenantId: {}", assetNo, tenantId);
        
        StringBuilder uri = new StringBuilder(estateConfiguration.getAssetServiceHost())
                .append(estateConfiguration.getAssetServiceSearchEndpoint())
                .append("?applicationNo=").append(assetNo)
                .append("&status=APPROVED")
                .append("&tenantId=").append(tenantId);
        
        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);
        
        try {
            Object response = serviceRequestRepository.fetchResult(uri, request);
            log.info("Asset service search response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error while searching asset in asset service: ", e);
            return null;
        }
    }

    /**
     * Checks if an asset exists in the asset service
     * 
     * @param assetNo The asset number to check
     * @param tenantId The tenant ID
     * @param requestInfo The request information
     * @return true if asset exists, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean isAssetExist(String assetNo, String tenantId, RequestInfo requestInfo) {
        Object response = searchAssetByAssetNo(assetNo, tenantId, requestInfo);
        
        if (response == null) {
            return false;
        }
        
        try {
            Map<String, Object> responseMap = objectMapper.convertValue(response, Map.class);
            Object assets = responseMap.get("Assets");
            
            if (assets instanceof List) {
                List<?> assetList = (List<?>) assets;
                return !assetList.isEmpty();
            }
        } catch (Exception e) {
            log.error("Error parsing asset service response: ", e);
        }
        
        return false;
    }
}

