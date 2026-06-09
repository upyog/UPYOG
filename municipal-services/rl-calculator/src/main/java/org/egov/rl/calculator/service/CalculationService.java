package org.egov.rl.calculator.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.rl.calculator.util.PropertyUtil;
import org.egov.rl.calculator.util.RLConstants;
import org.egov.rl.calculator.web.models.AllotmentDetails;
import org.egov.rl.calculator.web.models.AllotmentRequest;

import org.egov.rl.calculator.web.models.RLProperty;
import org.egov.rl.calculator.web.models.TaxRate;
import org.egov.rl.calculator.web.models.demand.BillingPeriod;
import org.egov.rl.calculator.web.models.demand.DemandDetail;
import org.egov.rl.calculator.web.models.demand.Demand;
import org.egov.rl.calculator.web.models.Owner;
import org.egov.rl.calculator.web.models.OwnerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private PropertyUtil mdmsUtil;
	
	@Autowired
	private MasterDataService masterDataService;
	

	@Autowired
	private DemandService demandService;


	/**
	 * @return A list of DemandDetail objects representing the calculated demand.
	 */
	public List<DemandDetail> calculateDemand(boolean isSecurityDeposite, AllotmentRequest allotmentRequest) {
		String tenantId = allotmentRequest.getAllotment().get(0).getTenantId();

		List<RLProperty> calculationTypes = mdmsUtil.getCalculateAmount(allotmentRequest.getAllotment().get(0).getPropertyId(),
				allotmentRequest.getRequestInfo(), tenantId, RLConstants.RL_MASTER_MODULE_NAME);

		return processCalculationForDemandGeneration(isSecurityDeposite, tenantId, calculationTypes, allotmentRequest);
	}

	private List<DemandDetail> processCalculationForDemandGeneration(boolean isSecurityDeposite, String tenantId,
			List<RLProperty> calculateAmount, AllotmentRequest allotmentRequest) {

		String applicationType = resolveApplicationType(allotmentRequest.getAllotment().get(0));
		BigDecimal fee = BigDecimal.ZERO;
		List<DemandDetail> demandDetails = new ArrayList<>();
		// Step 1: Calculate base fee
		for (RLProperty amount : calculateAmount) {
			if (isSecurityDeposite) {
				fee = new BigDecimal(amount.getSecurityDeposit());
				demandDetails.add(DemandDetail.builder().taxAmount(fee)
//						.collectionAmount(fee)
						.taxHeadMasterCode(RLConstants.SECURITY_DEPOSIT_FEE_RL_APPLICATION).tenantId(tenantId).build());
			}
			if (RLConstants.NEW_RL_APPLICATION.equalsIgnoreCase(applicationType)
					|| RLConstants.RENEWAL_RL_APPLICATION.equalsIgnoreCase(applicationType)
					|| RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(applicationType)) {
				
				fee = new BigDecimal(amount.getBaseRent());
				AllotmentDetails allotmentDetails=allotmentRequest.getAllotment().get(0);
				JsonNode additionalDetails = allotmentDetails.getAdditionalDetails();
				String cycle = additionalDetails.path("propertyDetails").get(0).path("feesPeriodCycle").asText();

				List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(allotmentRequest.getRequestInfo(), tenantId);
				BillingPeriod billingPeriod = billingPeriods.stream()
						.filter(b -> b.getBillingCycle().equalsIgnoreCase(cycle)).findFirst().orElse(null); // Assuming
				if (billingPeriod != null) {
					long startDay = billingPeriod.getTaxPeriodFrom() <= allotmentDetails.getStartDate()
							? allotmentDetails.getStartDate()
							: billingPeriod.getTaxPeriodFrom();

					long endDay = billingPeriod.getTaxPeriodTo() <= allotmentDetails.getEndDate()
							? billingPeriod.getTaxPeriodTo()
							: allotmentDetails.getEndDate();
					fee = calculatePaybleAmount(startDay, endDay, fee, cycle);
				}
				
				
				
				demandDetails.add(DemandDetail.builder().taxAmount(fee)
//						.collectionAmount(fee)
						.taxHeadMasterCode(RLConstants.RENT_LEASE_FEE_RL_APPLICATION).tenantId(tenantId).build());
			}
		}

		// Step 2: Calculate additional fees (Penality, cowcass, cgst,sgst)
		calculateAdditionalFees(fee,calculateAmount.get(0), allotmentRequest, tenantId, demandDetails);
		return demandDetails;
	}

	private String resolveApplicationType(AllotmentDetails allotmentDetails) {
		if (allotmentDetails == null) {
			return null;
		}

		// Only consider the root-level applicationType field. Do not read additionalDetails.
		String rootType = allotmentDetails.getApplicationType();
		if (rootType != null && !rootType.trim().isEmpty()) {
			return rootType;
		}
		return null;
	}

	private void calculateAdditionalFees(BigDecimal baseAmount,RLProperty calculateAmount, AllotmentRequest allotmentRequest, String tenantId,
			List<DemandDetail> demandDetails) {

//		BigDecimal baseAmount = new BigDecimal(calculateAmount.getBaseRent());
//		Long lastModifiedDate = allotmentRequest.getAllotment().get(0).getAuditDetails().getLastModifiedTime();
//		Long rentLeasePayDate = Instant.ofEpochMilli(lastModifiedDate).plus(Duration.ofDays(30)).toEpochMilli();
//		Long rentLeasePayWithPenaltyDate = Instant.ofEpochMilli(rentLeasePayDate).plus(Duration.ofDays(30)).toEpochMilli();

		List<TaxRate> taxRate = mdmsUtil.getHeadTaxAmount(allotmentRequest.getRequestInfo(), tenantId,
				RLConstants.RL_MASTER_MODULE_NAME);
		List<String> taxList = Arrays.asList(RLConstants.SGST_FEE_RL_APPLICATION, RLConstants.CGST_FEE_RL_APPLICATION,
				RLConstants.COWCESS_FEE_RL_APPLICATION);
			taxRate.stream().forEach(t -> {
//			String penaltyType = allotmentRequest.getAllotment().get(0).getPenaltyType();
			BigDecimal amount = BigDecimal.ZERO;
			if (taxList.contains(t.getTaxType()) && t.isActive()) {
				if (t.getType().contains("%")) {
					amount = baseAmount.multiply(new BigDecimal(t.getAmount())).divide(new BigDecimal(100));
				} else {
					amount = new BigDecimal(t.getAmount());
				}
				if (!amount.equals(BigDecimal.ZERO)) {
					demandDetails.add(DemandDetail.builder().taxAmount(amount)
//							.collectionAmount(amount)
							.taxHeadMasterCode(t.getTaxType())
							.tenantId(tenantId).build());
				}
			}
		});
		addRoundOffTaxHead(tenantId,demandDetails);
	}

	public List<DemandDetail> calculateSatelmentDemand(AllotmentRequest allotmentRequest) {
		String tenantId = allotmentRequest.getAllotment().get(0).getTenantId();

		List<RLProperty> calculationTypes = mdmsUtil.getCalculateAmount(allotmentRequest.getAllotment().get(0).getPropertyId(),
				allotmentRequest.getRequestInfo(), tenantId, RLConstants.RL_MASTER_MODULE_NAME);

		return satelmentCalculationForDemandGeneration(tenantId, calculationTypes, allotmentRequest);
	}

	private List<DemandDetail> satelmentCalculationForDemandGeneration(String tenantId,
			List<RLProperty> calculateAmount, AllotmentRequest allotmentRequest) {
		List<DemandDetail> demandDetails = new ArrayList<>();
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		BigDecimal amountDeducted = allotmentDetails.getAmountToBeDeducted(); // BigDecimal
		BigDecimal securityAmount = calculateAmount.stream()
				.filter(d -> d.getPropertyId().equals(allotmentDetails.getPropertyId())).findFirst()
				.map(d -> new BigDecimal(d.getSecurityDeposit())) // BigDecimal
				.orElse(BigDecimal.ZERO);

		BigDecimal amountToBeRefunded = securityAmount.subtract(amountDeducted).negate();

		demandDetails.add(DemandDetail.builder()
				.taxAmount(amountToBeRefunded)
				.taxHeadMasterCode(RLConstants.PENALTY_FEE_RL_APPLICATION)
				.tenantId(tenantId)
				.build());
		return demandDetails;
	}
	
	public BigDecimal calculatePaybleAmount(long startDay,long endDay,BigDecimal amount,String cycle) {
		int durationInDays=0;
		long durationInDays1 = TimeUnit.MILLISECONDS.toDays(endDay - startDay);
		try {
		  durationInDays = Math.toIntExact(durationInDays1); // throws exception if overflow
		}catch (Exception e) {
		}
		
		BigDecimal duration = BigDecimal.valueOf(durationInDays);
		
		System.out.println("Days = " + durationInDays);
        BigDecimal payAmount=BigDecimal.ZERO;
		switch (cycle) {
		case RLConstants.RL_MONTHLY_CYCLE:
			if(durationInDays>21) {
				payAmount=amount;
			}else {
			    payAmount = amount.divide(new BigDecimal(30), 8, RoundingMode.HALF_UP).multiply(duration); // high precision for intermediate
			}			
		break;
		case RLConstants.RL_QUATERLY_CYCLE:
			if(durationInDays>81) {
				payAmount=amount;
			}else {
			    payAmount = amount.divide(new BigDecimal(90), 8, RoundingMode.HALF_UP).multiply(duration); // high precision for intermediate
				}
		break;
		case RLConstants.RL_BIAANNUALY_CYCLE:
			if(durationInDays>171) {
				payAmount=amount;
			}else {
			    payAmount = amount.divide(new BigDecimal(180), 8, RoundingMode.HALF_UP).multiply(duration); // high precision for intermediate
				
			}
		break;
		default:
			if(durationInDays>356) {
				payAmount=amount;
			}else {
			    payAmount = amount.divide(new BigDecimal(365), 8, RoundingMode.HALF_UP).multiply(duration); // high precision for intermediate		
			}
		
		break;
		}
		System.out.println("payAmount = " + payAmount);
        
		return payAmount;
	}

	/**
	 * Builds a Demand object for a given allotment request without persisting it.
	 * Reuses existing calculation logic to create demand details and fills tax period and expiry
	 * using billing period information.
	 *
	 * @param allotmentRequest The allotment request containing application and requestInfo
	 * @param isSecurityDeposite Whether to include security deposit in calculation
	 * @return A constructed Demand or null if billing period couldn't be determined
	 */
	public Demand buildDemand(AllotmentRequest allotmentRequest, boolean isSecurityDeposite) {
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		String tenantId = allotmentDetails.getTenantId();
		String consumerCode = allotmentDetails.getApplicationNumber();

		OwnerInfo ownerInfo = allotmentDetails.getOwnerInfo().get(0);
		Owner payerUser = Owner.builder().name(ownerInfo.getName()).emailId(ownerInfo.getEmailId())
				.uuid(ownerInfo.getUserUuid()).mobileNumber(ownerInfo.getMobileNo()).tenantId(ownerInfo.getTenantId())
				.build();

		List<DemandDetail> demandDetails = calculateDemand(isSecurityDeposite, allotmentRequest);
		BigDecimal amountPayable = demandDetails.stream().map(DemandDetail::getTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		JsonNode additionalDetails = allotmentDetails.getAdditionalDetails();
		String cycle = additionalDetails.path("propertyDetails").get(0).path("feesPeriodCycle").asText();

		List<BillingPeriod> billingPeriods = masterDataService.getBillingPeriod(allotmentRequest.getRequestInfo(), tenantId);
		BillingPeriod billingPeriod = billingPeriods.stream()
				.filter(b -> b.getBillingCycle().equalsIgnoreCase(cycle)).findFirst().orElse(null);

		if (billingPeriod != null) {
			long startDay = billingPeriod.getTaxPeriodFrom() <= allotmentDetails.getStartDate()
					? allotmentDetails.getStartDate()
					: billingPeriod.getTaxPeriodFrom();

			long endDay = billingPeriod.getTaxPeriodTo() <= allotmentDetails.getEndDate()
					? billingPeriod.getTaxPeriodTo()
					: allotmentDetails.getEndDate();
			long expiryDate = billingPeriod.getDemandExpiryDate();

			Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(payerUser)
					.minimumAmountPayable(amountPayable).tenantId(tenantId).taxPeriodFrom(startDay).taxPeriodTo(endDay)
					.fixedbillexpirydate(expiryDate).billExpiryTime(expiryDate)
					.consumerType(allotmentDetails.getApplicationType())
					.businessService(RLConstants.RL_SERVICE_NAME).additionalDetails(null).build();
			return demand;
		}
		return null;
	}
	
	/**
	 * Adds roundOff taxHead if decimal values exists
	 * 
	 * @param tenantId      The tenantId of the demand
	 * @param demandDetails The list of demandDetail
	 */

	public void addRoundOffTaxHead(String tenantId, List<DemandDetail> demandDetails) {
		if (demandDetails == null || demandDetails.isEmpty())
			return;

		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal previousRoundOff = BigDecimal.ZERO;

		// Sum all taxHeads except RoundOff
		for (DemandDetail dd : demandDetails) {
			String code = dd.getTaxHeadMasterCode();
			if (code != null && RLConstants.ROUND_OFF_RL_APPLICATION.equalsIgnoreCase(code)) {
				previousRoundOff = previousRoundOff.add(safe(dd.getTaxAmount()));
			} else {
				totalTax = totalTax.add(safe(dd.getTaxAmount()));
			}
		}

		// Nearest rupee target via HALF_UP
		BigDecimal rounded = totalTax.setScale(0, RoundingMode.HALF_UP);
		BigDecimal roundOff = rounded.subtract(totalTax); // +ve to go up, -ve to go down

		// Adjust with any previous round-off already present
		if (previousRoundOff.compareTo(BigDecimal.ZERO) != 0) {
			roundOff = roundOff.subtract(previousRoundOff);
		}

		// Add only if non-zero
		if (roundOff.compareTo(BigDecimal.ZERO) != 0) {
			DemandDetail roundOffDemandDetail = DemandDetail.builder()
					.taxHeadMasterCode(RLConstants.ROUND_OFF_RL_APPLICATION).taxAmount(roundOff)
					.collectionAmount(BigDecimal.ZERO).tenantId(tenantId).build();
			demandDetails.add(roundOffDemandDetail);
		}
	}
	
	private static BigDecimal safe(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}


}
