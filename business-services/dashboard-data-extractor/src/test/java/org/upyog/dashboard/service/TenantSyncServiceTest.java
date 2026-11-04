
package org.upyog.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.mdms.client.MdmsClient;
import org.upyog.dashboard.model.IngestionModuleDetail;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

/**
 * Unit tests for {@link TenantSyncService} validating MDMS sync logic, caching, and fallback handling.
 */
@ExtendWith(MockitoExtension.class)
class TenantSyncServiceTest {

    @Mock
    private MdmsClient mdmsClient;

    @Mock
    private IngestionSummaryRepository summaryRepository;

    @Mock
    private SchemaMappingConfig schemaMappingConfig;

    @Mock
    private DashboardProperties dashboardProperties;

    @InjectMocks
    private TenantSyncService tenantSyncService;

    @BeforeEach
    void setUp() {
        lenient().when(dashboardProperties.getTenantId()).thenReturn("pg");
    }

    @Test
    @DisplayName("syncTenantsFromMdms fetches city tenants, builds module details and calls upsert")
    void syncTenantsFromMdms_successfulSync() {
        when(summaryRepository.hasAnyModuleDetails()).thenReturn(false);
        when(mdmsClient.fetchCityTenants("pg")).thenReturn(List.of("pg.citya"));
        when(schemaMappingConfig.getEnabledModules()).thenReturn(List.of(Module.PGR, Module.PT));

        List<IngestionModuleDetail> synced = tenantSyncService.syncTenantsFromMdms("pg");

        assertThat(synced).hasSize(2);
        assertThat(synced.get(0).getTenantId()).isEqualTo("pg.citya");
        assertThat(synced.get(0).isActive()).isTrue();

        verify(summaryRepository).replaceAllModuleDetails(synced);
    }

    @Test
    @DisplayName("syncTenantsFromMdms throws IllegalStateException if module details already exist in DB")
    void syncTenantsFromMdms_throwsWhenDataAlreadyExists() {
        when(summaryRepository.hasAnyModuleDetails()).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () ->
                tenantSyncService.syncTenantsFromMdms("pg"));
    }

    @Test
    @DisplayName("getActiveTenants returns cached DB tenants or falls back to properties")
    void getActiveTenants_returnsFromDbOrFallback() {
        when(summaryRepository.findActiveTenantsByModule("PT")).thenReturn(List.of("pg.citya", "pg.cityb"));

        List<String> tenants = tenantSyncService.getActiveTenants(Module.PT);

        assertThat(tenants).containsExactly("pg.citya", "pg.cityb");
    }

    @Test
    @DisplayName("getActiveTenants returns empty list if DB is empty")
    void getActiveTenants_returnsEmptyWhenDbIsEmpty() {
        when(summaryRepository.findActiveTenantsByModule("PT")).thenReturn(List.of());

        List<String> tenants = tenantSyncService.getActiveTenants(Module.PT);

        assertThat(tenants).isEmpty();
    }
}
