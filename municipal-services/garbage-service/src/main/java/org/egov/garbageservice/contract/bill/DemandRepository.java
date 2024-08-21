package org.egov.garbageservice.contract.bill;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.util.ApplicationPropertiesAndConstant;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class DemandRepository {


    @Autowired
    private RestCallRepository restCallRepository;

    @Autowired
    private ApplicationPropertiesAndConstant config;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Creates demand
     * @param requestInfo The RequestInfo of the calculation Request
     * @param demands The demands to be created
     * @return The list of demand created
     */
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


    /**
     * Updates the demand
     * @param requestInfo The RequestInfo of the calculation Request
     * @param demands The demands to be updated
     * @return The list of demand updated
     */
    public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands){
        StringBuilder url = new StringBuilder(config.getBillHost());
        url.append(config.getDemandUpdateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo,demands);
        Object result = restCallRepository.fetchResult(url,request);
        DemandResponse response = null;
        try{
            response = objectMapper.convertValue(result,DemandResponse.class);
        }
        catch(IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Failed to parse response of update demand");
        }
        return response.getDemands();

    }


	public DemandResponse search(String tenantId, Set<String> consumerCodes, RequestInfoWrapper requestInfoWrapper,
			String businessService) {
		
        String uri = config.getBillHost().concat(config.getDemandSearchEndpoint());
        uri = uri.replace("{1}",tenantId);
        uri = uri.replace("{2}",businessService);
        uri = uri.replace("{3}",StringUtils.join(consumerCodes, ','));

        Object result = restCallRepository.fetchResult(new StringBuilder(uri),requestInfoWrapper);
        DemandResponse response = null;
        
        try {
             response = objectMapper.convertValue(result, DemandResponse.class);
        }
        catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Failed to parse response from Demand Search");
        }
        
		return response;
	}


}
