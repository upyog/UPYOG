package org.ksmart.birth.birthcommon.repoisitory;

import java.util.List;

import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.model.demand.DemandRequest;
import org.ksmart.birth.birthcommon.model.demand.DemandResponse;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;


@Repository
public class DemandRepository {


    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private BirthConfiguration config;

    @Autowired
    @Qualifier("objectMapperBnd")
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
            throw new CustomException("PARSING ERROR","Failed to parse response of create demand");
        }
        
        return response.getDemands();
    }


}
