package org.ksmart.birth.newbirth.validator;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.adoption.validator.AdoptionMdmsValidator;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.BirthUtils;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ksmart.birth.utils.BirthConstants.*;
import static org.ksmart.birth.utils.enums.ErrorCodes.*;

@Component
@Slf4j
public class NewMdmsValidator {
    private final NewMdmsAddressValidation validAddress;
    private final MdmsUtil mdmsUtil;
    @Autowired
    NewMdmsValidator(NewMdmsAddressValidation validAddress,  MdmsUtil mdmsUtil) {

        this.validAddress = validAddress;
        this.mdmsUtil = mdmsUtil;
    }
    public void validateMdmsData(NewBirthDetailRequest request, Object mdmsData) {

        if (log.isDebugEnabled()) {
            log.debug("MDMS master data \n {}", BirthUtils.toJson(mdmsData));
        }

        Map<String, Object> masterBirthData = getBirthMasterData(mdmsData);
        validateBirthMasterData(masterBirthData);

        Map<String, Object> masterCommonData = getCommonMasterData(mdmsData);
        validateCommonMasterData(masterCommonData);

        Map<String, Object> masterTenantData = getTenantData(mdmsData);
        validateTenantMasterData(masterTenantData);

        Map<String, String> errorMap = new ConcurrentHashMap<>();
        String tenant = null;
        tenant = validateBasicDetails(request, mdmsData,  errorMap);

        Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), tenant);
        validateBirthPlace(request, mdmsData,mdmsDataLoc, errorMap);
        validateParentDetails(request, mdmsData, errorMap);
        validAddress.validateParentDetails(request, mdmsData, errorMap);


        if (MapUtils.isNotEmpty(errorMap)) {
            throw new CustomException(errorMap);
        }
    }
    private String  validateBasicDetails(NewBirthDetailRequest request, Object mdmsData, Map<String, String> errorMap) {
        List<String> tenantCodes = getTenantCodes(mdmsData);
        String tenant = "";
        for (NewBirthApplication birth : request.getNewBirthDetails()) {
            String tenantCodeCodeBasic = birth.getTenantId();
            tenant = tenantCodeCodeBasic;
            if (log.isDebugEnabled()) {
                log.debug("Tenant code : \n{}", tenantCodes);
                if (CollectionUtils.isEmpty(tenantCodes) || !tenantCodes.contains(tenantCodeCodeBasic)) {
                    errorMap.put(CR_MDMS_TENANTS, "The Tenant code  in application'" + tenantCodeCodeBasic + "' does not exists");
                }
            }
        }
        return tenant;
    }


    private void validateBirthPlace(NewBirthDetailRequest request, Object mdmsData, Object mdmsDataLoc, Map<String, String> errorMap) {

        List<String> placeCodes = getBirthPlaceCodes(mdmsData);
        List<String> postOfficeCodes = getPostOfficeCode(mdmsData);
        List<String> institutionCodes = getInstitutionCodes(mdmsDataLoc);
        List<String> hospitalCodes =getHospitalCodes(mdmsDataLoc);

        List<String> institutionTypeCodes = getInstitutionTypeCode(mdmsData);
        List<String> lBTypeCodes = getLbTypeCode(mdmsData);

        //WARD  Validation

        request.getNewBirthDetails()
                .forEach(birth -> {

                    String birthPlace = birth.getPlaceofBirthId();
                    if (log.isDebugEnabled()) {
                        log.debug("Birth place code : \n{}", birthPlace);
                        if (CollectionUtils.isEmpty(placeCodes) || !placeCodes.contains(birthPlace)) {
                            errorMap.put(CR_MDMS_PLACEMASTER, "The Birth Place code '" + birthPlace + "' does not exists");
                        }
                    }

                    // Birthplace Home
                    if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
                        String postOfficeCodePlace = birth.getAdrsPostOffice();
                        if (log.isDebugEnabled()) {
                            log.debug("Postoffice code : \n{}", postOfficeCodePlace);
                            if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePlace)) {
                                errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code  in birth place home'" + postOfficeCodePlace + "' does not exists");
                            }
                        }
                        //ward comment
                        
                        
//                        if (StringUtils.isBlank(birth.getWardId())) {
//                            throw new CustomException(INVALID_UPDATE.getCode(),
//                                    "Ward no is required for update request of birthplace details.");
//                        }
                        
                        //ward comment
                    }

                    // Birthplace Hospital
                    if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
                        String hospitalCode = birth.getHospitalId();
                        if (log.isDebugEnabled()) {
                            log.debug("Hospital code : \n{}", hospitalCode);
                            if (CollectionUtils.isEmpty(hospitalCodes) || !hospitalCodes.contains(hospitalCode)) {
                                errorMap.put(LOCATION_MDMS_HOSPITAL, "The Birthplace Hospital code'" + hospitalCode + "' does not exists");
                            }
                        }
                    }

                    // Birthplace Institution
                    if (birth.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
                        String institutionCode = birth.getInstitutionId();
                        if (log.isDebugEnabled()) {
                            log.debug("Institution code : \n{}", institutionCode);
                            if (CollectionUtils.isEmpty(institutionCodes) || !institutionCodes.contains(institutionCode)) {
                                errorMap.put(LOCATION_MDMS_INSTITUTION, "The Birthplace Institution code'" + institutionCode + "' does not exists");
                            }
                        }
                    }
                });
    }


    private void validateParentDetails(NewBirthDetailRequest request, Object mdmsData, Map<String, String> errorMap) {
        List<String> professionCodes = getProfessionCodes(mdmsData);
        List<String> qualificationCodes = getQualificationCode(mdmsData);
        List<String> countryCodes = getCountryCodes(mdmsData);
        request.getNewBirthDetails()
                .forEach(birth -> {
                    List<String> religionCodes = getReligionCodes(mdmsData);
                    if (birth.getParentsDetails() != null) {

                        // Religion of the family
                        String religionCode = birth.getParentsDetails().getReligionId();
                        if (log.isDebugEnabled()) {
                            log.debug("Religion code : \n{}", religionCode);
                        }
                        if (CollectionUtils.isEmpty(religionCodes) || !religionCodes.contains(religionCode)) {
                            errorMap.put(COMMON_MDMS_RELIGION, "The Religion code '" + religionCode + "' does not exists");                        }

                        //Father Information

                        if (!birth.getParentsDetails().getIsFatherInfoMissing()) {
                            //Profession

                            String professionCodeFather = birth.getParentsDetails().getFatherProffessionid();
                            if (log.isDebugEnabled()) {
                                log.debug("Father Profession code : \n{}", professionCodeFather);
                            }
                            if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeFather)) {

                                errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeFather + "' does not exists");
                            }

                            //Qualification

                            String qualificationCodeFather = birth.getParentsDetails().getFatherEucationid();

                            if (log.isDebugEnabled()) {
                                log.debug("Father Qualification code : \n{}", qualificationCodeFather);
                            }

                            if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeFather)) {
                                errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeFather + "' does not exists");
                            }


                            //Nationality
                            String fatherNationalityCode = birth.getParentsDetails().getFatherNationalityid();
                            if (log.isDebugEnabled()) {
                                log.debug("father Nationality Code: \n{}", fatherNationalityCode);
                            }
                            if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(fatherNationalityCode)) {
                                errorMap.put(COMMON_MDMS_COUNTRY, "The Father Nationality Code'" + fatherNationalityCode + "' does not exists");
                            }
                        }

                        // Mother Information

                        if (!birth.getParentsDetails().getIsMotherInfoMissing()) {

                            //Profession
                            String professionCodeMother = birth.getParentsDetails().getMotherProffessionid();
                            if (log.isDebugEnabled()) {
                                log.debug("Mother Profession code : \n{}", professionCodeMother);
                            }
                            if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeMother)) {

                                errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeMother + "' does not exists");
                            }

                            //Qualification
                            String qualificationCodeMother = birth.getParentsDetails().getMotherEducationid();

                            if (log.isDebugEnabled()) {
                                log.debug("Mother Qualification code : \n{}", qualificationCodeMother);
                            }

                            if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeMother)) {
                                errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeMother + "' does not exists");
                            }

                            //Nationality
                            String motherNationalityCode = birth.getParentsDetails().getMotherNationalityid();

                            if (log.isDebugEnabled()) {
                                log.debug("mother Nationality Code: \n{}", motherNationalityCode);
                            }
                            if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(motherNationalityCode)) {
                                errorMap.put(COMMON_MDMS_COUNTRY, "The Mother Nationality Code'" + motherNationalityCode + "' does not exists");
                            }
                        }
                    }
                });
    }



    private List<String> getBirthPlaceCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BIRTH_PLACES_CODE_JSONPATH);
    }

    private List<String> getInstitutionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODE_JSONPATH);
    }

    private List<String> getHospitalCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODE_JSONPATH);
    }

    private Map<String, Object> getBirthMasterData(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BIRTH_JSONPATH);
    }

    private Map<String, Object> getCommonMasterData(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.MDMS_COMMON_JSONPATH);
    }

    private Map<String, Object> getTenantData(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.MDMS_TENANT_JSONPATH);
    }

    private void validateTenantMasterData(Map<String, Object> masterData) {
        if (masterData.get(BirthConstants.CR_MDMS_TENANTS) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.CR_MDMS_TENANTS
                            + " codes from MDMS"));
        }

    }

    private void validateBirthMasterData(Map<String, Object> masterData) {
        if (masterData.get(BirthConstants.CR_MDMS_PROFESSION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.CR_MDMS_PROFESSION
                            + " codes from MDMS"));
        }

        if (masterData.get(BirthConstants.CR_MDMS_QUALIFICATION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.CR_MDMS_QUALIFICATION
                            + " codes from MDMS"));
        }

        if (masterData.get(BirthConstants.COMMON_MDMS_MEDICAL_ATTENTION_TYPE) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_MEDICAL_ATTENTION_TYPE
                            + " codes from MDMS"));
        }

        if (masterData.get(BirthConstants.CR_MDMS_WORKFLOW_NEW) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.CR_MDMS_WORKFLOW_NEW
                            + " codes from MDMS"));
        }
    }

    private void validateCommonMasterData(Map<String, Object> masterData) {
        if (masterData.get(COMMON_MDMS_RELIGION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_RELIGION
                            + " codes from MDMS"));
        }
        if (masterData.get(COMMON_MDMS_TALUK) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_TALUK
                            + " codes from MDMS"));
        }
        if (masterData.get(COMMON_MDMS_STATE) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_STATE
                            + " codes from MDMS"));
        }
        if (masterData.get(COMMON_MDMS_COUNTRY) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_COUNTRY
                            + " codes from MDMS"));
        }
    }
    //Tenant
    private List<String> getTenantCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_CODE_JSONPATH);
    }


    // CR MASTERS
    private List<String> getProfessionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROFESSION_CODE_JSONPATH);
    }

    private List<String> getQualificationCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_QUALIFICATION_CODE_JSONPATH);
    }

    private List<String> getInstitutionTypeCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INTITUTIONTYPE_CODE_JSONPATH);
    }
    private List<String> getdeliveryMethodCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DELIVERYMETHOD_CODE_JSONPATH);
    }

    // COMMON MASTERS

    private List<String> getReligionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_RELIGION_CODE_JSONPATH);
    }
    private List<String> getTaulkCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_CODE_JSONPATH);
    }
    private List<String> getStateCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_CODE_JSONPATH);
    }

    private List<String> getCountryCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_CODE_JSONPATH);
    }
//    private List<String> getInstitutionCodes(Object mdmsData) {
//        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTION_CODE_JSONPATH);
//    }
    private List<String> getMedicalCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_MEDICAL_ATTENTION_TYPE_CODE_JSONPATH);
    }

    private List<String> getVillageCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_VILLAGE_CODE_JSONPATH);
    }

    private List<String> getDistrictCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_CODE_JSONPATH);
    }

    private List<String> getPostOfficeCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_CODE_JSONPATH);
    }

    private List<String> getLbTypeCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_CODE_JSONPATH);
    }

    private List<String> getBirthPlaceCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_BIRTH_PLACES_CODE_JSONPATH);
    }
}