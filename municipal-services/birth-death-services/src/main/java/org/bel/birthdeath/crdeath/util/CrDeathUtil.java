package org.bel.birthdeath.crdeath.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bel.birthdeath.common.repository.ServiceRequestRepository;
import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrDeathUtil {

    private ServiceRequestRepository serviceRequestRepository;
    private CrDeathConfiguration config;

    @Autowired
    public CrDeathUtil(CrDeathConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }

    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }

    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        ModuleDetail tenantIdRequest = getTenantIdRequest();
       // List<ModuleDetail> tradeModuleRequest = getTradeModuleRequest();

        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.add(tenantIdRequest);
       // moduleDetails.addAll(tradeModuleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();

        System.out.println("mdmsreq1"+mdmsCriteriaReq);
        return mdmsCriteriaReq;
    }

     /**
     * Creates request to search tenantID in mdms
     * 
     * @return MDMS request for tenantID
     */
    private ModuleDetail getTenantIdRequest() {

        // master details for TL module
        List<MasterDetail> crDeathMasterDetails = new ArrayList<>();

        // filter to only get code field from master data

       // final String filterCodeForUom = "$.[?(@.tenantId=='kl' && @.moduleName=='tenant')]";

        // crDeathMasterDetails
        //         .add(MasterDetail.builder().name(CrDeathConstants.TENANTS).filter(filterCodeForUom).build());

        crDeathMasterDetails
                .add(MasterDetail.builder().name(CrDeathConstants.TENANTS).build());

        ModuleDetail crDeathModuleDtls = ModuleDetail.builder().masterDetails(crDeathMasterDetails)
                .moduleName(CrDeathConstants.TENANT_MODULE_NAME).build();

       
        return crDeathModuleDtls;
    }

    
}
