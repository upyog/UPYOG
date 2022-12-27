package org.ksmart.birth.birthcorrection.validator;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthDeathConfiguration;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class BirthDetailsValidator {
    private final BirthDeathConfiguration bndCofig;



    @Autowired
    public BirthDetailsValidator(BirthDeathConfiguration bndCofig) {
        this.bndCofig = bndCofig;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link BirthDetailsRequest}
     */
    public void validateCreate(BirthDetailsRequest request, Object mdmsData) {
        if (CollectionUtils.isEmpty(request.getBirthDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

       // mdmsValidator.validateMdmsData(request, mdmsData);
    }


}
