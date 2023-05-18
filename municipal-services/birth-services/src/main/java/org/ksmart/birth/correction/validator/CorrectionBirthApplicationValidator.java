package org.ksmart.birth.correction.validator;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.*;

@Component
public class CorrectionBirthApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final CorrectionMdmsValidator mdmsValidator;

    @Autowired
    CorrectionBirthApplicationValidator(BirthConfiguration bndConfig, CorrectionMdmsValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link NewBirthApplication}
     */
    public void validateCreate(CorrectionRequest request, Object mdmsData) {
        List<CorrectionApplication> birthApplications = request.getCorrectionDetails();
        if (CollectionUtils.isEmpty(request.getCorrectionDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (birthApplications.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  correction application create request.");
        }
        if (StringUtils.isBlank(birthApplications.get(0).getRegisterId())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Register id is required for create correction request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getRegistrationNo())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Registration number is required for create correction request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getTenantId())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Tenant id is required for create correction request.");
        }


    }

    public void validateUpdate(CorrectionRequest request, Object mdmsData) {
        List<CorrectionApplication> birthApplications = request.getCorrectionDetails();
        if (CollectionUtils.isEmpty(request.getCorrectionDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (birthApplications.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single application update request.");
        }

//        if (StringUtils.isBlank(birthApplications.get(0).getRegisterId())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Register id is required for update correction request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getRegistrationNo())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Registration number is required for update correction request.");
//        }

        if (StringUtils.isBlank(birthApplications.get(0).getTenantId())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Tenant id is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getId())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application id is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getApplicationType())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application type is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getBusinessService())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Bussiness service is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getApplicationNo())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application number is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getWorkFlowCode())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Workflow code is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getAction())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Workflow action is required for update request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
