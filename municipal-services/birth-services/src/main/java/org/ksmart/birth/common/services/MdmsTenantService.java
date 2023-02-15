package org.ksmart.birth.common.services;

import com.jayway.jsonpath.JsonPath;
import org.apache.coyote.RequestInfo;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MdmsTenantService {
    private final MdmsUtil mdmsUtil;

    @Autowired
    MdmsTenantService(MdmsUtil mdmsUtil) {
        this.mdmsUtil = mdmsUtil;
    }

    private List<String> getTenantCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_CODE_JSONPATH);
    }

    public String getTenantName(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].name");
    }

    public String getTenantLbType(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.lbtypecode");
    }

    public String getTenantTaluk(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.talukcode");
    }

    public String getTenantDistrict(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.distCodeStr");
    }

    public String getTenantState(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.statecode");
    }

    public String getTenantIdGenCode(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.idgencode");
    }

    public String getTenantLetterCode(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].city.code");
    }


}
