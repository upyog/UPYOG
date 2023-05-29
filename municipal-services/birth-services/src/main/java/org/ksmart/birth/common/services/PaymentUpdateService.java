package org.ksmart.birth.common.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.birthcommon.repoisitory.CommonRepository;
import org.ksmart.birth.birthcommon.services.CommonService;
import org.ksmart.birth.common.calculation.collections.models.PaymentDetail;
import org.ksmart.birth.common.calculation.collections.models.PaymentRequest;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthEnrichment;
import org.ksmart.birth.newbirth.repository.NewBirthRepository;
import org.ksmart.birth.newbirth.service.NewBirthService;
import org.ksmart.birth.utils.BirthUtils;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.workflow.WorkflowIntegratorNewBirth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ksmart.birth.utils.BirthDeathConstants.*;
import static org.ksmart.birth.utils.BirthConstants.STATUS_FOR_PAYMENT;

@Service
@Slf4j
public class PaymentUpdateService implements BaseEnrichment {
	private NewBirthService newBirthService;

	private BirthConfiguration config;

//	private NewBirthRepository repository;

	private final CommonRepository repository;

	private WorkflowIntegratorNewBirth wfIntegrator;

	private NewBirthEnrichment enrichmentService;
	private CommonService commonService;

	private final BirthConfiguration birthDeathConfiguration;

//	@Autowired
//	private objectMapper mapper;

	private BirthUtils util;
	private BirthNotificationService notificationService;

	public PaymentUpdateService(NewBirthService newBirthService, BirthConfiguration config,
			BirthNotificationService notificationService, WorkflowIntegratorNewBirth wfIntegrator,
			NewBirthEnrichment enrichmentService, BirthUtils util, CommonService commonService,
			CommonRepository repository, BirthConfiguration birthDeathConfiguration) {
		this.newBirthService = newBirthService;
		this.config = config;
		this.repository = repository;
		this.wfIntegrator = wfIntegrator;
		this.enrichmentService = enrichmentService;
		this.util = util;
		this.commonService = commonService;
		this.birthDeathConfiguration = birthDeathConfiguration;
		this.notificationService = notificationService;

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

				List<NewBirthApplication> birth = newBirthService.searchBirth(requestInfo, searchCriteria);

				NewBirthDetailRequest updateRequest = NewBirthDetailRequest.builder().requestInfo(requestInfo)
						.newBirthDetails(birth).build();

				wfIntegrator.callWorkFlow(updateRequest);

				User userInfo = requestInfo.getUserInfo();
				AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

				// Update birth table with status initiated
				List<CommonPay> commonPays = new ArrayList<>();
				CommonPay pay = new CommonPay();
				pay.setAction("INITIATE");
				pay.setApplicationStatus("INITIATED");
				pay.setHasPayment(true);
				pay.setAmount(new BigDecimal(10));
				pay.setIsPaymentSuccess(true);
				pay.setApplicationNumber(paymentDetail.getBill().getConsumerCode());
				pay.setAuditDetails(auditDetails);
				commonPays.add(pay);
				CommonPayRequest paymentReq = CommonPayRequest.builder().requestInfo(requestInfo).commonPays(commonPays)
						.build();

				repository.updatePaymentDetails(paymentReq);

				NacDetailRequest nacRequest = new NacDetailRequest();
				nacRequest = mapper.convertValue(record, NacDetailRequest.class);

				//notificationService.process(updateRequest);
				// End
			}

		} catch (Exception e) {
			log.error("KAFKA_PROCESS_ERROR", e);
		}

	}

}
