package org.egov.filemgmnt.validators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.filemgmnt.util.FMConstants;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MDMSValidator {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * method to validate the mdms data in the request
     *
     * @param licenseRequest
     */
    public void validateMdmsData(ApplicantPersonalRequest request, Object mdmsData) {

        Map<String, List<String>> masterData = getAttributeValues(mdmsData);
        log.info("*** MDMS MASTER DATA:- \n{}", formatJson(masterData));

        String[] masterArray = { FMConstants.FILE_SERVICE_SUBTYPE };
        validateIfMasterPresent(masterArray, masterData);

        Map<String, String> errorMap = new HashMap<>();
        request.getApplicantPersonals()
               .forEach(personal -> {
                   List<String> subTypes = masterData.get(FMConstants.FILE_SERVICE_SUBTYPE);
                   log.info("*** SUB TYPES:- \n{}", subTypes.toString());
                   if (CollectionUtils.isNotEmpty(subTypes)) {

                   }

                   if (!masterData.get(FMConstants.FILE_SERVICE_SUBTYPE)
                                  .contains(personal.getServiceDetails()
                                                    .getServiceCode())) {
                       errorMap.put("FileServiceSubtype",
                                    "The Service SubType '" + personal.getServiceDetails()
                                                                      .getServiceCode()
                                            + "' does not exists");
                   }
                   boolean f = (masterData.get(FMConstants.FILE_SERVICE_SUBTYPE)).contains(personal.getServiceDetails()
                                                                                                   .getServiceCode());
                   log.info("f is    :" + f);
               });
        log.info("errorMap          :" + errorMap);
        log.info("service subtype   :" + masterData.get(FMConstants.FILE_SERVICE_SUBTYPE));
        log.info("getServiceCode    :" + request.getApplicantPersonals()
                                                .get(0)
                                                .getServiceDetails()
                                                .getServiceCode());

        if (MapUtils.isNotEmpty(errorMap)) {
            throw new CustomException(errorMap);
        }
    }

    /**
     * Fetches all the values of particular attribute as map of field name to list
     * takes all the masters from each module and adds them in to a single map note
     * : if two masters from different modules have the same name then it will lead
     * to overriding of the earlier one by the latest one added to the map
     * 
     * @return Map of MasterData name to the list of code in the MasterData
     *
     */
    private Map<String, List<String>> getAttributeValues(Object mdmsData) {

        List<String> modulepaths = Arrays.asList(FMConstants.FM_JSONPATH_CODE);

        final Map<String, List<String>> mdmsResMap = new HashMap<>();
        modulepaths.forEach(modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
            } catch (Exception e) {
                log.error("Error while fetching MDMS data", e);
                throw new CustomException(ErrorCodes.MDMS_INVALID_TENANT_ID.getCode(),
                        FMConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
        });
        System.err.println(" the mdms response is : " + mdmsResMap);
        return mdmsResMap;
    }

    /**
     * Validates if MasterData is properly fetched for the given MasterData names
     * 
     * @param masterNames
     * @param codes
     */
    private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes) {
        final Map<String, String> errorMap = new HashMap<>();

        Arrays.stream(masterNames)
              .forEach(masterName -> {
                  if (CollectionUtils.isEmpty(codes.get(masterName))) {
                      errorMap.put(ErrorCodes.MDMS_DATA_ERROR.getCode(),
                                   "Unable to fetch " + masterName + " codes from MDMS");
                  }
              });

        if (MapUtils.isNotEmpty(errorMap)) {
            throw new CustomException(errorMap);
        }
    }

    private String formatJson(Object json) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                               .writeValueAsString(json);
        } catch (JsonProcessingException e) {
            ;
        }
        return StringUtils.EMPTY;
    }
}
