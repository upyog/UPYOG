package org.egov.pg.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.pg.constants.PgConstants;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.TransactionService;
import org.egov.pg.web.models.TransactionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RedirectController {

	@Value("${egov.default.citizen.url}")
	private String defaultURL;

	@Value("${paygov.original.return.url.key}")
	private String returnUrlKey;

	@Value("${paygov.citizen.redirect.domain.name}")
	private String citizenRedirectDomain;

	private final TransactionService transactionService;

	@Autowired
	public RedirectController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping(value = "/transaction/v1/_redirect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> method(
            @RequestBody MultiValueMap<String, String> formData,
            @RequestParam(value = "originalreturnurl", required = false) String originalReturnUrlParam,
            @RequestParam(value = "eg_pg_txnid", required = false) String txnIdParam,
    @RequestParam(value = "amp;eg_pg_txnid", required = false) String txnIdParamNTT) 
{

        log.info("formData in redirect::::"+formData);
        log.info("originalReturnUrlParam from query::::"+originalReturnUrlParam);
        log.info("txnIdParam from query::::"+txnIdParam);
    	// Spring Boot 3 fix: Try query param first, then formData for backward compatibility
    	String returnURL = originalReturnUrlParam;
    	String txnId=txnIdParam;
    	if(txnId==null)
    		txnId=txnIdParamNTT;
    		
    	if(returnURL == null && formData.get(returnUrlKey) != null) {
    		returnURL = formData.get(returnUrlKey).get(0);
    	}

    	log.info("returnURL resolved::::"+returnURL);

    	
    	if(formData.get(PgConstants.PG_TXN_IN_LABEL)!=null && txnId==null)
    	{
    		txnId = formData.get(PgConstants.PG_TXN_IN_LABEL).get(0);
    		if(txnId==null && returnURL != null && returnURL.contains(PgConstants.PG_TXN_IN_LABEL+"="))
    			txnId=returnURL.split(PgConstants.PG_TXN_IN_LABEL+"=")[1];
    	}
    	else if(formData.get(PgConstants.PG_TXN_IN_LABEL_NTTDATA)!=null && txnId==null)
    		txnId = formData.get(PgConstants.PG_TXN_IN_LABEL_NTTDATA).get(0);

    	else if(returnURL != null && returnURL.contains(PgConstants.PG_TXN_IN_LABEL) && txnId==null)
    	{
    		txnId=returnURL.split(PgConstants.PG_TXN_IN_LABEL+"=")[1];
    	}
    	

        //MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(returnURL).build().getQueryParams();
        log.info("returnUrl in redirect::::"+returnURL);
        log.info("txn Id"+txnId);
        /*
         * From redirect URL get transaction id.
         * And using transaction id fetch transaction details.
         * And from transaction details get the GATEWAY info.
         */
        String gateway = null;
        if(txnId!=null) {
        	 //List<String> txnId = params.get(PgConstants.PG_TXN_IN_LABEL);
             TransactionCriteria critria = new TransactionCriteria();
             critria.setTxnId(txnId);
            List<Transaction> transactions = transactionService.getTransactions(critria);
            if(!transactions.isEmpty())
                gateway = transactions.get(0).getGateway();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        /*
         * The NSDL PAYGOV integration is not allowing multiple schems or protocols (ex: HTTP, HTTPS)
         * in the success or fail or redirect URL after completing payment from payment gateway
         * used for posting response.
         * Example the URL resposne getting as follows,
         * https://test.org/pg-service/transaction/v1/_redirect?originalreturnurl=/digit-ui/citizen/payment/success/PT/PG-PT-2022-03-10-006063/pg.citya?eg_pg_txnid=PB_PG_2022_07_12_002082_48
         * Here we are reading originalreturnurl value and then forming redirect URL with domain name.
         */
        StringBuilder redirectURL = new StringBuilder();

        if(gateway != null && gateway.equalsIgnoreCase("PAYGOV")) {
            redirectURL.append(returnURL);
            if(returnURL != null) 
                returnURL=returnURL + "&eg_pg_txnid="+txnId;
            formData.remove(returnUrlKey);
            httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(redirectURL.toString())
                    .queryParams(formData).build().encode().toUri());
        }
        else if(gateway != null && gateway.equalsIgnoreCase("NTTDATA")) {
            if(returnURL != null) {
                returnURL=returnURL + "&eg_pg_txnid="+txnId;
                redirectURL.append(returnURL);
            } else {
                // Fallback to default URL if returnURL is null
                redirectURL.append(defaultURL);
            }
            formData.remove(returnUrlKey);
            formData.remove("encData");
            formData.remove("merchId");
            formData.remove(PgConstants.PG_TXN_IN_LABEL_NTTDATA);

            httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(redirectURL.toString())
                    .queryParams(formData).build().encode().toUri());
        }else {
            // Use returnURL if available, otherwise fall back to formData, then defaultURL
            String fallbackUrl = returnURL != null ? returnURL :
                                (formData.get(returnUrlKey) != null ? formData.get(returnUrlKey).get(0) : defaultURL);
            httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(fallbackUrl)
                    .queryParams(formData).build().encode().toUri());
        }

        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleError(Exception e) {
		log.error("EXCEPTION_WHILE_REDIRECTING", e);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(defaultURL).build().encode().toUri());
		return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
	}

}