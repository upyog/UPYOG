package org.egov.rentlease.repository;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rentlease.contract.bill.Demand;
import org.egov.rentlease.contract.bill.DemandRequest;
import org.egov.rentlease.contract.bill.DemandResponse;
import org.egov.rentlease.util.RentConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class DemandRepository {
	 @Autowired
	    private RestCallRepository restCallRepository;

	    @Autowired
	    private RentConstants config;

	    @Autowired
	    private ObjectMapper objectMapper;
	    
	    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands){
	        StringBuilder url = new StringBuilder(config.getBillHost());
	        url.append(config.getDemandCreateEndpoint());
	        DemandRequest request = new DemandRequest(requestInfo,demands);
	        Object result = restCallRepository.fetchResult(url,request);
	        DemandResponse response = null;
	        try{
	            response = objectMapper.convertValue(result,DemandResponse.class);
	        }
	        catch(IllegalArgumentException e){
	            throw new CustomException("PARSING ERROR","Failed to parse response of create demand");
	        }
	        return response.getDemands();
	    }


}
