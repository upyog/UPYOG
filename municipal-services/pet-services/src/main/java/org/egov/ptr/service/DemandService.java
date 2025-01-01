package org.egov.ptr.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.egov.common.contract.request.RequestInfo;
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

import java.math.BigDecimal;
import java.util.*;
import static org.egov.ptr.util.PTRConstants.*;

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
		String tenantId = petReq.getPetRegistrationApplications().get(0).getTenantId();
		String consumerCode = petReq.getPetRegistrationApplications().get(0).getApplicationNumber();

		PetRegistrationApplication petApplication = petReq.getPetRegistrationApplications().get(0);
		User owner = User.builder().name(petApplication.getApplicantName()).emailId(petApplication.getEmailId())
				.mobileNumber(petApplication.getMobileNumber()).tenantId(petApplication.getTenantId()).build();
//		List<DemandDetail> demandDetails = calculationService.calculateDemand(petReq);
		BigDecimal amountPayable = new BigDecimal(0);
		String applicationType = petReq.getPetRegistrationApplications().get(0).getApplicationType();
		if (applicationType.equals(PTRConstants.RENEW_PET_APPLICATION)) {
			amountPayable = config.getRenewApplicationFee();
		} else {
			amountPayable = config.getNewApplicationFee();
		}

		List<DemandDetail> demandDetails = new LinkedList<>();
		demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(amountPayable)
				.taxHeadMasterCode("PET_REGISTRATION_FEE").tenantId(null).build());
		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(amountPayable).tenantId(tenantId).taxPeriodFrom(Long.valueOf("1680307199000"))
				.taxPeriodTo(Long.valueOf("1711929599000")).consumerType(PET_BUSINESSSERVICE)
				.businessService(config.getBusinessService()).additionalDetails(null).build();
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(petReq.getRequestInfo(), demands);
	}

}
