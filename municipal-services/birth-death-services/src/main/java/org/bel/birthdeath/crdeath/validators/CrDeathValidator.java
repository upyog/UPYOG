package org.bel.birthdeath.crdeath.validators;

import java.util.List;
import static org.bel.birthdeath.crdeath.web.enums.ErrorCodes.DEATH_DETAILS_REQUIRED;
import static org.bel.birthdeath.crdeath.web.enums.ErrorCodes.DEATH_DETAILS_INVALID_CREATE;

import org.apache.commons.collections4.CollectionUtils;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on  04/12/2022
     */
@Component
public class CrDeathValidator {
    
     /**
     * Validate death  create request.
     *
     * @param request the
     *                {
     *                CrDeathDtlRequest}
     * RAKHI S on 05.12.2022
     */
    public void validateCreate(CrDeathDtlRequest request, Object mdmsData) {
        List<CrDeathDtl> deathDtls = request.getDeathCertificateDtls();

        if (CollectionUtils.isEmpty(deathDtls)) {
            throw new CustomException(DEATH_DETAILS_REQUIRED.getCode(), "Death details is required.");
        }

        if (deathDtls.size() > 1) { // NOPMD
            throw new CustomException(DEATH_DETAILS_INVALID_CREATE.getCode(),
                    "Supports only single death application create request.");
        }

        // mdmsValidator.validateMdmsData(request, mdmsData);
    }
}
