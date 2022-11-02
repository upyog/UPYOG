package org.egov.filemgmnt.validators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import org.egov.filemgmnt.repository.ServiceRequestRepository;
import org.egov.filemgmnt.util.FMConstants;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MDMSValidator {

	@Autowired
	private ObjectMapper objectMapper;

	private ServiceRequestRepository requestRepository;

	private ServiceRequestRepository serviceRequestRepository;

//	@Autowired
//	public MDMSValidator(ServiceRequestRepository requestRepository,
//			ServiceRequestRepository serviceRequestRepository) {
//		this.requestRepository = requestRepository;
//
//		this.serviceRequestRepository = serviceRequestRepository;
//	}

	/**
	 * method to validate the mdms data in the request
	 *
	 * @param licenseRequest
	 */
	@SuppressWarnings("unchecked")
	public void validateMdmsData(ApplicantPersonalRequest request, Object mdmsData) {

		Map<String, String> errorMap = new HashMap<>();

		Map<String, List<String>> masterData = getAttributeValues(mdmsData);
		log.info("*** MDMS MASTER DATA \n {}", formatJson(mdmsData));

		String[] masterArray = { FMConstants.FILE_SERVICE_SUBTYPE };
		validateIfMasterPresent(masterArray, masterData);

		List<String> subTypes = masterData.get(FMConstants.FILE_SERVICE_SUBTYPE);

		request.getApplicantPersonals().forEach(personal -> {
			String serviceCode = personal.getServiceDetails().getServiceCode();
			// log.info("service code : \n{}", serviceCode);

			boolean codeFound = false;
//			if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subTypes)
//					&& subTypes.contains(serviceCode)) {
//				codeFound = true;
//			}

			if (!CollectionUtils.isEmpty(subTypes)) {
				for (Object subType : subTypes) {
					Map<String, String> subTypeMap = (Map<String, String>) subType;
					// log.info("subType Map: \n{}", subTypeMap.toString());

					if (serviceCode.equals(subTypeMap.get("code"))) {
						codeFound = true;
					}
				}
//				log.info(" *** \n {}",
//						subTypes.stream().filter(LinkedHashMap.class::isInstance).map(LinkedHashMap.class::cast));

			}

			if (!codeFound) {
				errorMap.put("FileServiceSubtype", "The Service SubType '" + serviceCode + "' does not exists");
			}

		});

		if (MapUtils.isNotEmpty(errorMap)) {
			throw new CustomException(errorMap);
		}
	}

	/**
	 * Fetches all the values of particular attribute as map of field name to list
	 *
	 * takes all the masters from each module and adds them in to a single map
	 *
	 * note : if two masters from different modules have the same name then it
	 *
	 * will lead to overriding of the earlier one by the latest one added to the map
	 *
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	private Map<String, List<String>> getAttributeValues(Object mdmsData) {

		List<String> modulepaths = Arrays.asList(FMConstants.FM_JSONPATH_CODE);

		final Map<String, List<String>> mdmsResMap = new HashMap<>();
		modulepaths.forEach(modulepath -> {
			try {
				log.info("JSONPATH MAP : \n{}", JsonPath.read(mdmsData, modulepath).toString());
				mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
			} catch (Exception e) {
				log.error("Error while fetvhing MDMS data", e);
				throw new CustomException(FMConstants.INVALID_TENANT_ID_MDMS_KEY,
						FMConstants.INVALID_TENANT_ID_MDMS_MSG);
			}
		});
		System.err.println(" the mdms response is : " + mdmsResMap);
		return mdmsResMap;
	}

	/**
	 * Validates if MasterData is properly fetched for the given MasterData names
	 * 
	 * @param masterNames
	 * @param codes
	 */
	private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes) {
		Map<String, String> errorMap = new HashMap<>();
		for (String masterName : masterNames) {
			if (CollectionUtils.isEmpty(codes.get(masterName))) {
				errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
			}
		}
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	private String formatJson(Object json) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonProcessingException e) {
			;
		}
		return "";
	}
}
