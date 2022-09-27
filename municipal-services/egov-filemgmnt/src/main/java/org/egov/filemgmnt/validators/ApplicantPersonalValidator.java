package org.egov.filemgmnt.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.util.GlobalException;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.springframework.stereotype.Component;

/**
 * The Class ApplicantPersonalValidator.
 */
@Component
public class ApplicantPersonalValidator {

    /**
     * Validate applicant personal create request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateCreate(ApplicantPersonalRequest request) {
        if (CollectionUtils.isEmpty(request.getApplicantPersonals())) {
            new GlobalException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED, "Atleast one applicant personal is required.");
        }
    }

    /**
     * Validate applicant personal update request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateUpdate(ApplicantPersonalRequest request, List<ApplicantPersonal> searchResult) {
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        if (CollectionUtils.isEmpty(applicantPersonals)) {
            new GlobalException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED, "Atleast one applicant personal is required.");
        }

        if (applicantPersonals.size() != searchResult.size()) {
            throw new GlobalException(ErrorCodes.APPLICANT_PERSONAL_INVALID_UPDATE,
                    "Applicant Personal(s) not found in database.");
        }
    }

}
