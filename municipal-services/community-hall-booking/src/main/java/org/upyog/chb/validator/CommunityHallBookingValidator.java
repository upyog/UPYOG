package org.upyog.chb.validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingSlotDetail;
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
	 * 
	 * @param bookingRequest
	 * @param mdmsData
	 */
	public void validateCreate(CommunityHallBookingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for create booking request for applicant mobile no : "
				+ bookingRequest.getHallsBookingApplication().getApplicantDetail().getApplicantMobileNo());
		if (!isSameHallCode(bookingRequest.getHallsBookingApplication().getBookingSlotDetails())) {
			throw new CustomException(CommunityHallBookingConstants.MULTIPLE_HALL_CODES_ERROR,
					"Booking of multiple halls are not allowed");
		}
		
		if(!validateBookingDate(bookingRequest.getHallsBookingApplication().getBookingSlotDetails())) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_BOOKING_DATE,
					"Booking date is not valid.");
		}

		mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
		validateDuplicateDocuments(bookingRequest);
	}

	public void validateUpdate(CommunityHallBookingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for update  booking request for  applicant mobile no : " + bookingRequest.getHallsBookingApplication()
		.getApplicantDetail().getApplicantMobileNo());
		mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
	}
	
	private boolean validateBookingDate(List<BookingSlotDetail> bookingSlotDetails) {
		LocalDate currentDate = CommunityHallBookingUtil.getCurrentDate();
		boolean isBookingDateValid = bookingSlotDetails.stream().anyMatch(slotDetail ->
		currentDate.isBefore(slotDetail.getBookingDate()));
		return isBookingDateValid;
	}

	/**
	 * 
	 * @param bookingRequest
	 */
	private void validateDuplicateDocuments(CommunityHallBookingRequest bookingRequest) {
		if (bookingRequest.getHallsBookingApplication().getUploadedDocumentDetails() != null) {
			List<String> documentFileStoreIds = new LinkedList<String>();
			bookingRequest.getHallsBookingApplication().getUploadedDocumentDetails().forEach(document -> {
				if (documentFileStoreIds.contains(document.getFileStoreId()))
					throw new CustomException(CommunityHallBookingConstants.DUPLICATE_DOCUMENT_UPLOADED, "Same document cannot be used multiple times");
				else
					documentFileStoreIds.add(document.getFileStoreId());
			});
		} else {
			throw new CustomException(CommunityHallBookingConstants.EMPTY_DOCUMENT_ERROR, "Documents are mandatory for booking.");
		}
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
		
		
		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(CommunityHallBookingConstants.CITIZEN) && criteria.isEmpty())
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search without any paramters is not allowed");

		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(CommunityHallBookingConstants.CITIZEN) && !criteria.tenantIdOnly()
				&& criteria.getTenantId() == null)
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		if (requestInfo.getUserInfo().getType().equalsIgnoreCase(CommunityHallBookingConstants.CITIZEN) && !criteria.isEmpty()
				&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
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
		
		if (criteria.getMobileNumber() != null && !allowedParams.contains("mobileNumber"))
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH, "Search on mobiloe number is not allowed");

		/*
		 * if (criteria.getFromDate() != null &&
		 * (criteria.getFromDate().isBefore(LocalDate.now()))) throw new
		 * CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
		 * "From date cannot be a future date");
		 * 
		 * if (criteria.getToDate() != null && criteria.getFromDate() != null &&
		 * (criteria.getToDate().isBefore(criteria.getFromDate()))) throw new
		 * CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
		 * "To date cannot be prior to from date");
		 */
	}
	
	public boolean isSameHallCode(List<BookingSlotDetail> bookingSlotDetails) {
		String hallCode = bookingSlotDetails.get(0).getHallCode();
		boolean allSameCode = bookingSlotDetails.stream().allMatch(x -> x.getHallCode().equals(hallCode));
		return allSameCode;

	}

}
