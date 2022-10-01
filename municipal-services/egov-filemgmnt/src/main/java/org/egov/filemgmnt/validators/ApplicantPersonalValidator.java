package org.egov.filemgmnt.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.util.CoreUtils;
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
            throw CoreUtils.newException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED,
                                         "Atleast one applicant personal is required.");
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
            throw CoreUtils.newException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED,
                                         "Atleast one applicant personal is required.");
        }

        if (applicantPersonals.size() != searchResult.size()) {
            throw CoreUtils.newException(ErrorCodes.APPLICANT_PERSONAL_INVALID_UPDATE,
                                         "Applicant Personal(s) not found in database.");
        }
    }

}
