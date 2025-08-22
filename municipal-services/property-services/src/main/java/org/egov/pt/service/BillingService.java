package org.egov.pt.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.Assessment.ModeOfPayment;
import org.egov.pt.models.Property;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.repository.ServiceRequestRepository;
import org.egov.pt.web.contracts.AssessmentRequest;
import org.egov.pt.web.contracts.DemandRequest;
import org.egov.pt.web.contracts.DemandResponse;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.egov.pt.util.PTConstants.PT_BUSINESSSERVICE;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BillingService {
	
	@Value("${egbs.host}")
	private String billingHost;
	
	@Value("${egbs.fetchbill.endpoint}")
	private String fetchBillEndpoint;
	
	@Value("${egbs.fetchdemand.endpoint}")
	private String fetchdemandEndpoint;
	
	@Value("${egbs.updatedemand.endpoint}")
	private String updateDemandEndpoint;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;
	
	public BillResponse fetchBill(Property property, RequestInfo requestInfo, Assessment assessment) {
		
		StringBuilder uri = new StringBuilder(billingHost);
		uri.append(fetchBillEndpoint);
		uri.append("?").append("tenantId=").append(property.getTenantId());
		uri.append("&businessService=").append(PT_BUSINESSSERVICE);
		uri.append("&consumerCode=").append(property.getPropertyId());
		uri.append("&modeOfPayment=").append(assessment.getModeOfPayment());
		
		try {
        	Optional<Object> response = serviceRequestRepository.fetchResult(uri, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
        	
        	if(response.isPresent()) {
        		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.get();
                BillResponse billResponse = mapper.convertValue(responseMap,BillResponse.class);
                return billResponse;
        	}else {
        		throw new CustomException("IllegalArgumentException","Did not get any response from the billing services");
        		
        	}
        }

        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convert response into bill response");
        }
	}
	
	
public BillResponse fetchBillForDailyBillUpdate(String property, RequestInfo requestInfo, String tenanatId, ModeOfPayment modeOfPayment) {
		
		StringBuilder uri = new StringBuilder(billingHost);
		uri.append(fetchBillEndpoint);
		uri.append("?").append("tenantId=").append(tenanatId);
		uri.append("&businessService=").append(PT_BUSINESSSERVICE);
		uri.append("&consumerCode=").append(property);
		uri.append("&modeOfPayment=").append(modeOfPayment);
		
		try {
        	Optional<Object> response = serviceRequestRepository.fetchResult(uri, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
        	
        	if(response.isPresent()) {
        		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.get();
                BillResponse billResponse = mapper.convertValue(responseMap,BillResponse.class);
                return billResponse;
        	}else {
        		throw new CustomException("IllegalArgumentException","Did not get any response from the billing services");
        		
        	}
        }

        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convert response into bill response");
        }
	}
	
	
	public DemandResponse fetchDemand( AssessmentRequest assessment) {
		
		StringBuilder uri = new StringBuilder(billingHost);
		uri.append(fetchdemandEndpoint);
		uri.append("?").append("tenantId=").append(assessment.getAssessment().getTenantId());
		uri.append("&consumerCode=").append(assessment.getAssessment().getPropertyId());
		
		System.out.println(uri);
		
		
		try {
        	Optional<Object> response = serviceRequestRepository.fetchResult(uri, RequestInfoWrapper.builder().requestInfo(assessment.getRequestInfo()).build());
        	
        	if(response.isPresent()) {
        		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.get();
        		DemandResponse billResponse = mapper.convertValue(responseMap,DemandResponse.class);
                return billResponse;
        	}else {
        		throw new CustomException("IllegalArgumentException","Did not get any response from the billing services");
        		
        	}
        }

        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convert response into bill response");
        }
	}
	
	
	
public DemandResponse updateDemand( DemandRequest request) {
		
		StringBuilder uri = new StringBuilder(billingHost);
		uri.append(updateDemandEndpoint);
		//uri.append("?").append("tenantId=").append(assessment.getAssessment().getTenantId());
		//uri.append("&consumerCode=").append(assessment.getAssessment().getPropertyId());
		
		System.out.println(uri);
		
		
		try {
        	Optional<Object> response = serviceRequestRepository.fetchResult(uri,request);
        	
        	if(response.isPresent()) {
        		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.get();
        		DemandResponse billResponse = mapper.convertValue(responseMap,DemandResponse.class);
                return billResponse;
        	}else {
        		throw new CustomException("IllegalArgumentException","Did not get any response from the billing services");
        		
        	}
        }

        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convert response into bill response");
        }
	}
	
	
	

}
