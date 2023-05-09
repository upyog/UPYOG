package org.ksmart.birth.birthnac.validator;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.BIRTH_DETAILS_REQUIRED;
@Component
public class NacApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final NacMdmsValidator mdmsValidator;

    @Autowired
    NacApplicationValidator(BirthConfiguration bndConfig, NacMdmsValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate a adoption details create request.
     *
     * @param request the {@link adoption details}
     */
    public void validateCreate(NacDetailRequest request, Object mdmsData, Object mdmsDataLoc) {
        List<NacApplication> adoption = request.getNacDetails();
        if (CollectionUtils.isEmpty(request.getNacDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Nac details is required.");
        }

        if (adoption.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  application create request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
