package org.egov.garbageservice.util;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.repository.ServiceRequestRepository;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility component for building MDMS request criteria and fetching master configuration data (rates, penalties, rebates).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MdmsUtil {

    private static final String MDMS_REBATE_MASTER_NAME = "Rebate";
    private static final String MDMS_PENALTY_MASTER_NAME = "Penalty";
    private static final String MDMS_GARBAGE_MODULE_NAME = "Garbage";
    private static final String MDMS_REBATE_FILTER = "$.[?(@.serviceType=='GC_REBATE_FEE' && @.code=='{1}')].rate";
    private static final String MDMS_PENALTY_FILTER = "$.[?(@.serviceType=='GC_RENTAL_FEE' && @.feeType=='GC_PENALTY_FEE')].rate";
    private final ServiceRequestRepository serviceRequestRepository;
    private final GarbageServiceConfig config;

    /**
     * Executes getRebateRate query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @param requestInfo     the request information containing user session details
     * @param tenantId        the tenant ID string
     * @param specialCategory the specialCategory parameter
     * @return the output result
     */

    public BigDecimal getRebateRate(RequestInfo requestInfo, String tenantId, String specialCategory) {
        if (specialCategory == null || specialCategory.isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            List<Map<String, Object>> rebateList = JsonPath.read(getMdmsResponse(requestInfo, tenantId), "$.MdmsRes.Garbage.Rebate");
            String filter = MDMS_REBATE_FILTER.replace("{1}", specialCategory);
            List<Integer> rates = JsonPath.read(rebateList, filter);
            if (rates.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(rates.get(0)).divide(BigDecimal.valueOf(100));
        } catch (Exception e) {
            log.error("Error while fetching rebate rate from MDMS", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Executes getPenaltyRate query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param tenantId    the tenant ID string
     * @return the output result
     */

    public BigDecimal getPenaltyRate(RequestInfo requestInfo, String tenantId) {
        try {
            List<Map<String, Object>> penaltyList = JsonPath.read(getMdmsResponse(requestInfo, tenantId), "$.MdmsRes.Garbage.Penalty");
            List<Integer> penalty = JsonPath.read(penaltyList, MDMS_PENALTY_FILTER);
            if (penalty.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(penalty.get(0)).divide(BigDecimal.valueOf(100));
        } catch (Exception e) {
            log.error("Error while fetching penalty rate from MDMS", e);
            return BigDecimal.ZERO;
        }
    }


    /**
     * Executes getMdmsResponse query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param tenantId    the tenant ID string
     * @return the output result
     */

    public Object getMdmsResponse(RequestInfo requestInfo, String tenantId) {
        String stateLevelTenantId = tenantId.contains(".") ? tenantId.split("\\.")[0] : tenantId;
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, stateLevelTenantId);
        StringBuilder url = new StringBuilder(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
        Optional<Object> resultOptional = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);

        if (resultOptional.isEmpty()) {
            log.warn("[MDMS] Empty response from MDMS for tenantId={}", tenantId);
            return null;
        }

        return resultOptional.get();
    }

    /**
     * Executes getMdmsRequest query operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Parses input search filter criteria.</li>
     *   <li>Queries database or external service for matching records.</li>
     *   <li>Applies security filters and pagination boundaries.</li>
     *   <li>Returns response payload with matching entity list.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param tenantId    the tenant ID string
     * @return the output result
     */

    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId) {
        MasterDetail masterDetail1 = new MasterDetail();
        MasterDetail masterDetail2 = new MasterDetail();
        masterDetail1.setName(MDMS_REBATE_MASTER_NAME);
        masterDetail2.setName(MDMS_PENALTY_MASTER_NAME);
        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(List.of(masterDetail1, masterDetail2));
        moduleDetail.setModuleName(MDMS_GARBAGE_MODULE_NAME);
        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(List.of(moduleDetail));
        return new MdmsCriteriaReq(requestInfo, mdmsCriteria);
    }
}