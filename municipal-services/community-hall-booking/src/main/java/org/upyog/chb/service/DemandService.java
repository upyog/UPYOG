package org.upyog.chb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.DemandRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallDemandEstimationCriteria;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * This service class handles operations related to demand generation and management
 * in the Community Hall Booking module.
 * 
 * Purpose:
 * - To generate and manage demands for community hall bookings.
 * - To calculate charges, taxes, and other financial details for bookings.
 * - To interact with the billing service for creating and retrieving demands.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for demand operations.
 * - CalculationService: Handles the calculation of charges and taxes for bookings.
 * - DemandRepository: Interacts with the billing service to manage demands.
 * - CommunityHallBookingValidator: Validates demand-related requests and criteria.
 * - CommunityHallBookingUtil: Utility class for common operations related to bookings.
 * - MdmsUtil: Fetches and processes master data from MDMS for demand generation.
 * 
 * Features:
 * - Generates demands for bookings based on estimation criteria.
 * - Calculates demand details, including tax and charge breakdowns.
 * - Validates demand requests and ensures compliance with business rules.
 * - Logs important operations and errors for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. generateDemand:
 *    - Generates a new demand for a booking based on the provided criteria.
 *    - Interacts with the billing service to create the demand.
 * 
 * 2. validateDemandRequest:
 *    - Validates the demand request to ensure it meets the required criteria.
 * 
 * 3. calculateDemandDetails:
 *    - Calculates the demand details, including charges and taxes, for a booking.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever demand-related
 *   operations are required.
 * - It ensures consistent and reusable logic for managing demands in the module.
 */
@Service
@Slf4j
public class DemandService {

	@Autowired
	private CommunityHallBookingConfiguration config;
	
	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;
	
	@Autowired
	private CommunityHallBookingValidator bookingValidator;

	@Autowired
	private MdmsUtil mdmsUtil;
	
	
	/**
	 * 1. Fetch tax heads from mdms tax-heads.json
	 * 2. Map amount to tax heads from CalculateType.json
	 * 3. Create Demand for particular tax heads 
	 * 4. Bill will be automatically generated when fetch bill api is called after demand is created by this API
	 * @param bookingRequest
	 * @return
	 */

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest, Object mdmsData, boolean generateDemand) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User user =bookingRequest.getRequestInfo().getUserInfo();
		
		User owner = User.builder().name(user.getName()).emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber()).tenantId(bookingDetail.getTenantId()).build();
		
		List<DemandDetail> demandDetails = calculationService.calculateDemand(bookingRequest);
		
		LocalDate maxdate = getMaxBookingDate(bookingDetail);
		
		Demand demand = Demand.builder().consumerCode(consumerCode)
				 .demandDetails(demandDetails).payer(owner)
				 .tenantId(tenantId)
				.taxPeriodFrom(CommunityHallBookingUtil.getCurrentTimestamp()).taxPeriodTo(CommunityHallBookingUtil.minusOneDay(maxdate))
				.consumerType(config.getModuleName()).businessService(config.getBusinessServiceName()).additionalDetails(null).build();

		
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);
		if(!generateDemand) {
			BigDecimal totalAmount = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			demand.setAdditionalDetails(totalAmount);
			return demands;
		}
		log.info("Sending call to billing service for generating demand for booking no : " + consumerCode);
		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}
	
	
	public List<Demand> getDemand(CommunityHallDemandEstimationCriteria estimationCriteria){
		log.info("Getting demand for request without booking no");

		if(!bookingValidator.isSameHallCode(estimationCriteria.getBookingSlotDetails())) {
			throw new CustomException(CommunityHallBookingConstants.MULTIPLE_HALL_CODES_ERROR, "Booking of multiple halls are not allowed");
		}
		
		if (estimationCriteria.getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
		
		String tenantId = estimationCriteria.getTenantId().split("\\.")[0];
		
		CommunityHallBookingDetail bookingDetail = CommunityHallBookingDetail.builder().tenantId(tenantId)
				.bookingSlotDetails(estimationCriteria.getBookingSlotDetails())
				.communityHallCode(estimationCriteria.getCommunityHallCode()).build();
		CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder().hallsBookingApplication(bookingDetail)
				.requestInfo(estimationCriteria.getRequestInfo()).build();
		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);
		List<Demand> demands = createDemand(bookingRequest, mdmsData, false);
		return demands;
	}
	
	private LocalDate getMaxBookingDate(CommunityHallBookingDetail bookingDetail) {
		
		return bookingDetail.getBookingSlotDetails().stream().map(detail -> detail.getBookingDate())
				.max( LocalDate :: compareTo)
		        .get();
	}
	
	
	

}
