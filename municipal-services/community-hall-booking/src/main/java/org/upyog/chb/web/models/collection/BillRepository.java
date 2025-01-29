package org.upyog.chb.web.models.collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BillRepository {


    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CommunityHallBookingConfiguration communityHallBookingConfiguration;

    @Autowired
    private ObjectMapper objectMapper;
    
	public BillResponse fetchBill(GenerateBillCriteria billCriteria, RequestInfo requestInfo) {
		

        String uri = communityHallBookingConfiguration.getBillingHost().concat(communityHallBookingConfiguration.getBillGenerateEndpoint());
        uri = uri.concat("?consumerCode=").concat(billCriteria.getConsumerCode());
        uri = uri.concat("&tenantId=").concat(billCriteria.getTenantId());
        uri = uri.concat("&businessService=").concat(billCriteria.getBusinessService());

        Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                                                             .requestInfo(requestInfo).build());
        BillResponse response = null;
         try{
              response = objectMapper.convertValue(result,BillResponse.class);
         }
         catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Unable to parse response of generate bill");
         }
         
		return response;
	}
	

//	public List<Bill> searchBill(BillSearchCriteria billCriteria, RequestInfo requestInfo){
//		
//		String uri = communityHallBookingConfiguration.getBillingHost().concat(communityHallBookingConfiguration.getBillSearchEndpoint());
//        uri = uri.concat("?tenantId=").concat(billCriteria.getTenantId());
//        uri = uri.concat("&service=").concat(billCriteria.getService());
//        uri = uri.concat("&consumerCode=").concat(StringUtils.join(billCriteria.getConsumerCode(), ","));
//
//        Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
//                                                             .requestInfo(requestInfo).build());
//        
//        BillResponse billResponse = objectMapper.convertValue(result, BillResponse.class);
//        
//        return billResponse.getBill();
//	}
	
	
}

