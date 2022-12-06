package org.egov.filemgmnt.util;

import java.util.Collections;
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

    public Object mdmsCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId);

        String mdmsUri = String.format("%s%s", mdmsHost, mdmsUrl);
        Object result = null;
        try {
            result = restTemplate.postForObject(mdmsUri, mdmsCriteriaReq, Map.class);
        } catch (Exception e) {
            log.error("Exception occurred while fetching category lists from mdms: ", e);
        }

        return result;
    }

    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId) {

        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.addAll(getFMModuleDetails());

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                                                .moduleDetails(moduleDetails)
                                                .tenantId(tenantId)
                                                .build();

        return MdmsCriteriaReq.builder()
                              .mdmsCriteria(mdmsCriteria)
                              .requestInfo(requestInfo)
                              .build();
    }

    public List<ModuleDetail> getFMModuleDetails() {
        // master details for FM module
        List<MasterDetail> fmMasterDetails = Collections.singletonList(MasterDetail.builder()
                                                                                   .name(FMConstants.FM_MDMS_FILE_SERVICE_SUBTYPE)
                                                                                   .build());

        ModuleDetail fmModuleDetail = ModuleDetail.builder()
                                                  .masterDetails(fmMasterDetails)
                                                  .moduleName(FMConstants.FILEMANAGEMENT_MODULE)
                                                  .build();

        // return Arrays.asList(fmModuleDetail);
        return Collections.singletonList(fmModuleDetail);

    }
}