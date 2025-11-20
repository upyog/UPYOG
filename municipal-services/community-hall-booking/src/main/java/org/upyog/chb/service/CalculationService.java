package org.upyog.chb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.CalculationTypeCache;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.util.FeeCalculationUtil;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.AdditionalFeeRate;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.billing.DemandDetail;
import org.upyog.chb.web.models.billing.TaxHeadMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	@Autowired
	private CalculationTypeCache calculationTypeCache;

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private FeeCalculationUtil feeCalculationUtil;

	/**
	 * @param bookingRequest
	 * @return
	 */
	public List<DemandDetail> calculateDemand(CommunityHallBookingRequest bookingRequest) {

	// Use full tenant id (eg: pb.nangak) so MDMS calls are tenant-specific
	String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();

		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId,
				CommunityHallBookingConstants.BILLING_SERVICE);

		List<CalculationType> calculationTypes = calculationTypeCache.getcalculationType(bookingRequest.getRequestInfo(),
				tenantId, config.getModuleName(), bookingRequest.getHallsBookingApplication());

		log.info("Retrieved calculation types: {}", calculationTypes);

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, calculationTypes,
				bookingRequest, headMasters);

		return demandDetails;

	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, CommunityHallBookingRequest bookingRequest,
			List<TaxHeadMaster> headMasters) {

		Map<String, Long> hallCodeBookingDaysMap = bookingRequest.getHallsBookingApplication().getBookingSlotDetails()
				.stream().collect(Collectors.groupingBy(BookingSlotDetail::getHallCode, Collectors.counting()));

		// Demand will be generated using billing service for booking
		final List<DemandDetail> demandDetails = new LinkedList<>();

		List<String> taxHeadCodes = headMasters.stream().map(head -> head.getCode()).collect(Collectors.toList());

		log.info("tax head codes  : " + taxHeadCodes);

		// Demand for which tax is applicable is stored
		List<CalculationType> taxableFeeType = new ArrayList<>();

		BigDecimal hallCodeBookingDays = new BigDecimal(hallCodeBookingDaysMap
				.get(bookingRequest.getHallsBookingApplication().getBookingSlotDetails().get(0).getHallCode()));

		// We have two type of fee 1.taxable(Booking fee, electricity fee etc) and
		// 2.fixed( Security deposit)
		for (CalculationType type : calculationTypes) {
			if (taxHeadCodes.contains(type.getFeeType())) {
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

		// Calculating taxable demand as per no of days for taxable fee
		List<DemandDetail> taxableDemands = taxableFeeType.stream().map(data ->
		// log.info("data :" + data);
		DemandDetail.builder().taxAmount(data.getAmount().multiply(hallCodeBookingDays))
				.taxHeadMasterCode(data.getFeeType()).tenantId(tenantId).build()).collect(Collectors.toList());

		log.info("taxableDemands : " + taxableDemands);

		// Adding taxable demands to demand details
		demandDetails.addAll(taxableDemands);

		// *** Add additional fees ***
		addAdditionalFees(bookingRequest, tenantId, demandDetails, taxableDemands);

		// Add the newly added taxable fees (like ServiceCharge) to demandDetails as well
		List<DemandDetail> newTaxableFees = taxableDemands.stream()
				.filter(demand -> "CHB_SERVICE_CHARGE".equals(demand.getTaxHeadMasterCode()))
				.collect(Collectors.toList());
		demandDetails.addAll(newTaxableFees);

		// Recalculate total taxable amount (now includes ServiceCharge)
		BigDecimal totalTaxableAmount = taxableDemands.stream()
				.map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		log.info("Total Taxable amount (including additional fees): {}", totalTaxableAmount);

		calculateTaxDemands(bookingRequest, tenantId, demandDetails, totalTaxableAmount);

		return demandDetails;
	}

	private void calculateTaxDemands(CommunityHallBookingRequest bookingRequest, String tenantId,
			List<DemandDetail> demandDetails, BigDecimal totalTaxableAmount) {
		List<CalculationType> taxRates = mdmsUtil.getTaxRatesMasterList(bookingRequest.getRequestInfo(), tenantId,
				config.getModuleName(), bookingRequest.getHallsBookingApplication());
		taxRates.stream().forEach(tax -> {
			DemandDetail demandDetail = DemandDetail.builder()
					.taxAmount(calculateAmount(totalTaxableAmount, tax.getAmount())).taxHeadMasterCode(tax.getFeeType())
					.tenantId(tenantId).build();
			demandDetails.add(demandDetail);
		});
	}

	// Tax is in percentage
	private BigDecimal calculateAmount(BigDecimal amount, BigDecimal tax) {
		return amount.multiply(tax).divide(CommunityHallBookingConstants.ONE_HUNDRED, RoundingMode.FLOOR);
	}

	/**
	 * Calculate days elapsed AFTER the event date
	 * Returns 0 if event hasn't occurred yet
	 */
	private int calculateDaysAfterEvent(CommunityHallBookingRequest bookingRequest) {
		// Get event date from first booking slot
		BookingSlotDetail firstSlot = bookingRequest.getHallsBookingApplication()
				.getBookingSlotDetails().get(0);

		// Convert LocalDate to milliseconds (assuming start of day)
		long eventDateMillis = firstSlot.getBookingDate().toEpochDay() * 24 * 60 * 60 * 1000;

		long currentTimeMillis = System.currentTimeMillis();
		long elapsedMillis = currentTimeMillis - eventDateMillis;
		int daysAfterEvent = (int) (elapsedMillis / (24 * 60 * 60 * 1000));

		// If event hasn't occurred yet, return 0
		return Math.max(0, daysAfterEvent);
	}

	/**
	 * Get base amount for rate calculation from MDMS
	 * This is the PER DAY booking fee, NOT the total
	 */
	private BigDecimal getBaseAmountFromMDMS(CommunityHallBookingRequest bookingRequest) {
	String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();

		List<CalculationType> calculationTypes = calculationTypeCache.getcalculationType(
				bookingRequest.getRequestInfo(),
				tenantId,
				config.getModuleName(),
				bookingRequest.getHallsBookingApplication());

		return calculationTypes.stream()
				.filter(ct -> "BOOKING_FEES".equals(ct.getFeeType()))
				.findFirst()
				.map(CalculationType::getAmount)
				.orElse(BigDecimal.ZERO);
	}

	/**
	 * Calculate and add additional fees (ServiceCharge, Penalty, Interest,
	 * SecurityDeposit)
	 */
	private void addAdditionalFees(
			CommunityHallBookingRequest bookingRequest,
			String tenantId,
			List<DemandDetail> demandDetails,
			List<DemandDetail> taxableDemands) {

		String currentFY = feeCalculationUtil.getCurrentFinancialYear();
		BigDecimal baseAmount = getBaseAmountFromMDMS(bookingRequest);
		int daysAfterEvent = calculateDaysAfterEvent(bookingRequest);

		log.info("Additional fees calculation - Base amount: {}, Days after event: {}, Current FY: {}",
				baseAmount, daysAfterEvent, currentFY);

		// 1. ServiceCharge (always applicable, taxable)
		List<AdditionalFeeRate> serviceCharges = mdmsUtil.getServiceCharges(
				bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

		for (AdditionalFeeRate serviceCharge : serviceCharges) {
			BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
					serviceCharge, baseAmount, 0, currentFY);

			if (amount.compareTo(BigDecimal.ZERO) > 0) {
				DemandDetail demand = DemandDetail.builder()
						.taxAmount(amount)
						.taxHeadMasterCode("CHB_SERVICE_CHARGE")
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
						.taxHeadMasterCode("CHB_SECURITY_DEPOSIT")
						.tenantId(tenantId)
						.build();
				demandDetails.add(demand); // Non-taxable
				log.info("SecurityDeposit added: {}", amount);
			}
		}


        // 5. Cow Cess (hall-specific configuration in MDMS)
        List<AdditionalFeeRate> cowCessRates = mdmsUtil.getCowCessForHall(
                bookingRequest.getRequestInfo(), tenantId, config.getModuleName(),
                bookingRequest.getHallsBookingApplication());

        for (AdditionalFeeRate cow : cowCessRates) {
            BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
                    cow, baseAmount, daysAfterEvent, currentFY);

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                DemandDetail demand = DemandDetail.builder()
                        .taxAmount(amount)
                        .taxHeadMasterCode("CHB_COW_CESS")
                        .tenantId(tenantId)
                        .build();

                if (cow.isTaxApplicable()) {
                    // treat as taxable
                    taxableDemands.add(demand);
                } else {
                    demandDetails.add(demand);
                }

                log.info("CowCess added: {} for hall {}", amount, bookingRequest.getHallsBookingApplication().getCommunityHallCode());
            }
        }

		// 3. PenaltyFee (only after event, non-taxable)
		if (daysAfterEvent > 0) {
			List<AdditionalFeeRate> penaltyFees = mdmsUtil.getPenaltyFees(
					bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

			for (AdditionalFeeRate penalty : penaltyFees) {
				BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
						penalty, baseAmount, daysAfterEvent, currentFY);

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					// Multiply by days after event
					amount = amount.multiply(new BigDecimal(daysAfterEvent));

					DemandDetail demand = DemandDetail.builder()
							.taxAmount(amount)
							.taxHeadMasterCode("CHB_PENALTY_FEE")
							.tenantId(tenantId)
							.build();
					demandDetails.add(demand); // Non-taxable
					log.info("PenaltyFee added: {} for {} days", amount, daysAfterEvent);
				}
			}

			// 4. InterestAmount (only after event, non-taxable)
			List<AdditionalFeeRate> interestAmounts = mdmsUtil.getInterestAmounts(
					bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

			for (AdditionalFeeRate interest : interestAmounts) {
				BigDecimal amount = feeCalculationUtil.calculateFeeAmount(
						interest, baseAmount, daysAfterEvent, currentFY);

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					DemandDetail demand = DemandDetail.builder()
							.taxAmount(amount)
							.taxHeadMasterCode("CHB_INTEREST_AMOUNT")
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
			CommunityHallBookingRequest bookingRequest,
			BigDecimal securityDepositPaid) {

	String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String currentFY = feeCalculationUtil.getCurrentFinancialYear();
		BigDecimal baseAmount = getBaseAmountFromMDMS(bookingRequest);
		int daysAfterEvent = calculateDaysAfterEvent(bookingRequest);

		// If event hasn't occurred yet, full refund
		if (daysAfterEvent <= 0) {
			log.info("Event not yet occurred, full security refund: {}", securityDepositPaid);
			return securityDepositPaid;
		}

		// Calculate total penalty deduction
		BigDecimal totalPenaltyDeduction = BigDecimal.ZERO;

		List<AdditionalFeeRate> penaltyFees = mdmsUtil.getPenaltyFees(
				bookingRequest.getRequestInfo(), tenantId, config.getModuleName());

		for (AdditionalFeeRate penalty : penaltyFees) {
			BigDecimal penaltyAmount = feeCalculationUtil.calculateFeeAmount(
					penalty, baseAmount, daysAfterEvent, currentFY);

			if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				// Multiply by days after event
				totalPenaltyDeduction = totalPenaltyDeduction.add(
						penaltyAmount.multiply(new BigDecimal(daysAfterEvent)));
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
