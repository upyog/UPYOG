package org.egov.schedulerservice.contract.bill;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.egov.schedulerservice.util.RestCallRepository;
import org.egov.schedulerservice.util.SchedulerConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BillRepository {


    @Autowired
    private RestCallRepository restCallRepository;

    @Autowired
    private SchedulerConstants config;

    @Autowired
    private ObjectMapper objectMapper;
    
	public BillResponse fetchBill(GenerateBillCriteria billCriteria, RequestInfo requestInfo) {
		

        String uri = config.getBillHost().concat(config.getFetchBillEndpoint());
        uri = uri.concat("?consumerCode=").concat(billCriteria.getConsumerCode().stream().collect(Collectors.joining(",")));
        uri = uri.concat("&tenantId=").concat(billCriteria.getTenantId());
        uri = uri.concat("&businessService=").concat(billCriteria.getBusinessService());

        Object result = restCallRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
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
	

	public List<Bill> searchBill(BillSearchCriteria billCriteria, RequestInfo requestInfo){
		
		String uri = config.getBillHost().concat(config.getSearchBillEndpoint());
        uri = uri.concat("?tenantId=").concat(billCriteria.getTenantId());
        uri = uri.concat("&service=").concat(billCriteria.getService());
        uri = uri.concat("&retrieveAll=").concat("true");
        uri = uri.concat("&consumerCode=").concat(StringUtils.join(billCriteria.getConsumerCode(), ","));

        Object result = restCallRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                                                             .requestInfo(requestInfo).build());
        
        BillResponse billResponse = objectMapper.convertValue(result, BillResponse.class);
        
        return billResponse.getBill();
	}
	
	
}
