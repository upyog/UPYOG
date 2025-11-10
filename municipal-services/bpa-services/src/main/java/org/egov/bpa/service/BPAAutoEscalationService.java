package org.egov.bpa.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.repository.ServiceRequestRepository;
import org.egov.bpa.service.notification.PaymentNotificationService;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.web.model.BPARequest;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.egov.bpa.web.model.NOC.Noc;
import org.egov.bpa.web.model.NOC.NocRequest;
import org.egov.bpa.web.model.NOC.NocResponse;
import org.egov.bpa.web.model.workflow.ProcessInstance;
import org.egov.bpa.web.model.workflow.ProcessInstanceResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BPAAutoEscalationService {

    private final PaymentNotificationService paymentNotificationService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private BPAConfiguration config;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	BPAUtil bpaUtil;

    BPAAutoEscalationService(PaymentNotificationService paymentNotificationService) {
        this.paymentNotificationService = paymentNotificationService;
    }
	
	@SuppressWarnings("unchecked")
	public List<ProcessInstance> fetchAutoEscalationApplications(Map<String, Object> autoEscalationMdmsData) {

		StringBuilder url = bpaUtil.getAutoEscalationApplicationsURL(autoEscalationMdmsData);
		List<ProcessInstance> processInstances = new ArrayList<>();
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(new RequestInfo())
				.build();
		LinkedHashMap<String, Object> responseMap = null;
		try {
			responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(url, requestInfoWrapper);
			ProcessInstanceResponse instanceResponse = mapper.convertValue(responseMap, ProcessInstanceResponse.class);
			processInstances = instanceResponse.getProcessInstances();
		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.EG_WF_ERROR, " Unable to fetch the Auto Escalation Eligible Applications records");
		}
		
		return processInstances;
	}
	
	public List<Map<String, Object>> fetchAutoEscalationMdmsData(RequestInfo requestInfo){
		StringBuilder url = bpaUtil.getMdmsSearchUrl();
		MdmsCriteriaReq mdmsCriteriaReq = bpaUtil.getMDMSRequestForAutoEscalationData(requestInfo, "pb");
		List<Map<String, Object>> autoEscalationMdmsData = new ArrayList<>();
		try {
			Object result = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
			autoEscalationMdmsData = JsonPath.read(result, "$.MdmsRes.Workflow.AutoEscalation");
		} catch (Exception e) {
			throw new CustomException("MDMS_SEARCH_ERROR", " Unable to fetch the Auto Escalation data from MDMS.");
		}
		
		return autoEscalationMdmsData;
	}
	
}
