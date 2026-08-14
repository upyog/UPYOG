package org.egov.infra.mdms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.config.ApplicationConfig;
import org.egov.infra.mdms.model.ThemeConfig;
import org.egov.infra.mdms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of WorkflowService.
 *
 * Creates workflow instances for theme configuration updates.
 */
@Slf4j
@Service
public class WorkflowServiceImpl implements WorkflowService {


    private final ApplicationConfig applicationConfig;

    private final RestTemplate restTemplate;


    @Autowired
    public WorkflowServiceImpl(
            ApplicationConfig applicationConfig) {

        this.applicationConfig = applicationConfig;
        this.restTemplate = new RestTemplate();
    }


    /**
     * Creates workflow instance for theme configuration update.
     *
     * @param themeConfig theme configuration details
     * @param requestInfo request information
     * @return workflow id
     */
    @Override
    public String createWorkflow(
            ThemeConfig themeConfig,
            RequestInfo requestInfo) {


        String url = applicationConfig.getWorkflowHost()
                + "/egov-workflow-v2/egov-wf/process/_transition";


        Map<String, Object> process = new HashMap<>();

        // Same id will be used to track workflow against theme config row.
        process.put(
                "businessId",
                themeConfig.getId()
        );

        process.put(
                "tenantId",
                themeConfig.getTenantId()
        );

        process.put(
                "businessService",
                applicationConfig.getThemeConfigBusinessService()
        );

        process.put(
                "moduleName",
                "MDMS"
        );

        process.put(
                "action",
                "INITIATE"
        );

        process.put(
                "comment",
                "Theme config update"
        );


        List<Map<String, Object>> processInstances = new ArrayList<>();
        processInstances.add(process);


        Map<String, Object> request = new HashMap<>();

        request.put(
                "RequestInfo",
                requestInfo
        );

        request.put(
                "ProcessInstances",
                processInstances
        );


        log.info(
                "Creating workflow for theme config id : {}",
                themeConfig.getId()
        );

        try {

            Map<String, Object> response =
                    restTemplate.postForObject(
                            url,
                            request,
                            Map.class
                    );


            log.info(
                    "Workflow URL : {}",
                    url
            );

            log.info(
                    "Workflow Request : {}",
                    request
            );

            log.info(
                    "Workflow Response : {}",
                    response
            );


            if (response != null
                    && response.get("ProcessInstances") != null) {


                List<Map<String, Object>> instances =
                        (List<Map<String, Object>>) response.get("ProcessInstances");


                if (!instances.isEmpty()) {

                    return String.valueOf(
                            instances.get(0).get("id")
                    );
                }
            }


        } catch (Exception e) {

            log.error(
                    "Error while creating workflow",
                    e
            );
        }


        return null;
    }


    @Override
    public String transitionWorkflow(
            ThemeConfig themeConfig,
            RequestInfo requestInfo,
            String action) {

        String url = applicationConfig.getWorkflowHost()
                + "/egov-workflow-v2/egov-wf/process/_transition";

        Map<String, Object> process = new HashMap<>();

        process.put("businessId", themeConfig.getId());
        process.put("tenantId", themeConfig.getTenantId());
        process.put(
                "businessService",
                applicationConfig.getThemeConfigBusinessService()
        );
        process.put("moduleName", "MDMS");
        process.put("action", action);
        process.put("comment", "Theme config " + action);

        List<Map<String, Object>> processInstances = new ArrayList<>();
        processInstances.add(process);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);
        request.put("ProcessInstances", processInstances);

        log.info("Workflow transition request : {}", request);

        Map<String, Object> response =
                restTemplate.postForObject(
                        url,
                        request,
                        Map.class
                );

        log.info("Workflow transition response : {}", response);

        if (response != null
                && response.get("ProcessInstances") != null) {

            List<Map<String, Object>> instances =
                    (List<Map<String, Object>>) response.get("ProcessInstances");

            if (!instances.isEmpty()) {
                return String.valueOf(
                        instances.get(0).get("id")
                );
            }
        }

        return null;
    }

}
