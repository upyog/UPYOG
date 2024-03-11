package org.egov.ptr.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.service.PetRegistrationService;
import org.egov.ptr.service.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PetApplicationValidator {

	@Autowired
	private PetConfiguration configs;

	@Autowired
	private PetRegistrationService service;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private PetRegistrationRepository repository;

	/**
	 * Validate the masterData and ctizenInfo of the given petRequest
	 * 
	 * @param request PetRequest for create
	 */

	public void validatePetApplication(PetRegistrationRequest petRegistrationRequest) {
		petRegistrationRequest.getPetRegistrationApplications().forEach(application -> {
			if (ObjectUtils.isEmpty(application.getTenantId()))
				throw new CustomException("EG_BT_APP_ERR",
						"tenantId is mandatory for creating pet registration applications");
		});
	}

	public PetRegistrationApplication validateApplicationExistence(
			PetRegistrationApplication petRegistrationApplication) {
		return repository.getApplications(PetApplicationSearchCriteria.builder()
				.applicationNumber(petRegistrationApplication.getApplicationNumber()).build()).get(0);
	}

	/**
	 * Validates if MasterData is properly fetched for the given MasterData names
	 * 
	 * @param masterNames
	 * @param codes
	 */
	private void validateMDMSData(List<String> masterNames, Map<String, List<String>> codes) {

		Map<String, String> errorMap = new HashMap<>();
		for (String masterName : masterNames) {
			if (CollectionUtils.isEmpty(codes.get(masterName))) {
				errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
			}
		}
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validates if the mobileNumber is 10 digit and starts with 5 or greater
	 * 
	 * @param mobileNumber The mobileNumber to be validated
	 * @return True if valid mobileNumber else false
	 */
	private Boolean isMobileNumberValid(String mobileNumber) {

		if (mobileNumber == null)
			return false;
		else if (mobileNumber.length() != 10)
			return false;
		else if (Character.getNumericValue(mobileNumber.charAt(0)) < 5)
			return false;
		else
			return true;
	}

}
