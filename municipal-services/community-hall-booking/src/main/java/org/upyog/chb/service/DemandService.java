package org.upyog.chb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.DemandRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CommunityHallDemandEstimationCriteria;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

	private final CommunityHallBookingConfiguration config;
	private final CalculationService calculationService;
	private final DemandRepository demandRepository;
	private final CommunityHallBookingValidator bookingValidator;
	private final MdmsUtil mdmsUtil;

	public DemandService(CommunityHallBookingConfiguration config, CalculationService calculationService,
			DemandRepository demandRepository, CommunityHallBookingValidator bookingValidator, MdmsUtil mdmsUtil) {
		this.config = config;
		this.calculationService = calculationService;
		this.demandRepository = demandRepository;
		this.bookingValidator = bookingValidator;
		this.mdmsUtil = mdmsUtil;
	}

	public List<Demand> createDemand(VenueBookingRequest bookingRequest, boolean generateDemand) {
		String tenantId = bookingRequest.getVenueBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getVenueBookingApplication().getBookingNo();
		
		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
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
		
		String tenantId = estimationCriteria.getTenantId();
		
		VenueBookingDetail bookingDetail = VenueBookingDetail.builder().tenantId(tenantId)
				.bookingSlotDetails(estimationCriteria.getBookingSlotDetails())
				.venueCode(estimationCriteria.getVenueCode()).build();
		VenueBookingRequest bookingRequest = VenueBookingRequest.builder().venueBookingApplication(bookingDetail)
				.requestInfo(estimationCriteria.getRequestInfo()).build();
		mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);
		return createDemand(bookingRequest, false);
	}
	
	private LocalDate getMaxBookingDate(VenueBookingDetail bookingDetail) {
		return bookingDetail.getBookingSlotDetails().stream().map(BookingSlotDetail::getBookingDate)
				.max(LocalDate::compareTo)
		        .orElseThrow(() -> new CustomException("INVALID_BOOKING_DATE", "Booking date is not valid."));
	}
}
