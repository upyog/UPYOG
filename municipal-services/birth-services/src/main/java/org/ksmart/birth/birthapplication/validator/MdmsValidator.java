package org.ksmart.birth.birthapplication.validator;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.BirthUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ksmart.birth.utils.BirthConstants.*;
import static org.ksmart.birth.utils.enums.ErrorCodes.MDMS_DATA_ERROR;
@Component
@Slf4j
public class MdmsValidator {

    public void validateMdmsData(BirthDetailsRequest request, Object mdmsData) {

        if (log.isDebugEnabled()) {
            log.debug("MDMS master data \n {}", BirthUtils.toJson(mdmsData));
        }

        Map<String, Object> masterBirthData = getBirthMasterData(mdmsData);
        validateBirthMasterData(masterBirthData);

        Map<String, Object> masterCommonData = getCommonMasterData(mdmsData);
        validateCommonMasterData(masterCommonData);

        List<String> professionCodes = getProfessionCodes(mdmsData);
        List<String> religionCodes = getReligionCodes(mdmsData);
        List<String> qualificationCodes = getQualificationCode(mdmsData);

        Map<String, String> errorMap = new ConcurrentHashMap<>();
        request.getBirthDetails()
               .forEach(birth -> {
                   String professionCodeFather = birth.getBirthStatisticalInformation().getFatherProffessionId();
                   String professionCodeMother = birth.getBirthStatisticalInformation().getMotherProffessionId();
                   String religionCode = birth.getBirthStatisticalInformation().getReligionId();
                   String qualificationCodeMother= birth.getBirthStatisticalInformation().getMotherEducationId();

                   if (log.isDebugEnabled()) {
                       log.debug("Father Profession code : \n{}", professionCodeFather);
                   }

                   if (log.isDebugEnabled()) {
                       log.debug("Mother Profession code : \n{}", professionCodeMother);
                   }

                   if (log.isDebugEnabled()) {
                       log.debug("Mother Qualification code : \n{}", qualificationCodeMother);
                   }

                   if (log.isDebugEnabled()) {
                       log.debug("Religion code : \n{}", religionCode);
                   }

                   if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeFather)) {
                       errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeFather + "' does not exists");
                   }

                   if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeMother)) {
                       errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeMother + "' does not exists");
                   }

                   if (CollectionUtils.isEmpty(qualificationCodes) || !qualificationCodes.contains(qualificationCodeMother)) {
                       errorMap.put(CR_MDMS_QUALIFICATION, "The Education code '" + qualificationCodeMother + "' does not exists");
                   }

                   if (CollectionUtils.isEmpty(religionCodes) || !religionCodes.contains(religionCode)) {
                       errorMap.put(COMMON_MDMS_RELIGION, "The Religion code '" + religionCode + "' does not exists");
                   }
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

    private void validateBirthMasterData(Map<String, Object> masterData) {
        if (masterData.get(BirthConstants.CR_MDMS_PROFESSION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                                                               "Unable to fetch "
                                                                       + BirthConstants.CR_MDMS_PROFESSION
                                                                       + " codes from MDMS"));
        }

//        if (masterData.get(BirthConstants.CR_MDMS_QUALIFICATION) == null) {
//            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
//                    "Unable to fetch "
//                            + BirthConstants.CR_MDMS_QUALIFICATION
//                            + " codes from MDMS"));
//        }
    }

    private void validateCommonMasterData(Map<String, Object> masterData) {
       if (masterData.get(COMMON_MDMS_RELIGION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                    "Unable to fetch "
                            + BirthConstants.COMMON_MDMS_RELIGION
                            + " codes from MDMS"));
        }
    }
// CR MASTERS
    private List<String> getProfessionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROFESSION_CODE_JSONPATH);
    }

    private List<String> getQualificationCode(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_QUALIFICATION_CODE_JSONPATH);
    }

    // COMMON MASTERS

    private List<String> getReligionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_RELIGION_CODE_JSONPATH);
    }

}
