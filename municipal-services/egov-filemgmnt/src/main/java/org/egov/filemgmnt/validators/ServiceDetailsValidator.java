package org.egov.filemgmnt.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.filemgmnt.util.CoreUtils;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ServiceDetails;
import org.egov.filemgmnt.web.models.ServiceDetailsRequest;
import org.springframework.stereotype.Component;

@Component
public class ServiceDetailsValidator {

    /**
     * Validate applicant service create request.
     *
     * @param request the {@link ApplicantServiceRequest}
     */
    public void validateCreate(ServiceDetailsRequest request) {
        if (CollectionUtils.isEmpty(request.getServiceDetails())) {
            throw CoreUtils.newException(ErrorCodes.APPLICANT_SERVICE_REQUIRED,
                                         "Atleast one applicant service is required.");
        }
    }

    /**
     * Validate applicant service update request.
     *
     * @param request the {@link ServiceDetailsRequest}
     */
    public void validateUpdate(ServiceDetailsRequest request, List<ServiceDetails> searchResult) {
        List<ServiceDetails> applicantServices = request.getServiceDetails();

        if (CollectionUtils.isEmpty(applicantServices)) {
            throw CoreUtils.newException(ErrorCodes.APPLICANT_SERVICE_REQUIRED,
                                         "Atleast one applicant service is required.");
        }

        if (applicantServices.size() != searchResult.size()) {
            throw CoreUtils.newException(ErrorCodes.APPLICANT_SERVICE_INVALID_UPDATE,
                                         "Applicant Service(s) not found in database.");
        }
    }

}
