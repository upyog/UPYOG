package org.egov.ptr.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Demand;
import org.egov.ptr.models.DemandDetail;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.repository.DemandRepository;
import org.egov.ptr.util.CommonUtils;
import org.egov.ptr.util.PTRConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Service class responsible for handling demand creation for pet registration.
 */
@Service
public class DemandService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private CalculationService calculationService;


	/**
	 * Creates a demand for a pet registration request.
	 *
	 * @param petReq The pet registration request.
	 * @return A list containing the created demand.
	 */
	public List<Demand> createDemand(PetRegistrationRequest petReq) {
		if (petReq == null || petReq.getPetRegistrationApplications().isEmpty()) {
			throw new IllegalArgumentException("Pet registration request is Empty");
		}

		// Extracting required information from the pet application
		PetRegistrationApplication petApplication = petReq.getPetRegistrationApplications().get(0);
		String tenantId = petApplication.getTenantId().split("\\.")[0];
		String consumerCode = petApplication.getApplicationNumber();

		// Calculating the fee for the pet registration
		BigDecimal amountPayable = calculationService.calculateFee(petApplication.getApplicationType(),
				petReq.getRequestInfo(), tenantId);

		User owner = buildUser(petApplication);
		List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, tenantId);
		Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails, amountPayable);

		return demandRepository.saveDemand(petReq.getRequestInfo(), Collections.singletonList(demand));
	}

	/**
	 * Builds a User object based on the applicant's details.
	 *
	 * @param petApplication The pet registration application.
	 * @return A User object representing the applicant.
	 */
	private User buildUser(PetRegistrationApplication petApplication) {
		return User.builder().name(petApplication.getApplicantName()).emailId(petApplication.getEmailId())
				.mobileNumber(petApplication.getMobileNumber()).tenantId(petApplication.getTenantId()).build();
	}

	/**
	 * Creates a list of demand details based on the payable amount.
	 *
	 * @param amountPayable The total tax amount payable.
	 * @param tenantId The tenant ID.
	 * @return A list of DemandDetail objects.
	 */
	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId) {
		return Collections
				.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(amountPayable)
						.taxHeadMasterCode(PTRConstants.PET_TAX_MASTER_CODE).tenantId(tenantId).build());
	}


	/**
	 * Builds the Demand object for the given details.
	 *
	 * @param tenantId The tenant ID.
	 * @param consumerCode The unique application number.
	 * @param owner The applicant's details.
	 * @param demandDetails The list of demand details.
	 * @param amountPayable The total amount payable.
	 * @return A Demand object.
	 */
	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
			BigDecimal amountPayable) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(amountPayable).tenantId(tenantId)
				.taxPeriodFrom(CommonUtils.getFinancialYearStart()).taxPeriodTo(CommonUtils.getFinancialYearEnd())
				.consumerType(PTRConstants.PET_BUSINESS_SERVICE).businessService(config.getBusinessService())
				.additionalDetails(null).build();
	}

}
