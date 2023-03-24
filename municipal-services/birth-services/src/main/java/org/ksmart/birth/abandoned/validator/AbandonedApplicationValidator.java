package org.ksmart.birth.abandoned.validator;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.BIRTH_DETAILS_REQUIRED;
@Component
public class AbandonedApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final AbandonedMdmsValidator mdmsValidator;

    @Autowired
    AbandonedApplicationValidator(BirthConfiguration bndConfig, AbandonedMdmsValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link NewBirthApplication}
     */
    public void validateCreate(NewBirthDetailRequest request, Object mdmsData) {
        List<NewBirthApplication> applicantPersonals = request.getNewBirthDetails();
        if (CollectionUtils.isEmpty(request.getNewBirthDetails())) {
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (applicantPersonals.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  application create request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
