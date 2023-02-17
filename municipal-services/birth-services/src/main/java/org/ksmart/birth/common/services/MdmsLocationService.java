package org.ksmart.birth.common.services;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class MdmsLocationService {


    private List<String> getHospitalCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODE_JSONPATH);
    }

    public String getHospitalNameEn(Object mdmsData, String hospitalId) {
        List<String> hospitals  = getHospitalCode(mdmsData);
        int index = hospitals.indexOf(hospitalId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH+"["+index+"].hospitalName");
    }

    public String getHospitalNameMl(Object mdmsData, String hospitalId) {
        List<String> hospitals  = getHospitalCode(mdmsData);
        int index = hospitals.indexOf(hospitalId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH+"["+index+"].hospitalNamelocal");
    }

    public String getHospitalAddressEn(Object mdmsData, String hospitalId) {
        List<String> hospitals  = getHospitalCode(mdmsData);
        int index = hospitals.indexOf(hospitalId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH+"["+index+"].address");
    }

    public String getHospitalAddressMl(Object mdmsData, String hospitalId) {
        List<String> tenants  = getHospitalCode(mdmsData);
        int index = tenants.indexOf(hospitalId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH+"["+index+"].addressLocal");
    }

    public String getInstitutionNameEn(Object mdmsData, String institutionId) {
        List<String> tenants  = getHospitalCode(mdmsData);
        int index = tenants.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].institutionName");
    }

    public String getInstitutionNameMl(Object mdmsData, String institutionId) {
        List<String> tenants  = getHospitalCode(mdmsData);
        int index = tenants.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].institutionNamelocal");
    }

    public String getInstitutionAddressEn(Object mdmsData, String institutionId) {
        List<String> tenants  = getHospitalCode(mdmsData);
        int index = tenants.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].address");
    }

    public String getInstitutionAddressMl(Object mdmsData, String institutionId) {
        List<String> tenants  = getHospitalCode(mdmsData);
        int index = tenants.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].addressLocal");
    }

}
