package org.upyog.chb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.DemandRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandDetail;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DemandService {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRepository demandRepository;
	
	/**
	 * 1. Fetch tax heads from mdms tax-heads.json
	 * 2. Map amount to tax heads from CalculateType.json
	 * 3. Create XDemand for particular tax heads 
	 * 4. Bill will be automatically generated when fetch bill api is called for demand created by this API
	 * @param bookingRequest
	 * @return
	 */

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User user =bookingRequest.getRequestInfo().getUserInfo();
		
		User owner = User.builder().name(user.getName()).emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber()).tenantId(bookingDetail.getTenantId()).build();
		
		List<DemandDetail> demandDetails = new LinkedList<>();
		
		demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(BigDecimal.valueOf(500.00))
				.taxHeadMasterCode("CHB_BOOKING_FEE").tenantId(null).build());

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(BigDecimal.valueOf(500.00)).tenantId(tenantId)
				.taxPeriodFrom(Long.valueOf("1680307199000")).taxPeriodTo(Long.valueOf("1711929599000"))
				.consumerType(config.getModuleName()).businessService(config.getBusinessServiceName()).additionalDetails(null).build();
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}

}
