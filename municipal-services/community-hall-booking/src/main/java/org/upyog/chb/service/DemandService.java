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

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User owner = User.builder().name(bookingDetail.getApplicantName()).emailId(bookingDetail.getApplicantEmailId())
				.mobileNumber(bookingDetail.getApplicantMobileNo()).tenantId(bookingDetail.getTenantId()).build();
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
