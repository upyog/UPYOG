package org.egov.garbageservice.util;

import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.stereotype.Component;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.repository.ServiceRequestRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class MdmsUtil {

    private final ServiceRequestRepository serviceRequestRepository;
    private final GarbageServiceConfig config;

    private static final String MDMS_REBATE_MASTER_NAME = "Rebate";
    private static final String MDMS_GARBAGE_MODULE_NAME = "Garbage";
    private static final String MDMS_REBATE_FILTER = "$.[?(@.serviceType=='GC_REBATE_FEE' && @.code=='{1}')].rate";

    public BigDecimal getRebateRate(RequestInfo requestInfo, String tenantId, String specialCategory) {
        if (specialCategory == null || specialCategory.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String stateLevelTenantId = tenantId.contains(".") ? tenantId.split("\\.")[0] : tenantId;

        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, stateLevelTenantId);
        StringBuilder url = new StringBuilder(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
        Optional<Object> resultOptional = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);

        if (resultOptional.isEmpty()) {
            log.warn("[MDMS][Rebate] Empty response from MDMS for tenantId={}", tenantId);
            return BigDecimal.ZERO;
        }

        Object result = resultOptional.get();

        try {
            List<Map<String, Object>> rebateList = JsonPath.read(result, "$.MdmsRes.Garbage.Rebate");
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
    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(MDMS_REBATE_MASTER_NAME);
        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(List.of(masterDetail));
        moduleDetail.setModuleName(MDMS_GARBAGE_MODULE_NAME);
        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(List.of(moduleDetail));
        return new MdmsCriteriaReq(requestInfo, mdmsCriteria);
    }
}