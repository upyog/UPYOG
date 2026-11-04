package org.upyog.adv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CalculationType;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.DemandDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import lombok.extern.slf4j.Slf4j;
/**
 * Service class for handling calculations in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Calculates demand details for advertisement bookings.
 * - Fetches tax head masters and calculation types from the billing service and MDMS.
 * - Processes calculations for demand generation based on booking details and tax rates.
 * 
 * Dependencies:
 * - MdmsUtil: Utility class for fetching data from MDMS.
 * - BookingConfiguration: Provides configuration properties for calculations.
 * 
 * Methods:
 * - `calculateDemand`: Calculates demand details for a booking request.
 * - `processCalculationForDemandGeneration`: Processes calculations for generating demand details.
 * - `fetchTaxHeadMasters`: Fetches tax head masters from the billing service.
 * - `fetchCalculationType`: Retrieves calculation types from MDMS.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 * - @Slf4j: Enables logging for debugging and monitoring calculation processes.
 */

@Slf4j
@Service
public class CalculationService {

	private final MdmsUtil mdmsUtil;
	private final BookingConfiguration config;

	public CalculationService(MdmsUtil mdmsUtil, BookingConfiguration config) {
		this.mdmsUtil = mdmsUtil;
		this.config = config;
	}

	/**
	 * @param bookingRequest booking request used for demand calculation
	 * @param taxRateCodes tax rate codes applicable to the booking
	 * @return calculated demand details
	 */
	public List<DemandDetail> calculateDemand(BookingRequest bookingRequest, List<String> taxRateCodes) {

		String tenantId = bookingRequest.getBookingApplication().getTenantId().split("\\.")[0];

		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId,
				BookingConstants.BILLING_SERVICE);

		List<CalculationType> calculationTypes = mdmsUtil.getcalculationType(bookingRequest.getRequestInfo(), tenantId,
				config.getModuleName(), bookingRequest.getBookingApplication().getCartDetails().get(0));

		log.info("calculationTypes " + calculationTypes);

		return processCalculationForDemandGeneration(tenantId, calculationTypes, bookingRequest, headMasters,
				taxRateCodes);

	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, BookingRequest bookingRequest, List<TaxHeadMaster> headMasters,
			List<String> taxRateCodes) {

		Map<String, Long> advBookingDaysMap = bookingRequest.getBookingApplication().getCartDetails().stream()
				.collect(Collectors.groupingBy(CartDetail::getAddType, Collectors.counting()));
		final List<DemandDetail> demandDetails = new LinkedList<>();

		List<String> taxHeadCodes = headMasters.stream().map(TaxHeadMaster::getCode).toList();

		log.info("tax head codes  : " + taxHeadCodes);

		List<CalculationType> taxableFeeType = new ArrayList<>();

		BigDecimal advBookingDays = new BigDecimal(advBookingDaysMap
				.get(bookingRequest.getBookingApplication().getCartDetails().get(0).getAddType()));

		for (CalculationType type : calculationTypes) {
			if (taxHeadCodes.contains(type.getFeeType())) {

				if (type.isTaxApplicable()) {
					taxableFeeType.add(type);
				} else if (!taxRateCodes.contains(type.getFeeType())) {
					DemandDetail data = DemandDetail.builder().taxAmount(type.getAmount())
							.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
					demandDetails.add(data);
				}
			}
		}

		log.info("taxable fee type : " + taxableFeeType);

		List<DemandDetail> taxableDemands = taxableFeeType.stream()
				.map(data -> DemandDetail.builder().taxAmount(data.getAmount().multiply(advBookingDays))
						.taxHeadMasterCode(data.getFeeType()).tenantId(tenantId).build())
				.toList();

		log.info("taxableDemands : " + taxableDemands);

		BigDecimal totalTaxableAmount = taxableDemands.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		demandDetails.addAll(taxableDemands);

		log.info("Total Taxable amount for the booking : " + totalTaxableAmount);

		for (CalculationType type : calculationTypes) {
			if (taxRateCodes.stream().anyMatch(code -> code.trim().equalsIgnoreCase(type.getFeeType().trim()))) {
				DemandDetail demandDetail = DemandDetail.builder()
						.taxAmount(calculateAmount(totalTaxableAmount, type.getRate()))
						.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
				demandDetails.add(demandDetail);
			}
		}

		return demandDetails;
	}

	private BigDecimal calculateAmount(BigDecimal base, BigDecimal pct) {
		return base.multiply(pct).divide(BookingConstants.ONE_HUNDRED, RoundingMode.FLOOR);
	}

}
