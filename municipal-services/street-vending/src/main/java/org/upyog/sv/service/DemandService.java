package org.upyog.sv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.repository.DemandRepository;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.validator.StreetVendingValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private StreetVendingValidator bookingValidator;

	@Autowired
	private MdmsUtil mdmsUtil;

	/**
	 * 1. Fetch tax heads from mdms tax-heads.json 2. Map amount to tax heads from
	 * CalculateType.json 3. Create XDemand for particular tax heads 4. Bill will be
	 * automatically generated when fetch bill api is called for demand created by
	 * this API
	 * 
	 * @param bookingRequest
	 * @return
	 *//*
		 * 
		 * public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest,
		 * Object mdmsData, boolean generateDemand) { String tenantId =
		 * bookingRequest.getHallsBookingApplication().getTenantId(); String
		 * consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		 * 
		 * CommunityHallBookingDetail bookingDetail =
		 * bookingRequest.getHallsBookingApplication(); User user
		 * =bookingRequest.getRequestInfo().getUserInfo();
		 * 
		 * User owner = User.builder().name(user.getName()).emailId(user.getEmailId())
		 * .mobileNumber(user.getMobileNumber()).tenantId(bookingDetail.getTenantId()).
		 * build();
		 * 
		 * List<DemandDetail> demandDetails =
		 * calculationService.calculateDemand(bookingRequest);
		 * 
		 * LocalDate maxdate = getMaxBookingDate(bookingDetail);
		 * 
		 * Demand demand = Demand.builder().consumerCode(consumerCode)
		 * .demandDetails(demandDetails).payer(owner) .tenantId(tenantId)
		 * .taxPeriodFrom(CommunityHallBookingUtil.getCurrentTimestamp()).taxPeriodTo(
		 * CommunityHallBookingUtil.minusOneDay(maxdate))
		 * .consumerType(config.getModuleName()).businessService(config.
		 * getBusinessServiceName()).additionalDetails(null).build();
		 * 
		 * 
		 * List<Demand> demands = new ArrayList<>(); demands.add(demand);
		 * if(!generateDemand) { BigDecimal totalAmount =
		 * demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.
		 * ZERO, BigDecimal::add); demand.setAdditionalDetails(totalAmount); return
		 * demands; } log.
		 * info("Sending call to billing service for generating demand for booking no : "
		 * + consumerCode); return
		 * demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands); }
		 * 
		 * 
		 * public List<Demand> getDemand(CommunityHallDemandEstimationCriteria
		 * estimationCriteria){
		 * log.info("Getting demand for request without booking no");
		 * 
		 * if(!bookingValidator.isSameHallCode(estimationCriteria.getBookingSlotDetails(
		 * ))) { throw new
		 * CustomException(CommunityHallBookingConstants.MULTIPLE_HALL_CODES_ERROR,
		 * "Booking of multiple halls are not allowed"); }
		 * 
		 * String tenantId = estimationCriteria.getTenantId().split("\\.")[0]; if
		 * (estimationCriteria.getTenantId().split("\\.").length == 1) { throw new
		 * CustomException(CommunityHallBookingConstants.INVALID_TENANT,
		 * "Please provide valid tenant id for booking creation"); }
		 * CommunityHallBookingDetail bookingDetail =
		 * CommunityHallBookingDetail.builder().tenantId(tenantId)
		 * .bookingSlotDetails(estimationCriteria.getBookingSlotDetails())
		 * .communityHallCode(estimationCriteria.getCommunityHallCode()).build();
		 * CommunityHallBookingRequest bookingRequest =
		 * CommunityHallBookingRequest.builder().hallsBookingApplication(bookingDetail)
		 * .requestInfo(estimationCriteria.getRequestInfo()).build(); Object mdmsData =
		 * mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId); List<Demand>
		 * demands = createDemand(bookingRequest, mdmsData, false); return demands; }
		 * 
		 * private LocalDate getMaxBookingDate(CommunityHallBookingDetail bookingDetail)
		 * {
		 * 
		 * return bookingDetail.getBookingSlotDetails().stream().map(detail ->
		 * detail.getBookingDate()) .max( LocalDate :: compareTo) .get(); }
		 */

}
