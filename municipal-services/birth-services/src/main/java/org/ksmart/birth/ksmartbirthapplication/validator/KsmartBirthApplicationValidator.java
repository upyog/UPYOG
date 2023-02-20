package org.ksmart.birth.ksmartbirthapplication.validator;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.BIRTH_DETAILS_REQUIRED;
@Component
public class KsmartBirthApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final KsmartMdmsValidator mdmsValidator;

    @Autowired
    KsmartBirthApplicationValidator(BirthConfiguration bndConfig, KsmartMdmsValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link KsmartBirthDetailsRequest}
     */
    public void validateCreate(KsmartBirthDetailsRequest request, Object mdmsData) {
        List<KsmartBirthAppliactionDetail> applicantPersonals = request.getKsmartBirthDetails();
        if (CollectionUtils.isEmpty(request.getKsmartBirthDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (applicantPersonals.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  application create request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
