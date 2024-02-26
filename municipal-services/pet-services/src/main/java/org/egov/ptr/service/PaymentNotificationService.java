package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.collection.PaymentDetail;
import org.egov.ptr.models.collection.PaymentRequest;
import org.egov.ptr.models.event.Action;
import org.egov.ptr.models.event.ActionItem;
import org.egov.ptr.models.event.Event;
import org.egov.ptr.models.event.EventRequest;
import org.egov.ptr.models.event.Recepient;
import org.egov.ptr.models.event.Source;
import org.egov.ptr.models.transaction.Transaction;
import org.egov.ptr.models.transaction.TransactionRequest;
import org.egov.ptr.util.NotificationUtil;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.web.contracts.EmailRequest;
import org.egov.ptr.web.contracts.PetRequest;
import org.egov.ptr.web.contracts.SMSRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class PaymentNotificationService {

    @Autowired
    private PetConfiguration petConfiguration;

    @Autowired
    private NotificationUtil util;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private WorkflowService wfService;

    private PetConfiguration config;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

    /**
     *
     * @param record
     * @param topic
     */
    public void process(PetRegistrationRequest petRequest, String topic){
    	
    	 
    	if (petRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction()=="PAY") {
    		wfService.updateWorkflowStatus(petRequest);
    	}
       
    }




}
