package org.upyog.adv.service.impl;


import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.service.BookingService;
//import org.upyog.adv.service.EncryptionService;
import org.upyog.adv.service.EnrichmentService;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.validator.BookingValidator;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

	   @Autowired
	    private MdmsUtil mdmsUtil; 
	   
	   @Autowired
		private BookingRepository bookingRepository;
		@Autowired
		private BookingValidator bookingValidator;
		
		@Autowired
		private EnrichmentService enrichmentService;
		
//		@Autowired
//		EncryptionService encryptionService;
	
	@Override
	public BookingDetail createBooking(@Valid BookingRequest bookingRequest) {
		log.info("Create advertisement booking for user : "
				+ bookingRequest.getRequestInfo().getUserInfo().getId());
		// TODO move to util calss 
		String tenantId = bookingRequest.getBookingApplication().getTenantId().split("\\.")[0];
		if (bookingRequest.getBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(BookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}
		

		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);

		// 1. Validate request master data to confirm it has only valid data in records
		bookingValidator.validateCreate(bookingRequest, mdmsData);
		
		// 2. Add fields that has custom logic like booking no, ids using UUID
		enrichmentService.enrichCreateBookingRequest(bookingRequest);
		
		//ENcrypt PII data of applicant
		//encryptionService.encryptObject(bookingRequest);

		/**
		 * Workflow will come into picture once hall location changes or booking is
		 * cancelled otherwise after payment booking will be auto approved
		 * 
		 */

		// 3.Update workflow of the application
		// workflowService.updateWorkflow(bookingRequest,
		// WorkflowStatus.CREATE);

		//demandService.createDemand(bookingRequest, mdmsData, true);

		// 4.Persist the request using persister service
		bookingRepository.saveBooking(bookingRequest);

		return bookingRequest.getBookingApplication();
	}
	
	

}
