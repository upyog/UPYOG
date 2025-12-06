package org.egov.dx.gis.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.dx.gis.config.GisDxConfiguration;
import org.egov.dx.gis.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Payment Service for verifying payment status from collection service
 */
@Slf4j
@Service
public class PaymentService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private GisDxConfiguration config;

    /**
     * Fetches payment data for entities from collection service
     * Filters payments by financial year period
     * @return Map of consumerCode to payment details
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchPaymentData(List<String> entityIds, String tenantId, String businessService, 
                                                 Long fromDate, Long toDate, Object requestInfo) {
        Map<String, Object> paymentDataMap = new HashMap<>();

        if (entityIds == null || entityIds.isEmpty()) {
            return paymentDataMap;
        }

        try {
            String endpoint = config.getCollectionPaymentSearchEndpoint().replace("{moduleName}", businessService);
            
            StringBuilder uri = new StringBuilder(
                UriComponentsBuilder.fromHttpUrl(config.getCollectionHost() + endpoint)
                    .queryParam("tenantId", tenantId)
                    .queryParam("businessService", businessService)
                    .toUriString()
            );

            for (String entityId : entityIds) {
                uri.append("&consumerCodes=").append(entityId);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("RequestInfo", requestInfo);

            log.debug("Calling collection service at: {}", uri);
            Optional<Object> responseOptional = serviceRequestRepository.fetchResult(uri, requestBody);
            
            if (responseOptional.isPresent()) {
                Map<String, Object> response = (Map<String, Object>) responseOptional.get();
                if (response.containsKey("Payments")) {
                    List<Map<String, Object>> payments = (List<Map<String, Object>>) response.get("Payments");
                    log.info("Received {} payments from collection service", payments != null ? payments.size() : 0);
                    
                    for (Map<String, Object> payment : payments) {
                        List<Map<String, Object>> paymentDetails = (List<Map<String, Object>>) payment.get("paymentDetails");
                        
                        if (paymentDetails != null) {
                            for (Map<String, Object> detail : paymentDetails) {
                                Map<String, Object> bill = (Map<String, Object>) detail.get("bill");
                                if (bill != null) {
                                    String consumerCode = (String) bill.get("consumerCode");
                                    
                                    if (isPaymentForFinancialYear(bill, fromDate, toDate)) {
                                        Map<String, Object> paymentInfo = new HashMap<>();
                                        paymentInfo.put("paymentStatus", "PAID");
                                        paymentInfo.put("transactionNumber", payment.get("transactionNumber"));
                                        paymentInfo.put("transactionDate", payment.get("transactionDate"));
                                        paymentInfo.put("amountPaid", detail.get("totalAmountPaid"));
                                        paymentInfo.put("amountDue", detail.get("totalDue"));
                                        
                                        paymentDataMap.put(consumerCode, paymentInfo);
                                        log.debug("Found payment for consumerCode: {}", consumerCode);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    log.info("No payments found in collection service response");
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch payment data from collection service", e);
        }

        log.info("Returning payment data for {} entities", paymentDataMap.size());
        return paymentDataMap;
    }

    /**
     * Checks if payment is for the requested financial year
     * Compares bill detail's fromPeriod and toPeriod with request dates
     */
    @SuppressWarnings("unchecked")
    private boolean isPaymentForFinancialYear(Map<String, Object> bill, Long fromDate, Long toDate) {
        if (fromDate == null || toDate == null) {
            return true;
        }

        List<Map<String, Object>> billDetails = (List<Map<String, Object>>) bill.get("billDetails");
        if (billDetails != null) {
            for (Map<String, Object> detail : billDetails) {
                Object fromPeriodObj = detail.get("fromPeriod");
                Object toPeriodObj = detail.get("toPeriod");
                
                if (fromPeriodObj instanceof Number && toPeriodObj instanceof Number) {
                    Long fromPeriod = ((Number) fromPeriodObj).longValue();
                    Long toPeriod = ((Number) toPeriodObj).longValue();
                    
                    if (fromPeriod >= fromDate && toPeriod <= toDate) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}

