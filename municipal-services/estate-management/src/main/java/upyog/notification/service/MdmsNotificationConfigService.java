package upyog.notification.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.NotificationConfig;
import upyog.repository.ServiceRequestRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

/**
 * Loads notification rules from MDMS. Cached per tenant+module for high-throughput consumption.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MdmsNotificationConfigService {

    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {};

    private final NotificationProperties properties;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, CachedConfigs> cache = new ConcurrentHashMap<>();

    public List<NotificationConfig> getConfigs(RequestInfo requestInfo, String tenantId, String module) {
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(module)) {
            return Collections.emptyList();
        }

        String stateTenant = tenantId.contains(".") ? tenantId.split("\\.")[0] : tenantId;
        String cacheKey = stateTenant + ":" + module;
        CachedConfigs cached = cache.get(cacheKey);
        long now = System.currentTimeMillis();

        if (cached != null && now - cached.loadedAtMs() < properties.getConfigCacheTtlMs()) {
            return cached.configs();
        }

        List<NotificationConfig> configs = loadFromMdms(requestInfo, stateTenant, module);
        cache.put(cacheKey, new CachedConfigs(configs, now));
        return configs;
    }

    @SuppressWarnings("unchecked")
    private List<NotificationConfig> loadFromMdms(RequestInfo requestInfo, String tenantId, String module) {
        StringBuilder uri = new StringBuilder()
                .append(properties.getMdmsHost())
                .append(properties.getMdmsEndPoint());

        MdmsCriteriaReq criteriaReq = buildRequest(requestInfo, tenantId, module);
        Filter moduleFilter = filter(where("module").is(module));

        try {
            Object response = serviceRequestRepository.fetchResult(uri, criteriaReq);
            String jsonPath = "$.MdmsRes." + properties.getMdmsConfigModule()
                    + "." + properties.getMdmsConfigMaster() + "[?]";
            List<Map<String, Object>> rows = JsonPath.parse(response).read(jsonPath, moduleFilter);
            if (CollectionUtils.isEmpty(rows)) {
                log.warn("No MDMS notification configs for module={}, tenant={}", module, tenantId);
                return Collections.emptyList();
            }
            return rows.stream().map(this::toConfig).toList();
        } catch (Exception e) {
            log.error("Failed to load MDMS notification configs for module={}: {}", module, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private NotificationConfig toConfig(Map<String, Object> row) {
        return NotificationConfig.builder()
                .module(stringValue(row.get("module")))
                .action(stringValue(row.get("action")))
                .triggerTopic(stringValue(row.get("triggerTopic")))
                .triggerField(stringValue(row.get("triggerField")))
                .triggerValue(stringValue(row.get("triggerValue")))
                .channelNames(readStringList(row.get("channelNames")))
                .messages(readStringMap(row.get("messages")))
                .localizationModule(stringValue(row.get("localizationModule")))
                .tenantIdPath(stringValue(row.get("tenantIdPath")))
                .variables(readStringMap(row.get("variables")))
                .recipientMobilePath(stringValue(row.get("recipientMobilePath")))
                .recipientUuidPath(stringValue(row.get("recipientUuidPath")))
                .recipientEmailPath(stringValue(row.get("recipientEmailPath")))
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> readStringList(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of(String.valueOf(value));
    }

    private Map<String, String> readStringMap(Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        return objectMapper.convertValue(value, MAP_TYPE);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private MdmsCriteriaReq buildRequest(RequestInfo requestInfo, String tenantId, String module) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(properties.getMdmsConfigMaster());
        masterDetail.setFilter("[?(@['module'] == '" + module + "')]");

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setModuleName(properties.getMdmsConfigModule());
        moduleDetail.setMasterDetails(List.of(masterDetail));

        MdmsCriteria criteria = new MdmsCriteria();
        criteria.setTenantId(tenantId);
        criteria.setModuleDetails(List.of(moduleDetail));

        MdmsCriteriaReq req = new MdmsCriteriaReq();
        req.setRequestInfo(requestInfo);
        req.setMdmsCriteria(criteria);
        return req;
    }

    private record CachedConfigs(List<NotificationConfig> configs, long loadedAtMs) {
    }
}
