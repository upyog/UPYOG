package org.egov.garbageservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbagePaymentDetails;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.garbageservice.util.GrbgUtils;
import org.egov.garbageservice.util.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer service responsible for intercepting external payment events and updating
 * the internal state of garbage accounts accordingly.
 *
 * <p>When a payment is made on a garbage account, this service handles parsing the payment payload,
 * updating the local {@code STATUS_PAID} state, generating audit logs, and persisting the resulting
 * transaction details to the database via Kafka topics.
 */
@Service
@Slf4j
public class PaymentUpdateService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private GarbageServiceConfig config;

    @Autowired
    private GarbageAccountRepository garbageAccountRepository;

    /**
     * Processes payment events received from the payment consumer and
     * creates rent payment entries for Estate Management payments.
     *
     * @param record payment event payload
     * @param topic  kafka topic name
     * @throws JsonProcessingException if payment payload conversion fails
     */
    public void process(HashMap<String, Object> record, String topic) throws JsonProcessingException {
        try {
            PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
            String businessService = paymentRequest.getPayment().getPaymentDetails().get(0).getBusinessService();

            if (!config.getBusinessService().equals(businessService)) {
                log.debug("Ignoring payment for businessService: {}", businessService);
                return;
            }

            String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
            BigDecimal paidAmount = paymentRequest.getPayment().getPaymentDetails().get(0).getTotalAmountPaid();
            String tenantId = paymentRequest.getPayment().getTenantId();
            String userUuid = paymentRequest.getRequestInfo().getUserInfo() != null
                    ? paymentRequest.getRequestInfo().getUserInfo().getUuid() : ServiceConstants.STATUS_SYSTEM;
            long now = Instant.now().toEpochMilli();
            LocalDate today = LocalDate.now();

            log.info("Processing payment for consumerCode: {}, amount: {}", consumerCode, paidAmount);

            List<String> consumerCodes = List.of(consumerCode);
            SearchCriteriaGarbageAccount searchCriteria = new SearchCriteriaGarbageAccount();
            searchCriteria.setApplicationNumber(consumerCodes);
            searchCriteria.setTenantId(tenantId);
            List<GarbageAccount> garbageAccounts = garbageAccountRepository.searchV2(searchCriteria);

            String garbageApplicationNo = consumerCode;
            if (garbageAccounts != null && !garbageAccounts.isEmpty()) {
                garbageApplicationNo = garbageAccounts.get(0).getGrbgApplicationNumber();
            } else {
                log.error("GarbageAccount not found for consumerCode: {}", consumerCode);
            }

            Long garbageApplicationId = garbageAccounts.get(0).getId();

            GarbagePaymentDetails garbagePaymentDetails = GarbagePaymentDetails.builder()
                    .id(UUID.randomUUID().toString())
                    .applicationId(garbageApplicationId)
                    .applicationNo(garbageApplicationNo)
                    .penaltyAmount(BigDecimal.ZERO)
                    .rent(paidAmount)
                    .previousMonth(today.minusMonths(1).withDayOfMonth(1))
                    .paymentDate(today)
                    .lastDateOfPayment(today)
                    .duePaymentDate(today.plusMonths(1).withDayOfMonth(1))
                    .paymentStatus(ServiceConstants.STATUS_PAID)
                    .duePayment(BigDecimal.ZERO)
                    .validityDays(30)
                    .createdBy(userUuid)
                    .lastModifiedBy(userUuid)
                    .createdTime(now)
                    .lastModifiedTime(now)
                    .build();

            garbageAccountRepository.save(config.getMonthlyRentPaymentSaveTopic(), Map.of("garbagePaymentDetails", garbagePaymentDetails));
            log.info("GarbagePaymentDetails saved to ug_grbg_monthly_rent_payment for consumerCode: {}", consumerCode);

            // Update allotment status to PAID
            try {
                if (garbageAccounts != null && !garbageAccounts.isEmpty()) {
                    GarbageAccount garbageAccount = garbageAccounts.get(0);
                    garbageAccount.setStatus(ServiceConstants.STATUS_PAID);
                    if (garbageAccount.getGrbgApplication() != null) {
                        garbageAccount.getGrbgApplication().setStatus(ServiceConstants.STATUS_PAID);
                    }

                    // Update last modified audit details
                    if (garbageAccount.getAuditDetails() != null) {
                        garbageAccount.getAuditDetails().setLastModifiedBy(userUuid);
                        garbageAccount.getAuditDetails().setLastModifiedTime(now);
                    } else {
                        garbageAccount.setAuditDetails(GrbgUtils.getAuditDetails(userUuid, false));
                    }

                    GarbageAccountRequest garbageAccountRequest = new GarbageAccountRequest(paymentRequest.getRequestInfo(), List.of(garbageAccount), false, false);
                    garbageAccountRepository.save(config.getUpdateGarbageAccountTopic(), garbageAccountRequest);
                    log.info("Updated Garbage Application status to PAID for Application No: {}", consumerCode);
                }
            } catch (Exception e) {
                log.error("Failed to update garbage application status on payment: {}", e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("Error processing payment update for Garbage: {}", e.getMessage(), e);
        }
    }
}
