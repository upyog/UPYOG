package org.egov.ewst.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.repository.EwasteApplicationRepository;
import org.egov.ewst.service.EwasteService;
import org.egov.ewst.service.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EwasteRequestValidator {

	@Autowired
	private EwasteConfiguration configs;

	@Autowired
	private EwasteService service;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private EwasteApplicationRepository repository;

	/**
	 * Validate the masterData and ctizenInfo of the given ewasterequest
	 * 
	 * @param request PetRequest for create
	 */

	public EwasteApplication validateApplicationExistence(EwasteApplication ewasteApplication) {
		return repository
				.getApplication(
						EwasteApplicationSearchCriteria.builder().requestId(ewasteApplication.getRequestId()).build())
				.get(0);
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
