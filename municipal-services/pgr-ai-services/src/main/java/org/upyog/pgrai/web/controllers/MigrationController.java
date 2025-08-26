package org.upyog.pgrai.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.upyog.pgrai.service.MigrationService;
import org.upyog.pgrai.web.models.pgrV1.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
public class MigrationController {

    @Autowired
    private MigrationService migrationService;

    /**
     * Endpoint to transform and migrate service requests.
     *
     * @param request The service response object containing data to be migrated.
     * @return A ResponseEntity containing the migration result as a map.
     * @throws IOException If an error occurs during the migration process.
     */
    @RequestMapping(value = "/_transform", method = RequestMethod.POST)
    public ResponseEntity<Map> requestsCreatePost(@Valid @RequestBody ServiceResponse request) throws IOException {

        Map<String, Object> response = migrationService.migrate(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}