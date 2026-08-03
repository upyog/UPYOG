package org.egov.ndc.calculator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.ndc.calculator.config.NDCCalculatorConfig;
import org.egov.ndc.calculator.repository.ServiceRequestRepository;
import org.egov.ndc.calculator.utils.CalculatorUtils;
import org.egov.ndc.calculator.web.models.Calculation;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.ndc.calculator.web.models.RequestInfoWrapper;
import org.egov.ndc.calculator.web.models.bill.GetBillCriteria;
import org.egov.ndc.calculator.web.models.demand.Demand;
import org.egov.ndc.calculator.web.models.demand.DemandDetail;
import org.egov.ndc.calculator.web.models.demand.DemandRequest;
import org.egov.ndc.calculator.web.models.demand.DemandResponse;
import org.egov.ndc.calculator.web.models.ndc.Application;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.egov.ndc.calculator.utils.NDCConstants.EMPTY_DEMAND_ERROR_CODE;
import static org.egov.ndc.calculator.utils.NDCConstants.EMPTY_DEMAND_ERROR_MESSAGE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandService {

    private final NDCCalculatorConfig ndcConfiguration;

    private final CalculatorUtils utils;

    private final ObjectMapper mapper;

    private final ServiceRequestRepository repository;

    public List<Demand> generateDemands(RequestInfo requestInfo, List<Calculation> calculations, CalculationReq calculationReq) {
        List<Demand> demands = new ArrayList<>();

        for (Calculation calculation : calculations) {
            DemandDetail demandDetail = DemandDetail.builder()
                    .tenantId(calculation.getTenantId())
                    .taxAmount(BigDecimal.valueOf(calculation.getTotalAmount()))
                    .taxHeadMasterCode(ndcConfiguration.getTaxHeadMasterCode()).build();
            log.info("Calculation for which demand is to be created is: {}", calculation);
            log.info("CalculationReq for which demand is to be created is: {}", calculationReq);
            AtomicReference<User> owner = new AtomicReference<>(calculationReq.getCalculationCriteria().get(0).getNdcApplicationRequest().getApplications().get(0).getOwners().get(0).toCommonUser());

            Application application = calculationReq.getCalculationCriteria().get(0).getNdcApplicationRequest().getApplications().get(0);
            application.getOwners().forEach(ownerInfo -> {
                if (ownerInfo.getIsPrimaryOwner() != null && ownerInfo.getIsPrimaryOwner())
                    owner.set(ownerInfo.toCommonUser());
            });
            Demand demand = Demand.builder()
                    .tenantId(calculation.getTenantId()).consumerCode(calculation.getApplicationNumber())
                    .consumerType("NDC_APPLICATION_FEE")
                    .businessService("NDC")
                    .payer(owner.get())
                    .taxPeriodFrom(System.currentTimeMillis()).taxPeriodTo(System.currentTimeMillis())
                    .demandDetails(Collections.singletonList(demandDetail))
                    .build();

            demands.add(demand);
        }

        StringBuilder url = new StringBuilder().append(ndcConfiguration.getBillingHost())
                .append(ndcConfiguration.getDemandCreateEndpoint());

        DemandRequest demandRequest = DemandRequest.builder().requestInfo(requestInfo).demands(demands).build();

        Object response = repository.fetchResult(url, demandRequest);

        DemandResponse demandResponse = mapper.convertValue(response, DemandResponse.class);
        return demandResponse.getDemands();
    }



    public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

        if (getBillCriteria.getAmountExpected() == null) getBillCriteria.setAmountExpected(BigDecimal.ZERO);

        DemandResponse res = mapper.convertValue(
                repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
                DemandResponse.class);
        if (CollectionUtils.isEmpty(res.getDemands())) {
            Map<String, String> map = new HashMap<>();
            map.put(EMPTY_DEMAND_ERROR_CODE, EMPTY_DEMAND_ERROR_MESSAGE);
        }

        return res;
    }


}
