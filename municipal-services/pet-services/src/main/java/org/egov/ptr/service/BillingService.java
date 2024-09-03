package org.egov.ptr.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.collection.Bill;
import org.egov.ptr.models.collection.BillRepository;
//import org.egov.ptr.models.Property;
import org.egov.ptr.models.collection.BillResponse;
import org.egov.ptr.models.collection.BillSearchCriteria;
import org.egov.ptr.models.collection.GenerateBillCriteria;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.ResponseInfoFactory;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BillingService {

	@Value("${egbs.host}")
	private String billingHost;

	@Value("${egbs.fetchbill.endpoint}")
	private String fetchBillEndpoint;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private PetConfiguration config;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private BillRepository billRepository;
//
//	@Autowired
//	private ResponseInfoFactory responseInfoFactory;
//
//	public BillResponse fetchBill(Property property, RequestInfo requestInfo) {
//		
//		StringBuilder uri = new StringBuilder(billingHost);
//		uri.append(fetchBillEndpoint);
//		uri.append("?").append("tenantId=").append(property.getTenantId());
//		uri.append("&businessService=").append(PT_BUSINESSSERVICE);
//		uri.append("&consumerCode=").append(property.getPropertyId());
//		
//		try {
//        	Optional<Object> response = serviceRequestRepository.fetchResult(uri, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
//        	
//        	if(response.isPresent()) {
//        		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.get();
//                BillResponse billResponse = mapper.convertValue(responseMap,BillResponse.class);
//                return billResponse;
//        	}else {
//        		throw new CustomException("IllegalArgumentException","Did not get any response from the billing services");
//        		
//        	}
//        }
//
//        catch(IllegalArgumentException  e)
//        {
//            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convert response into bill response");
//        }
//	}
	
	

    BillResponse generateBill(RequestInfo requestInfo,GenerateBillCriteria billCriteria){

        BillResponse billResponse = billRepository.fetchBill(billCriteria, requestInfo);
        
         return billResponse;
    }
    

	public BillResponse fetchBill(GenerateBillCriteria billCriteria, RequestInfo requestInfo) {
		

        String uri = config.getBillingHost().concat(config.getBillGenerateEndpoint());
        uri = uri.concat("?consumerCode=").concat(billCriteria.getConsumerCode());
        uri = uri.concat("&tenantId=").concat(billCriteria.getTenantId());
        uri = uri.concat("&businessService=").concat(billCriteria.getBusinessService());

        Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                                                             .requestInfo(requestInfo).build());
        BillResponse response = null;
         try{
              response = mapper.convertValue(result,BillResponse.class);
         }
         catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Unable to parse response of generate bill");
         }
         
		return response;
	}
	

	public List<Bill> searchBill(BillSearchCriteria billCriteria, RequestInfo requestInfo){
		
		String uri = config.getBillingHost().concat(config.getBillSearchEndpoint());
        uri = uri.concat("?tenantId=").concat(billCriteria.getTenantId());
        uri = uri.concat("&service=").concat(billCriteria.getService());
        uri = uri.concat("&consumerCode=").concat(StringUtils.join(billCriteria.getConsumerCode(), ","));

        Optional<Object> result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                                                             .requestInfo(requestInfo).build());
        
        BillResponse billResponse = mapper.convertValue(result.get(), BillResponse.class);
        
        return billResponse.getBill();
	}
	

}
