package org.egov.wscalculation.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.web.models.Demand;
import org.egov.wscalculation.web.models.TaxHeadEstimate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class PayService {

	@Autowired
	private MasterDataService mDService;
	
	@Autowired
	private EstimationService estimationService;

	/**
	 * Decimal is ceiled for all the tax heads
	 * 
	 * if the decimal is greater than 0.5 upper bound will be applied
	 * 
	 * else if decimal is lesser than 0.5 lower bound is applied
	 * 
	 */
	public TaxHeadEstimate roundOfDecimals(BigDecimal creditAmount, BigDecimal debitAmount, boolean isConnectionFee) {
		BigDecimal roundOffPos = BigDecimal.ZERO;
		BigDecimal roundOffNeg = BigDecimal.ZERO;
        String taxHead = isConnectionFee ? WSCalculationConstant.WS_Round_Off : WSCalculationConstant.WS_ONE_TIME_FEE_ROUND_OFF;
		BigDecimal result = creditAmount.add(debitAmount);
		BigDecimal roundOffAmount = result.setScale(2, 2);
		BigDecimal reminder = roundOffAmount.remainder(BigDecimal.ONE);

		if (reminder.doubleValue() >= 0.5)
			roundOffPos = roundOffPos.add(BigDecimal.ONE.subtract(reminder));
		else if (reminder.doubleValue() < 0.5)
			roundOffNeg = roundOffNeg.add(reminder).negate();

		if (roundOffPos.doubleValue() > 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffPos).taxHeadCode(taxHead)
					.build();
		else if (roundOffNeg.doubleValue() < 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffNeg).taxHeadCode(taxHead)
					.build();
		else
			return null;
	}
	
	/**
	 * 
	 * @param waterCharge - Water Charge Amount
	 * @param assessmentYear - Assessment Year
	 * @param timeBasedExemptionMasterMap - Time Based Exemption Master Data
	 * @param billingExpiryDate - Billing Expiry Date
	 * @return estimation of time based exemption
	 */
	public Map<String, BigDecimal> applyPenaltyRebateAndInterest(BigDecimal waterCharge,
			String assessmentYear, Map<String, JSONArray> timeBasedExemptionMasterMap, Long billingExpiryDate, Demand demand) {

		if (BigDecimal.ZERO.compareTo(waterCharge) >= 0)
			return Collections.emptyMap();
		Map<String, BigDecimal> estimates = new HashMap<>();
		long currentUTC = System.currentTimeMillis();
		long numberOfDaysInMillis = billingExpiryDate - currentUTC;
		BigDecimal noOfDays = BigDecimal.valueOf((TimeUnit.MILLISECONDS.toDays(Math.abs(numberOfDaysInMillis))));
		log.info("No. of days for Demand expiry are ::" + noOfDays );//19648
		if(BigDecimal.ONE.compareTo(noOfDays) <= 0) noOfDays = noOfDays.add(BigDecimal.ONE);
		log.info("No. of days for Demand expiry after comparison are ::" + noOfDays );

		BigDecimal penalty = getApplicablePenalty(demand,waterCharge, noOfDays, timeBasedExemptionMasterMap.get(WSCalculationConstant.WC_PENANLTY_MASTER));
		BigDecimal interest = getApplicableInterest(demand,waterCharge, noOfDays, timeBasedExemptionMasterMap.get(WSCalculationConstant.WC_INTEREST_MASTER));
		//TODO: Disabling REBATE for now
//		BigDecimal rebate = getApplicableRebate(waterCharge, demand, timeBasedExemptionMasterMap.get(WSCalculationConstant.WC_REBATE_MASTER));

		log.info("time based applicables are: penalty = " + penalty +" interest = "+  interest
//				+ " rebate = " +rebate
				);
		estimates.put(WSCalculationConstant.WS_TIME_PENALTY, penalty.setScale(2, 2));
		estimates.put(WSCalculationConstant.WS_TIME_INTEREST, interest.setScale(2, 2));
//		estimates.put(WSCalculationConstant.WS_TIME_REBATE, rebate.negate().setScale(2, 2));
		return estimates;
	}

	/**
	 * Returns the Amount of penalty that has to be applied on the given tax amount for the given period
	 * 
	 * @param taxAmt - Tax Amount
	 * @param assessmentYear - Assessment Year
	 * @return applicable penalty for given time
	 */
	public BigDecimal getPenalty(Demand demand,BigDecimal taxAmt, String assessmentYear, JSONArray penaltyMasterList, BigDecimal noOfDays) {

		BigDecimal penaltyAmt = BigDecimal.ZERO;
		Map<String, Object> penalty = mDService.getApplicableMaster(demand,assessmentYear, penaltyMasterList);
		if (null == penalty) return penaltyAmt;
			penaltyAmt = mDService.calculateApplicable(taxAmt, penalty);
		return penaltyAmt;
	}
	
	/**
	 * 
	 * @param waterCharge - Water Charge amount
	 * @param noOfDays - No.Of.Days
	 * @param config
	 *            master configuration
	 * @return applicable penalty
	 */
	public BigDecimal getApplicablePenalty(Demand demand,BigDecimal waterCharge, BigDecimal noOfDays, JSONArray config) {
		BigDecimal applicablePenalty = BigDecimal.ZERO;
		Map<String, Object> penaltyMaster = mDService.getApplicableMaster(demand,estimationService.getAssessmentYear(), config);
		if (null == penaltyMaster) return applicablePenalty;
		BigDecimal daysApplicable = null != penaltyMaster.get(WSCalculationConstant.DAYA_APPLICABLE_NAME)
				? BigDecimal.valueOf(((Number) penaltyMaster.get(WSCalculationConstant.DAYA_APPLICABLE_NAME)).intValue())
				: null;
		if (daysApplicable == null)
			return applicablePenalty;
		BigDecimal daysDiff = noOfDays.subtract(daysApplicable);
		if (daysDiff.compareTo(BigDecimal.ONE) < 0) {
			return applicablePenalty;
		}
		BigDecimal rate = null != penaltyMaster.get(WSCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) penaltyMaster.get(WSCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != penaltyMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) penaltyMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicablePenalty = flatAmt.compareTo(waterCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else {
			// rate of penalty
			applicablePenalty = waterCharge.multiply(rate.divide(WSCalculationConstant.HUNDRED));
		}
		return applicablePenalty;
	}
	
	/**
	 * 
	 * @param waterCharge - Water Charge
	 * @param noOfDays - No.Of Days value
	 * @param config
	 *            master configuration
	 * @return applicable Interest
	 */
	public BigDecimal getApplicableInterest(Demand demand,BigDecimal waterCharge, BigDecimal noOfDays, JSONArray config) {
		BigDecimal applicableInterest = BigDecimal.ZERO;
		Map<String, Object> interestMaster = mDService.getApplicableMaster(demand,estimationService.getAssessmentYear(), config);
		if (null == interestMaster) return applicableInterest;
		
		BigDecimal daysApplicable = null != interestMaster.get(WSCalculationConstant.DAYA_APPLICABLE_NAME)
				? BigDecimal.valueOf(((Number) interestMaster.get(WSCalculationConstant.DAYA_APPLICABLE_NAME)).intValue())
				: null;
		if (daysApplicable == null)
			return applicableInterest;
		
		
		BigDecimal daysDiff = noOfDays.subtract(daysApplicable);
		// if daysDiff < 1 interest zero
		if (daysDiff.compareTo(BigDecimal.ONE) < 0) {
			return applicableInterest;
		}
//		long currentUTC = System.currentTimeMillis();
//		long numberOfDaysInMillis = demand.getAuditDetails().getCreatedTime() - currentUTC;
//		BigDecimal noOfDaysforInterest = BigDecimal.valueOf((TimeUnit.MILLISECONDS.toDays(Math.abs(numberOfDaysInMillis))))
//				.subtract(BigDecimal.valueOf(Double.parseDouble(interestMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES).toString())));
//		log.info("No. of days applicable for interest ::" + noOfDaysforInterest );
		
		BigDecimal rate = null != interestMaster.get(WSCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) interestMaster.get(WSCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != interestMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) interestMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicableInterest = flatAmt.compareTo(waterCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else{
			// rate of interest
			applicableInterest = waterCharge.multiply(rate.divide(WSCalculationConstant.HUNDRED));
		}
		
		// Interest calculation when rate is entered per annum
		//applicableInterest=applicableInterest.multiply(noOfDaysforInterest).divide(BigDecimal.valueOf(365)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return applicableInterest;
	}
	
	public BigDecimal getApplicableRebate(BigDecimal waterCharge, Demand demand, JSONArray config) {
		BigDecimal applicableRebate = BigDecimal.ZERO;
		Map<String, Object> rebateMaster = mDService.getApplicableMaster(demand,estimationService.getAssessmentYear(), config);
		if (null == rebateMaster) return applicableRebate;
		
		long currentUTC = System.currentTimeMillis();
		long numberOfDaysInMillis =System.currentTimeMillis();
		if(demand==null)
			numberOfDaysInMillis=currentUTC-numberOfDaysInMillis;
		else
			numberOfDaysInMillis = currentUTC-demand.getAuditDetails().getCreatedTime() ;
		BigDecimal noOfDays = BigDecimal.valueOf((TimeUnit.MILLISECONDS.toDays(Math.abs(numberOfDaysInMillis))));
		
		log.info("noOfDays after demand Generation are "+noOfDays);
		BigDecimal daysApplicable = null != rebateMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES)
				? BigDecimal.valueOf(((Number) rebateMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES)).intValue())
				: null;
	
		if (daysApplicable == null)
			return applicableRebate;
		log.info("Days allowed for rebate "+ daysApplicable);

		if (noOfDays.compareTo(daysApplicable) > 0) {
			log.info("when days are more than applicable for rebate");

			return applicableRebate;
		}
		BigDecimal rate = null != rebateMaster.get(WSCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) rebateMaster.get(WSCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != rebateMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) rebateMaster.get(WSCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicableRebate = flatAmt.compareTo(waterCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else{
			// rate of interest
			applicableRebate = waterCharge.multiply(rate.divide(WSCalculationConstant.HUNDRED));
		}
		log.info("Rebate amount is "+ applicableRebate);

		return applicableRebate;
	}
}
