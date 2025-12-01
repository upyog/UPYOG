package org.egov.ptr.service;

import org.egov.ptr.models.AdditionalFeeRate;
import org.egov.ptr.models.BreedType;
import org.egov.ptr.models.DemandDetail;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.util.FeeCalculationUtil;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private PetUtil mdmsUtil;

	@Autowired
	private FeeCalculationUtil feeCalculationUtil;
	
	@Autowired
	private PetRegistrationRepository petRegistrationRepository;

	/**
	 * Calculates the demand based on the provided PetRegistrationRequest.
	 *
	 * @param petRegistrationRequest The request containing pet registration
	 *                               applications.
	 * @return A list of DemandDetail objects representing the calculated demand.
	 */
	public List<DemandDetail> calculateDemand(PetRegistrationRequest petRegistrationRequest) {
		String tenantId = petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId();

		List<BreedType> calculationTypes = mdmsUtil.getcalculationType(petRegistrationRequest.getRequestInfo(),
				tenantId, PTRConstants.PET_MASTER_MODULE_NAME);


		return processCalculationForDemandGeneration(tenantId, calculationTypes, petRegistrationRequest);
	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
																	 List<BreedType> calculationTypes, PetRegistrationRequest petRegistrationRequest) {

		String applicationType = petRegistrationRequest.getPetRegistrationApplications().get(0).getApplicationType();
		BigDecimal baseRegistrationFee = BigDecimal.ZERO;
		List<DemandDetail> demandDetails = new ArrayList<>();

		// Step 1: Calculate base registration fee
		for (BreedType type : calculationTypes) {
			if (applicationType.equalsIgnoreCase("NEWAPPLICATION") &&
					petRegistrationRequest.getPetRegistrationApplications().get(0).getPetDetails().getBreedType().equals(type.getName())) {
				baseRegistrationFee = type.getNewapplication();
				DemandDetail baseDemandDetail = DemandDetail.builder()
						.taxAmount(baseRegistrationFee)
						.taxHeadMasterCode(type.getFeeType())
						.tenantId(tenantId)
						.build();
				demandDetails.add(baseDemandDetail);
				break;
			}

			if (applicationType.equalsIgnoreCase("RENEWAPPLICATION") &&
					petRegistrationRequest.getPetRegistrationApplications().get(0).getPetDetails().getBreedType().equals(type.getName())) {
				baseRegistrationFee = type.getRenewapplication();
				DemandDetail baseDemandDetail = DemandDetail.builder()
						.taxAmount(baseRegistrationFee)
						.taxHeadMasterCode(type.getFeeType())
						.tenantId(tenantId)
						.build();
				demandDetails.add(baseDemandDetail);
				break;
			}
		}

		// Step 2: Calculate additional fees (ServiceCharge, PenaltyFee, InterestAmount)
		calculateAdditionalFees(petRegistrationRequest, tenantId, baseRegistrationFee, demandDetails);

		return demandDetails;
	}

	/**
	 * Calculates additional fees (ServiceCharge, PenaltyFee, InterestAmount) and adds them to demand details
	 * Flexible method that can handle any number of fee types from MDMS
	 */
	private void calculateAdditionalFees(PetRegistrationRequest petRegistrationRequest, String tenantId, 
										BigDecimal baseRegistrationFee, List<DemandDetail> demandDetails) {
		
		String currentFY = feeCalculationUtil.getCurrentFinancialYear();
		PetRegistrationApplication application = petRegistrationRequest.getPetRegistrationApplications().get(0);
		String applicationType = application.getApplicationType();
		long applicationDateMillis = application.getAuditDetails().getCreatedTime();
		
		// Define fee types to calculate - this can be easily extended
		String[] feeTypes = {"ServiceCharge", "PenaltyFee", "InterestAmount"};
		String[] taxHeadCodes = {"SERVICE_CHARGE", "PENALTY_FEE", "INTEREST_AMOUNT"};

		// Calculate each fee type
		for (int i = 0; i < feeTypes.length; i++) {
			String feeType = feeTypes[i];
			String taxHeadCode = taxHeadCodes[i];
			
			// Special handling for PenaltyFee
			if ("PenaltyFee".equals(feeType)) {
				calculatePenaltyFee(petRegistrationRequest, tenantId, baseRegistrationFee, 
					applicationType, applicationDateMillis, currentFY, taxHeadCode, demandDetails);
				continue;
			}
			
			// For other fees, use standard calculation
			int daysElapsed = feeCalculationUtil.calculateDaysElapsed(applicationDateMillis);
			
			// Fetch configurations from MDMS
			List<AdditionalFeeRate> feeConfigs = mdmsUtil.getFeeConfig(
				petRegistrationRequest.getRequestInfo(), tenantId, PTRConstants.PET_MASTER_MODULE_NAME, feeType);
			
			// ServiceCharge and InterestAmount must always be added, even if calculation is 0
			boolean alwaysAdd = "ServiceCharge".equals(feeType) || "InterestAmount".equals(feeType);
			
			// Calculate and add to demand details
			calculateAndAddFeeToDemandDetails(feeConfigs, baseRegistrationFee, daysElapsed, 
				currentFY, tenantId, taxHeadCode, demandDetails, alwaysAdd);
		}
	}
	
	/**
	 * Calculates penalty fee based on application type and validity date
	 * For NEW applications: PenaltyFee = 0 (but still added to demand)
	 * For RENEW applications: Calculated based on days from validity date with tiered structure
	 * Penalty fee is always added to demand details, even if amount is 0
	 */
	private void calculatePenaltyFee(PetRegistrationRequest petRegistrationRequest, String tenantId,
			BigDecimal baseRegistrationFee, String applicationType, long applicationDateMillis, 
			String currentFY, String taxHeadCode, List<DemandDetail> demandDetails) {
		
		BigDecimal penaltyAmount = BigDecimal.ZERO;
		PetRegistrationApplication application = petRegistrationRequest.getPetRegistrationApplications().get(0);
		
		// For NEW applications, penalty fee is always 0
		if ("NEWAPPLICATION".equalsIgnoreCase(applicationType)) {
			penaltyAmount = BigDecimal.ZERO;
		}
		// For RENEW applications, calculate penalty based on validity date
		else if ("RENEWAPPLICATION".equalsIgnoreCase(applicationType)) {
			// Get previous application's validity date
			Long validityDate = getPreviousApplicationValidityDate(application, petRegistrationRequest.getRequestInfo());
			
			if (validityDate == null) {
				log.warn("Could not find previous application validity date for renewal application: {}. Setting penalty to 0.", 
					application.getApplicationNumber());
				penaltyAmount = BigDecimal.ZERO;
			} else {
				// Calculate days from validity date to application creation date
				int daysFromValidityDate = feeCalculationUtil.calculateDaysFromValidityDate(validityDate, applicationDateMillis);
				
				// If renewal happens before validity date expires, no penalty
				if (daysFromValidityDate <= 0) {
					log.debug("Renewal application {} is before or on validity date ({} days), penalty = 0", 
						application.getApplicationNumber(), daysFromValidityDate);
					penaltyAmount = BigDecimal.ZERO;
				} else {
					log.debug("Renewal application {} is {} days after validity date", 
						application.getApplicationNumber(), daysFromValidityDate);
					
					// Fetch penalty configurations from MDMS
					List<AdditionalFeeRate> penaltyConfigs = mdmsUtil.getPenaltyFeeConfig(
						petRegistrationRequest.getRequestInfo(), tenantId, PTRConstants.PET_MASTER_MODULE_NAME);
					
					// Calculate tiered penalty
					penaltyAmount = feeCalculationUtil.calculateTieredPenalty(
						penaltyConfigs, daysFromValidityDate, currentFY);
				}
			}
		}
		
		// Always add penalty fee to demand details, even if amount is 0
		DemandDetail penaltyDemandDetail = DemandDetail.builder()
			.taxAmount(penaltyAmount)
			.taxHeadMasterCode(taxHeadCode)
			.tenantId(tenantId)
			.build();
		demandDetails.add(penaltyDemandDetail);
	}
	
	/**
	 * Gets the validity date from the previous application for renewal applications
	 */
	private Long getPreviousApplicationValidityDate(PetRegistrationApplication application, 
			org.egov.common.contract.request.RequestInfo requestInfo) {
		
		if (application.getPreviousApplicationNumber() == null || 
			application.getPreviousApplicationNumber().isEmpty()) {
			log.warn("Previous application number is not provided for renewal: {}", 
				application.getApplicationNumber());
			return null;
		}
		
		try {
			// Search for previous application
			PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder()
				.applicationNumber(Collections.singletonList(application.getPreviousApplicationNumber()))
				.tenantId(application.getTenantId())
				.build();
			
			List<PetRegistrationApplication> previousApps = petRegistrationRepository.getApplications(criteria);
			
			if (previousApps == null || previousApps.isEmpty()) {
				log.warn("Previous application not found: {} for renewal: {}", 
					application.getPreviousApplicationNumber(), application.getApplicationNumber());
				return null;
			}
			
			PetRegistrationApplication previousApp = previousApps.get(0);
			Long validityDate = previousApp.getValidityDate();
			
			if (validityDate == null) {
				log.warn("Previous application {} does not have validity date", 
					previousApp.getApplicationNumber());
				return null;
			}
			
			// Convert validity date from seconds to milliseconds if needed
			// Timestamps less than year 2001 in milliseconds are likely in seconds
			if (validityDate < 1000000000000L) {
				validityDate = validityDate * 1000;
				log.debug("Converted validity date from seconds to milliseconds: {}", validityDate);
			}
			
			return validityDate;
		} catch (Exception e) {
			log.error("Error fetching previous application validity date: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Calculates fee amount for given configurations and adds to demand details
	 * @param alwaysAdd If true, adds demand detail even if feeAmount is 0 (for ServiceCharge and InterestAmount)
	 */
	private void calculateAndAddFeeToDemandDetails(List<AdditionalFeeRate> feeConfigs, BigDecimal baseAmount, 
		int daysElapsed, String currentFY, String tenantId, String taxHeadCode, List<DemandDetail> demandDetails, boolean alwaysAdd) {
		
		BigDecimal totalFeeAmount = BigDecimal.ZERO;
		
		// Calculate total fee amount from all configurations
		if (feeConfigs != null && !feeConfigs.isEmpty()) {
			for (AdditionalFeeRate feeConfig : feeConfigs) {
				BigDecimal feeAmount = feeCalculationUtil.calculateFeeAmount(feeConfig, baseAmount, daysElapsed, currentFY);
				if (feeAmount != null && feeAmount.compareTo(BigDecimal.ZERO) >= 0) {
					totalFeeAmount = totalFeeAmount.add(feeAmount);
				}
			}
		}
		
		// Add demand detail if amount is >= 0, or if alwaysAdd is true (for ServiceCharge and InterestAmount)
		// This ensures ServiceCharge and InterestAmount are always added, even if calculation is 0
		if (alwaysAdd || totalFeeAmount.compareTo(BigDecimal.ZERO) >= 0) {
			DemandDetail demandDetail = DemandDetail.builder()
					.taxAmount(totalFeeAmount)
					.taxHeadMasterCode(taxHeadCode)
					.tenantId(tenantId)
					.build();
			demandDetails.add(demandDetail);
		}
	}
}

