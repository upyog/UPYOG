package org.egov.ptr.validator;

import static org.egov.ptr.util.PTRConstants.JSONPATH_PETSERVICE_RESPONSE;
import static org.egov.ptr.util.PTRConstants.PET_MASTER_BREED_TYPE;
import static org.egov.ptr.util.PTRConstants.PET_MASTER_MODULE_NAME;
import static org.egov.ptr.util.PTRConstants.PET_MASTER_PET_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.util.MdmsUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PetApplicationValidator {

	@Autowired
	private PetRegistrationRepository repository;

	@Autowired
	private MdmsUtil mdmsUtil;

	/**
	 * Validate the masterData and ctizenInfo of the given petRequest
	 * 
	 * @param request PetRequest for create
	 */

	public void validatePetApplication(PetRegistrationRequest petRegistrationRequest) {
		petRegistrationRequest.getPetRegistrationApplications().forEach(application -> {
			if (ObjectUtils.isEmpty(application.getTenantId()))
				throw new CustomException("EG_BT_APP_ERR",
						"tenantId is mandatory for creating pet registration application");
		});
		Map<String, String> errorMap = new HashMap<>();
		validateMasterData(petRegistrationRequest, errorMap);
		isMobileNumberValid(petRegistrationRequest.getPetRegistrationApplications().get(0).getMobileNumber());
	}

	public PetRegistrationApplication validateApplicationExistence(
			PetRegistrationApplication petRegistrationApplication) {
		return repository.getApplications(PetApplicationSearchCriteria.builder()
				.applicationNumber(petRegistrationApplication.getApplicationNumber()).build()).get(0);
	}

	private void validateMasterData(PetRegistrationRequest request, Map<String, String> errorMap) {

		PetRegistrationApplication application = request.getPetRegistrationApplications().get(0);
		String tenantId = application.getTenantId().split("\\.")[0];

		List<String> masterNames = new ArrayList<>(Arrays.asList(PET_MASTER_PET_TYPE, PET_MASTER_BREED_TYPE));

		// Fetch master data from MDMS
		Map<String, List<Map<String, Object>>> petMasterData = mdmsUtil.getAttributeValues(tenantId,
				PET_MASTER_MODULE_NAME, masterNames, null, JSONPATH_PETSERVICE_RESPONSE, request.getRequestInfo());

		if (petMasterData != null && !petMasterData.isEmpty()) {
			validateMDMSDataExistence(masterNames, petMasterData);

			// Convert master data to required format for validation
			Map<String, List<String>> petTypeCodes = petMasterData.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
							.map(obj -> (String) obj.get("name")).collect(Collectors.toList())));

			validateCodes(application, petTypeCodes, errorMap);
		} else {
			errorMap.put("MASTER_FETCH_FAILED", "Couldn't fetch master data for validation");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validates if MasterData is properly fetched for the given MasterData names
	 * 
	 * @param masterNames
	 * @param codes
	 */
	private void validateMDMSDataExistence(List<String> masterNames, Map<String, List<Map<String, Object>>> petcodes) {

		Map<String, String> errorMap = new HashMap<>();
		for (String masterName : masterNames) {
			List<Map<String, Object>> codeList = petcodes.get(masterName);

			if (CollectionUtils.isEmpty(codeList)) {
				errorMap.put("MDMS DATA ERROR", "Unable to fetch " + masterName + " codes from MDMS");
			}
		}

		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

	/**
	 * Checks if the codes of all fields are in the list of codes obtain from master
	 * data
	 *
	 * @param pet      pet from petRequest which are to validated
	 * @param codes    Map of MasterData name to List of codes in that MasterData
	 * @param errorMap Map to fill all errors caught to send as custom Exception
	 * @return Error map containing error if existed
	 *
	 */
	private static Map<String, String> validateCodes(PetRegistrationApplication application,
			Map<String, List<String>> codes, Map<String, String> errorMap) {

		// Check for PetType validation
		if (!codes.get(PET_MASTER_PET_TYPE).contains(application.getPetDetails().getPetType())) {
			errorMap.put("INVALID_PET_CATEGORY",
					"The pet category '" + application.getPetDetails().getPetType() + "' does not exist in MDMS");
		}

		// Check for BreedType validation
		if (!codes.get(PET_MASTER_BREED_TYPE).contains(application.getPetDetails().getBreedType())) {
			errorMap.put("INVALID_PET_BREED",
					"The pet breed '" + application.getPetDetails().getBreedType() + "' does not exist in MDMS");
		}

		return errorMap;
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
