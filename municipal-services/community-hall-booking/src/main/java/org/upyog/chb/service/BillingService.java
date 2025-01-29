package org.upyog.chb.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.collection.Bill;
import org.upyog.chb.web.models.collection.BillRepository;
//import org.egov.ptr.models.Property;
import org.upyog.chb.web.models.collection.BillResponse;
import org.upyog.chb.web.models.collection.BillSearchCriteria;
import org.upyog.chb.web.models.collection.GenerateBillCriteria;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.util.ResponseInfoFactory;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BillingService {

	@Value("${egbs.host}")
	private String billingHost;

	@Value("${egov.bill.gen.endpoint}")
	private String fetchBillEndpoint;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private BillRepository billRepository;

	

    public BillResponse generateBill(RequestInfo requestInfo,GenerateBillCriteria billCriteria){

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

        Optional<Object> result = serviceRequestRepository.fetchResultV1(new StringBuilder(uri),RequestInfoWrapper.builder()
                .requestInfo(requestInfo).build());                          
       
        BillResponse billResponse = mapper.convertValue(result.get(), BillResponse.class);
       
        return billResponse.getBill();
	}
	

}
