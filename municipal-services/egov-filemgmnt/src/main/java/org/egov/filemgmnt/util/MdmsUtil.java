package org.egov.filemgmnt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

    @Value("${egov.mdms.master.name}")
    private String masterName;

    @Value("${egov.mdms.module.name}")
    private String moduleName;

    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(mdmsHost)
           .append(mdmsUrl);
        Object result = null;
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId);

        try {
            result = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);

        } catch (Exception e) {
            log.error("Exception occurred while fetching category lists from mdms: ", e);
        }

        return result;
    }

    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId) {

        List<ModuleDetail> fmModuleRequest = getFMModuleRequest();

        List<ModuleDetail> moduleDetails = new LinkedList<>();

        moduleDetails.addAll(fmModuleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                                                .moduleDetails(moduleDetails)
                                                .tenantId(tenantId)
                                                .build();

        return MdmsCriteriaReq.builder()
                              .mdmsCriteria(mdmsCriteria)
                              .requestInfo(requestInfo)
                              .build();
    }

    public List<ModuleDetail> getFMModuleRequest() {

        // master details for FM module
        List<MasterDetail> fmMasterDetails = new ArrayList<>();

        fmMasterDetails.add(MasterDetail.builder()
                                        .name(FMConstants.FM_MDMS_FILE_SERVICE_SUBTYPE)
                                        .build());

        ModuleDetail fmModuleDtls = ModuleDetail.builder()
                                                .masterDetails(fmMasterDetails)
                                                .moduleName(FMConstants.FILEMANAGEMENT_MODULE)
                                                .build();

        return Arrays.asList(fmModuleDtls);

    }
}