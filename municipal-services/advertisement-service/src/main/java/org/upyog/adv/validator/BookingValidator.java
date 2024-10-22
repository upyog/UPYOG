package org.upyog.adv.validator;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.BookingSlotDetail;

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
//			if (!isSameHallCode(bookingRequest.getBookingApplication().getBookingSlotDetails())) {
//				throw new CustomException(BookingConstants.MULTIPLE_HALL_CODES_ERROR,
//						"Booking of multiple halls are not allowed.");
//			}
			
			if(!validateBookingDate(bookingRequest.getBookingApplication().getBookingSlotDetails())) {
				throw new CustomException(BookingConstants.INVALID_BOOKING_DATE,
						"Booking date is not valid.");
			}

			mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
			validateDuplicateDocuments(bookingRequest);
		}

		private boolean validateBookingDate(List<BookingSlotDetail> bookingSlotDetails) {
			LocalDate currentDate = BookingUtil.getCurrentDate();
			boolean isBookingDateValid = bookingSlotDetails.stream().anyMatch(slotDetail ->
			currentDate.isBefore(slotDetail.getBookingDate()));
			return isBookingDateValid;
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

		
		
		
//		public boolean isSameHallCode(List<BookingSlotDetail> bookingSlotDetails) {
//			String hallCode = bookingSlotDetails.get(0).getHallCode();
//			boolean allSameCode = bookingSlotDetails.stream().allMatch(x -> x.getHallCode().equals(hallCode));
//			return allSameCode;
//
//		}


}
