package org.ksmart.birth.birthregistry.validation;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.*;

@Component
public class RegistryRequestValidator {
    private final BirthConfiguration bndConfig;
    private final MdmsForRegistryValidator mdmsValidator;

    @Autowired
    RegistryRequestValidator(BirthConfiguration bndConfig, MdmsForRegistryValidator mdmsValidator) {

        this.bndConfig = bndConfig;

        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link NewBirthApplication}
     */
    public void validateCreate(RegisterBirthDetailsRequest request, List<RegisterBirthDetail> registerBirthDetails) {
        List<RegisterBirthDetail> registerDetails = request.getRegisterBirthDetails();
        if (CollectionUtils.isEmpty(registerDetails)) {
            throw new CustomException(ErrorCodes.REQUIRED.getCode(),
                    "Register details is required.");
        }

        if (registerDetails.size() > 1) { // NOPMD
            throw new CustomException(REQUIRED.getCode(),
                    "Supports only single Register  application create request.");
        }
        if (StringUtils.isBlank(registerDetails.get(0).getAckNumber())) {
                throw new CustomException(REQUIRED.getCode(),
                        "Application number cannot be null while creating a registry request");
        }
        if (StringUtils.isBlank(registerDetails.get(0).getTenantId())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Tenant id is required for create request.");
        }

        if (registerBirthDetails.size() > 0) {
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Registration exist in the tenant against the given application number.");
        }
//        if (registerDetails.size() > 0) {
//            if (registerBirthDetails.get(0).getRegistrationNo() == null) {
//                throw new CustomException(REQUIRED.getCode(),
//                        "Registration number cannot be null while creating a registry request");
//            }
//        }
            //        if (StringUtils.isBlank(birthApplications.get(0).getTenantId())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Tenant id is required for create request.");
//        }
//
//        if (birthApplications.get(0).getDateOfBirth() == null) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Date of birth is required for create request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getPlaceofBirthId())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Place of birth id is required for create request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getApplicationType())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Application type is required for create request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getBusinessService())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Bussiness service is required for create request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getWorkFlowCode())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Workflow code is required for create request.");
//        }
//
//        if (StringUtils.isBlank(birthApplications.get(0).getAction())) {
//            throw new CustomException(INVALID_CREATE.getCode(),
//                    "Workflow action is required for create request.");
//        }

        //mdmsValidator.validateMdmsData(request, mdmsData);
    }

    public void validateUpdate(RegisterBirthDetailsRequest request, Object mdmsData) {
        List<RegisterBirthDetail> birthApplications = request.getRegisterBirthDetails();
        if (CollectionUtils.isEmpty(request.getRegisterBirthDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (birthApplications.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single application create request.");
        }

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


        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
