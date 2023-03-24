package org.ksmart.birth.workflow;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.BirthDeathConstants;
import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkflowIntegratorAbandoned {



    private   final BirthConfiguration bndConfig;
    private   final RestTemplate restTemplate;

    @Autowired
    public WorkflowIntegratorAbandoned(RestTemplate restTemplate, BirthConfiguration bndConfig) {
        this.restTemplate = restTemplate;
        this.bndConfig = bndConfig;

    }

    /**
     * Method to integrate with workflow
     *
     * takes the filedetails request (now take applicant personal request) as
     * parameter constructs the work-flow request
     *
     * and sets the resultant status from wf-response back to file details object
     *
     * @param request the {@link NewBirthDetailRequest}
     */
    public  void callWorkFlow(AbandonedRequest request) {

        AbandonedApplication currentFile = request.getBirthDetails().get(0);
        String wfTenantId = currentFile.getTenantId();
        String businessServiceFromMDMS = currentFile.getBusinessService();

        if (businessServiceFromMDMS == null) {
            businessServiceFromMDMS = BirthDeathConstants.BUSINESS_SERVICE_BND;
        }

        JSONArray array = new JSONArray();

        for (AbandonedApplication birth : request.getBirthDetails()) {
            if (businessServiceFromMDMS.equals(BirthDeathConstants.BUSINESS_SERVICE_BND) || !request.getBirthDetails()
                    .get(0).getAction().equalsIgnoreCase(BirthDeathConstants.TRIGGER_NOWORKFLOW)) {

                JSONObject obj = new JSONObject();
                List<Map<String, String>> uuidmaps = new LinkedList<>();

                if (!CollectionUtils.isEmpty(birth.getAssignee())) {

                    // Adding assignes to processInstance

                    birth.getAssignee().forEach(assignee -> {

                        Map<String, String> uuidMap = new HashMap<>();

                        uuidMap.put(BirthDeathConstants.UUIDKEY, assignee);
                        uuidmaps.add(uuidMap);
                    });
                }

                obj.put(BirthDeathConstants.BUSINESSIDKEY, birth.getApplicationNo());
                obj.put(BirthDeathConstants.TENANTIDKEY, wfTenantId);
                obj.put(BirthDeathConstants.BUSINESSSERVICEKEY, currentFile.getWorkFlowCode());
                obj.put(BirthDeathConstants.MODULENAMEKEY, BirthDeathConstants.BNDMODULENAMEVALUE);
                obj.put(BirthDeathConstants.ACTIONKEY, birth.getAction());
                obj.put(BirthDeathConstants.COMMENTKEY, birth.getComment());
                if (!CollectionUtils.isEmpty(birth.getAssignee())) {
                    obj.put(BirthDeathConstants.ASSIGNEEKEY, uuidmaps);
                }
                obj.put(BirthDeathConstants.DOCUMENTSKEY, birth.getWfDocuments());
                array.add(obj);
            }
        }

        if (!CollectionUtils.isEmpty(array)) {
            JSONObject workFlowRequest = new JSONObject();
            workFlowRequest.put(BirthDeathConstants.REQUESTINFOKEY, request.getRequestInfo());
            workFlowRequest.put(BirthDeathConstants.WORKFLOWREQUESTARRAYKEY, array);
            String response = null;
            log.info("workflow integrator request " + workFlowRequest);

            try {
                response = restTemplate.postForObject(bndConfig.getWfHost().concat(bndConfig.getWfTransitionPath()),
                        workFlowRequest, String.class);
            } catch (HttpClientErrorException e) {
                /*
                 * extracting message from client error exception
                 */
                DocumentContext responseContext = JsonPath.parse(e.getResponseBodyAsString());
                List<Object> errros = null;
                try {
                    errros = responseContext.read("$.Errors");
                } catch (PathNotFoundException pnfe) {
                    log.error("EG_BND_WF_ERROR_KEY_NOT_FOUND",
                            " Unable to read the json path in error object : " + pnfe.getMessage());
                    throw new CustomException("EG_BND_WF_ERROR_KEY_NOT_FOUND",
                            " Unable to read the json path in error object : " + pnfe.getMessage());
                }
                throw new CustomException("EG_WF_ERROR", errros.toString());
            } catch (Exception e) {
                throw new CustomException("EG_WF_ERROR",
                        " Exception occured while integrating with workflow : " + e.getMessage());
            }

            log.info("workflow integrator response " + response);

            /*
             * on success result from work-flow read the data and set the status back to TL
             * object
             */
            DocumentContext responseContext = JsonPath.parse(response);
            List<Map<String, Object>> responseArray = responseContext.read(BirthDeathConstants.PROCESSINSTANCESJOSNKEY);
            Map<String, String> idStatusMap = new HashMap<>();
            responseArray.forEach(object -> {

                DocumentContext instanceContext = JsonPath.parse(object);
                idStatusMap.put(instanceContext.read(BirthDeathConstants.BUSINESSIDJOSNKEY),
                        instanceContext.read(BirthDeathConstants.STATUSJSONKEY));
            });
            // setting the status back to TL object from wf response

                  request.getBirthDetails().forEach(
                    bndObj -> bndObj.setApplicationStatus(idStatusMap.get(bndObj.getApplicationNo())));

        }

    }

}