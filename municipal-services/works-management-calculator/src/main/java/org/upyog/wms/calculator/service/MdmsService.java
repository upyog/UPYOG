package org.upyog.wms.calculator.service;

import static org.upyog.wms.calculator.constants.CalculatorConstants.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.wms.calculator.config.CalculatorConfiguration;
import org.upyog.wms.calculator.repository.ServiceRequestRepository;
import org.upyog.wms.calculator.web.models.mdms.CalculatorMdmsData;
import org.upyog.wms.calculator.web.models.mdms.FeeConfig;
import org.upyog.wms.calculator.web.models.mdms.FinancialYearPeriod;
import org.upyog.wms.calculator.web.models.mdms.MasterDetail;
import org.upyog.wms.calculator.web.models.mdms.MdmsCriteria;
import org.upyog.wms.calculator.web.models.mdms.MdmsCriteriaReq;
import org.upyog.wms.calculator.web.models.mdms.ModuleDetail;

/** FeeConfig + FinancialYear from MDMS, cached per state tenant. */
@Service
@RequiredArgsConstructor
public class MdmsService {

    private record CacheEntry(CalculatorMdmsData data, long expiresAt) {
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ServiceRequestRepository serviceRequestRepository;
    private final CalculatorConfiguration config;

    public CalculatorMdmsData getMasterData(RequestInfo requestInfo, String tenantId) {
        String stateTenant = tenantId.contains(".") ? tenantId.substring(0, tenantId.indexOf('.')) : tenantId;
        long now = System.currentTimeMillis();
        long ttl = config.getMdmsCacheTtlSeconds() * 1000L;
        CacheEntry entry = cache.get(stateTenant);
        if (ttl > 0 && entry != null && now < entry.expiresAt()) {
            return entry.data();
        }
        CalculatorMdmsData data = parse(fetch(requestInfo, stateTenant));
        if (ttl > 0) {
            cache.put(stateTenant, new CacheEntry(data, now + ttl));
        }
        return data;
    }

    private JsonNode fetch(RequestInfo requestInfo, String stateTenant) {
        MdmsCriteriaReq request = MdmsCriteriaReq.builder()
                .requestInfo(requestInfo)
                .mdmsCriteria(MdmsCriteria.builder().tenantId(stateTenant)
                        .moduleDetails(List.of(ModuleDetail.builder().moduleName(MDMS_MODULE)
                                .masterDetails(List.of(MasterDetail.builder().name(MASTER_FEE_CONFIG).build(),
                                        MasterDetail.builder().name(MASTER_FINANCIAL_YEAR).build()))
                                .build()))
                        .build())
                .build();
        JsonNode response = serviceRequestRepository.post(config.getMdmsHost() + config.getMdmsSearchPath(), request,
                JsonNode.class);
        return response == null ? null : response.path("MdmsRes").path(MDMS_MODULE);
    }

    static CalculatorMdmsData parse(JsonNode module) {
        List<FeeConfig> fees = new ArrayList<>();
        Map<String, FinancialYearPeriod> years = new LinkedHashMap<>();
        if (module != null) {
            module.path(MASTER_FEE_CONFIG).forEach(f -> {
                List<String> sources = new ArrayList<>();
                f.path("applicableFundSources").forEach(s -> sources.add(s.asText()));
                fees.add(new FeeConfig(f.path("code").asText(), f.path("businessService").asText(),
                        f.path("taxHeadCode").asText(), f.path("calculationType").asText(),
                        decimal(f.get("value")), decimal(f.get("minAmount")), decimal(f.get("maxAmount")),
                        List.copyOf(sources), f.path("active").asBoolean(false)));
            });
            module.path(MASTER_FINANCIAL_YEAR).forEach(y -> years.put(y.path("code").asText(),
                    new FinancialYearPeriod(y.path("code").asText(),
                            y.hasNonNull("startingDate") ? y.get("startingDate").asLong() : null,
                            y.hasNonNull("endingDate") ? y.get("endingDate").asLong() : null)));
        }
        return new CalculatorMdmsData(List.copyOf(fees), Map.copyOf(years));
    }

    private static BigDecimal decimal(JsonNode node) {
        return node == null || node.isNull() ? null : new BigDecimal(node.asText());
    }
}
