package org.ksmart.birth.common.services;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.internal.StringUtil;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class MdmsLocationService {


    private List<String> getHospitalCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODE_JSONPATH);
    }

    private List<String> getInstitutionCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODE_JSONPATH);
    }

    private List<String> getInstitutionTypeCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_CODE_JSONPATH);
    }
    private List<String> getBoundaryCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BOUNDARY_CODE_JSONPATH);
    }

    public String getHospitalNameEn(Object mdmsData, String hospitalId) {
        if (!StringUtil.isBlank(hospitalId)) {
            List<String> hospitals = getHospitalCode(mdmsData);
            if (!CollectionUtils.isEmpty(hospitals) || hospitals.contains(hospitalId)) {
                int index = hospitals.indexOf(hospitalId);
                return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH + "[" + index + "].hospitalName");
            } else {
                return "Not Recorded";
            }
        } else {
            return "Not Recorded";
        }
    }
    public String getHospitalNameMl(Object mdmsData, String hospitalId) {
        if (!StringUtil.isBlank(hospitalId)) {
            List<String> hospitals = getHospitalCode(mdmsData);
            if (!CollectionUtils.isEmpty(hospitals) || hospitals.contains(hospitalId)) {
                int index = hospitals.indexOf(hospitalId);
                return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH + "[" + index + "].hospitalNamelocal");
            } else {
                return "Not Recorded";
            }
        } else {
            return "Not Recorded";
        }
    }

    public String getHospitalAddressEn(Object mdmsData, String hospitalId) {
        List<String> hospitals = getHospitalCode(mdmsData);
        if (!CollectionUtils.isEmpty(hospitals) || hospitals.contains(hospitalId)) {
            int index = hospitals.indexOf(hospitalId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH + "[" + index + "].mainPlace");
        } else {
            return "Not Recorded";
        }
    }

    public String getHospitalAddressMl(Object mdmsData, String hospitalId) {
        List<String> hospitals = getHospitalCode(mdmsData);
        if (!CollectionUtils.isEmpty(hospitals) || hospitals.contains(hospitalId)) {
            int index = hospitals.indexOf(hospitalId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODES_JSONPATH + "[" + index + "].mainPlaceLocal");
        } else {
            return "Not Recorded";
        }
    }

    public String getInstitutionNameEn(Object mdmsData, String institutionId) {
        List<String> institutions  = getInstitutionCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionId)) {
            int index = institutions.indexOf(institutionId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH + "[" + index + "].institutionName");
        }else {
            return "Not Recorded";
        }
    }

    public String getInstitutionNameMl(Object mdmsData, String institutionId) {
        List<String> institutions  = getInstitutionCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionId)) {
        int index = institutions.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].institutionNamelocal");
        }else {
            return "Not Recorded";
        }
    }

    public String getInstitutionTypeEn(Object mdmsData, String institutionTypeId) {
        List<String> institutions = getInstitutionTypeCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionTypeId)) {
            int index = institutions.indexOf(institutionTypeId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_JSONPATH + "[" + index + "].name");
        } else {
            return "Not Recorded";
        }
    }

    public String getInstitutionTypeMl(Object mdmsData, String institutionTypeId) {
        List<String> institutions = getInstitutionTypeCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionTypeId)) {
            int index = institutions.indexOf(institutionTypeId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_JSONPATH + "[" + index + "].namelocal");
        } else {
            return "Not Recorded";
        }
    }

    public String getInstitutionAddressEn(Object mdmsData, String institutionId) {
        List<String> institutions = getInstitutionCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionId)) {
            int index = institutions.indexOf(institutionId);
            return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH + "[" + index + "].mainPlace");
        } else {
            return "Not Recorded";
        }
    }

    public String getInstitutionAddressMl(Object mdmsData, String institutionId) {
        List<String> institutions  = getInstitutionCode(mdmsData);
        if (!CollectionUtils.isEmpty(institutions) || institutions.contains(institutionId)) {
        int index = institutions.indexOf(institutionId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODES_JSONPATH+"["+index+"].mainPlaceLocal");
        }else {
            return "Not Recorded";
        }
    }


    public String getWardNameEn(Object mdmsData, String WardId) {
        List<String> tenants  = getBoundaryCode(mdmsData);
        int index = tenants.indexOf(WardId);
        ArrayList<String> names =  JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BOUNDARY_CODES_JSONPATH+".name");
        return names.get(index);
    }

    public String getWardNameMl(Object mdmsData, String WardId) {
        List<String> tenants  = getBoundaryCode(mdmsData);
        int index = tenants.indexOf(WardId);
        ArrayList<String> names =  JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BOUNDARY_CODES_JSONPATH+".localname");
        return names.get(index);
    }

    public String getWardNo(Object mdmsData, String WardId) {
        List<String> tenants  = getBoundaryCode(mdmsData);
        int index = tenants.indexOf(WardId);
        ArrayList<String> names =  JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BOUNDARY_CODES_JSONPATH+".wardno");
        return names.get(index);
    }

}
