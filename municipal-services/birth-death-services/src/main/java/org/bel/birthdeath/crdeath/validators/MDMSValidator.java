package org.bel.birthdeath.crdeath.validators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bel.birthdeath.crdeath.util.CrDeathConstants;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
    
    /**
     * Creates MDMSValidator 
     * Rakhi S IKM
     * on 26.11.2022
     */
@Component
@Slf4j
public class MDMSValidator {
    

    public void validateMDMSData(CrDeathDtlRequest request,Object mdmsdata){
        Map<String,String> errorMap = new HashMap<>();
        Map<String,List<String>> masterData = getAttributeValues(mdmsdata);
        
        String[] masterArray = {CrDeathConstants.TENANTS,CrDeathConstants.GENDERTYPE
                            ,CrDeathConstants.HOSPITAL_LIST,CrDeathConstants.DEATH_PLACE};
        validateIfMasterPresent(masterArray,masterData);

        System.out.println("hairakhi4"+masterData.get(CrDeathConstants.TENANTS));
        System.out.println("rakhi1"+request.getDeathCertificateDtls().get(0).getTenantId());
        System.out.println("resultcompare:"+masterData.get(CrDeathConstants.TENANTS)
        .contains(request.getDeathCertificateDtls().get(0).getTenantId()));

        if(!masterData.get(CrDeathConstants.TENANTS)
                .contains(request.getDeathCertificateDtls().get(0).getTenantId()))
        errorMap.put("INVALID TENAND ID", "The tenand id  "+ request.getDeathCertificateDtls().get(0).getTenantId() +
                    " does not exists");

         System.out.println("genderMAster"+masterData.get(CrDeathConstants.GENDERTYPE).contains(request.getDeathCertificateDtls().get(0).getDeceasedGender()));

        if(!masterData.get(CrDeathConstants.GENDERTYPE)
                .contains(request.getDeathCertificateDtls().get(0).getDeceasedGender()))
        errorMap.put("INVALID GENDER TYPE", "The gender of the deceased " +
                    request.getDeathCertificateDtls().get(0).getDeceasedGender()+ " is invalid");

        if(!masterData.get(CrDeathConstants.HOSPITAL_LIST)
                    .contains(request.getDeathCertificateDtls().get(0).getDeathPlaceOfficeName()))
            errorMap.put("HOSPITAL DETAILS INVALID", "The deceased hospital details " +
                        request.getDeathCertificateDtls().get(0).getDeathPlaceOfficeName()+ " is invalid");

        if(!masterData.get(CrDeathConstants.DEATH_PLACE)
                        .contains(request.getDeathCertificateDtls().get(0).getDeathPlace()))
            errorMap.put("DEATH PLACE DETAILS INVALID", "The deceased death place details " +
                            request.getDeathCertificateDtls().get(0).getDeathPlace()+ " is invalid");


        if(!CollectionUtils.isEmpty(errorMap))
            throw new CustomException(errorMap);

    }

    private Map<String, List<String>> getAttributeValues(Object mdmsdata){
        List<String> modulepaths = Arrays.asList(CrDeathConstants.TENANT_JSONPATH, 
                                    CrDeathConstants.COMMON_MASTER_JSONPATH,
                                    CrDeathConstants.HOSPITAL_LIST_JSONPATH);
        final Map<String, List<String>> mdmsResMap = new HashMap<>();
       
        modulepaths.forEach(modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsdata,modulepath));
                log.error("jsonpath1"+JsonPath.read(mdmsdata,modulepath));
            } catch (Exception e) {
                log.error("Error while fetching MDMS data",e);
                throw new CustomException(CrDeathConstants.INVALID_TENANT_ID_MDMS_KEY,
                                CrDeathConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
           
        });
        System.out.println("mdmsResMap"+mdmsResMap);
        return mdmsResMap;
    }
    private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes){
        System.out.println("codescheck"+codes);
        Map<String,String> errorMap = new HashMap<>();
        for(String masterName : masterNames){
            System.out.println("masterName"+masterName);
            System.out.println("codesfound1"+codes.containsKey(masterName));
            // if(CollectionUtils.isEmpty(codes.get(masterName))){
                if(!codes.containsKey(masterName)){
                errorMap.put("MDMS DATA ERROR ","Unable to fetch "+ masterName + " codes from MDMS ");
            }
        }
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }
    
   
}
