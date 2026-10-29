package org.egov.infra.mdms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.config.ApplicationConfig;
import org.egov.infra.mdms.model.ThemeConfig;
import org.egov.infra.mdms.model.WorkflowProcessInstance;
import org.egov.infra.mdms.model.WorkflowRequest;
import org.egov.infra.mdms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
                + applicationConfig.getWorkflowTransitionEndpoint();


        WorkflowProcessInstance process = new WorkflowProcessInstance();

        // Same id will be used to track workflow against theme config row.
        process.setBusinessId(themeConfig.getId());
        process.setTenantId(themeConfig.getTenantId());
        process.setBusinessService(
                applicationConfig.getThemeConfigBusinessService()
        );
        process.setModuleName("MDMS");
        process.setAction("INITIATE");
        process.setComment("Theme config update");


        List<WorkflowProcessInstance> processInstances = new ArrayList<>();
        processInstances.add(process);


        WorkflowRequest request = new WorkflowRequest();
        request.setRequestInfo(requestInfo);
        request.setProcessInstances(processInstances);


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


    /**
     * Updates workflow transition.
     *
     * @param themeConfig theme configuration details
     * @param requestInfo request information
     * @param action workflow action
     * @return workflow id
     */
    @Override
    public String transitionWorkflow(
            ThemeConfig themeConfig,
            RequestInfo requestInfo,
            String action) {


        String url = applicationConfig.getWorkflowHost()
                + applicationConfig.getWorkflowTransitionEndpoint();


        WorkflowProcessInstance process = new WorkflowProcessInstance();

        process.setBusinessId(themeConfig.getId());
        process.setTenantId(themeConfig.getTenantId());
        process.setBusinessService(
                applicationConfig.getThemeConfigBusinessService()
        );
        process.setModuleName("MDMS");
        process.setAction(action);
        process.setComment("Theme config " + action);


        List<WorkflowProcessInstance> processInstances = new ArrayList<>();
        processInstances.add(process);


        WorkflowRequest request = new WorkflowRequest();
        request.setRequestInfo(requestInfo);
        request.setProcessInstances(processInstances);


        log.info(
                "Workflow transition request : {}",
                request
        );


        Map<String, Object> response =
                restTemplate.postForObject(
                        url,
                        request,
                        Map.class
                );


        log.info(
                "Workflow transition response : {}",
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


        return null;
    }

}
