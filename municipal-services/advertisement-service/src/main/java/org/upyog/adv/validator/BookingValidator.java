package org.upyog.adv.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CartDetail;

import lombok.extern.slf4j.Slf4j;
/**
 * Validator class for validating booking requests in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Validates booking requests for creating and updating advertisement bookings.
 * - Ensures that the provided data adheres to the defined business rules and constraints.
 * - Validates master data from MDMS for consistency and correctness.
 * 
 * Methods:
 * - `validateCreate`: Validates booking requests during the creation process.
 * - `validateUpdate`: Validates booking requests during the update process.
 * 
 * Dependencies:
 * - MDMSValidator: Validates master data from MDMS.
 * - BookingConfiguration: Provides configuration properties for validation.
 * - BookingUtil: Provides utility methods for validation-related operations.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 * - @Slf4j: Enables logging for debugging and monitoring validation processes.
 */
@Component
@Slf4j
public class BookingValidator {

	private final MDMSValidator mdmsValidator;
	private final BookingConfiguration config;

	public BookingValidator(MDMSValidator mdmsValidator, BookingConfiguration config) {
		this.mdmsValidator = mdmsValidator;
		this.config = config;
	}

	/**
	 * Validates a create booking request against business rules and MDMS master data.
	 *
	 * @param bookingRequest booking request to validate
	 * @param mdmsData MDMS master data used for validation
	 */
	public void validateCreate(BookingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for create booking request for applicant mobile no : "
				+ bookingRequest.getBookingApplication().getApplicantDetail().getApplicantMobileNo());

		List<CartDetail> cartDetails = bookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new CustomException(BookingConstants.INVALID_BOOKING_DATE, "Cart details are required for booking.");
		}

		if (!validateBookingDate(cartDetails)) {
			throw new CustomException(BookingConstants.INVALID_BOOKING_DATE, "Booking date is not valid.");
		}

		mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
		validateDuplicateDocuments(bookingRequest);
	}

	private boolean validateBookingDate(List<CartDetail> cartDetails) {
		LocalDate currentDate = BookingUtil.getCurrentDate();
		return cartDetails.stream().anyMatch(cartDetail -> currentDate.isBefore(cartDetail.getBookingDate()));
	}

	public void validateUpdate(BookingRequest bookingDetailFromRequest, Object mdmsData, String status) {
		log.info("validating master data for update  booking request for  booking no : "
				+ bookingDetailFromRequest.getBookingApplication().getBookingNo());

		if (status == null || !isValidStatus(status)) {
			throw new CustomException("INVALID_STATUS", "The status " + status + " is not valid.");
		}

		mdmsValidator.validateMdmsData(bookingDetailFromRequest, mdmsData);
		validateDuplicateDocuments(bookingDetailFromRequest);
	}

	public boolean isValidStatus(String status) {
		try {
			BookingStatusEnum.valueOf(status);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private void validateDuplicateDocuments(BookingRequest bookingRequest) {
		if (bookingRequest.getBookingApplication().getUploadedDocumentDetails() == null) {
			throw new CustomException(BookingConstants.EMPTY_DOCUMENT_ERROR, "Documents are mandatory for booking.");
		}
		List<String> documentFileStoreIds = new LinkedList<>();
		bookingRequest.getBookingApplication().getUploadedDocumentDetails().forEach(document -> {
			if (documentFileStoreIds.contains(document.getFileStoreId()))
				throw new CustomException(BookingConstants.DUPLICATE_DOCUMENT_UPLOADED,
						"Same document cannot be used multiple times");
			documentFileStoreIds.add(document.getFileStoreId());
		});
	}

	/**
	 * Validates if the search parameters are valid
	 * 
	 * @param requestInfo The requestInfo of the incoming request
	 * @param criteria    The Advertisement Criteria
	 */
	public void validateSearch(RequestInfo requestInfo, AdvertisementSearchCriteria criteria) {
		log.info("Validating search request for criteria " + criteria);
		String userType = requestInfo.getUserInfo().getType();

		if (!BookingConstants.CITIZEN.equalsIgnoreCase(userType) && criteria.isEmpty())
			throw new CustomException(BookingConstants.INVALID_SEARCH, "Search without any paramters is not allowed");

		if (!BookingConstants.CITIZEN.equalsIgnoreCase(userType) && !criteria.tenantIdOnly()
				&& criteria.getTenantId() == null)
			throw new CustomException(BookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		if (BookingConstants.CITIZEN.equalsIgnoreCase(userType) && !criteria.isEmpty() && !criteria.tenantIdOnly()
				&& criteria.getTenantId() == null)
			throw new CustomException(BookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		String allowedParamStr = BookingConstants.EMPLOYEE.equalsIgnoreCase(userType)
				? config.getAllowedEmployeeSearchParameters()
				: null;

		if (allowedParamStr == null) {
			throw new CustomException(BookingConstants.INVALID_SEARCH,
					"The userType: " + userType + " does not have any search config");
		}

		if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
			throw new CustomException(BookingConstants.INVALID_SEARCH, "No search parameters are expected");

		validateSearchParams(criteria, Arrays.asList(allowedParamStr.split(",")));
	}

	/**
	 * Validates if the parameters coming in search are allowed
	 * 
	 * @param criteria      Advertisement search criteria
	 * @param allowedParams Allowed Params for search
	 */
	private void validateSearchParams(AdvertisementSearchCriteria criteria, List<String> allowedParams) {
		log.info("Validating search params for allowedParams " + allowedParams);

		validateAllowedParam(criteria.getBookingNo() != null, "bookingNo", allowedParams, "Search on booking no is not allowed");
		validateAllowedParam(criteria.getStatus() != null, "status", allowedParams, "Search on Status is not allowed");
		validateAllowedParam(criteria.getBookingIds() != null, "ids", allowedParams, "Search on ids is not allowed");
		validateAllowedParam(criteria.getOffset() != null, "offset", allowedParams, "Search on offset is not allowed");
		validateAllowedParam(criteria.getLimit() != null, "limit", allowedParams, "Search on limit is not allowed");
		validateAllowedParam(criteria.getMobileNumber() != null, "mobileNumber", allowedParams,
				"Search on mobile number is not allowed");

		validateFromDate(criteria.getFromDate());
		validateFromDateWithinSixMonths(criteria.getFromDate());
		validateDateRange(criteria.getFromDate(), criteria.getToDate());
	}

	private void validateAllowedParam(boolean present, String paramName, List<String> allowedParams, String message) {
		if (present && !allowedParams.contains(paramName)) {
			throw new CustomException(BookingConstants.INVALID_SEARCH, message);
		}
	}

	private void validateFromDate(String fromDate) {
		if (fromDate == null) {
			return;
		}
		LocalDate parsedFromDate = BookingUtil.parseStringToLocalDate(fromDate);
		if (parsedFromDate.isAfter(LocalDate.now(ZoneId.systemDefault()))) {
			throw new CustomException(BookingConstants.INVALID_SEARCH, "From date cannot be a future date");
		}
	}

	private void validateFromDateWithinSixMonths(String fromDate) {
		if (fromDate == null) {
			return;
		}
		LocalDate parsedFromDate = BookingUtil.parseStringToLocalDate(fromDate);
		if (parsedFromDate.isBefore(BookingUtil.getMonthsAgo(6))) {
			throw new CustomException(BookingConstants.INVALID_SEARCH, "From date cannot be prior 6 months");
		}
	}

	private void validateDateRange(String fromDate, String toDate) {
		if (fromDate == null || toDate == null) {
			return;
		}
		LocalDate parsedFromDate = BookingUtil.parseStringToLocalDate(fromDate);
		LocalDate parsedToDate = BookingUtil.parseStringToLocalDate(toDate);
		if (parsedToDate.isBefore(parsedFromDate)) {
			throw new CustomException(BookingConstants.INVALID_SEARCH, "To date cannot be prior to from date");
		}
	}

}
