package org.upyog.dashboard.extractor.impl;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.chb.constants.CHBDatabaseConstants;
import org.upyog.dashboard.chb.dto.CHBAggregatedData;
import org.upyog.dashboard.chb.dto.CHBDTO;
import org.upyog.dashboard.chb.mapper.CHBRowMapper;
import org.upyog.dashboard.client.UserFeignClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.model.UserSearchResponse;
import org.upyog.dashboard.service.OAuthTokenService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChbModuleExtractor implements ModuleExtractor<CHBDTO> {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private SchemaMappingConfig schemaMappingConfig;

    @Autowired
    private DashboardProperties dashboardProperties;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OAuthTokenService oAuthTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private String ulb;
    private String ward;
    private String region;
    private String state;
    private String dbTenantId;
    private int dbMaxAttempts;
    private long dbBaseDelayMs;
    private long dbMaxDelayMs;

    @PostConstruct
    public void init() {
        this.ulb = dashboardProperties.getMetricUlb();
        this.ward = dashboardProperties.getMetricWard();
        this.region = dashboardProperties.getMetricRegion();
        this.state = dashboardProperties.getMetricState();
        this.dbTenantId = dashboardProperties.getMetricUlb();
        this.dbMaxAttempts = dashboardProperties.getDbMaxAttempts();
        this.dbBaseDelayMs = dashboardProperties.getDbBaseDelayMs();
        this.dbMaxDelayMs = dashboardProperties.getDbMaxDelayMs();
    }

    @Override
    public Module getModule() {
        return Module.CHB;
    }

    @Override
    public CHBDTO extractData(LocalDate targetDate) {
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", dbTenantId);

        SchemaMappingConfig.ModuleQueries chbQueries = schemaMappingConfig.getQueriesForModule(Module.CHB);
        if (chbQueries == null) {
            throw new IllegalStateException("No query mapping found in chb-schema-mapping.yml for module CHB");
        }

        CHBAggregatedData combinedResult = executeQueryWithRetry(chbQueries.getCombinedMetricsQuery(), params);

        String bookingTypeJson = buildBookingTypeJson(combinedResult.getCreatedByListJson());
        combinedResult.setBookingTypeJson(bookingTypeJson);

        return CHBDTO.builder()
                .date(dateStr)
                .module(getModule().name())
                .ward(ward)
                .ulb(ulb)
                .region(region)
                .state(state)
                .combinedMetrics(combinedResult)
                .build();
    }

    private String buildBookingTypeJson(String createdByListJson) {
        if (createdByListJson == null || createdByListJson.isBlank() || "[]".equals(createdByListJson)) {
            return "[]";
        }

        List<String> uuids;
        try {
            List<Map<String, Object>> rows = objectMapper.readValue(createdByListJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            LinkedHashSet<String> uuidSet = new LinkedHashSet<>();
            for (Map<String, Object> row : rows) {
                Object cb = row.get("createdby");
                if (cb != null) uuidSet.add(cb.toString());
            }
            uuids = List.copyOf(uuidSet);
        } catch (Exception e) {
            log.warn("ChbModuleExtractor | Failed to parse createdByListJson: {}", e.getMessage());
            return "[]";
        }

        if (uuids.isEmpty()) return "[]";

        int online = 0, offline = 0;
        try {
            String token = oAuthTokenService.getToken();
            Map<String, Object> requestBody = Map.of(
                    "RequestInfo", Map.of("apiId", "Rainmaker", "authToken", token),
                    "tenantId", dashboardProperties.getTenantId(),
                    "uuid", uuids);

            String url = dashboardProperties.getOauthHost() + dashboardProperties.getUserSearchPath();
            UserSearchResponse response = userFeignClient.searchUser(URI.create(url), requestBody);

            if (response != null && response.getUser() != null) {
                for (UserInfo user : response.getUser()) {
                    if ("CITIZEN".equalsIgnoreCase(user.getType())) {
                        online++;
                    } else {
                        offline++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("ChbModuleExtractor | User search failed, bookingType will be empty: {}", e.getMessage());
            return "[]";
        }

        return "[{\"name\":\"Online\",\"value\":" + online + "},{\"name\":\"Offline\",\"value\":" + offline + "}]";
    }

    private CHBAggregatedData executeQueryWithRetry(String query, Map<String, Object> params) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return namedParameterJdbcTemplate.queryForObject(query, params, CHBRowMapper.COMBINED_ROW_MAPPER);
            } catch (Exception e) {
                if (attempt >= dbMaxAttempts) {
                    log.error("ChbModuleExtractor | DB query failed after {} attempts.", attempt, e);
                    throw e;
                }
                long backoff = calculateBackoff(attempt);
                log.warn("ChbModuleExtractor | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        attempt, dbMaxAttempts, backoff, e.getMessage());
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("DB query retry interrupted", ie);
                }
            }
        }
    }

    private long calculateBackoff(int attempt) {
        int power = Math.min(attempt - 1, 30);
        long expDelay = dbBaseDelayMs * (1L << power);
        if (expDelay < 0) expDelay = dbMaxDelayMs;
        long currentMax = Math.min(dbMaxDelayMs, expDelay);
        return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, currentMax + 1);
    }
}