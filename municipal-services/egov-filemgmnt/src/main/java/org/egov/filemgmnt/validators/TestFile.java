package org.egov.filemgmnt.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.util.GlobalException;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantService;
import org.egov.filemgmnt.web.models.ApplicantServiceRequest;
import org.springframework.stereotype.Component;

/**
 * The Class ApplicantPersonalValidator.
 */
@Component
public class ApplicantServiceValidator {

    /**
     * Validate applicant service create request.
     *
     * @param request the {@link ApplicantServiceRequest}
     */
    public void validateCreate(ApplicantServiceRequest request) {
        if (CollectionUtils.isEmpty(request.getApplicantServices())) {
            new GlobalException(ErrorCodes.APPLICANT_SERVICE_REQUIRED, "Atleast one applicant service is required.");
        }
    }

    /**
     * Validate applicant service update request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateUpdate(ApplicantServiceRequest request, List<ApplicantService> searchResult) {
        List<ApplicantService> applicantServices = request.getApplicantServices();

        if (CollectionUtils.isEmpty(applicantServices)) {
            new GlobalException(ErrorCodes.APPLICANT_SERVICE_REQUIRED, "Atleast one applicant service is required.");
        }

        if (applicantServices.size() != searchResult.size()) {
            throw new GlobalException(ErrorCodes.APPLICANT_SERVICE_INVALID_UPDATE,
                    "Applicant Service(s) not found in database.");
        }
    }

}
