package org.upyog.dashboard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.upyog.dashboard.model.IngestionModuleDetail;
import org.upyog.dashboard.service.TenantSyncService;

/**
 * Unit tests for {@link TenantController} verifying tenant synchronization and search endpoints.
 */
@ExtendWith(MockitoExtension.class)
class TenantControllerTest {

    @Mock
    private TenantSyncService tenantSyncService;

    @InjectMocks
    private TenantController controller;

    @Test
    @DisplayName("syncTenants endpoint returns 200 OK and synced details")
    void syncTenants_returnsOk() {
        IngestionModuleDetail detail = IngestionModuleDetail.builder()
                .tenantId("pg.citya")
                .moduleName("PT")
                .active(true)
                .build();

        when(tenantSyncService.syncTenantsFromMdms("pg")).thenReturn(List.of(detail));

        ResponseEntity<Map<String, Object>> response = controller.syncTenants("pg");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("SUCCESS");
        assertThat(response.getBody().get("totalRecordsSynced")).isEqualTo(1);
    }

    @Test
    @DisplayName("syncTenants endpoint returns 400 Bad Request when sync rejected because data already exists")
    void syncTenants_whenAlreadySynced_returnsBadRequest() {
        when(tenantSyncService.syncTenantsFromMdms("pg")).thenThrow(
                new IllegalStateException("This API is allowed to be used only once as tenant data is already present in the table. If you want, you can insert the data directly into the table or you can first delete the data of this table manually and then retry to hit the API."));

        ResponseEntity<Map<String, Object>> response = controller.syncTenants("pg");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("FAILURE");
        assertThat(response.getBody().get("message")).toString().contains("allowed to be used only once");
    }

    @Test
    @DisplayName("searchTenants endpoint returns 200 OK with active tenants list")
    void searchTenants_returnsOk() {
        when(tenantSyncService.getActiveTenants(null)).thenReturn(List.of("pg.citya"));
        when(tenantSyncService.getActiveModuleDetails()).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> response = controller.searchTenants();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalActiveTenants")).isEqualTo(1);
    }

    @Test
    @DisplayName("IngestionModuleDetail serializes only active, detailId, tenantId, moduleName")
    void ingestionModuleDetail_jsonSerializationExcludesAuditAndDroppedFields() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        IngestionModuleDetail detail = IngestionModuleDetail.builder()
                .detailId("9c3704dd-1fc5-3b4b-a2f3-ab9d89ea8bec")
                .tenantId("ap.addanki")
                .moduleName("CHB")
                .active(true)
                .createdBy("SYSTEM")
                .createdTime(123456789L)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(123456789L)
                .build();

        String json = mapper.writeValueAsString(detail);

        assertThat(json).contains("\"detailId\":\"9c3704dd-1fc5-3b4b-a2f3-ab9d89ea8bec\"");
        assertThat(json).contains("\"tenantId\":\"ap.addanki\"");
        assertThat(json).contains("\"moduleName\":\"CHB\"");
        assertThat(json).contains("\"active\":true");

        assertThat(json).doesNotContain("ulbName");
        assertThat(json).doesNotContain("lastIngestedDate");
        assertThat(json).doesNotContain("scheduleCron");
        assertThat(json).doesNotContain("createdBy");
        assertThat(json).doesNotContain("createdTime");
        assertThat(json).doesNotContain("lastModifiedBy");
        assertThat(json).doesNotContain("lastModifiedTime");
        assertThat(json).doesNotContain("legacyDataIngested");
        assertThat(json).doesNotContain("isLegacyDataIngested");
    }
}
