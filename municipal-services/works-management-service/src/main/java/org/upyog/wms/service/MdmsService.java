package org.upyog.wms.service;

import static org.upyog.wms.constants.WmsConstants.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ErrorCodes;
import org.upyog.wms.repository.ServiceRequestRepository;
import org.upyog.wms.util.WmsUtil;
import org.upyog.wms.web.models.mdms.ActionPlanConfig;
import org.upyog.wms.web.models.mdms.DocumentTypeConfig;
import org.upyog.wms.web.models.mdms.MasterDetail;
import org.upyog.wms.web.models.mdms.MdmsCriteria;
import org.upyog.wms.web.models.mdms.MdmsCriteriaReq;
import org.upyog.wms.web.models.mdms.MdmsData;
import org.upyog.wms.web.models.mdms.ModuleDetail;

/**
 * Loads all WorksManagement masters in ONE MDMS call and keeps the parsed result per state tenant for a
 * short time (wms.mdms.cache.ttl-seconds). Master data changes rarely, so this removes an HTTP round trip
 * from almost every request.
 */
@Slf4j
@Service
public class MdmsService {

    private static final List<String> MASTERS = List.of(
            MASTER_FINANCIAL_YEAR, MASTER_PURPOSE, MASTER_TYPE_OF_WORK, MASTER_WORK_CATEGORY,
            MASTER_RECOMMENDED_BY, MASTER_SCHEME, MASTER_GOVERNMENT_GRANT, MASTER_OWN_SOURCE_REVENUE,
            MASTER_COMPONENT_CATEGORY, MASTER_DOCUMENT_TYPE, MASTER_APPROVING_DEPARTMENT, MASTER_STATE,
            MASTER_DISTRICT, MASTER_ACTION_PLAN_CONFIG);

    private record CacheEntry(MdmsData data, long expiresAt) {
        boolean isValid(long now) {
            return now < expiresAt;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ServiceRequestRepository serviceRequestRepository;
    private final WmsConfiguration config;

    public MdmsService(ServiceRequestRepository serviceRequestRepository, WmsConfiguration config) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    public MdmsData getMasterData(RequestInfo requestInfo, String tenantId) {
        String stateTenant = WmsUtil.stateLevelTenant(tenantId);
        long ttlMillis = config.getMdmsCacheTtlSeconds() * 1000L;
        long now = System.currentTimeMillis();
        if (ttlMillis > 0) {
            CacheEntry entry = cache.get(stateTenant);
            if (entry != null && entry.isValid(now)) {
                return entry.data();
            }
        }
        MdmsData data = parse(fetch(requestInfo, stateTenant));
        if (ttlMillis > 0) {
            cache.put(stateTenant, new CacheEntry(data, now + ttlMillis));
        }
        return data;
    }

    /** Lets operations refresh masters without a restart (e.g. from a future admin endpoint). */
    public void evict(String tenantId) {
        cache.remove(WmsUtil.stateLevelTenant(tenantId));
    }

    private JsonNode fetch(RequestInfo requestInfo, String stateTenant) {
        List<MasterDetail> masterDetails = MASTERS.stream()
                .map(name -> MasterDetail.builder().name(name).build())
                .toList();
        MdmsCriteriaReq request = MdmsCriteriaReq.builder()
                .requestInfo(requestInfo)
                .mdmsCriteria(MdmsCriteria.builder()
                        .tenantId(stateTenant)
                        .moduleDetails(List.of(ModuleDetail.builder()
                                .moduleName(MDMS_MODULE)
                                .masterDetails(masterDetails)
                                .build()))
                        .build())
                .build();
        JsonNode response = serviceRequestRepository.post(config.getMdmsHost() + config.getMdmsSearchPath(),
                request, JsonNode.class);
        JsonNode module = response == null ? null : response.path("MdmsRes").path(MDMS_MODULE);
        if (module == null || module.isMissingNode() || module.isEmpty()) {
            throw new CustomException(ErrorCodes.CONFIG_MISSING,
                    "MDMS module " + MDMS_MODULE + " is not configured for tenant " + stateTenant);
        }
        return module;
    }

    MdmsData parse(JsonNode module) {
        Map<String, Set<String>> schemes = new LinkedHashMap<>();
        active(module.path(MASTER_SCHEME)).forEach(s -> schemes.put(s.path("code").asText(),
                codes(s.path("subSchemes"))));

        Map<String, Set<String>> categories = new LinkedHashMap<>();
        active(module.path(MASTER_COMPONENT_CATEGORY)).forEach(c -> categories.put(c.path("code").asText(),
                codes(c.path("subCategories"))));

        Map<String, DocumentTypeConfig> documentTypes = new LinkedHashMap<>();
        active(module.path(MASTER_DOCUMENT_TYPE)).forEach(d -> documentTypes.put(d.path("code").asText(),
                new DocumentTypeConfig(d.path("code").asText(), d.path("owner").asText(DOC_OWNER_ACTION_PLAN),
                        d.path("required").asBoolean(false), d.path("supporting").asBoolean(false))));

        Map<String, String> districts = new LinkedHashMap<>();
        active(module.path(MASTER_DISTRICT)).forEach(d -> districts.put(d.path("code").asText(),
                d.path("stateCode").asText()));

        return new MdmsData(
                codes(module.path(MASTER_FINANCIAL_YEAR)),
                codes(module.path(MASTER_PURPOSE)),
                codes(module.path(MASTER_TYPE_OF_WORK)),
                codes(module.path(MASTER_WORK_CATEGORY)),
                codes(module.path(MASTER_RECOMMENDED_BY)),
                Map.copyOf(schemes),
                codes(module.path(MASTER_GOVERNMENT_GRANT)),
                codes(module.path(MASTER_OWN_SOURCE_REVENUE)),
                Map.copyOf(categories),
                Map.copyOf(documentTypes),
                codes(module.path(MASTER_APPROVING_DEPARTMENT)),
                codes(module.path(MASTER_STATE)),
                Map.copyOf(districts),
                parseConfig(module.path(MASTER_ACTION_PLAN_CONFIG)));
    }

    private ActionPlanConfig parseConfig(JsonNode node) {
        ActionPlanConfig d = ActionPlanConfig.defaults();
        if (!node.isArray() || node.isEmpty()) {
            log.warn("MDMS ActionPlanConfig missing, FRS defaults are used");
            return d;
        }
        JsonNode c = node.get(0);
        Set<String> senior = c.has("seniorApprovalFundSources")
                ? texts(c.get("seniorApprovalFundSources"))
                : d.seniorApprovalFundSources();
        return new ActionPlanConfig(
                c.path("verificationRequired").asBoolean(d.verificationRequired()),
                senior,
                c.path("makerCheckerSeparation").asBoolean(d.makerCheckerSeparation()),
                c.path("fullAllocationRequired").asBoolean(d.fullAllocationRequired()),
                c.path("enforceComponentCount").asBoolean(d.enforceComponentCount()),
                c.path("technicalSanctionRequired").asBoolean(d.technicalSanctionRequired()),
                c.path("maxSupportingDocuments").asInt(d.maxSupportingDocuments()),
                c.hasNonNull("workflowEnabled") ? c.get("workflowEnabled").asBoolean() : null);
    }

    /** Master entries that are not switched off (active missing = active). */
    private static List<JsonNode> active(JsonNode array) {
        if (array == null || !array.isArray()) {
            return List.of();
        }
        return StreamSupport.stream(array.spliterator(), false)
                .filter(n -> n.path("active").asBoolean(true))
                .toList();
    }

    private static Set<String> texts(JsonNode array) {
        if (array == null || !array.isArray()) {
            return Set.of();
        }
        Set<String> values = new HashSet<>();
        array.forEach(n -> values.add(n.asText()));
        return Set.copyOf(values);
    }

    private static Set<String> codes(JsonNode array) {
        Set<String> codes = new HashSet<>();
        active(array).forEach(n -> codes.add(n.path("code").asText()));
        return Set.copyOf(codes);
    }
}
