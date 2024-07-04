package org.upyog.chb.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommunityHallBookingValidator {

	@Autowired
	private MDMSValidator mdmsValidator;

	@Autowired
	private CommunityHallBookingConfiguration config;

	/**
	 * TODO: uncomment validation after adding data to MDMS
	 * 
	 * @param bookingRequest
	 * @param mdmsData
	 */
	public void validateCreate(CommunityHallBookingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for create booking request for mdmsdata : " + mdmsData);
		 mdmsValidator.validateMdmsData(bookingRequest, mdmsData);

	}

	public void validateUpdate(CommunityHallBookingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for update  booking request for mdmsdata : " + mdmsData);

		// mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
	}

	/**
	 * TODO: add slot and document validation here
	 * 
	 * @param bookingRequest
	 */
	private void validateApplicationDocuments(CommunityHallBookingRequest bookingRequest) {

	}

	/**
	 * Validates if the search parameters are valid
	 * 
	 * @param requestInfo The requestInfo of the incoming request
	 * @param criteria    The CommunityHallBookingSearchCriteria Criteria
	 */
	// TODO need to make the changes in the data
	public void validateSearch(RequestInfo requestInfo, CommunityHallBookingSearchCriteria criteria) {
		log.info("Validating search request for criteria " + criteria);
		String userType = requestInfo.getUserInfo().getType();
		if (criteria.isEmpty())
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"Search without any paramters is not allowed");

		if (!userType.equalsIgnoreCase(CommunityHallBookingConstants.EMPLOYEE)
				&& !criteria.isEmpty() && criteria.getTenantId() == null)
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		if (userType.equalsIgnoreCase(CommunityHallBookingConstants.EMPLOYEE)
				&& !criteria.isEmpty() && criteria.getTenantId() == null)
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		String allowedParamStr = null;

		if (!userType.equalsIgnoreCase(CommunityHallBookingConstants.EMPLOYEE))
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
		else if (userType.equalsIgnoreCase(CommunityHallBookingConstants.EMPLOYEE))
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
		else
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"The userType: " + requestInfo.getUserInfo().getType() + " does not have any search config");

		if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"No search parameters are expected");
		else {
			List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
			validateSearchParams(criteria, allowedParams);
		}
	}

	/**
	 * Validates if the paramters coming in search are allowed
	 * 
	 * @param criteria      CHB search criteria
	 * @param allowedParams Allowed Params for search
	 */
	private void validateSearchParams(CommunityHallBookingSearchCriteria criteria, List<String> allowedParams) {
		log.info("Validating search params for allowedParams " + allowedParams);

		if (criteria.getBookingNo() != null && !allowedParams.contains("bookingNo"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"Search on booking no is not allowed");

		if (criteria.getStatus() != null && !allowedParams.contains("status"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search on Status is not allowed");

		if (criteria.getBookingIds() != null && !allowedParams.contains("ids"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search on ids is not allowed");

		if (criteria.getOffset() != null && !allowedParams.contains("offset"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search on offset is not allowed");

		if (criteria.getLimit() != null && !allowedParams.contains("limit"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search on limit is not allowed");

		if (criteria.getFromDate() != null && !allowedParams.contains("limit") && (criteria.getFromDate() > new Date().getTime()))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"From date cannot be a future date");

		if (criteria.getToDate() != null && criteria.getFromDate() != null
				&& (criteria.getFromDate() > criteria.getToDate()))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"To date cannot be prior to from date");
	}

}
