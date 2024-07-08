package org.upyog.chb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.DemandRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandDetail;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest, Object mdmsData) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User user =bookingRequest.getRequestInfo().getUserInfo();

		
		User owner = User.builder().name(user.getName()).emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber()).tenantId(bookingDetail.getTenantId()).build();
		
		List<DemandDetail> demandDetails = getDemandDetails(bookingRequest);

		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner)
				.minimumAmountPayable(BigDecimal.valueOf(500.00)).tenantId(tenantId)
				.taxPeriodFrom(Long.valueOf("1680307199000")).taxPeriodTo(Long.valueOf("1711929599000"))
				.consumerType(config.getModuleName()).businessService(config.getBusinessServiceName()).additionalDetails(null).build();
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}
	
	private List<DemandDetail> getDemandDetails(CommunityHallBookingRequest bookingRequest){
		Object response = MdmsUtil.getMDMSDataMap();
		
		log.info("Calculkation data for CHB booking demand service  :" + 
				JsonPath.read(response,
						CommunityHallBookingConstants.CHB_JSONPATH_CODE +  "." + CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));
		
		/*
		 * log.info("Calculkation data for CHB booking :" + JsonPath.read(response,
		 * CommunityHallBookingConstants.CHB_JSONPATH_CODE + "." +
		 * CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));
		 */
	
		//log.info("first calculation data : " + list.get(0));
		
	//	log.info("first calculation data : " + list.get(0).);
		
		List<CalculationType> calculationTypes = new ArrayList<>();
		
		for (int i = 0; i < 4; i++) {
			String basePath = CommunityHallBookingConstants.CHB_JSONPATH_CODE +  "." + CommunityHallBookingConstants.CHB_CALCULATION_TYPE 
					+".["+i +"].";
			int amount = JsonPath.read(response, basePath + "amount");
			CalculationType calculationType = CalculationType.builder()
					.amount( new BigDecimal(amount))
					.applicationType(JsonPath.read(response, basePath + "applicationType"))
					.feeType(JsonPath.read(response, basePath + "feeType"))
					.serviceType(JsonPath.read(response, basePath + "serviceType"))
					.build();
			
			calculationTypes.add(calculationType);
		}
		
		log.info("calculationTypes " + calculationTypes);
		
       List<DemandDetail> demandDetails = new LinkedList<>();
		
        String tenantId = bookingRequest.getHallsBookingApplication().getTenantId().split("\\.")[0];
       
		
		demandDetails = calculationTypes.stream().map(data ->
		//log.info("data :" + data);
		DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(data.getAmount())
				.taxHeadMasterCode(data.getFeeType()).tenantId(tenantId).build()
		).collect(Collectors.toList());
		
		 //TODO: remove this with json and data
		demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).taxAmount(BigDecimal.valueOf(500.00))
				.taxHeadMasterCode("CHB_BOOKING_FEE").tenantId(tenantId).build());
		
		return demandDetails;

	}

}
