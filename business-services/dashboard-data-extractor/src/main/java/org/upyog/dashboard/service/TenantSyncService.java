package org.upyog.dashboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.mdms.client.MdmsClient;
import org.upyog.dashboard.model.IngestionModuleDetail;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import org.upyog.dashboard.util.CommonUtils;

/**
 * Service responsible for synchronizing tenant lists from MDMS into the local
 * {@code ug_ingestion_module_detail} table and providing cached tenant lookups.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSyncService {

    private final MdmsClient mdmsClient;
    private final IngestionSummaryRepository summaryRepository;
    private final SchemaMappingConfig schemaMappingConfig;
    private final DashboardProperties dashboardProperties;

    /**
     * Synchronizes city tenants from MDMS for the given state and upserts
     * records into {@code ug_ingestion_module_detail} for all enabled modules.
     *
     * @param stateTenantId state identifier (e.g. {@code "pg"})
     * @return list of synced {@link IngestionModuleDetail} records
     */
    @CacheEvict(value = {DashboardExtractorConstants.CACHE_ACTIVE_TENANTS, DashboardExtractorConstants.CACHE_TENANT_MODULE_DETAILS}, allEntries = true)
    public List<IngestionModuleDetail> syncTenantsFromMdms(String stateTenantId) {
        if (summaryRepository.hasAnyModuleDetails()) {
            log.warn("MDMS tenant sync rejected: ug_ingestion_module_detail table already contains data.");
            throw new IllegalStateException("This API is allowed to be used only once as tenant data is already present in the table. If you want, you can insert the data directly in the table or you can first delete the data of this table manually and then retry to hit the API.");
        }

        String effectiveState = StringUtils.isNotBlank(stateTenantId) ? stateTenantId
                : dashboardProperties.getTenantId();
        if (StringUtils.isBlank(effectiveState)) {
            log.error("State tenant ID is not provided or configured. MDMS sync aborted.");
            throw new IllegalArgumentException("State tenant ID must not be blank for MDMS tenant synchronization");
        }
        log.info("Starting tenant sync from MDMS for state: {}", effectiveState);

        List<String> cityTenants = mdmsClient.fetchCityTenants(effectiveState);
        if (cityTenants.isEmpty()) {
            log.warn("No city tenants returned from MDMS for state {}. Sync aborted.", effectiveState);
            return Collections.emptyList();
        }

        List<Module> enabledModules = schemaMappingConfig.getEnabledModules();
        if (enabledModules.isEmpty()) {
            log.warn("No modules enabled in SchemaMappingConfig. Defaulting to all known modules.");
            enabledModules = List.of(Module.values());
        }

        List<IngestionModuleDetail> detailsToUpsert = new ArrayList<>();
        long now = CommonUtils.getCurrentEpochMillis();

        for (String tenantCode : cityTenants) {
            for (Module module : enabledModules) {
                String deterministicId = UUID.nameUUIDFromBytes((tenantCode + ":" + module.name()).getBytes()).toString();
                IngestionModuleDetail detail = IngestionModuleDetail.builder()
                        .detailId(deterministicId)
                        .tenantId(tenantCode)
                        .moduleName(module.name())
                        .active(true)
                        .createdBy(DashboardExtractorConstants.MDMS_SYNC_USER)
                        .createdTime(now)
                        .lastModifiedBy(DashboardExtractorConstants.MDMS_SYNC_USER)
                        .lastModifiedTime(now)
                        .build();
                detailsToUpsert.add(detail);
            }
        }

        if (detailsToUpsert.isEmpty()) {
            log.warn("No module details built to upsert for state {}. Sync aborted without modifying database.", effectiveState);
            return Collections.emptyList();
        }

        summaryRepository.replaceAllModuleDetails(detailsToUpsert);
        log.info("Successfully synced and replaced {} module detail records for {} ULBs",
                detailsToUpsert.size(), cityTenants.size());

        return detailsToUpsert;
    }

    /**
     * Retrieves active tenant IDs for a given module, utilizing in-memory
     * cache. If the database contains no active records, falls back to the
     * configured default tenant.
     *
     * @param module the target module
     * @return list of active tenant IDs
     */
    @Cacheable(value = DashboardExtractorConstants.CACHE_ACTIVE_TENANTS, key = "#module != null ? #module.name() : 'ALL'")
    public List<String> getActiveTenants(Module module) {
        return (module != null)
                ? summaryRepository.findActiveTenantsByModule(module.name())
                : summaryRepository.findAllActiveTenants();
    }

    /**
     * Retrieves all active module detail records, utilizing in-memory cache.
     *
     * @return list of active {@link IngestionModuleDetail} entities
     */
    @Cacheable(value = DashboardExtractorConstants.CACHE_TENANT_MODULE_DETAILS)
    public List<IngestionModuleDetail> getActiveModuleDetails() {
        return summaryRepository.findAllActiveModuleDetails();
    }

    /**
     * Resolves the detail ID for a given tenant ID and module name from the cached
     * module details or computes a deterministic UUID fallback.
     *
     * @param tenantId   the tenant identifier (e.g. "pg.citya")
     * @param moduleName the module name (e.g. "PT")
     * @return module detail ID string
     */
    public String getModuleDetailId(String tenantId, String moduleName) {
        if (StringUtils.isAnyBlank(tenantId, moduleName)) {
            return null;
        }
        List<IngestionModuleDetail> details = getActiveModuleDetails();
        if (details != null && !details.isEmpty()) {
            for (IngestionModuleDetail detail : details) {
                if (tenantId.equalsIgnoreCase(detail.getTenantId()) && moduleName.equalsIgnoreCase(detail.getModuleName())) {
                    return detail.getDetailId();
                }
            }
        }
        throw new IllegalStateException("No active module configuration found in ug_ingestion_module_detail table for tenant: " 
                + tenantId + " and module: " + moduleName + ". Please run tenant sync API (POST /extractor/v1/tenants/_sync) first.");
    }
}

