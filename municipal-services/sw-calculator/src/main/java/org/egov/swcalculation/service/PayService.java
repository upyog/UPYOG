package org.egov.swcalculation.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.web.models.Demand;
import org.egov.swcalculation.web.models.TaxHeadEstimate;
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
	 * 
	 * @param creditAmount - Credit Amount
	 * @param debitAmount - Debit Amount
	 * @return TaxHead for SW round off
	 */
	public TaxHeadEstimate roundOfDecimals(BigDecimal creditAmount, BigDecimal debitAmount, boolean isConnectionFee) {
		BigDecimal roundOffPos = BigDecimal.ZERO;
		BigDecimal roundOffNeg = BigDecimal.ZERO;
		String taxHead = isConnectionFee ? SWCalculationConstant.SW_Round_Off
				: SWCalculationConstant.SW_ONE_TIME_FEE_ROUND_OFF;
		BigDecimal result = creditAmount.add(debitAmount);
		BigDecimal roundOffAmount = result.setScale(2, 2);
		BigDecimal reminder = roundOffAmount.remainder(BigDecimal.ONE);

		if (reminder.doubleValue() >= 0.5)
			roundOffPos = roundOffPos.add(BigDecimal.ONE.subtract(reminder));
		else if (reminder.doubleValue() < 0.5)
			roundOffNeg = roundOffNeg.add(reminder).negate();

		if (roundOffPos.doubleValue() > 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffPos).taxHeadCode(taxHead).build();
		else if (roundOffNeg.doubleValue() < 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffNeg).taxHeadCode(taxHead).build();
		else
			return null;
		
	}
	
	
	/**
	 * 
	 * @param sewerageCharge - Sewerage Charge
	 * @param assessmentYear - Assessment Year
	 * @param timeBasedExemptionMasterMap - List of time based exemption map
	 * @param billingExpiryDate - Bill Expiry date
	 * @return estimation of time based exemption
	 */
	public Map<String, BigDecimal> applyPenaltyRebateAndInterest(BigDecimal sewerageCharge,
			String assessmentYear, Map<String, JSONArray> timeBasedExemptionMasterMap, Long billingExpiryDate, Demand demand) {

		if (BigDecimal.ZERO.compareTo(sewerageCharge) >= 0)
			return Collections.emptyMap();
		Map<String, BigDecimal> estimates = new HashMap<>();
		long currentUTC = System.currentTimeMillis();
		long numberOfDaysInMillis = billingExpiryDate - currentUTC;
		BigDecimal noOfDays = BigDecimal.valueOf((TimeUnit.MILLISECONDS.toDays(Math.abs(numberOfDaysInMillis))));
		if(BigDecimal.ONE.compareTo(noOfDays) <= 0) noOfDays = noOfDays.add(BigDecimal.ONE);
		BigDecimal penalty = getApplicablePenalty(demand,sewerageCharge, noOfDays, timeBasedExemptionMasterMap.get(SWCalculationConstant.SW_PENANLTY_MASTER));
		BigDecimal interest = getApplicableInterest(demand,sewerageCharge, noOfDays, timeBasedExemptionMasterMap.get(SWCalculationConstant.SW_INTEREST_MASTER));
		//BigDecimal rebate = getApplicableRebate(sewerageCharge, demand, timeBasedExemptionMasterMap.get(SWCalculationConstant.SW_REBATE_MASTER));

		estimates.put(SWCalculationConstant.SW_TIME_PENALTY, penalty.setScale(2, 2));
		estimates.put(SWCalculationConstant.SW_TIME_INTEREST, interest.setScale(2, 2));
		//estimates.put(SWCalculationConstant.SW_TIME_REBATE, rebate.negate().setScale(2, 2));

		return estimates;
	}
	
	
	/**
	 * 
	 * @param sewerageCharge - Sewerage Charge
	 * @param noOfDays - No of Days
	 * @param config - Config object
	 * @return - Returns Penalty details
	 */
	public BigDecimal getApplicablePenalty(Demand demand,BigDecimal sewerageCharge, BigDecimal noOfDays, JSONArray config) {
		BigDecimal applicablePenalty = BigDecimal.ZERO;
		Map<String, Object> penaltyMaster = mDService.getApplicableMaster(demand,estimationService.getAssessmentYear(), config);
		if (null == penaltyMaster) return applicablePenalty;
		BigDecimal daysApplicable = null != penaltyMaster.get(SWCalculationConstant.DAYA_APPLICABLE_NAME)
				? BigDecimal.valueOf(((Number) penaltyMaster.get(SWCalculationConstant.DAYA_APPLICABLE_NAME)).intValue())
				: null;
		if (daysApplicable == null)
			return applicablePenalty;
		BigDecimal daysDiff = noOfDays.subtract(daysApplicable);
		if (daysDiff.compareTo(BigDecimal.ONE) < 0) {
			return applicablePenalty;
		}
		BigDecimal rate = null != penaltyMaster.get(SWCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) penaltyMaster.get(SWCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != penaltyMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) penaltyMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicablePenalty = flatAmt.compareTo(sewerageCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else {
			applicablePenalty = sewerageCharge.multiply(rate.divide(SWCalculationConstant.HUNDRED));
		}
		return applicablePenalty;
	}
	
	/**
	 * 
	 * @param sewerageCharge - Sewerage Charge
	 * @param noOfDays - No of Days
	 * @param config - Config object
	 * @return - Returns applicable interest details
	 */
	public BigDecimal getApplicableInterest(Demand demand,BigDecimal sewerageCharge, BigDecimal noOfDays, JSONArray config) {
		BigDecimal applicableInterest = BigDecimal.ZERO;
		Map<String, Object> interestMaster = mDService.getApplicableMaster(demand,estimationService.getAssessmentYear(), config);
		if (null == interestMaster) return applicableInterest;
		BigDecimal daysApplicable = null != interestMaster.get(SWCalculationConstant.DAYA_APPLICABLE_NAME)
				? BigDecimal.valueOf(((Number) interestMaster.get(SWCalculationConstant.DAYA_APPLICABLE_NAME)).intValue())
				: null;
		if (daysApplicable == null)
			return applicableInterest;
		BigDecimal daysDiff = noOfDays.subtract(daysApplicable);
		if (daysDiff.compareTo(BigDecimal.ONE) < 0) {
			return applicableInterest;
		}
		BigDecimal rate = null != interestMaster.get(SWCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) interestMaster.get(SWCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != interestMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) interestMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicableInterest = flatAmt.compareTo(sewerageCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else {
			// rate of interest
			applicableInterest = sewerageCharge.multiply(rate.divide(SWCalculationConstant.HUNDRED));
		}
		//applicableInterest.multiply(noOfDays.divide(BigDecimal.valueOf(365), 6, 5));
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
		BigDecimal daysApplicable = null != rebateMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES)
				? BigDecimal.valueOf(((Number) rebateMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES)).intValue())
				: null;
	
		if (daysApplicable == null)
			return applicableRebate;
		log.info("Days allowed for rebate "+ daysApplicable);

		if (noOfDays.compareTo(daysApplicable) > 0) {
			log.info("when days are more than applicable for rebate");

			return applicableRebate;
		}
		BigDecimal rate = null != rebateMaster.get(SWCalculationConstant.RATE_FIELD_NAME)
				? BigDecimal.valueOf(((Number) rebateMaster.get(SWCalculationConstant.RATE_FIELD_NAME)).doubleValue())
				: null;

		BigDecimal flatAmt = null != rebateMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)
				? BigDecimal
						.valueOf(((Number) rebateMaster.get(SWCalculationConstant.FLAT_AMOUNT_FIELD_NAME)).doubleValue())
				: BigDecimal.ZERO;

		if (rate == null)
			applicableRebate = flatAmt.compareTo(waterCharge) > 0 ? BigDecimal.ZERO : flatAmt;
		else{
			// rate of interest
			applicableRebate = waterCharge.multiply(rate.divide(SWCalculationConstant.HUNDRED));
		}
		log.info("Rebate amount is "+ applicableRebate);

		return applicableRebate;
	}
}
