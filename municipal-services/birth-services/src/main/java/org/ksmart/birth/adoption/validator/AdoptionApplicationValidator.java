package org.ksmart.birth.adoption.validator;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;

import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.BIRTH_DETAILS_REQUIRED;
@Component
public class AdoptionApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final AdoptionMdmsValidator mdmsValidator;

    @Autowired
    AdoptionApplicationValidator(BirthConfiguration bndConfig, AdoptionMdmsValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate a adoption details create request.
     *
     * @param request the {@link adoption details}
     */
    public void validateCreate(AdoptionDetailRequest request, Object mdmsData, Object mdmsDataLoc) {
        List<AdoptionApplication> adoption = request.getAdoptionDetails();
        if (CollectionUtils.isEmpty(request.getAdoptionDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (adoption.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  application create request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData,mdmsDataLoc);
    }

}
