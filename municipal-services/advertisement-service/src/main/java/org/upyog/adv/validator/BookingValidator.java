package org.upyog.adv.validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CartDetail;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BookingValidator {
	
	
	

		@Autowired
		private MDMSValidator mdmsValidator;

		@Autowired
		private BookingConfiguration config;

		/**
		 * 
		 * @param bookingRequest
		 * @param mdmsData
		 */
		public void validateCreate(BookingRequest bookingRequest, Object mdmsData) {
			log.info("validating master data for create booking request for applicant mobile no : "
					+ bookingRequest.getBookingApplication().getApplicantDetail().getApplicantMobileNo());

			
			if(!validateBookingDate(bookingRequest.getBookingApplication().getCartDetails())) {
				throw new CustomException(BookingConstants.INVALID_BOOKING_DATE,
						"Booking date is not valid.");
			}

			mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
			validateDuplicateDocuments(bookingRequest);
		}

		private boolean validateBookingDate(List<CartDetail> CartDetails) {
			LocalDate currentDate = BookingUtil.getCurrentDate();
			boolean isBookingDateValid = CartDetails.stream().anyMatch(slotDetail ->
			currentDate.isBefore(slotDetail.getBookingDate()));
			return isBookingDateValid;
		}
		
		public void validateUpdate(BookingDetail bookingDetailFromRequest, BookingDetail bookingDetailFromDB) {
			log.info("validating master data for update  booking request for  booking no : " + bookingDetailFromRequest.getBookingNo());
			
		}


		/**
		 * 
		 * @param bookingRequest
		 */
		private void validateDuplicateDocuments(BookingRequest bookingRequest) {
			if (bookingRequest.getBookingApplication().getUploadedDocumentDetails() != null) {
				List<String> documentFileStoreIds = new LinkedList<String>();
				bookingRequest.getBookingApplication().getUploadedDocumentDetails().forEach(document -> {
					if (documentFileStoreIds.contains(document.getFileStoreId()))
						throw new CustomException(BookingConstants.DUPLICATE_DOCUMENT_UPLOADED, "Same document cannot be used multiple times");
					else
						documentFileStoreIds.add(document.getFileStoreId());
				});
			} else {
				throw new CustomException(BookingConstants.EMPTY_DOCUMENT_ERROR, "Documents are mandatory for booking.");
			}
		}

		/**
		 * Validates if the search parameters are valid
		 * 
		 * @param requestInfo The requestInfo of the incoming request
		 * @param criteria    The Advertisement Criteria
		 */
		// TODO need to make the changes in the data
		public void validateSearch(RequestInfo requestInfo, AdvertisementSearchCriteria criteria) {
			log.info("Validating search request for criteria " + criteria);
			String userType = requestInfo.getUserInfo().getType();
			
			
			if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(BookingConstants.CITIZEN) && criteria.isEmpty())
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search without any paramters is not allowed");

			if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(BookingConstants.CITIZEN) && !criteria.tenantIdOnly()
					&& criteria.getTenantId() == null)
				throw new CustomException(BookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");

			if (requestInfo.getUserInfo().getType().equalsIgnoreCase(BookingConstants.CITIZEN) && !criteria.isEmpty()
					&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
				throw new CustomException(BookingConstants.INVALID_SEARCH, "TenantId is mandatory in search");
				

			String allowedParamStr = null;

			if (!userType.equalsIgnoreCase(BookingConstants.EMPLOYEE))
				allowedParamStr = config.getAllowedEmployeeSearchParameters();
			else if (userType.equalsIgnoreCase(BookingConstants.EMPLOYEE))
				allowedParamStr = config.getAllowedEmployeeSearchParameters();
			else
				throw new CustomException(BookingConstants.INVALID_SEARCH,
						"The userType: " + requestInfo.getUserInfo().getType() + " does not have any search config");

			if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
				throw new CustomException(BookingConstants.INVALID_SEARCH,
						"No search parameters are expected");
			else {
				List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
				validateSearchParams(criteria, allowedParams);
			}
		}

		/**
		 * Validates if the parameters coming in search are allowed
		 * 
		 * @param criteria      Advertisement search criteria
		 * @param allowedParams Allowed Params for search
		 */
		private void validateSearchParams(AdvertisementSearchCriteria criteria, List<String> allowedParams) {
			log.info("Validating search params for allowedParams " + allowedParams);

			if (criteria.getBookingNo() != null && !allowedParams.contains("bookingNo"))
				throw new CustomException(BookingConstants.INVALID_SEARCH,
						"Search on booking no is not allowed");

			if (criteria.getStatus() != null && !allowedParams.contains("status"))
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search on Status is not allowed");

			if (criteria.getBookingIds() != null && !allowedParams.contains("ids"))
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search on ids is not allowed");

			if (criteria.getOffset() != null && !allowedParams.contains("offset"))
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search on offset is not allowed");

			if (criteria.getLimit() != null && !allowedParams.contains("limit"))
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search on limit is not allowed");
			
			if (criteria.getMobileNumber() != null && !allowedParams.contains("mobileNumber"))
				throw new CustomException(BookingConstants.INVALID_SEARCH, "Search on mobile number is not allowed");
			
			if (criteria.getFromDate() != null) {
				LocalDate fromDate = BookingUtil.parseStringToLocalDate(criteria.getFromDate());
				if (fromDate.isAfter(LocalDate.now())) {
					throw new CustomException(BookingConstants.INVALID_SEARCH,
							"From date cannot be a future date");
				}
			}
			
			if(criteria.getFromDate() != null) {
				LocalDate fromDate = BookingUtil.parseStringToLocalDate(criteria.getFromDate());
				 if (fromDate.isBefore(BookingUtil.getMonthsAgo(6))) {
					 throw new CustomException(BookingConstants.INVALID_SEARCH,
								"From date cannot be prior 6 months");
			     }
			}

			if (criteria.getToDate() != null && criteria.getFromDate() != null) {
				LocalDate fromDate = BookingUtil.parseStringToLocalDate(criteria.getFromDate());
				LocalDate toDate = BookingUtil.parseStringToLocalDate(criteria.getToDate());
				if (toDate.isBefore(fromDate)) {
					throw new CustomException(BookingConstants.INVALID_SEARCH,
							"To date cannot be prior to from date");
				}
			}
		}
		
		


}
