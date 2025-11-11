package org.egov.ptr.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.*;
import org.egov.ptr.models.collection.GetBillCriteria;
import org.egov.ptr.repository.DemandRepository;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.PetUtil;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
		Owner owner = Owner.builder().permanentAddress(petApplication.getAddress().getAddressId()).name(petApplication.getOwner().getName()).emailId(petApplication.getOwner().getEmailId())
				.mobileNumber(petApplication.getOwner().getMobileNumber()).tenantId(petApplication.getTenantId()).build();
		List<DemandDetail> demandDetails = calculationService.calculateDemand(petReq);
		BigDecimal amountPayable = new BigDecimal(0);
		String applicationType = petReq.getPetRegistrationApplications().get(0).getApplicationType();


		amountPayable = demandDetails.stream()
				.map(DemandDetail::getTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		Demand demand = Demand.builder()
				.consumerCode(consumerCode)
				.demandDetails(demandDetails)
				.payer(owner)
				.minimumAmountPayable(amountPayable)
				.tenantId(tenantId)
				.taxPeriodFrom(Long.valueOf("1743445800000"))
				.taxPeriodTo(Long.valueOf("1774981799000"))
				.consumerType(PET_BUSINESSSERVICE)
				.businessService("pet-services")
				.additionalDetails(null)
				.build();

		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(petReq.getRequestInfo(), demands);
	}


	public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

		if (getBillCriteria.getAmountExpected() == null) getBillCriteria.setAmountExpected(BigDecimal.ZERO);
//		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		DemandResponse res = mapper.convertValue(
				serviceRequestRepository.fetchResult(petUtil.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);
		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(EMPTY_DEMAND_ERROR_CODE, EMPTY_DEMAND_ERROR_MESSAGE);
		}

		Map<String,List<Demand>> consumerCodeToDemandMap = new HashMap<>();
		res.getDemands().forEach(demand -> {
			if(consumerCodeToDemandMap.containsKey(demand.getConsumerCode()))
				consumerCodeToDemandMap.get(demand.getConsumerCode()).add(demand);
			else {
				List<Demand> demands = new LinkedList<>();
				demands.add(demand);
				consumerCodeToDemandMap.put(demand.getConsumerCode(),demands);
			}
		});

//		if (!CollectionUtils.isEmpty(consumerCodeToDemandMap)) {
//			List<Demand> demandsToBeUpdated = new LinkedList<>();
//			DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
//			StringBuilder updateDemandUrl = petUtil.getUpdateDemandUrl();
//            repository.fetchResult(updateDemandUrl, request);
//		}
		return res;
	}

}
