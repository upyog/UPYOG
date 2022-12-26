package org.ksmart.birth.crbirth.validator;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.crbirth.model.BirthDetailsRequest;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.BirthUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ksmart.birth.utils.BirthConstants.CR_MDMS_PROFESSION;
import static org.ksmart.birth.utils.enums.ErrorCodes.MDMS_DATA_ERROR;
@Component
@Slf4j
public class MdmsValidator {

//    private final ServiceRequestRepository requestRepository;
//
//    @Autowired
//    public MdmsValidator(ServiceRequestRepository requestRepository) {
//        this.requestRepository = requestRepository;
//    }

    public void validateMdmsData(BirthDetailsRequest request, Object mdmsData) {

        if (log.isDebugEnabled()) {
            log.debug("MDMS master data \n {}", BirthUtils.toJson(mdmsData));
        }

       // Map<String, Object> masterData = getBirthMasterData(mdmsData);
       // validateBirthMasterData(masterData);

        List<String> professionCodes = getProfessionCodes(mdmsData);

        Map<String, String> errorMap = new ConcurrentHashMap<>();
        request.getBirthDetails()
               .forEach(birth -> {
                   String professionCodeFather = birth.getBirthStatisticalInformation().getFatherProffessionId();

                   if (log.isDebugEnabled()) {
                       log.debug("Profession code : \n{}", professionCodeFather);
                   }

                   if (CollectionUtils.isEmpty(professionCodes) || !professionCodes.contains(professionCodeFather)) {
                       errorMap.put(CR_MDMS_PROFESSION, "The Profession code '" + professionCodeFather + "' does not exists");
                   }
               });

        if (MapUtils.isNotEmpty(errorMap)) {
            throw new CustomException(errorMap);
        }
    }

    private Map<String, Object> getBirthMasterData(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.COMMON_MASTER_JSONPATH_CODE);
    }

    private void validateBirthMasterData(Map<String, Object> masterData) {
        if (masterData.get(CR_MDMS_PROFESSION) == null) {
            throw new CustomException(Collections.singletonMap(MDMS_DATA_ERROR.getCode(),
                                                               "Unable to fetch "
                                                                       + CR_MDMS_PROFESSION
                                                                       + " codes from MDMS"));
        }
    }

    private List<String> getProfessionCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROFESSION_CODE_JSONPATH);
    }

}
