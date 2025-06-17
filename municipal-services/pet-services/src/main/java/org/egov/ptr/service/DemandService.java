package org.egov.ptr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.User;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Demand;
import org.egov.ptr.models.DemandDetail;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.repository.DemandRepository;
import org.egov.ptr.repository.ServiceRequestRepository;
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
	


	public List<Demand> createDemand(PetRegistrationRequest petReq, BigDecimal taxAmount) {
		String tenantId = petReq.getPetRegistrationApplications().get(0).getTenantId();
		String consumerCode = petReq.getPetRegistrationApplications().get(0).getApplicationNumber();
		PetRegistrationApplication petApplication = petReq.getPetRegistrationApplications().get(0);
		User owner = User.builder().name(petApplication.getApplicantName()).emailId(petApplication.getEmailId())
				.mobileNumber(petApplication.getMobileNumber()).tenantId(petApplication.getTenantId()).build();
		List<DemandDetail> demandDetails = new LinkedList<>();
		demandDetails.add(DemandDetail.builder()
		.collectionAmount(BigDecimal.ZERO)
		.taxAmount(taxAmount)
				.taxHeadMasterCode("PET_REGISTRATION_FEE").tenantId(null).build());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(config.getBillExpiryAfter()));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		
		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(taxAmount).tenantId(tenantId)
				.taxPeriodFrom(new Date().getTime()).taxPeriodTo(new Date().getTime() + 31536000L)	// fee for 1year
				.consumerType("ptr").businessService("pet-services")
				.fixedBillExpiryDate(cal.getTimeInMillis())
				.additionalDetails(null).build();
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(petReq.getRequestInfo(), demands);
	}

}
