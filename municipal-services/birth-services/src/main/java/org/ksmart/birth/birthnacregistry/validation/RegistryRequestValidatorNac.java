package org.ksmart.birth.birthnacregistry.validation;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.birthnac.validator.NacMdmsValidator;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.*;

@Component
public class RegistryRequestValidatorNac {

	 private final BirthConfiguration bndConfig;
	    private final NacMdmsValidator mdmsValidator;

	    @Autowired
	    RegistryRequestValidatorNac(BirthConfiguration bndConfig, NacMdmsValidator mdmsValidator) {

	        this.bndConfig = bndConfig;

	        this.mdmsValidator = mdmsValidator;
	    }
	    
	    
	    public void validateCreate(RegisterNacRequest request, List<RegisterNac> registerNacDetails) {
	        List<RegisterNac> registerDetails = request.getRegisternacDetails();
	         
	        if (CollectionUtils.isEmpty(registerDetails)) {
	            throw new CustomException(ErrorCodes.REQUIRED.getCode(),
	                    "Register details is required.");
	        }

	        if (registerDetails.size() > 1) { // NOPMD
	            throw new CustomException(REQUIRED.getCode(),
	                    "Supports only single Register  application create request.");
	        }

//	        if (registerNacDetails.size() > 0) {// NOPMD
//	            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
//	                    "Registration exist in the tenant against the given application number.");
//	        }

	        //mdmsValidator.validateMdmsData(request, mdmsData);
	    }

	    public void validateUpdate(NacDetailRequest request, Object mdmsData) {
	        List<NacApplication> birthApplications = request.getNacDetails();
	        if (CollectionUtils.isEmpty(request.getNacDetails())) {
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

//	        mdmsValidator.validateMdmsData(request, mdmsData);
	    }



}

