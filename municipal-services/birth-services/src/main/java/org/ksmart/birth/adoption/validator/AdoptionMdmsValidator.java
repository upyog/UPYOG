package org.ksmart.birth.adoption.validator;

import com.jayway.jsonpath.JsonPath;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.util.StringUtil;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.BirthUtils;
import org.ksmart.birth.web.model.adoption.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ksmart.birth.utils.BirthConstants.*;
import static org.ksmart.birth.utils.enums.ErrorCodes.MDMS_DATA_ERROR;

@Component
@Slf4j
public class AdoptionMdmsValidator {

    public void validateMdmsData(AdoptionDetailRequest request, Object mdmsData,Object mdmsDataLoc) {

        if (log.isDebugEnabled()) {
            log.debug("MDMS master data \n {}", BirthUtils.toJson(mdmsData));
        }

        Map<String, Object> masterBirthData = getBirthMasterData(mdmsData);
        validateBirthMasterData(masterBirthData);


        Map<String, Object> masterCommonData = getCommonMasterData(mdmsData);
        validateCommonMasterData(masterCommonData);

        Map<String, Object> masterTenantData = getTenantData(mdmsData);
        validateTenantMasterData(masterTenantData);

        List<String> tenantCode = getTenantCodes(mdmsData);

        List<String> professionCodes = getProfessionCodes(mdmsData);
        List<String> qualificationCodes = getQualificationCode(mdmsData);

        List<String> religionCodes = getReligionCodes(mdmsData);

        List<String> talukCodes = getTaulkCodes(mdmsData);
        List<String> stateCodes = getStateCodes(mdmsData);
        List<String> countryCodes = getCountryCodes(mdmsData);
        List<String> instCodes = getInstitutionCodes(mdmsDataLoc);
        List<String> hospCodes =getHospitalCodes(mdmsDataLoc);
        List<String> medicalCodes = getMedicalCodes(mdmsData);
        List<String> villageCodes = getVillageCode(mdmsData);
        List<String> districtCodes = getDistrictCode(mdmsData);
        List<String> postOfficeCodes = getPostOfficeCode(mdmsData);
        List<String> institutionTypeCodes = getInstitutionTypeCode(mdmsData);
        List<String> deliveryMethodCodes = getdeliveryMethodCode(mdmsData);
        List<String> lBTypeCodes = getLbTypeCode(mdmsData);
        List<String> birthPlaceCodes = getBirthPlaceCode(mdmsData);

        Map<String, String> errorMap = new ConcurrentHashMap<>();
        request.getAdoptionDetails()
                .forEach(birth -> {
//                	if(birth.getPregnancyDuration() == null) {
//                		  errorMap.put(BIRTH_DURATION, "Duration of pregnancy required");
//                	}
//                	 
                	  
                    if(birth.getParentAddress() != null) {
                        String religionCode = birth.getParentsDetails().getReligionId();
                        if (log.isDebugEnabled()) {
                        log.debug("Religion code : \n{}", religionCode);
                    }
                        if (CollectionUtils.isEmpty(religionCodes) || !religionCodes.contains(religionCode)) {
                        errorMap.put(COMMON_MDMS_RELIGION, "The Religion code '" + religionCode + "' does not exists");
                    }

                        System.out.println("master  :"+birth.getParentsDetails().getIsFatherInfoMissing());
                        if (birth.getParentsDetails().getIsFatherInfoMissing() == false) {
                            String professionCodeFather = birth.getParentsDetails().getFatherProffessionid();
                            if (log.isDebugEnabled()) {
                                log.debug("Father Profession code : \n{}", professionCodeFather);
                            }
                            if (CollectionUtils.isEmpty(professionCodes) ||   !professionCodes.contains(professionCodeFather)) {

                                errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeFather + "' does not exists");
                            }

                            String qualificationCodeFather = birth.getParentsDetails().getFatherEucationid();

                            if (log.isDebugEnabled()) {
                                log.debug("Father Qualification code : \n{}", qualificationCodeFather);
                            }

                            if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeFather)) {
                                errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeFather + "' does not exists");
                            }

                            String fatherNationalityCode = birth.getParentsDetails().getFatherNationalityid();
                            if (log.isDebugEnabled()) {
                                log.debug("father Nationality Code: \n{}", fatherNationalityCode);
                            }
                            if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(fatherNationalityCode)) {
                                errorMap.put(COMMON_MDMS_COUNTRY, "The Father Nationality Code'" + fatherNationalityCode + "' does not exists");
                            }
                        }

                        if (birth.getParentsDetails().getIsMotherInfoMissing() == false) {
                            String professionCodeMother = birth.getParentsDetails().getMotherProffessionid();
                            if (log.isDebugEnabled()) {
                                log.debug("Father Profession code : \n{}", professionCodeMother);
                            }
                            if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeMother)) {

                                errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeMother + "' does not exists");
                            }

                            String qualificationCodeMother = birth.getParentsDetails().getMotherEducationid();

                            if (log.isDebugEnabled()) {
                                log.debug("Mother Qualification code : \n{}", qualificationCodeMother);
                            }

                            if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeMother)) {
                                errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeMother + "' does not exists");
                            }

                            String motherNationalityCode = birth.getParentsDetails().getMotherNationalityid();

                            if (log.isDebugEnabled()) {
                                log.debug("mother Nationality Code: \n{}", motherNationalityCode);
                            }
                            if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(motherNationalityCode)) {
                                errorMap.put(COMMON_MDMS_COUNTRY, "The Mother Nationality Code'" + motherNationalityCode + "' does not exists");
                            }
                        }
                    }
                    if(birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
                        String postOfficeCodePlace = birth.getAdrsPostOffice();
                        if (log.isDebugEnabled()) {
                            log.debug("Postoffice code : \n{}", postOfficeCodePlace);
                            if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePlace)) {
                                errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code '" + postOfficeCodePlace + "' does not exists");
                            }
                        }

                    }
                    System.out.println("master  :"+birth.getPlaceofBirthId());
                    if(birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)) {
                    	
                    	 String hospCode= birth.getHospitalId();   
                    	 if (log.isDebugEnabled()) {
                             log.debug("Hospital code : \n{}", hospCode);
	                         if (CollectionUtils.isEmpty(hospCodes) || !hospCodes.contains(hospCode)) {
	                           errorMap.put(COMMON_MDMS_HOSPITAL, "The Hospital code '" + hospCode + "' does not exists");
	                        }
                    	 }                       
                         
                     

                    }
                  
                    if(birth.getPlaceofBirthId().contains(BIRTH_PLACE_HOME)) {
                        String postOfficeCodePlace = birth.getAdrsPostOffice();
                        if (log.isDebugEnabled()) {
                            log.debug("Postoffice code : \n{}", postOfficeCodePlace);
                            if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePlace)) {
                                errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code '" + postOfficeCodePlace + "' does not exists");
                            }
                        }

                    }

                    if(birth.getParentAddress() != null) {
                        String talukCodePresent = birth.getParentAddress().getPresentInsideKeralaTaluk();
                        String talukCodePermanent = birth.getParentAddress().getPermntInKeralaAdrTaluk();

                        String villageCodePresent = birth.getParentAddress().getPresentInsideKeralaVillage();
                        String villageCodePermanent = birth.getParentAddress().getPermntInKeralaAdrVillage();

                        String districtInKeralaCodePresent = birth.getParentAddress().getPresentInsideKeralaDistrict();
                        String districtInKeralaCodePermanent = birth.getParentAddress().getPermntInKeralaAdrDistrict();

                        String districtOutKeralaCodePresent = birth.getParentAddress().getPresentOutsideKeralaDistrict();
                        String districtOutKeralaCodePermanent = birth.getParentAddress().getPermntOutsideKeralaDistrict();

                        String stateCodesPresent = birth.getParentAddress().getPresentaddressStateName();
                        String stateCodesPermanent = birth.getParentAddress().getPermtaddressStateName();

                        String countryCodesPresent = birth.getParentAddress().getPresentaddressCountry();
                        String countryCodesPermanent = birth.getParentAddress().getPermtaddressCountry();


                    String postOfficeCodePresent = birth.getParentAddress().getPresentInsideKeralaPostOffice();
                    String postOfficeCodePermanent = birth.getParentAddress().getPermntInKeralaAdrPostOffice();


                    }
                    
                    
                    //MDMS CODE VALIDATION START //////////////////////////////////////////////////////////////////
                   
                    String birthPlaceCode = birth.getPlaceofBirthId();
                    if (CollectionUtils.isEmpty(birthPlaceCodes) || !birthPlaceCodes.contains(birthPlaceCode)) {
                      errorMap.put(COMMON_MDMS_PLACEMASTER, "The Birth Place code '" + birthPlaceCode + "' does not exists");
                  }
//                    String institutionCode= birth.getInstitutionNameCode();
//                    if (CollectionUtils.isEmpty(instCodes) || !instCodes.contains(institutionCode)) {
//                      errorMap.put(COMMON_MDMS_INSTITUTION, "The Institution code '" + institutionCode + "' does not exists");
//                  }
//                    //
//                    String talukCodePresent= birth.getBirthPresentAddress().getTalukId();
//                    String talukCodePermanent= birth.getBirthPermanentAddress().getTalukId();
//                    String talukCodePlace= birth.getBirthPlace().getHoTalukId();
//                    String stateCodesPresent= birth.getBirthPresentAddress().getStateId();
//                    String stateCodesPermanent= birth.getBirthPermanentAddress().getStateId();
//                    String stateCodesPlace= birth.getBirthPlace().getHoStateId();
//                    String stateCodesStatitical= birth.getBirthStatisticalInformation().getMotherResdnceStateId();
//                    String countryCodesPresent= birth.getBirthPresentAddress().getCountryId();
//                    String countryCodesPermanent= birth.getBirthPermanentAddress().getCountryId();
//                    String countryCodesPlace= birth.getBirthPlace().getHoCountryId();
//                    String countryCodesStatitical= birth.getBirthStatisticalInformation().getMotherResdnceCountryId();
//                    String motherNationalityCode=birth.getBirthStatisticalInformation().getMotherNationalityId();
//                    String fatherNationalityCode=birth.getBirthStatisticalInformation().getFatherNationalityId();
//                    String institutionCode= birth.getBirthPlace().getInstitutionId();
//                    String medicalCode= birth.getBirthStatisticalInformation().getNatureOfMedicalAttention();
//                    String villageCodePresent = birth.getBirthPresentAddress().getVillageId();
//                    String villageCodePermanent = birth.getBirthPermanentAddress().getVillageId();
//                    String villageCodePlace = birth.getBirthPlace().getHoVillageId();
//                    String districtCodePresent = birth.getBirthPresentAddress().getDistrictId();
//                    String districtCodePermanent = birth.getBirthPermanentAddress().getDistrictId();
//                    String districtCodePlace = birth.getBirthPlace().getHoDistrictId();
//                    String districtCodeStatitical = birth.getBirthStatisticalInformation().getMotherResdnceDistrictId();
//                    String postOfficeCodePlace = birth.getBirthPlace().getHoPoId();
//                    String postOfficeCodePresent = birth.getBirthPresentAddress().getPoId();
//                    String postOfficeCodePermanent = birth.getBirthPermanentAddress().getPoId();
//                    String institutionTypeCodePlace = birth.getBirthPlace().getInstitutionTypeId();
//                    String deliveryMethodCode = birth.getBirthStatisticalInformation().getDeliveryMethod();
//                    String lBTypeCode = birth.getBirthStatisticalInformation().getMotherResdnceLbType();
//                    String birthPlaceCode = birth.getBirthPlace().getPlaceOfBirthId();
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Mother Profession code : \n{}", professionCodeMother);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Mother Qualification code : \n{}", qualificationCodeMother);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Religion code : \n{}", religionCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Taulk code Present: \n{}", talukCodePresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Taulk code Permanent: \n{}", talukCodePermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Taulk code Place: \n{}", talukCodePlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("State code Present: \n{}", stateCodesPresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("State code Permanent: \n{}", stateCodesPermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("State code Place: \n{}", stateCodesPlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("State code Statitical: \n{}", stateCodesStatitical);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Country code Present: \n{}", countryCodesPresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Country code Permanent: \n{}", countryCodesPermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Country code Place: \n{}", countryCodesPlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Country code Statitical: \n{}", countryCodesStatitical);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("mother Nationality Code: \n{}", motherNationalityCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("father Nationality Code: \n{}", fatherNationalityCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Institution code : \n{}", institutionCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Medical Code : \n{}", medicalCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Village code : \n{}", villageCodePresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Village code : \n{}", villageCodePermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Village code : \n{}", villageCodePlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("District code : \n{}", districtCodePresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("District code : \n{}", districtCodePermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("District code : \n{}", districtCodePlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("District code : \n{}", districtCodeStatitical);
//                    }
//
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Postoffice code : \n{}", postOfficeCodePlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Postoffice code : \n{}", postOfficeCodePresent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Postoffice code : \n{}", postOfficeCodePermanent);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Institution Type code : \n{}", institutionTypeCodePlace);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("Delivery method code : \n{}", deliveryMethodCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("LBType code : \n{}", lBTypeCode);
//                    }
//
//                    if (log.isDebugEnabled()) {
//                        log.debug("place of birth code : \n{}", birthPlaceCode);
//                    }
//
//
//
//                    if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeMother)) {
//                        errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeMother + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeMother)) {
//                        errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeMother + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(religionCodes) || !religionCodes.contains(religionCode)) {
//                        errorMap.put(COMMON_MDMS_RELIGION, "The Religion code '" + religionCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(talukCodes) || !talukCodes.contains(talukCodePresent)) {
//                        errorMap.put(COMMON_MDMS_TALUK, "The Taulk code Present'" + talukCodePresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(talukCodes) || !talukCodes.contains(talukCodePermanent)) {
//                        errorMap.put(COMMON_MDMS_TALUK, "The Taulk code Permanent'" + talukCodePermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(talukCodes) || !talukCodes.contains(talukCodePlace)) {
//                        errorMap.put(COMMON_MDMS_TALUK, "The Taulk code Place'" + talukCodePlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(stateCodes) || !stateCodes.contains(stateCodesPresent)) {
//                        errorMap.put(COMMON_MDMS_STATE, "The State code Present'" + stateCodesPresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(stateCodes) || !stateCodes.contains(stateCodesPermanent)) {
//                        errorMap.put(COMMON_MDMS_STATE, "The State code Permanent'" + stateCodesPermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(stateCodes) || !stateCodes.contains(stateCodesPlace)) {
//                        errorMap.put(COMMON_MDMS_STATE, "The State code Place'" + stateCodesPlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(stateCodes) || !stateCodes.contains(stateCodesStatitical)) {
//                        errorMap.put(COMMON_MDMS_STATE, "The State code Statitical'" + stateCodesStatitical + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(countryCodesPresent)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Country code Present'" + countryCodesPresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(countryCodesPermanent)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Country code Permanent'" + countryCodesPermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(countryCodesPlace)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Country code Place'" + countryCodesPlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(motherNationalityCode)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Mother Nationality Code'" + motherNationalityCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(fatherNationalityCode)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Father Nationality Code'" + fatherNationalityCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(countryCodes) || !countryCodes.contains(countryCodesStatitical)) {
//                        errorMap.put(COMMON_MDMS_COUNTRY, "The Country code Statitical'" + countryCodesStatitical + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(instCodes) || !instCodes.contains(institutionCode)) {
//                        errorMap.put(COMMON_MDMS_INSTITUTION, "The Institution code '" + institutionCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(medicalCodes) || !medicalCodes.contains(medicalCode)) {
//                        errorMap.put(COMMON_MDMS_MEDICAL_ATTENTION_TYPE, "The medicalCode code '" + medicalCode + "' does not exists");
//
//                    }
//
//                    if (CollectionUtils.isEmpty(villageCodes) || !villageCodes.contains(villageCodePresent)) {
//                        errorMap.put(COMMON_MDMS_VILLAGE, "The Village code '" + villageCodePresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(villageCodes) || !villageCodes.contains(villageCodePermanent)) {
//                        errorMap.put(COMMON_MDMS_VILLAGE, "The Village code '" + villageCodePermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(villageCodes) || !villageCodes.contains(villageCodePlace)) {
//                        errorMap.put(COMMON_MDMS_VILLAGE, "The Village code '" + villageCodePlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(districtCodes) || !districtCodes.contains(districtCodePresent)) {
//                        errorMap.put(COMMON_MDMS_DISTRICT, "The District code '" + districtCodePresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(districtCodes) || !districtCodes.contains(districtCodePermanent)) {
//                        errorMap.put(COMMON_MDMS_DISTRICT, "The District code '" + districtCodePermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(districtCodes) || !districtCodes.contains(districtCodePlace)) {
//                        errorMap.put(COMMON_MDMS_DISTRICT, "The District code '" + districtCodePlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(districtCodes) || !districtCodes.contains(districtCodeStatitical)) {
//                        errorMap.put(COMMON_MDMS_DISTRICT, "The District code '" + districtCodeStatitical + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePlace)) {
//                        errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code '" + postOfficeCodePlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePresent)) {
//                        errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code '" + postOfficeCodePresent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(postOfficeCodes) || !postOfficeCodes.contains(postOfficeCodePermanent)) {
//                        errorMap.put(COMMON_MDMS_POSTOFFICE, "The Postoffice code '" + postOfficeCodePermanent + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(institutionTypeCodes) || !institutionTypeCodes.contains(institutionTypeCodePlace)) {
//                        errorMap.put(CR_MDMS_INSTITUTIONTYPE, "The Institution Type code '" + institutionTypeCodePlace + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(deliveryMethodCodes) || !deliveryMethodCodes.contains(deliveryMethodCode)) {
//                        errorMap.put(CR_MDMS_DELIVERYMETHOD, "The Delivery Method code '" + deliveryMethodCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(lBTypeCodes) || !lBTypeCodes.contains(lBTypeCode)) {
//                        errorMap.put(COMMON_MDMS_LBTYPE, "The LBType code '" + lBTypeCode + "' does not exists");
//                    }
//
//                    if (CollectionUtils.isEmpty(birthPlaceCodes) || !birthPlaceCodes.contains(birthPlaceCode)) {
//                        errorMap.put(COMMON_MDMS_PLACEMASTER, "The Birth Place code '" + birthPlaceCode + "' does not exists");
//                    }
                });

        if (MapUtils.isNotEmpty(errorMap)) {
            throw new CustomException(errorMap);
        }
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
//System.out.println("master  :"+masterData);
//        if (masterData.get(BirthConstants.COMMON_MDMS_INSTITUTION) == null) {
//            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
//                    "Unable to fetch "
//                            + BirthConstants.COMMON_MDMS_INSTITUTION
//                            + " codes from MDMS"));
//        }

        if (masterData.get(BirthConstants.COMMON_MDMS_MEDICAL_ATTENTION_TYPE) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_MEDICAL_ATTENTION_TYPE
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
    private List<String> getInstitutionCodes(Object mdmsData) {    	
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INSTITUTIONS_CODE_JSONPATH);
    }
    
    private List<String> getHospitalCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_HOSPITALS_CODE_JSONPATH);
    }
    
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
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PLACEMASTER_CODE_JSONPATH);
    }
}