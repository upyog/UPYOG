package org.ksmart.birth.birthregistry.service;

import com.jayway.jsonpath.JsonPath;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthapplication.repository.BirthApplicationRepository;
import org.ksmart.birth.birthapplication.validator.BirthApplicationValidator;
import org.ksmart.birth.birthapplication.validator.MdmsValidator;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class MdmsDataService {
    private final MdmsUtil mdmsUtil;

    @Autowired
    MdmsDataService(MdmsUtil mdmsUtil) {
        this.mdmsUtil = mdmsUtil;
    }

    public void getPdfDataForMaster(RequestInfo requestInfo) {

        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);

        List<String> tenantCode = getTenantCodes(mdmsData);
        System.out.println(tenantCode.get(0));

        List<String> tenantDistCode = getTenantDistCodes(mdmsData);
        System.out.println(tenantDistCode.get(0));
    }


    private List<String> getTenantCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_CODE_JSONPATH);
    }

    private List<String> getTenantDistCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_DIST_CODE_JSONPATH);
    }


    private void getTenantDistCodeForCurrentTenant(Object mdmsData) {
        List<String> tenantCodes = getTenantCodes(mdmsData);
        Iterator<String> iteratorTenants = tenantCodes.iterator();
        while (iteratorTenants.hasNext()){
            if(iteratorTenants.next().contains("kl.cochin")){
                System.out.println(tenantCodes.get(0));
            }
        }
    }


}
