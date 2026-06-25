package org.upyog.adv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

		List<Map<String, Object>> taxRateList = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) mdmsDataMap
				.get("MdmsRes")).get("Advertisement")).get("TaxAmount");

		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId, BookingConstants.BILLING_SERVICE);

		List<Advertisements> calculationTypes = mdmsUtil.getAdvertisements(bookingRequest.getRequestInfo(), tenantId, config.getModuleName(), bookingRequest.getBookingApplication().getCartDetails().get(0));

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

		CartDetail cartDetail = cartDetails.get(0);
		String advertisementId = cartDetail.getAdvertisementId();

		final List<DemandDetail> demandDetails = new LinkedList<>();

		List<String> taxHeadCodes = headMasters.stream().map(head -> head.getCode()).collect(Collectors.toList());

		log.info("tax head codes  : " + taxHeadCodes);

		// Demand for which tax is applicable is stored
		List<Advertisements> taxableFeeType = new ArrayList<>();

		// We have two type of fee 1.taxable(Booking fee, advertisement fee etc) and 2.fixed(Security deposit)
		for (Advertisements type : advertisements) {
			if (taxHeadCodes.contains(type.getFeeType()) && type.getId().equals(Integer.parseInt(advertisementId))) {
				if (type.isTaxApplicable()) {
					// Add taxable fee
					taxableFeeType.add(type);
				} else {
					DemandDetail data = DemandDetail.builder().taxAmount(type.getAmount())
							.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
					// Add fixed fee for which tax is not applicable like security deposit
					demandDetails.add(data);
				}
			}
		}

		log.info("taxable fee type : " + taxableFeeType);

		// Find the matching advertisement for pricing
		Advertisements matchingAdv = advertisements.stream()
				.filter(a -> a.getId().equals(Integer.parseInt(advertisementId)))
				.findFirst().orElse(null);
		if (matchingAdv == null) {
			throw new CustomException("ADVERTISEMENT_NOT_FOUND",
					"No advertisement found with id: " + advertisementId);
		}

		// Sum per-day rate for each cart detail's actual date — auto‑detects whether
		// to use monthly/weekly/yearly/biannual/daily rate based on which amount field
		// is populated AND how long the total booking is.
		// Handles multi‑month ranges correctly (e.g. June has 30 days, July has 31).
		final long totalBookingDays = cartDetails.size();
		BigDecimal totalTaxBaseAmount = cartDetails.stream()
				.map(cd -> BookingUtil.getPerDayRate(matchingAdv, cd.getBookingDate(), totalBookingDays))
				.filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalTaxBaseAmount.compareTo(BigDecimal.ZERO) == 0) {
			throw new CustomException("ZERO_TAX_BASE",
					"Total taxable amount is zero for advertisement id: " + advertisementId
							+ ". Ensure the advertisement has a valid amount/monthlyAmount/weeklyAmount/yearlyAmount/biannualAmount.");
		}
		log.info("totalTaxBaseAmount={} (from {} cart entries)", totalTaxBaseAmount, cartDetails.size());

		// Calculating taxable demand
		final BigDecimal taxBase = totalTaxBaseAmount;
		List<DemandDetail> taxableDemands = taxableFeeType.stream().map(data -> {
			return DemandDetail.builder()
					.taxAmount(taxBase)
					.taxHeadMasterCode(data.getFeeType())
					.tenantId(tenantId)
					.build();
		}).collect(Collectors.toList());

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
		// Get booking end date from first cart detail
		CartDetail firstCart = bookingRequest.getBookingApplication()
				.getCartDetails().get(0);

		/*
		 * Assuming CartDetail.getBookingDate() returns a java.time.LocalDate (or similar)
		 * Convert to epoch-day then to milliseconds (start of day), like CHB implementation.
		 * Use long literals to avoid int overflow.
		 */
		long bookingEndDateMillis = firstCart.getBookingDate().toEpochDay() * 24L * 60 * 60 * 1000;
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
		CartDetail cartDetail = bookingRequest.getBookingApplication().getCartDetails().get(0);

		List<Advertisements> advertisements = null;
		try {
			advertisements = mdmsUtil.getAdvertisements(
					bookingRequest.getRequestInfo(),
					tenantId,
					config.getModuleName(),
					cartDetail);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		String advertisementId = cartDetail.getAdvertisementId();

		return advertisements.stream()
				.filter(ad -> ad.getId().equals(Integer.parseInt(advertisementId)) &&
						"BOOKING_FEES".equals(ad.getFeeType()))
				.findFirst()
				.map(Advertisements::getAmount)
				.orElse(BigDecimal.ZERO);
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
