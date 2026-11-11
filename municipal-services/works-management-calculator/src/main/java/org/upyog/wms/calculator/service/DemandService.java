package org.upyog.wms.calculator.service;

import static org.upyog.wms.calculator.constants.CalculatorConstants.CONFIG_MISSING;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.calculator.config.CalculatorConfiguration;
import org.upyog.wms.calculator.repository.ServiceRequestRepository;
import org.upyog.wms.calculator.web.models.ActionPlan;
import org.upyog.wms.calculator.web.models.RequestInfoWrapper;
import org.upyog.wms.calculator.web.models.TaxHeadEstimate;
import org.upyog.wms.calculator.web.models.demand.Demand;
import org.upyog.wms.calculator.web.models.demand.DemandDetail;
import org.upyog.wms.calculator.web.models.demand.DemandRequest;
import org.upyog.wms.calculator.web.models.demand.DemandResponse;
import org.upyog.wms.calculator.web.models.mdms.FinancialYearPeriod;

/**
 * billing-service integration. A changed fee is applied as an adjustment row (difference per tax head)
 * instead of editing existing rows, so amounts already collected are never touched.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DemandService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CalculatorConfiguration config;

    public Optional<Demand> searchActive(RequestInfo requestInfo, String tenantId, String consumerCode) {
        String url = config.getBillingHost() + config.getDemandSearchPath()
                + "?tenantId=" + encode(tenantId)
                + "&consumerCode=" + encode(consumerCode)
                + "&businessService=" + encode(config.getBusinessService());
        DemandResponse response = serviceRequestRepository.post(url,
                RequestInfoWrapper.builder().requestInfo(requestInfo).build(), DemandResponse.class);
        return Optional.ofNullable(response).map(DemandResponse::getDemands).orElse(List.of()).stream()
                .filter(d -> !"CANCELLED".equalsIgnoreCase(d.getStatus()))
                .findFirst();
    }

    public Demand create(RequestInfo requestInfo, ActionPlan plan, List<TaxHeadEstimate> fees,
                         FinancialYearPeriod period) {
        if (period == null || period.startingDate() == null || period.endingDate() == null) {
            throw new CustomException(CONFIG_MISSING, "FinancialYear " + plan.getFinancialYear()
                    + " has no startingDate / endingDate in MDMS; it is needed as the demand tax period");
        }
        User payer = new User();
        payer.setUuid(plan.getAuditDetails() == null ? null : plan.getAuditDetails().getCreatedBy());
        Demand demand = Demand.builder()
                .tenantId(plan.getTenantId())
                .consumerCode(plan.getApplicationNumber())
                .consumerType(config.getConsumerType())
                .businessService(config.getBusinessService())
                .payer(payer)
                .taxPeriodFrom(period.startingDate())
                .taxPeriodTo(period.endingDate())
                .minimumAmountPayable(BigDecimal.ZERO)
                .demandDetails(fees.stream().map(f -> detail(plan.getTenantId(), f.taxHeadCode(), f.estimateAmount()))
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
        return call(config.getDemandCreatePath(), requestInfo, demand);
    }

    /** Brings the demand in line with the new fees. Returns empty when nothing changed. */
    public Optional<Demand> update(RequestInfo requestInfo, Demand demand, List<TaxHeadEstimate> fees) {
        Map<String, BigDecimal> current = new LinkedHashMap<>();
        Optional.ofNullable(demand.getDemandDetails()).orElse(List.of()).forEach(d -> current.merge(
                d.getTaxHeadMasterCode(), d.getTaxAmount() == null ? BigDecimal.ZERO : d.getTaxAmount(),
                BigDecimal::add));
        Map<String, BigDecimal> target = fees.stream().collect(Collectors.toMap(TaxHeadEstimate::taxHeadCode,
                TaxHeadEstimate::estimateAmount, BigDecimal::add, LinkedHashMap::new));

        Set<String> heads = new HashSet<>(current.keySet());
        heads.addAll(target.keySet());
        List<DemandDetail> adjustments = heads.stream().sorted()
                .map(head -> Map.entry(head, target.getOrDefault(head, BigDecimal.ZERO)
                        .subtract(current.getOrDefault(head, BigDecimal.ZERO))))
                .filter(e -> e.getValue().signum() != 0)
                .map(e -> detail(demand.getTenantId(), e.getKey(), e.getValue()))
                .toList();
        if (adjustments.isEmpty()) {
            return Optional.empty();
        }
        List<DemandDetail> details = new ArrayList<>(Optional.ofNullable(demand.getDemandDetails()).orElse(List.of()));
        details.addAll(adjustments);
        demand.setDemandDetails(details);
        return Optional.of(call(config.getDemandUpdatePath(), requestInfo, demand));
    }

    private Demand call(String path, RequestInfo requestInfo, Demand demand) {
        DemandResponse response = serviceRequestRepository.post(config.getBillingHost() + path,
                DemandRequest.builder().requestInfo(requestInfo).demands(List.of(demand)).build(),
                DemandResponse.class);
        return Optional.ofNullable(response).map(DemandResponse::getDemands).filter(l -> !l.isEmpty())
                .map(l -> l.get(0)).orElse(demand);
    }

    private static DemandDetail detail(String tenantId, String taxHead, BigDecimal amount) {
        return DemandDetail.builder().tenantId(tenantId).taxHeadMasterCode(taxHead).taxAmount(amount)
                .collectionAmount(BigDecimal.ZERO).build();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
