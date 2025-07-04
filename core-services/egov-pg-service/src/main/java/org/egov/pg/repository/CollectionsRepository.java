package org.egov.pg.repository;

import lombok.extern.slf4j.Slf4j;

import java.beans.Transient;

import org.egov.pg.config.AppProperties;
import org.egov.pg.models.PaymentResponse;
import org.egov.pg.models.PaymentSearchCriteria;
import org.egov.pg.models.ReceiptReq;
import org.egov.pg.models.ReceiptRes;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



@Repository
@Slf4j
public class CollectionsRepository {


    private AppProperties appProperties;
    private RestTemplate restTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    

    @Autowired
    CollectionsRepository(RestTemplate restTemplate, AppProperties appProperties) {
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
    }
    
    


    public ReceiptRes generateReceipt(ReceiptReq receiptReq) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(appProperties.getCollectionServiceHost())
                .path(appProperties.getCollectionServiceCreatePath())
                .build()
                .toUriString();


        return fetchReceipt(receiptReq, uri);
    }

    public ReceiptRes validateReceipt(ReceiptReq receiptReq){
        String uri = UriComponentsBuilder
                .fromHttpUrl(appProperties.getCollectionServiceHost())
                .path(appProperties.getCollectionServiceValidatePath())
                .build()
                .toUriString();

        return fetchReceipt(receiptReq, uri);
    }

    private ReceiptRes fetchReceipt(ReceiptReq receiptReq, String uri){
        try {
            return restTemplate.postForObject(uri, receiptReq, ReceiptRes.class);
        } catch (HttpClientErrorException e) {
            log.error("Error occurred during collections API call", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unknown error occurred during collections API call", e);
            throw new CustomException("COLLECTION_SERVICE_ERROR", "Unknown error occurred during collections " +
                    "API call");
        }
    }
    
    
    public PaymentResponse serachPaidBillInEGCL(PaymentSearchCriteria paymentSearchCriteria, String uri){
        try {
            return restTemplate.postForObject(uri, paymentSearchCriteria, PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Error occurred during collections API call", e);
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unknown error occurred during collections API call", e);
            throw new CustomException("COLLECTION_SERVICE_ERROR", "Unknown error occurred during collections " +
                    "API call");
        }
        
    }
    
   public int getPaymentCountByBillId(String billId) {
        String sql = "SELECT COUNT(*) " +
                     "FROM egcl_payment ep " +
                     "JOIN egcl_paymentdetail ep2 ON ep.id = ep2.paymentid " +
                     "WHERE ep2.billid = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{billId}, Integer.class);
    }


}
