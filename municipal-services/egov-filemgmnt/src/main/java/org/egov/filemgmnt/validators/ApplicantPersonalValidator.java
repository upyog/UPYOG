package org.egov.filemgmnt.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.repository.ApplicantPersonalRepository;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ApplicantPersonalValidator.
 */
@Component
public class ApplicantPersonalValidator {

    private final ApplicantPersonalRepository appRepository;
    private final FilemgmntConfiguration config;
    private final MDMSValidator mdmsValidator;

    @Autowired
    public ApplicantPersonalValidator(ApplicantPersonalRepository appRepository, FilemgmntConfiguration config,
                                      MDMSValidator mdmsValidator) {
        this.appRepository = appRepository;
        this.config = config;
        this.mdmsValidator = mdmsValidator;

    }

    /**
     * Validate applicant personal create request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateCreate(ApplicantPersonalRequest request, Object mdmsData) {
        if (CollectionUtils.isEmpty(request.getApplicantPersonals())) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED.getCode(),
                    "Atleast one applicant personal is required.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

    /**
     * Validate applicant personal update request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateUpdate(ApplicantPersonalRequest request, List<ApplicantPersonal> searchResult) {
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        if (CollectionUtils.isEmpty(applicantPersonals)) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED.getCode(),
                    "Atleast one applicant personal is required.");
        }

        if (applicantPersonals.size() != searchResult.size()) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_INVALID_UPDATE.getCode(),
                    "Applicant Personal(s) not found in database.");
        }
    }

}
