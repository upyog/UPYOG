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
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DemandService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private PetUtil petUtil;

	@Autowired
	private CalculationService calculationService;

	public List<Demand> createDemand(PetRegistrationRequest petReq) {
	    if (petReq == null || petReq.getPetRegistrationApplications().isEmpty()) {
	        throw new IllegalArgumentException("Pet registration request is Empty");
	    }
	    PetRegistrationApplication petApplication = petReq.getPetRegistrationApplications().get(0);
	    String tenantId = petApplication.getTenantId();
	    String consumerCode = petApplication.getApplicationNumber();
	    BigDecimal amountPayable = calculateFee(petApplication.getApplicationType());

	    User owner = buildUser(petApplication);
	    List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, tenantId);
	    Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails, amountPayable);

	    return demandRepository.saveDemand(petReq.getRequestInfo(), Collections.singletonList(demand));
	}

	private BigDecimal calculateFee(String applicationType) {
	    if (PTRConstants.RENEW_PET_APPLICATION.equals(applicationType)) {
	        return config.getRenewApplicationFee();
	    } else {
	        return config.getNewApplicationFee();
	    }
	}

	private User buildUser(PetRegistrationApplication petApplication) {
	    return User.builder()
	            .name(petApplication.getApplicantName())
	            .emailId(petApplication.getEmailId())
	            .mobileNumber(petApplication.getMobileNumber())
	            .tenantId(petApplication.getTenantId())
	            .build();
	}

	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId) {
	    return Collections.singletonList(
	            DemandDetail.builder()
	                    .collectionAmount(BigDecimal.ZERO)
	                    .taxAmount(amountPayable)
	                    .taxHeadMasterCode(PTRConstants.PET_TAX_MASTER_CODE)
	                    .tenantId(tenantId)
	                    .build()
	    );
	}

	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails, BigDecimal amountPayable) {
	    return Demand.builder()
	            .consumerCode(consumerCode)
	            .demandDetails(demandDetails)
	            .payer(owner)
	            .minimumAmountPayable(amountPayable)
	            .tenantId(tenantId)
	            .taxPeriodFrom(PetUtil.getFinancialYearStart())
	            .taxPeriodTo(PetUtil.getFinancialYearEnd())
	            .consumerType(PTRConstants.PET_BUSINESSSERVICE)
	            .businessService(config.getBusinessService())
	            .additionalDetails(null)
	            .build();
	}

}
