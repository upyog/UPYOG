package org.upyog.pgrai.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.pgrai.service.MigrationService;
import org.upyog.pgrai.web.models.pgrv1.ServiceResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * Controller for handling migration-related operations.
 * This controller is enabled only when the `migration.enabled` property is set to true.
 */
@ConditionalOnProperty(
        value = "migration.enabled",
        havingValue = "true",
        matchIfMissing = false)
@RestController
@RequestMapping("/migration")
@Slf4j
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;

    /**
     * Endpoint to transform and migrate service requests.
     *
     * @param request The service response object containing data to be migrated.
     * @return A ResponseEntity containing the migration result as a map.
     * @throws IOException If an error occurs during the migration process.
     */
    @PostMapping("/_transform")
    public ResponseEntity<Map<String, Object>> requestsCreatePost(@Valid @RequestBody ServiceResponse request) throws IOException {

        Map<String, Object> response = migrationService.migrate(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}