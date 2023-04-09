package org.ksmart.birth.common.services;

import com.jayway.jsonpath.JsonPath;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MdmsTenantService {

    private List<String> getTenantCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_CODE_JSONPATH);
    }

    private List<String> getLbTypeCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_CODE_JSONPATH);
    }

    private List<String> getTalukCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_CODE_JSONPATH);
    }

    private List<String> getDistrictCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_CODE_JSONPATH);
    }

    private List<String> getStateCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_CODE_JSONPATH);
    }

    private List<String> getCountryCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_CODE_JSONPATH);
    }

    private List<String> getPoCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_CODE_JSONPATH);
    }

    private List<String> getInstType(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_CODE_JSONPATH);
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

    public String getTenantUrl(Object mdmsData, String tenantId) {
        List<String> tenants  = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH+"["+index+"].logoId");
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
    public String getLbTypeNameEn(Object mdmsData, String code) {
        List<String> tenants  = getLbTypeCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_JSONPATH+"["+index+"].name");
    }
    public String getPostOfficeNameEn(Object mdmsData, String code) {
        List<String> tenants  = getPoCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH+"["+index+"].name");
    }

    public Integer getPostOfficePinCode(Object mdmsData, String code) {
        List<String> tenants  = getPoCodes(mdmsData);
        Integer index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH+"["+index+"].pincode");
    }
    public String getTalukNameEn(Object mdmsData, String code) {
        List<String> tenants  = getTalukCodes(mdmsData);
        Integer index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_JSONPATH+"["+index+"].name");
    }

    public String getDistrictNameEn(Object mdmsData, String code) {
        List<String> tenants  = getDistrictCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_JSONPATH+"["+index+"].name");
    }

    public String getStateNameEn(Object mdmsData, String code) {
        List<String> tenants  = getStateCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_JSONPATH+"["+index+"].name");
    }

    public String getCountryNameEn(Object mdmsData, String code) {
        List<String> tenants  = getCountryCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH+"["+index+"].name");
    }
    public String getLbTypeNameMl(Object mdmsData, String code) {
        List<String> tenants  = getLbTypeCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_JSONPATH+"["+index+"].namelocal");
    }
    public String getPostOfficeNameMl(Object mdmsData, String code) {
        List<String> tenants  = getTalukCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH+"["+index+"].namelocal");
    }
    public String getTalukNameMl(Object mdmsData, String code) {
        List<String> tenants  = getTalukCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_JSONPATH+"["+index+"].namelocal");
    }

    public String getDistrictNameMl(Object mdmsData, String code) {
        List<String> tenants  = getDistrictCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_JSONPATH+"["+index+"].namelocal");
    }

    public String getStateNameMl(Object mdmsData, String code) {
        List<String> tenants  = getStateCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_JSONPATH+"["+index+"].namelocal");
    }

    public String getCountryNameMl(Object mdmsData, String code) {
        List<String> tenants  = getCountryCodes(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH+"["+index+"].namelocal");
    }

    public String getInstitutionTypeName(Object mdmsData, String code) {
        List<String> tenants  = getInstType(mdmsData);
        int index = tenants.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_JSONPATH+"["+index+"].name");
    }
}
