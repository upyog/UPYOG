package org.upyog.tp.service;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.repository.TreePruningRepository;
import org.upyog.tp.repository.ServiceRequestRepository;
import org.upyog.tp.service.impl.TreePruningServiceImpl;
import org.upyog.tp.web.models.workflow.State;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TreePruningConfiguration configs;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TreePruningRepository repo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TreePruningServiceImpl treePruningService;

    /**
     *
     * @param record
     * @param topic
     */

    public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
        log.info(" Receipt consumer class entry " + record.toString());
        try {
            PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
            String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode().split("-")[0];
            log.info("paymentRequest : " + paymentRequest);
            String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();
            log.info("Payment request processing in tree Pruning method for businessService : " + businessService);
            log.info("consumerCode : " + consumerCode);
            if(configs.getTpModuleName()
                    .equals(businessService)){
                String applicationNo = paymentRequest.getPayment().getPaymentDetails().get(0).getBill()
                        .getConsumerCode();
                log.info("Updating payment status for Tree Pruning booking : " + applicationNo);
                State state = workflowService.updateWorkflowStatus(paymentRequest, null);
                String applicationStatus = state.getApplicationStatus();
                treePruningService.updateTreePruningBooking(paymentRequest, applicationStatus);
            }
        } catch (IllegalArgumentException e) {
            log.error(
                    "Illegal argument exception occured while sending notification Request Service : " + e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected exception occurred while processing payment in Request Service : ", e);
        }

    }

}

