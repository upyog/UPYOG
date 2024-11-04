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
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

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

	public List<Demand> createDemand(PetRegistrationRequest petReq) {
		String tenantId = petReq.getPetRegistrationApplications().get(0).getTenantId();
		String consumerCode = petReq.getPetRegistrationApplications().get(0).getApplicationNumber();
		BigDecimal amountPayableInteger = getAmountPayableMdms(petReq);
		PetRegistrationApplication petApplication = petReq.getPetRegistrationApplications().get(0);
		User owner = User.builder().name(petApplication.getApplicantName()).emailId(petApplication.getEmailId())
				.mobileNumber(petApplication.getMobileNumber()).tenantId(petApplication.getTenantId()).build();
		List<DemandDetail> demandDetails = new LinkedList<>();
		demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(BigDecimal.valueOf(500.00))
				.taxHeadMasterCode("PET_REGISTRATION_FEE").tenantId(null).build());

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(amountPayableInteger).tenantId(tenantId)
				.taxPeriodFrom(Long.valueOf("1680307199000")).taxPeriodTo(Long.valueOf("1711929599000"))
				.consumerType("ptr").businessService(config.getBusinessService()).additionalDetails(null).build();
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(petReq.getRequestInfo(), demands);
	}

	private BigDecimal getAmountPayableMdms(PetRegistrationRequest petReq) {
		
		return null;
	}

}
