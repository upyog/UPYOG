package org.upyog.adv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.util.FeeCalculationUtil;
import org.upyog.adv.web.models.*;
import org.upyog.adv.web.models.AdditionalFeeRate;
import org.upyog.adv.web.models.billing.DemandDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private FeeCalculationUtil feeCalculationUtil;

	/**
	 * @param bookingRequest
	 * @param mdmsData
	 * @return
	 */
	public List<DemandDetail> calculateDemand(BookingRequest bookingRequest, List<String> taxRateCodes, Object mdmsData) throws JsonProcessingException {

		String tenantId = bookingRequest.getBookingApplication().getTenantId();
		Map<String, Object> mdmsDataMap = (Map<String, Object>) mdmsData;

		List<CartDetail> cartDetails = bookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new CustomException("EMPTY_CART", "Cart details cannot be empty for demand calculation");
		}

		List<Map<String, Object>> taxRateList = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) mdmsDataMap
				.get("MdmsRes")).get("Advertisement")).get("TaxAmount");

		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId, BookingConstants.BILLING_SERVICE);

		// Fetch ALL advertisements from MDMS — the cartDetail parameter is unused by the method (no per-item filtering)
		List<Advertisements> calculationTypes = mdmsUtil.getAdvertisements(bookingRequest.getRequestInfo(), tenantId, config.getModuleName(), null);

		log.info("Retrieved calculation types: {}", calculationTypes);

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, calculationTypes,
				bookingRequest, headMasters, taxRateCodes, taxRateList);

		return demandDetails;
	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
																	 List<Advertisements> advertisements, BookingRequest bookingRequest, List<TaxHeadMaster> headMasters, List<String> taxRateCodes, Object taxRateList) {

		List<CartDetail> cartDetails = bookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new CustomException("EMPTY_CART", "Cart details cannot be empty for demand calculation");
		}

		final List<DemandDetail> demandDetails = new LinkedList<>();

		List<String> taxHeadCodes = headMasters.stream().map(head -> head.getCode()).collect(Collectors.toList());
		log.info("tax head codes  : " + taxHeadCodes);

		// Build lookup: advertisementId → Advertisements for fast per-entry matching
		Map<Integer, Advertisements> advById = advertisements.stream()
				.filter(a -> a.getId() != null)
				.collect(Collectors.toMap(Advertisements::getId, a -> a, (existing, replacement) -> existing));

		// Collect all unique advertisement IDs from cart entries
		Set<Integer> bookedAdIds = cartDetails.stream()
				.map(CartDetail::getAdvertisementId)
				.filter(Objects::nonNull)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());

		// Fixed fee (non-taxable like security deposit) — once per unique advertisement
		for (Integer adId : bookedAdIds) {
			Advertisements adv = advById.get(adId);
			if (adv == null) {
				throw new CustomException("ADVERTISEMENT_NOT_FOUND",
						"No advertisement found with id: " + adId);
			}
			if (taxHeadCodes.contains(adv.getFeeType()) && !adv.isTaxApplicable()) {
				DemandDetail data = DemandDetail.builder().taxAmount(adv.getAmount())
						.taxHeadMasterCode(adv.getFeeType()).tenantId(tenantId).build();
				demandDetails.add(data);
			}
		}

		// Sum per-day rate for each cart detail using its OWN advertisement's rate
		// Handles multi-month ranges correctly (e.g. June has 30 days, July has 31)
		Map<String, Long> daysPerAd = cartDetails.stream()
				.map(CartDetail::getAdvertisementId)
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(id -> id, Collectors.counting()));

		BigDecimal totalTaxBaseAmount = cartDetails.stream()
				.map(cd -> {
					if (cd.getAdvertisementId() == null) return null;
					Advertisements adv = advById.get(Integer.parseInt(cd.getAdvertisementId()));
					if (adv == null) {
						throw new CustomException("ADVERTISEMENT_NOT_FOUND",
								"No advertisement found with id: " + cd.getAdvertisementId());
					}
					long bookingDaysForAd = daysPerAd.getOrDefault(cd.getAdvertisementId(), 0L);
					return BookingUtil.getPerDayRate(adv, cd.getBookingDate(), bookingDaysForAd);
				})
				.filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (totalTaxBaseAmount.compareTo(BigDecimal.ZERO) == 0) {
			throw new CustomException("ZERO_TAX_BASE",
					"No valid advertisement rates found. Ensure each advertisement has an amount field populated.");
		}
		log.info("totalTaxBaseAmount={} (from {} cart entries across {} unique ads)",
				totalTaxBaseAmount, cartDetails.size(), bookedAdIds.size());

		Map<String, BigDecimal> taxableByFeeType = cartDetails.stream()
				.filter(cd -> cd.getAdvertisementId() != null)
				.map(cd -> {
					Advertisements adv = advById.get(Integer.parseInt(cd.getAdvertisementId()));
					if (adv == null || !adv.isTaxApplicable() || !taxHeadCodes.contains(adv.getFeeType())) return null;
					long daysForAd = daysPerAd.getOrDefault(cd.getAdvertisementId(), 0L);
					BigDecimal rate = BookingUtil.getPerDayRate(adv, cd.getBookingDate(), daysForAd);
					return (rate == null) ? null : new java.util.AbstractMap.SimpleEntry<>(adv.getFeeType(), rate);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));

		List<DemandDetail> taxableDemands = taxableByFeeType.entrySet().stream()
				.map(e -> DemandDetail.builder()
						.taxAmount(e.getValue())
						.taxHeadMasterCode(e.getKey())
						.tenantId(tenantId)
						.build())
				.collect(Collectors.toList());

		log.info("taxableDemands : " + taxableDemands);

		// Adding taxable demands to demand details
		demandDetails.addAll(taxableDemands);

		// *** Add additional fees ***
		addAdditionalFees(bookingRequest, tenantId, demandDetails, taxableDemands);

		// Add the newly added taxable fees (like ServiceCharge) to demandDetails as well
		List<DemandDetail> newTaxableFees = taxableDemands.stream()
				.filter(demand -> "ADV_SERVICE_CHARGE".equals(demand.getTaxHeadMasterCode()))
				.collect(Collectors.toList());
		demandDetails.addAll(newTaxableFees);

		// Recalculate total taxable amount (now includes ServiceCharge)
		BigDecimal totalTaxableAmount = taxableDemands.stream()
				.map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		log.info("Total Taxable amount (including additional fees): {}", totalTaxableAmount);

		calculateTaxDemands(tenantId, demandDetails, totalTaxableAmount, taxRateList);

		return demandDetails;
	}

	private void calculateTaxDemands(String tenantId, List<DemandDetail> demandDetails,
									 BigDecimal totalTaxableAmount, Object taxRateList) {

		List<Map<String, Object>> taxRateListMap = (List<Map<String, Object>>) taxRateList;

		taxRateListMap.forEach(rateMap -> {
			// Extract values from map
			String feeType = (String) rateMap.get("feeType");
			BigDecimal rate = new BigDecimal(rateMap.get("rate").toString());

			// Calculate tax amount
			BigDecimal taxAmount = calculateAmount(totalTaxableAmount, rate);

			// Build DemandDetail object
			DemandDetail demandDetail = DemandDetail.builder()
					.taxAmount(taxAmount)
					.taxHeadMasterCode(feeType)
					.tenantId(tenantId)
					.build();

			demandDetails.add(demandDetail);
		});
	}

	// Tax is in percentage
	private BigDecimal calculateAmount(BigDecimal base, BigDecimal pct) {
		return base.multiply(pct).divide(BookingConstants.ONE_HUNDRED, RoundingMode.FLOOR);
	}

	/**
	 * Calculate days elapsed AFTER the booking end date
	 * Returns 0 if booking hasn't ended yet
	 */
	private int calculateDaysAfterBooking(BookingRequest bookingRequest) {
		List<CartDetail> cartDetails = bookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new CustomException("EMPTY_CART", "Cart details cannot be empty for demand calculation");
		}

		LocalDate bookingEndDate = cartDetails.stream()
				.map(CartDetail::getBookingDate)
				.filter(Objects::nonNull)
				.max(LocalDate::compareTo)
				.orElseThrow(() -> new CustomException("BOOKING_DATE_NOT_FOUND",
						"Booking date is required for demand calculation"));

		/*
		 * Assuming CartDetail.getBookingDate() returns a java.time.LocalDate (or similar)
		 * Convert to epoch-day then to milliseconds (start of day), like CHB implementation.
		 * Use long literals to avoid int overflow.
		 */
		long bookingEndDateMillis = bookingEndDate.toEpochDay() * 24L * 60 * 60 * 1000;
		long currentTimeMillis = System.currentTimeMillis();
		long elapsedMillis = currentTimeMillis - bookingEndDateMillis;
		int daysAfterBooking = (int) (elapsedMillis / (24L * 60 * 60 * 1000));

		// If booking hasn't ended yet, return 0
		return Math.max(0, daysAfterBooking);
	}

	/**
	 * Get base amount for rate calculation from MDMS
	 * This is the PER DAY booking fee, NOT the total
	 */
	private BigDecimal getBaseAmountFromMDMS(BookingRequest bookingRequest) {
		String tenantId = bookingRequest.getBookingApplication().getTenantId();
		List<CartDetail> cartDetails = bookingRequest.getBookingApplication().getCartDetails();
		if (cartDetails == null || cartDetails.isEmpty()) {
			throw new CustomException("EMPTY_CART", "Cart details cannot be empty for demand calculation");
		}

		// Fetch ALL advertisements from MDMS — the cartDetail parameter is unused (no per-item filtering)
		List<Advertisements> advertisements = null;
		try {
			advertisements = mdmsUtil.getAdvertisements(
					bookingRequest.getRequestInfo(),
					tenantId,
					config.getModuleName(),
					null);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		Map<Integer, Advertisements> advById = advertisements.stream()
				.filter(ad -> ad.getId() != null)
				.collect(Collectors.toMap(Advertisements::getId, ad -> ad, (existing, replacement) -> existing));

		Map<String, Long> daysPerAd = cartDetails.stream()
				.map(CartDetail::getAdvertisementId)
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(id -> id, Collectors.counting()));

		return cartDetails.stream()
				.filter(cartDetail -> cartDetail.getAdvertisementId() != null)
				.map(cartDetail -> {
					Advertisements advertisement = advById.get(Integer.parseInt(cartDetail.getAdvertisementId()));
					if (advertisement == null) {
						throw new CustomException("ADVERTISEMENT_NOT_FOUND",
								"No advertisement found with id: " + cartDetail.getAdvertisementId());
					}
					if (!"BOOKING_FEES".equals(advertisement.getFeeType())) {
						return BigDecimal.ZERO;
					}
					long bookingDaysForAd = daysPerAd.getOrDefault(cartDetail.getAdvertisementId(), 0L);
					BigDecimal rate = BookingUtil.getPerDayRate(advertisement,
							cartDetail.getBookingDate(), bookingDaysForAd);
					return rate == null ? BigDecimal.ZERO : rate;
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Calculate and add additional fees (ServiceCharge, Penalty, Interest, SecurityDeposit)
	 */
	private void addAdditionalFees(
			BookingRequest bookingRequest,
			String tenantId,
			List<DemandDetail> demandDetails,
			List<DemandDetail> taxableDemands) {

		String currentFY = feeCalculationUtil.getCurrentFinancialYear();
		BigDecimal baseAmount = getBaseAmountFromMDMS(bookingRequest);
		int daysAfterBooking = calculateDaysAfterBooking(bookingRequest);

		log.info("Additional fees calculation - Base amount: {}, Days after booking: {}, Current FY: {}",
				baseAmount, daysAfterBooking, currentFY);

		// 1. ServiceCharge (always applicable, taxable)
		List<AdditionalFeeRate> serviceCharges = mdmsUtil.getServiceCharges(
				bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

		for (AdditionalFeeRate serviceCharge : serviceCharges) {
			BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
					serviceCharge, baseAmount, 0, currentFY);

			if (amount.compareTo(BigDecimal.ZERO) > 0) {
				DemandDetail demand = DemandDetail.builder()
						.taxAmount(amount)
						.taxHeadMasterCode("ADV_SERVICE_CHARGE")
						.tenantId(tenantId)
						.build();
				taxableDemands.add(demand); // Add to taxable list
				log.info("ServiceCharge added: {}", amount);
			}
		}

		// 2. SecurityDeposit (always applicable, non-taxable)
		List<AdditionalFeeRate> securityDeposits = mdmsUtil.getSecurityDeposits(
				bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

		for (AdditionalFeeRate security : securityDeposits) {
			BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
					security, baseAmount, 0, currentFY);

			if (amount.compareTo(BigDecimal.ZERO) > 0) {
				DemandDetail demand = DemandDetail.builder()
						.taxAmount(amount)
						.taxHeadMasterCode("ADV_SECURITY_DEPOSIT")
						.tenantId(tenantId)
						.build();
				demandDetails.add(demand); // Non-taxable
				log.info("SecurityDeposit added: {}", amount);
			}
		}

		// 3. PenaltyFee (only after booking ends, non-taxable)
		if (daysAfterBooking > 0) {
			List<AdditionalFeeRate> penaltyFees = mdmsUtil.getPenaltyFees(
					bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

			for (AdditionalFeeRate penalty : penaltyFees) {
				BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
						penalty, baseAmount, daysAfterBooking, currentFY);

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					// Multiply by days after booking
					amount = amount.multiply(BigDecimal.valueOf(daysAfterBooking));

					DemandDetail demand = DemandDetail.builder()
							.taxAmount(amount)
							.taxHeadMasterCode("ADV_PENALTY_FEE")
							.tenantId(tenantId)
							.build();
					demandDetails.add(demand); // Non-taxable
					log.info("PenaltyFee added: {} for {} days", amount, daysAfterBooking);
				}
			}

			// 4. InterestAmount (only after booking ends, non-taxable)
			List<AdditionalFeeRate> interestAmounts = mdmsUtil.getInterestAmounts(
					bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

			for (AdditionalFeeRate interest : interestAmounts) {
				BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
						interest, baseAmount, daysAfterBooking, currentFY);

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					DemandDetail demand = DemandDetail.builder()
							.taxAmount(amount)
							.taxHeadMasterCode("ADV_INTEREST_AMOUNT")
							.tenantId(tenantId)
							.build();
					demandDetails.add(demand); // Non-taxable
					log.info("InterestAmount added: {}", amount);
				}
			}
		}
	}

	/**
	 * Calculate security deposit refund amount
	 * Deducts penalty fees if applicable
	 */
	public BigDecimal calculateSecurityRefund(
			BookingRequest bookingRequest,
			BigDecimal securityDepositPaid) {

		String tenantId = bookingRequest.getBookingApplication().getTenantId();
		String currentFY = feeCalculationUtil.getCurrentFinancialYear();
		BigDecimal baseAmount = getBaseAmountFromMDMS(bookingRequest);
		int daysAfterBooking = calculateDaysAfterBooking(bookingRequest);

		// If booking hasn't ended yet, full refund
		if (daysAfterBooking <= 0) {
			log.info("Booking not yet ended, full security refund: {}", securityDepositPaid);
			return securityDepositPaid;
		}

		// Calculate total penalty deduction
		BigDecimal totalPenaltyDeduction = BigDecimal.ZERO;

		List<AdditionalFeeRate> penaltyFees = mdmsUtil.getPenaltyFees(
				bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

		for (AdditionalFeeRate penalty : penaltyFees) {
			BigDecimal penaltyAmount = feeCalculationUtil.calculateFeeAmount(
					penalty, baseAmount, daysAfterBooking, currentFY);

			if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				// Multiply by days after booking
				totalPenaltyDeduction = totalPenaltyDeduction.add(
						penaltyAmount.multiply(BigDecimal.valueOf(daysAfterBooking)));
			}
		}

		// Calculate refund
		BigDecimal refundAmount = securityDepositPaid.subtract(totalPenaltyDeduction);

		// Cannot refund negative amount
		refundAmount = refundAmount.max(BigDecimal.ZERO);

		log.info("Security Refund - Paid: {}, Penalty: {}, Refund: {}",
				securityDepositPaid, totalPenaltyDeduction, refundAmount);

		return refundAmount;
	}
}
