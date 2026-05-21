package org.upyog.adv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.web.models.workflow.ProcessInstance;
import org.upyog.adv.web.models.RequestInfoWrapper;
import org.upyog.adv.web.models.workflow.ProcessInstanceResponse;
import org.upyog.adv.repository.ServiceRequestRepository;

import java.util.LinkedHashMap;
import java.lang.StringBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdvAutoEscalationService {

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    

    @Value("${egov.workflow.host}")
    private String workflowHost;

    @Value("${workflow.escalation.path}")
    private String workflowEscalationPath;

    @Value("${workflow.escalation.auth.token:}")
    private String workflowEscalationAuthToken;

    /**
     * Fetch AutoEscalation master data for adv from MDMS. Caller will filter SLAs locally.
     */
    public List<Map<String, Object>> fetchAutoEscalationMdmsData(RequestInfo requestInfo, String tenantId) {
        try {
            // Build MDMS request specifically for AutoEscalation master (uses MdmsUtil helper)
            org.egov.mdms.model.MdmsCriteriaReq mdmsCriteriaReq = mdmsUtil.getMDMSRequestForAutoEscalationData(requestInfo, tenantId);
            StringBuilder uri = mdmsUtil.getMdmsSearchUrl();
            Object mdmsResp = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);

            // Use the existing mapper in this service to convert response -> Map and extract Workflow.AutoEscalation
            Map<?, ?> root = mapper.convertValue(mdmsResp, Map.class);
            if (root == null) return new ArrayList<>();
            Object mdms = root.get("MdmsRes");
            if (mdms == null) return new ArrayList<>();
            Map<?, ?> mdmsMap = mapper.convertValue(mdms, Map.class);
            Object wf = mdmsMap.get("Workflow");
            if (wf == null) return new ArrayList<>();
            Map<?, ?> wfMap = mapper.convertValue(wf, Map.class);
            Object auto = wfMap.get("AutoEscalation");
            if (auto == null) return new ArrayList<>();
            List<Map<String, Object>> list = mapper.convertValue(auto, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>(){
            });
            return list != null ? list : new ArrayList<>();
        } catch (Exception ex) {
            log.error("Failed to fetch AutoEscalation MDMS data: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Same as fetchAutoEscalationApplications(Map) but uses provided RequestInfo (so caller
     * can supply system user or custom RequestInfo).
     */
    public List<ProcessInstance> fetchAutoEscalationApplications(Map<String, Object> autoEscalationMdmsData, RequestInfo requestInfo) {
        List<ProcessInstance> processInstances = new ArrayList<>();

        // Build URL from MDMS data or fall back to configured URL + defaults
        String businessService = autoEscalationMdmsData.getOrDefault("businessService", "ADV").toString();
        String moduleName = autoEscalationMdmsData.getOrDefault("moduleName", "advandhoarding-services").toString();
        Object slaObj = autoEscalationMdmsData.get("sla");
        String sla = slaObj != null ? slaObj.toString() : "";
        String startSlaState = autoEscalationMdmsData.getOrDefault("startSlaState", "BOOKED").toString();
        String currentStates = autoEscalationMdmsData.getOrDefault("currentStates", "BOOKED").toString();

    StringBuilder url = new StringBuilder(workflowHost).append(workflowEscalationPath);
        System.out.println(url.toString());
    url.append("?businessService=").append(businessService)
                .append("&moduleName=").append(moduleName);
        if (!sla.isEmpty()) url.append("&sla=").append(sla);
        url.append("&startSlaState=").append(startSlaState).append("&currentStates=").append(currentStates);

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo != null ? requestInfo : createDefaultRequestInfo()).build();

        LinkedHashMap<String, Object> responseMap = null;
        try {
            responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(url, requestInfoWrapper);
            ProcessInstanceResponse instanceResponse = mapper.convertValue(responseMap, ProcessInstanceResponse.class);
            processInstances = instanceResponse.getProcessInstances();
        } catch (Exception e) {
            log.error("Unable to fetch the Auto Escalation Eligible Applications records: {}", e.getMessage());
            return new ArrayList<>();
        }

        return processInstances != null ? processInstances : new ArrayList<>();
    }

    private RequestInfo createDefaultRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("Rainmaker");
        requestInfo.setVer(".01");
        requestInfo.setMsgId(String.valueOf(System.currentTimeMillis()));
        requestInfo.setTs(System.currentTimeMillis());
        if (workflowEscalationAuthToken != null && !workflowEscalationAuthToken.isEmpty())
            requestInfo.setAuthToken(workflowEscalationAuthToken);
        return requestInfo;
    }

}
