package org.egov.pg.service.gateways.icici;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


import org.egov.pg.models.Refund;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.Transaction.TxnStatusEnum;
import org.egov.pg.service.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Gateway implementation for ICICI Bank payment integration.
 * This class implements the {@link Gateway} interface to provide payment initiation
 * and status check capabilities via ICICI's payment gateway API. It handles secure
 * hash generation for request authentication, builds the sale initiation request,
 * and parses status check responses into the internal {@link Transaction} model.
 */
@Service
@Slf4j
public class ICICIGateway implements Gateway {

    private static final String GATEWAY_NAME = "ICICI";
    private static final String CURRENCY_CODE = "356";

    private final String SECURE_SECRET;
    private final String MERCHANT_ID;
    private final String INITIATE_SALE_URL;
    private final String STATUS_CHECK_URL;
    private final String REDIRECT_URL;

    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    /**
     * Aggregator ID provided by ICICI Payment Gateway.
     * Used to identify the payment aggregator while initiating transactions.
     */
    private final String AGGREGATOR_ID;
    private final String ORIGINAL_RETURN_URL_KEY;
    private final boolean ACTIVE;

    /**
     * Initialize by populating all required config parameters
     *
     * @param restTemplate rest template instance to be used to make REST calls
     * @param environment  containing all required config parameters
     * @param objectMapper
     */
    @Autowired
    public ICICIGateway(Environment environment, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("icici.active"));
        MERCHANT_ID = environment.getRequiredProperty("icici.merchant.id");
        SECURE_SECRET = environment.getRequiredProperty("icici.merchant.secret.key");
        INITIATE_SALE_URL = environment.getRequiredProperty("icici.gateway.url");
        STATUS_CHECK_URL = environment.getRequiredProperty("icici.gateway.url.status");
        REDIRECT_URL = environment.getRequiredProperty("icici.redirect.url");
        AGGREGATOR_ID = environment.getRequiredProperty("icici.aggregator.id");
        ORIGINAL_RETURN_URL_KEY = environment.getRequiredProperty("icici.original.return.url.key");

    }

    /**
     * Generates the redirect URI for initiating a sale transaction on the ICICI payment gateway.
     *
     * Builds the initiate-sale request payload, computes and attaches a secure hash for
     * authentication, and posts it to ICICI's initiate sale endpoint. On a successful
     * response (response code {@code R1000}), constructs the redirect URI by appending
     * the {@code tranCtx} returned by ICICI to the {@code redirectURI}.
     *
     * @param transaction the transaction for which the redirect URI is to be generated
     * @return the {@link URI} to which the user should be redirected to complete payment
     * @throws RuntimeException if the response from ICICI is empty, indicates failure,
     *                           or any error occurs during the request
     */
    @Override
    public URI generateRedirectURI(Transaction transaction) {

        try {

            log.info("ICICI request transaction: {}", transaction);
            Map<String, Object> request = buildInitiateSaleRequest(transaction);

            String secureHash = ICICIUtils.generateSecureHash(request, SECURE_SECRET);

            request.put("secureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            log.info("ICICI request : {}", request);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(INITIATE_SALE_URL, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();

            log.info("ICICI initiate sale response : {}", responseBody);

            if (responseBody == null) {

                throw new RuntimeException("Empty response from ICICI");
            }

            String responseCode = (String) responseBody.get("responseCode");

            log.info("responseCode : {}", responseCode);

            if (!"R1000".equals(responseCode)) {

                throw new RuntimeException("ICICI payment initiation failed : " + responseBody);
            }

            String redirectURI = (String) responseBody.get("redirectURI");

            String tranCtx = (String) responseBody.get("tranCtx");

            return UriComponentsBuilder.fromHttpUrl(redirectURI).queryParam("tranCtx", tranCtx).build().toUri();

        } catch (Exception ex) {

            log.info("Error while generating ICICI redirect URI", ex);

            throw new RuntimeException("ICICI payment initiation failed", ex);
        }
    }

    /**
     * Fetches the current status of a transaction from the ICICI payment gateway.
     */
    @Override
    public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {

        try {

            Map<String, Object> request = new HashMap<>();

            request.put("merchantId", MERCHANT_ID);
            request.put("aggregatorID", AGGREGATOR_ID);
            request.put("merchantTxnNo", currentStatus.getTxnId());
            request.put("originalTxnNo", currentStatus.getTxnId());
            request.put("transactionType", "STATUS");

            String secureHash = ICICIUtils.generateSecureHash(request, SECURE_SECRET);

            request.put("secureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(STATUS_CHECK_URL, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            log.info("ICICI status response : {}", responseBody);

            if (responseBody == null) {
                throw new RuntimeException("Empty response from ICICI");
            }

            currentStatus.setGatewayTxnId((String) responseBody.get("txnID"));
            currentStatus.setGatewayStatusCode((String) responseBody.get("txnResponseCode"));
            currentStatus.setGatewayStatusMsg((String) responseBody.get("txnRespDescription"));
            currentStatus.setTxnAmount((String) responseBody.get("amount"));
            currentStatus.setResponseJson(objectMapper.writeValueAsString(responseBody));

            String txnStatus = (String) responseBody.get("txnStatus");
            log.info("ICICI Transaction Status: {}", txnStatus);
            if ("SUC".equalsIgnoreCase(txnStatus)) {

                currentStatus.setTxnStatus(TxnStatusEnum.SUCCESS);
           } else if ("FAIL".equalsIgnoreCase(txnStatus) || "REJ".equalsIgnoreCase(txnStatus)) {
                currentStatus.setTxnStatus(TxnStatusEnum.FAILURE);

            } else {

                currentStatus.setTxnStatus(TxnStatusEnum.PENDING);
            }

            return currentStatus;

        } catch (Exception ex) {

            log.info("Error while fetching ICICI payment status:{}", ex);

            throw new RuntimeException("ICICI payment status fetch failed", ex);
        }
    }

    @Override
    public boolean isActive() {

        return ACTIVE;
    }

    /**
     * ======================== GATEWAY NAME====================================
     */
    @Override
    public String gatewayName() {

        return GATEWAY_NAME;
    }

    /**
     * ================================================== CALLBACK TXN ID
     * KEY==================================================
     */
    @Override
    public String transactionIdKeyInResponse() {

        return "merchantTxnNo";
    }

    /**
     * ================================================== FORM
     * DATA==================================================
     */
    @Override
    public String generateRedirectFormData(Transaction transaction) {

        return null;
    }

    @Override
    public Refund initiateRefund(Refund refundTxn) {
        throw new RuntimeException("Method not Implemented");
    }

    @Override
    public Refund fetchRefundStatus(Refund refundRequest) {
        throw new RuntimeException("Method not Implemented");
    }

    /**
     * ==== BUILD INITIATE SALE REQUEST ====
     *
     *
     *       Builds the request payload required to initiate a sale transaction with ICICI.
     *
     *       Populates merchant identification, transaction amount (formatted to two decimal
     *       places), currency code, customer details, return URL, transaction date, and
     *       additional product/module information.
     *
     *
     *       @param transaction the transaction for which the initiate sale request is to be built
     *       @return a map representing the initiate sale request payload (excluding the secure hash)
     *
     */
    private Map<String, Object> buildInitiateSaleRequest(Transaction transaction) {

        log.info("request transaction :{}", transaction);
        Map<String, Object> request = new HashMap<>();

        request.put("merchantId", MERCHANT_ID);
        request.put("aggregatorID", AGGREGATOR_ID);
        request.put("merchantTxnNo", transaction.getTxnId());

        request.put("amount", new BigDecimal(transaction.getTxnAmount()).setScale(2, RoundingMode.HALF_UP).toString());

        request.put("currencyCode", CURRENCY_CODE);

        request.put("payType", "0");

        request.put("customerEmailID", transaction.getUser().getEmailId());

        request.put("transactionType", "SALE");

        // Route ICICI callback through redirect controller instead of directly hitting frontend
        String icicReturnUrl = UriComponentsBuilder
                .fromHttpUrl(REDIRECT_URL)
                .queryParam(ORIGINAL_RETURN_URL_KEY, transaction.getCallbackUrl())
                .build()
                .toUriString();

        log.info("ICICI returnURL (wrapped) = {}", icicReturnUrl);

        request.put("returnURL", icicReturnUrl);

        request.put("txnDate", ICICIUtils.getCurrentTxnDate());

        request.put("customerMobileNo", transaction.getUser().getMobileNumber());

        request.put("customerName", transaction.getUser().getName());

        request.put("addlParam1", transaction.getProductInfo());
        request.put("addlParam2", transaction.getModule());

        log.info("request:{}", request);
        return request;
    }

    /* TODO: will uncomment when refund will implement */
//    @Override
//    public Refund initiateRefund(Refund refundTxn) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Refund fetchRefundStatus(Refund refundRequest) {
//        // TODO Auto-generated method stub
//        return null;
//    }

}