package org.upyog.chb.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.web.models.CommunityHallBookingRequest;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MDMSValidator {

	/**
	 * method to validate the mdms data in the request
	 *
	 * @param communityHallBookingRequest
	 */
	public void validateMdmsData(CommunityHallBookingRequest communityHallBookingRequest, Object mdmsData) {

		Map<String, List<String>> masterData = getAttributeValues(mdmsData);
		/*
		 * if(MdmsUtil.getMDMSDataMap().isEmpty()) {
		 * MdmsUtil.setMDMSDataMap(masterData); }
		 */
		String[] masterArray = { CommunityHallBookingConstants.CHB_PURPOSE, CommunityHallBookingConstants.CHB_SPECIAL_CATEGORY,
				CommunityHallBookingConstants.CHB_COMMNUITY_HALLS,
				CommunityHallBookingConstants.CHB_HALL_CODES, CommunityHallBookingConstants.CHB_DOCUMENTS};
		
		log.info("Validating master data from MDMS for : " + communityHallBookingRequest.getHallsBookingApplication().getBookingNo());

		validateIfMasterPresent(masterArray, masterData);
	}


	/**
	 * Fetches all the values of particular attribute as map of field name to
	 * list
	 *
	 * takes all the masters from each module and adds them in to a single map
	 *
	 * note : if two masters from different modules have the same name then it
	 *
	 * will lead to overriding of the earlier one by the latest one added to the
	 * map
	 *
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	public Map<String, List<String>> getAttributeValues(Object mdmsData) {

		List<String> modulepaths = Arrays.asList(CommunityHallBookingConstants.CHB_JSONPATH_CODE,
				CommunityHallBookingConstants.COMMON_MASTER_JSONPATH_CODE);
		final Map<String, List<String>> mdmsResMap = new HashMap<>();
		modulepaths.forEach(modulepath -> {
			try {
				mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
			} catch (Exception e) {
				throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT_ID_MDMS_KEY,
						CommunityHallBookingConstants.INVALID_TENANT_ID_MDMS_MSG);
			}
		});
		return mdmsResMap;
	}

	/**
	 * Validates if MasterData is properly fetched for the given MasterData
	 * names
	 * 
	 * @param masterNames
	 * @param codes
	 */
	private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes) {
		log.info("Master names in validation : " + masterNames);
		log.info("Mdms data map : " + codes);
		Map<String, String> errorMap = new HashMap<>();
		for (String masterName : masterNames) {
			if (CollectionUtils.isEmpty(codes.get(masterName))) {
				errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
			}
		}
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

}
