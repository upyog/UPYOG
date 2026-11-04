package org.upyog.dashboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dashboard.model.IngestionModuleDetail;
import org.upyog.dashboard.service.TenantSyncService;

/**
 * REST Controller exposing endpoints for MDMS tenant synchronization and
 * search.
 */
@Slf4j
@RestController
@RequestMapping({"/extractor/v1/tenants"})
@RequiredArgsConstructor
public class TenantController {

    private final TenantSyncService tenantSyncService;

    /**
     * Synchronizes tenant metadata from MDMS and persists them to
     * {@code ug_ingestion_module_detail}.
     *
     * @param stateTenantId optional state tenant identifier (e.g. {@code "pg"})
     * @return response entity with synced record count and details
     */
    @PostMapping("/_sync")
    public ResponseEntity<Map<String, Object>> syncTenants(
            @RequestParam(required = false) String stateTenantId) {
        log.info("Received request to sync tenants from MDMS for stateTenantId: {}", stateTenantId);
        try {
            List<IngestionModuleDetail> syncedDetails = tenantSyncService.syncTenantsFromMdms(stateTenantId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Successfully synced " + syncedDetails.size() + " module details from MDMS");
            response.put("totalRecordsSynced", syncedDetails.size());
            response.put("details", syncedDetails);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            log.warn("Tenant sync rejected: {}", exception.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "FAILURE");
            errorResponse.put("message", exception.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves currently active module details and tenant IDs from the
     * database/cache.
     *
     * @return response entity containing active tenants and module details
     */
    @GetMapping("/_search")
    public ResponseEntity<Map<String, Object>> searchTenants() {
        List<IngestionModuleDetail> activeDetails = tenantSyncService.getActiveModuleDetails();
        List<String> activeTenants = tenantSyncService.getActiveTenants(null);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("totalActiveTenants", activeTenants.size());
        response.put("activeTenants", activeTenants);
        response.put("activeModuleDetails", activeDetails);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
