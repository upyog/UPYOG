package org.upyog.cdwm.calculator.repository;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandRequest;
import org.upyog.cdwm.calculator.web.models.demand.DemandResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class DemandRepository {


    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CalculatorConfig config;

    @Autowired
    private ObjectMapper mapper;


    /**
     * Creates demand
     * @param requestInfo The RequestInfo of the calculation Request
     * @param demands The demands to be created
     * @return The list of demand created
     */
    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandCreateEndpoint());
        DemandRequest request = new DemandRequest(requestInfo,demands);
        Object result = serviceRequestRepository.fetchResult(url,request);
        DemandResponse response = null;
        try{
            response = mapper.convertValue(result,DemandResponse.class);
        }
        catch(IllegalArgumentException e){
            throw new CustomException(CalculatorConstants.PARSING_ERROR,"Failed to parse response of create demand");
        }
        return response.getDemands();
    }
}
