package org.egov.ewst.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.models.Applicant;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.models.EwasteDetails;
import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.repository.EwasteApplicationRepository;
import org.egov.ewst.service.EwasteService;
import org.egov.ewst.service.WorkflowService;
import org.egov.ewst.util.EwasteConstants;
import org.egov.ewst.util.EwasteUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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

	@Autowired
	private EwasteUtil ewasteUtil;

	/**
	 * Validate the masterData and ctizenInfo of the given ewaste request
	 * 
	 * @param request EwasteRequest for create
	 */
	public void validateCreateRequest(EwasteRegistrationRequest request) {

		Map<String, String> errorMap = new HashMap<>();

		validateMasterData(request, errorMap);
		validateMobileNumber(request, errorMap);
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validates if the fields in EwasteRequest are present in the MDMS master Data
	 *
	 * @param request EwasteRequest received for creating or update
	 *
	 */
	private void validateMasterData(EwasteRegistrationRequest request, Map<String, String> errorMap) {

		EwasteApplication ewasteApplication = request.getEwasteApplication().get(0);
		String tenantId = ewasteApplication.getTenantId().split("\\.")[0];

		List<String> masterNames = new ArrayList<>(Arrays.asList(EwasteConstants.MDMS_EW_PRODUCTNAME));

		Map<String, List<String>> codes = ewasteUtil.getAttributeValues(tenantId, EwasteConstants.MDMS_EW_MOD_NAME,
				masterNames, "$.*.name", EwasteConstants.JSONPATH_CODES, request.getRequestInfo());

		if (null != codes) {
			validateMDMSData(masterNames, codes);
			validateCodes(ewasteApplication, codes, errorMap);
		} else {
			errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validate the masterData and ctizenInfo of the given ewasterequest
	 * 
	 * @param request Ewaste Request for create
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
	 * Checks if the codes of all fields are in the list of codes obtain from master
	 * data
	 *
	 * @param Ewaste   Ewaste from EwasteRequest which are to validated
	 * @param codes    Map of MasterData name to List of codes in that MasterData
	 * @param errorMap Map to fill all errors caught to send as custom Exception
	 * @return Error map containing error if existed
	 *
	 */
	private static Map<String, String> validateCodes(EwasteApplication ewasteApplication,
			Map<String, List<String>> codes, Map<String, String> errorMap) {

		if (!CollectionUtils.isEmpty(ewasteApplication.getEwasteDetails()))
			for (EwasteDetails eDetails : ewasteApplication.getEwasteDetails()) {

				if (!codes.get(EwasteConstants.MDMS_EW_PRODUCTNAME).contains(eDetails.getProductName())) {
					errorMap.put("INVALID PRODUCT CATEGORY ",
							"The Product category '" + eDetails.getProductName() + "' does not exists in MDMS");
				}

			}

		return errorMap;

	}

	/**
	 * Validates the mobileNumber of applicant
	 * 
	 * @param request The ewaste request received for create or update
	 */
	private void validateMobileNumber(EwasteRegistrationRequest request, Map<String, String> errorMap) {

		EwasteApplication ewasteApplication = request.getEwasteApplication().get(0);
		Applicant applicant = ewasteApplication.getApplicant();

		if (!isMobileNumberValid(applicant.getMobileNumber()))
			errorMap.put("INVALID applicant",
					"MobileNumber is not valid for applicant : " + applicant.getApplicantName());

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
