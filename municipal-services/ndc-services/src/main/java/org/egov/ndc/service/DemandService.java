package org.egov.ndc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.web.model.calculator.Calculation;
import org.egov.ndc.web.model.demand.Demand;
import org.egov.ndc.web.model.demand.DemandDetail;
import org.egov.ndc.web.model.demand.DemandRequest;
import org.egov.ndc.web.model.demand.DemandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DemandService {

    @Autowired
    private NDCConfiguration ndcConfiguration;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ServiceRequestRepository repository;

    public List<Demand> generateDemands(RequestInfo requestInfo, List<Calculation> calculations){
        List<Demand> demands = new ArrayList<>();

        for(Calculation calculation : calculations){
            DemandDetail demandDetail = DemandDetail.builder()
                    .tenantId(calculation.getTenantId())
                    .taxAmount(BigDecimal.valueOf(calculation.getTotalAmount()))
                    .taxHeadMasterCode(ndcConfiguration.getTaxHeadMasterCode()).build();

            Demand demand = Demand.builder()
                    .tenantId(calculation.getTenantId()).consumerCode(calculation.getApplicationNumber())
                    .consumerType("NDC_APPLICATION_FEE")
                    .businessService(NDCConstants.NDC_MODULE)
                    .taxPeriodFrom(System.currentTimeMillis()).taxPeriodTo(System.currentTimeMillis())
                    .demandDetails(Collections.singletonList(demandDetail))
                    .build();

            demands.add(demand);
        }

        StringBuilder url = new StringBuilder().append(ndcConfiguration.getBillingServiceHost())
                .append(ndcConfiguration.getDemandCreateEndpoint());

        DemandRequest demandRequest = DemandRequest.builder().requestInfo(requestInfo).demands(demands).build();

        Object response = repository.fetchResult(url,demandRequest);

        DemandResponse demandResponse = mapper.convertValue(response,DemandResponse.class);
        return demandResponse.getDemands();
    }
}
