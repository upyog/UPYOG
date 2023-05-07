package org.ksmart.birth.common.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
 
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.ksmart.birth.common.calculation.collections.models.PaymentDetail;
import org.ksmart.birth.common.calculation.collections.models.PaymentRequest;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthEnrichment;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.newbirth.service.NewBirthService;
import org.ksmart.birth.utils.BirthUtils;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ksmart.birth.utils.BirthDeathConstants.*;
@Service
@Slf4j
public class PaymentUpdateService {
	private NewBirthService newBirthService;
	
	private BirthConfiguration config;

	private NewBirthRepository repository;
	
	private WorkflowIntegratorNewBirth wfIntegrator;
	
	private NewBirthEnrichment enrichmentService;

//	@Autowired
//	private objectMapper mapper;
	
	private BirthUtils util;
	
	public PaymentUpdateService(NewBirthService newBirthService,BirthConfiguration config, NewBirthRepository repository,
			WorkflowIntegratorNewBirth wfIntegrator,NewBirthEnrichment enrichmentService, BirthUtils util) {
		this.newBirthService=newBirthService;
		this.config=config;
		this.repository=repository;
		this.wfIntegrator=wfIntegrator;
		this.enrichmentService= enrichmentService;
//		this.mapper= mapper;
		this.util=util;
		
	}
	
	final String tenantId = "tenantId";

	final String businessService = "businessService";

	final String consumerCode = "consumerCode";
	
	
	/**
	 * Process the message from kafka and updates the status to paid
	 * 
	 * @param record The incoming message from receipt create consumer
	 */
	public void process(HashMap<String, Object> record) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			RequestInfo requestInfo = paymentRequest.getRequestInfo();
			
			List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
			String tenantId = paymentRequest.getPayment().getTenantId();
			for (PaymentDetail paymentDetail : paymentDetails) {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setTenantId(tenantId);
			 
			searchCriteria.setAppNumber(paymentDetail.getBill().getConsumerCode());
			searchCriteria.setBusinessService(paymentDetail.getBusinessService());
			System.out.println(" payment detail tenantId:"+tenantId);
			System.out.println(" payment detail tenantId:"+paymentDetail.getBill().getConsumerCode());
			System.out.println(" payment detail tenantId:"+paymentDetail.getBusinessService());
			
			List<NewBirthApplication> birth = newBirthService.searchBirth(requestInfo,searchCriteria);
			
			NewBirthDetailRequest updateRequest = NewBirthDetailRequest.builder().requestInfo(requestInfo)
					.newBirthDetails(birth).build();
			System.out.println(" payment detail updateRequest:"+updateRequest);
			wfIntegrator.callWorkFlow(updateRequest);
			}
			

		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR", e);
		}

	}

}
