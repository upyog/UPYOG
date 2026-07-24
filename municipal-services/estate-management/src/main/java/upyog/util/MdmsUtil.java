package upyog.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import upyog.client.MdmsClient;
import upyog.config.EstateConfiguration;
import upyog.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static upyog.config.ServiceConstants.*;

/**
 * Utility class for MDMS (Master Data Management Service) operations.
 * Handles fetching master data from MDMS service for Estate Management module.
 * 
 * Key Responsibilities:
 * - Constructs MDMS request criteria for Estate module
 * - Fetches tax calculation types and rates from MDMS
 * - Handles tenant-level data retrieval
 */
@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EstateConfiguration configs;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    /**
     * Makes MDMS API call to fetch Estate module master data.
     * 
     * @param requestInfo Request metadata containing user info and correlation ID
     * @param tenantId Tenant identifier (e.g., pg.citya)
     * @return MDMS response containing master data for Estate module
     */
    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }
    
    /**
     * Constructs MDMS service search URL.
     * 
     * @return Complete MDMS search endpoint URL
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(configs.getMdmsHost()).append(configs.getMdmsEndPoint());
    }
    
    /**
     * Constructs MDMS criteria request for Estate module.
     * Splits tenantId to get state-level tenant (e.g., pg.citya -> pg)
     * as MDMS data is stored at state level.
     * 
     * @param requestInfo Request metadata
     * @param tenantId Full tenant identifier (city level)
     * @return MDMS criteria request object
     */
    public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        List<ModuleDetail> moduleRequest = getEstateModuleRequest();
        
        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.addAll(moduleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                .moduleDetails(moduleDetails)
                .tenantId(tenantId.split("\\.")[0])  // Extract state-level tenant (pg from pg.citya)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder()
                .mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo)
                .build();
        return mdmsCriteriaReq;
    }
    
    /**
     * Prepares Estate module request with required master data.
     * Fetches EstateCalculationType master which contains tax rates and fee types.
     * 
     * @return List of module details for Estate module
     */
    public List<ModuleDetail> getEstateModuleRequest() {
        List<MasterDetail> estateMasterDetails = new ArrayList<>();

        // Request EstateCalculationType master containing tax configuration
        estateMasterDetails.add(MasterDetail.builder()
                .name(MDMS_MASTER_PENALTY)
                .build());

        ModuleDetail moduleDetail = ModuleDetail.builder()
                .masterDetails(estateMasterDetails)
                .moduleName(MDMS_MODULE_ESTATE)
                .build();

        return Arrays.asList(moduleDetail);
    }

    /**
     * Fetches the penalty rate configured in MDMS.
     * Returns a default value of 5% if MDMS lookup fails.
     *
     * @param requestInfo request information
     * @param tenantId tenant identifier
     * @return penalty rate as a decimal value
     */
   @SuppressWarnings("unchecked")
   public BigDecimal getPenaltyRate(RequestInfo requestInfo, String tenantId) {
        try {
            Object mdmsData = mDMSCall(requestInfo, tenantId);
            Map<String, Object> mdmsMap = (Map<String, Object>) mdmsData;
            List<Map<String, Object>> penaltyList = (List<Map<String, Object>>)
                    ((Map<String, Object>) ((Map<String, Object>) mdmsMap
                            .get(MDMS_RES)).get(MDMS_MODULE_ESTATE)).get(MDMS_MASTER_PENALTY);
    
            if (penaltyList != null && !penaltyList.isEmpty()) {
                Object rate = penaltyList.get(0).get(MDMS_PENALTY_RATE_KEY);
                if (rate != null) {
                    BigDecimal penaltyRate = new BigDecimal(rate.toString())
                            .divide(BigDecimal.valueOf(100));
                    log.info("Penalty rate fetched from MDMS: {}%", rate);
                    return penaltyRate;
                }
            }
    
            log.warn("Penalty master is empty in MDMS.");
            throw new CustomException(
                    "PENALTY_RATE_NOT_FOUND",
                    "Penalty rate is not configured in MDMS."
            );
    
        } catch (Exception e) {
            log.error("Failed to fetch penalty rate from MDMS: {}", e.getMessage(), e);
            throw new CustomException(
                    "MDMS_FETCH_ERROR",
                    "Unable to fetch penalty rate from MDMS."
            );
        }
    }

    /**
     * Generic method to fetch MDMS data for any module and master list.
     * Currently not in use - kept for future extensibility.
     * 
     * @param requestInfo Request metadata
     * @param tenantId Tenant identifier
     * @param moduleName MDMS module name
     * @param masterNameList List of master names to fetch
     * @return Map of module to master data
     */
    public Map<String, Map<String, JSONArray>> fetchMdmsData(RequestInfo requestInfo, String tenantId, String moduleName,
                                                             List<String> masterNameList) {
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId, moduleName, masterNameList);
        try {
            MdmsResponse mdmsResponse = null;//mdmsFeignClient.fetchMdmsData(mdmsCriteriaReq);
            return mdmsResponse.getMdmsRes();
        } catch (Exception e) {
            log.error(ERROR_WHILE_FETCHING_FROM_MDMS, e);
        }
        return Collections.emptyMap();
    }

    /**
     * Generic MDMS request builder for any module and master list.
     * Extracts state-level tenant from full tenantId for MDMS lookup.
     * 
     * @param requestInfo Request metadata
     * @param tenantId Full tenant identifier
     * @param moduleName MDMS module name
     * @param masterNameList List of master names to fetch
     * @return MDMS criteria request object
     */
    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId,
                                           String moduleName, List<String> masterNameList) {
        List<MasterDetail> masterDetailList = new ArrayList<>();
        for (String masterName : masterNameList) {
            MasterDetail masterDetail = new MasterDetail();
            masterDetail.setName(masterName);
            masterDetailList.add(masterDetail);
        }

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName(moduleName);
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId.split("\\.")[0]);  // Extract state-level tenant
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }
}
