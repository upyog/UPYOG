package org.egov.infra.mdms.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.model.ThemeConfig;
import org.egov.infra.mdms.repository.ThemeConfigRepository;
import org.egov.infra.mdms.service.ThemeConfigService;
import org.egov.infra.mdms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of ThemeConfigService.
 *
 * Handles theme configuration creation,
 * update and workflow status management.
 */
@Slf4j
@RequiredArgsConstructor    
@Service
public class ThemeConfigServiceImpl implements ThemeConfigService {

    private final ThemeConfigRepository themeConfigRepository;
    private final WorkflowService workflowService;


    /**
     * Creates theme configuration.
     *
     * @param themeConfig theme configuration details
     * @return created theme configuration
     */
    @Override
    public ThemeConfig create(ThemeConfig themeConfig) {

        log.info(
                "Creating theme configuration for tenantId: {} and themeType: {}",
                themeConfig.getTenantId(),
                themeConfig.getThemeType()
        );

        if (themeConfig.getId() == null) {
            themeConfig.setId(UUID.randomUUID().toString());
        }

        long currentTime = System.currentTimeMillis();

        if (themeConfig.getCreatedTime() == null) {
            themeConfig.setCreatedTime(currentTime);
        }

        if (themeConfig.getLastModifiedTime() == null) {
            themeConfig.setLastModifiedTime(currentTime);
        }

        themeConfigRepository.create(themeConfig);

        return themeConfig;
    }


    /**
     * Creates updated theme configuration with pending status.
     *
     * A new configuration row is created for workflow approval.
     * Existing approved configuration remains unchanged.
     *
     * @param themeConfig updated theme configuration
     * @param requestInfo request information
     * @return pending theme configuration
     */
    @Override
    public ThemeConfig update(
            ThemeConfig themeConfig,
            RequestInfo requestInfo) {

        log.info(
                "Creating pending theme configuration for tenantId: {} and themeType: {}",
                themeConfig.getTenantId(),
                themeConfig.getThemeType()
        );

        // Prevent duplicate pending modification requests for same tenant and theme type
        if (themeConfigRepository.existsPendingTheme(
                themeConfig.getTenantId(),
                themeConfig.getThemeType())) {

            throw new RuntimeException(
                    "Modification already sent to the Admin for verification"
            );
        }

        // Generate new id so existing configuration remains untouched.
        if (themeConfig.getId() == null) {
            themeConfig.setId(UUID.randomUUID().toString());
        }

        long currentTime = System.currentTimeMillis();

        if (themeConfig.getCreatedTime() == null) {
            themeConfig.setCreatedTime(currentTime);
        }

        themeConfig.setLastModifiedTime(currentTime);

        // New changes require workflow approval.
        themeConfig.setStatus("PENDING");

        log.info("THEME CONFIG BEFORE WORKFLOW : {}", themeConfig);

        String workflowId = workflowService.createWorkflow(
                themeConfig,
                requestInfo
        );

        themeConfig.setWorkflowId(workflowId);

        log.info("THEME CONFIG AFTER WORKFLOW : {}", themeConfig);

        // Store pending configuration in the same theme config table.
        themeConfigRepository.createStaging(themeConfig);

        return themeConfig;
    }


    /**
     * Updates theme configuration workflow status.
     *
     * @param themeConfig theme configuration
     * @param action workflow action
     * @param requestInfo request information
     * @return updated theme configuration
     */
    @Override
    public ThemeConfig updateWorkflowStatus(
            ThemeConfig themeConfig,
            String action,
            RequestInfo requestInfo) {

        if ("APPROVE".equals(action)) {
            themeConfig.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            themeConfig.setStatus("REJECTED");
        }

        themeConfig.setLastModifiedTime(System.currentTimeMillis());

        themeConfig.setLastModifiedBy(
                requestInfo.getUserInfo().getUuid()
        );


        String workflowId = workflowService.transitionWorkflow(
                themeConfig,
                requestInfo,
                action
        );

        themeConfig.setWorkflowId(workflowId);

        themeConfigRepository.update(themeConfig);

        return themeConfig;
    }


    /**
     * Fetch theme configuration.
     *
     * @param tenantId tenant identifier
     * @param themeType theme type
     * @return theme configuration
     */
    @Override
    public ThemeConfig search(
            String tenantId,
            String themeType) {

        return themeConfigRepository.search(
                tenantId,
                themeType
        );
    }
}
