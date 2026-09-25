package org.upyog.adv.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.repository.DemandRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.web.models.AdvertisementDemandEstimationCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.Demand;
import org.upyog.adv.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;
/**
 * Service class for handling demand-related operations in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Generates and manages demands for advertisement bookings.
 * - Validates booking details before demand generation.
 * - Fetches and processes demand details for billing purposes.
 * 
 * Dependencies:
 * - BookingConfiguration: Provides configuration properties for demand generation.
 * - CalculationService: Handles calculations for demand details.
 * - DemandRepository: Interacts with the database for demand-related operations.
 * - MdmsUtil: Fetches data from MDMS for demand processing.
 * - BookingUtil: Provides utility methods for demand-related operations.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 * - @Slf4j: Enables logging for debugging and monitoring demand-related processes.
 */
@Service
@Slf4j
public class DemandService {

	private final BookingConfiguration config;
	private final CalculationService calculationService;
	private final DemandRepository demandRepository;
	private final MdmsUtil mdmsUtil;

	public DemandService(BookingConfiguration config, CalculationService calculationService,
			DemandRepository demandRepository, MdmsUtil mdmsUtil) {
		this.config = config;
		this.calculationService = calculationService;
		this.demandRepository = demandRepository;
		this.mdmsUtil = mdmsUtil;
	}

	/**
	 * 1. Fetch tax heads from mdms tax-heads.json 2. Map amount to tax heads from
	 * CalculateType.json 3. Create XDemand for particular tax heads 4. Bill will be
	 * automatically generated when fetch bill api is called for demand created by
	 * this API
	 * 
	 * @param bookingRequest booking request for which demand is created
	 * @param mdmsData MDMS master data used for demand calculation
	 * @param generateDemand whether to persist demand through billing service
	 * @return generated demand list
	 */

	public List<Demand> createDemand(BookingRequest bookingRequest, Object mdmsData, boolean generateDemand) {
		String tenantId = bookingRequest.getBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getBookingApplication().getBookingNo();
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		User user = bookingRequest.getRequestInfo().getUserInfo();

		User owner = User.builder().name(user.getName()).emailId(user.getEmailId()).mobileNumber(user.getMobileNumber())
				.build();

		Map<String, Object> mdmsDataMap = (Map<String, Object>) mdmsData;

		List<Map<String, Object>> taxRateList = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) mdmsDataMap
				.get("MdmsRes")).get("Advertisement")).get("TaxAmount");
		List<String> taxRateCodes = taxRateList.stream().map(tax -> (String) tax.get("feeType")).toList();

		List<DemandDetail> demandDetails = calculationService.calculateDemand(bookingRequest, taxRateCodes);

		LocalDate maxdate = getMaxBookingDate(bookingDetail);

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.tenantId(tenantId).taxPeriodFrom(BookingUtil.getCurrentTimestamp())
				.taxPeriodTo(BookingUtil.minusOneDay(maxdate)).consumerType(config.getModuleName())
				.businessService(config.getBusinessServiceName()).additionalDetails(null).build();

		List<Demand> demands = new ArrayList<>();
		demands.add(demand);
		if (!generateDemand) {
			BigDecimal totalAmount = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			demand.setAdditionalDetails(totalAmount);
			return demands;
		}
		log.info("Sending call to billing service for generating demand for booking no : " + consumerCode);
		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}

	public List<Demand> getDemand(AdvertisementDemandEstimationCriteria estimationCriteria) {
		log.info("Getting demand for request without booking no");

		String tenantId = estimationCriteria.getTenantId().split("\\.")[0];
		if (estimationCriteria.getTenantId().split("\\.").length == 1) {
			throw new CustomException(BookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}
		BookingDetail bookingDetail = BookingDetail.builder().tenantId(tenantId)
				.cartDetails(estimationCriteria.getCartDetails()).build();
		BookingRequest bookingRequest = BookingRequest.builder().bookingApplication(bookingDetail)
				.requestInfo(estimationCriteria.getRequestInfo()).build();
		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);
		return createDemand(bookingRequest, mdmsData, false);
	}

	private LocalDate getMaxBookingDate(BookingDetail bookingDetail) {
		return bookingDetail.getCartDetails().stream().map(CartDetail::getBookingDate).max(LocalDate::compareTo)
				.orElseThrow(() -> new CustomException("INVALID_BOOKING_DATE", "No booking dates found in cart details"));
	}

}
