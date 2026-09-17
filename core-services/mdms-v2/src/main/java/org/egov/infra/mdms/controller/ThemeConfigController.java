package org.egov.infra.mdms.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.egov.infra.mdms.model.ThemeConfig;
import org.egov.infra.mdms.model.ThemeConfigWorkflowRequest;
import org.egov.infra.mdms.service.ThemeConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



/**
 * Controller for managing Theme Configuration.
 *
 * Supports:
 *
 * 1. Creating theme configuration
 * 2. Updating theme configuration through workflow approval
 *
 * Persistence is handled asynchronously through Persister.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/theme-config")
public class ThemeConfigController {


    private final ThemeConfigService themeConfigService;


    /**
     * Creates theme configuration.
     *
     * @param themeConfig theme configuration request
     * @return created theme configuration
     */
    @RequestMapping(
            value = "/_create",
            method = RequestMethod.POST
    )
    public ResponseEntity<?> create(
            @Valid @RequestBody ThemeConfig themeConfig) {

        ThemeConfig response =
                themeConfigService.create(themeConfig);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


    /**
     * Creates updated theme configuration.
     *
     * A new row is created in the same ug_theme_config table
     * with pending workflow status.
     *
     * @param themeConfig updated theme configuration
     * @return pending theme configuration
     */
    @RequestMapping(
            value = "/_update",
            method = RequestMethod.POST
    )
    public ResponseEntity<?> update(
            @Valid @RequestBody ThemeConfigWorkflowRequest request) {

        ThemeConfig response =
                themeConfigService.update(
                        request.getThemeConfig(),
                        request.getRequestInfo()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


  

    /**
     * Fetches active theme configuration.
     *
     * Priority:
     * APPROVED theme first.
     * DEFAULT theme as fallback.
     */
    @RequestMapping(
            value = "/_search",
            method = RequestMethod.POST
    )
    public ResponseEntity<?> search(
          @Valid @RequestBody ThemeConfig request) {

        ThemeConfig response =
                themeConfigService.search(
                        request.getTenantId(),
                        request.getThemeType()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

  /**
     * Handles workflow approval/rejection callback.
     *
     * APPROVE:
     * - Updates configuration status to APPROVED
     *
     * REJECT:
     * - Updates configuration status to REJECTED
     *
     * @param request workflow action request
     * @return updated theme configuration
     */
    @RequestMapping(
            value = "/_action",
            method = RequestMethod.POST
    )
    public ResponseEntity<?> workflowUpdate(
            @Valid @RequestBody ThemeConfigWorkflowRequest request) {


        ThemeConfig response =
                themeConfigService.updateWorkflowStatus(
                        request.getThemeConfig(),
                        request.getAction(),
                        request.getRequestInfo()
                );


        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

}
