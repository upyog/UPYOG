package com.cdac.esign.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cdac.esign.form.RequestXmlForm;
import com.cdac.esign.service.ESignService;

@Controller
public class ESignController {

    private static final Logger logger = LoggerFactory.getLogger(ESignController.class);

    @Autowired
    private ESignService eSignService;

    @PostMapping("/upload")
    public ResponseEntity<RequestXmlForm> uploadAndSignDocument(
            @RequestParam("file") String fileStoreId, 
            @RequestParam("tenantid") String tenantId,
            // 1. ADDED: New Parameter for Signer Name (Optional)
            @RequestParam(value = "signerName", required = false) String signerName,
            @RequestParam(value = "callbackUrl") String callbackUrl,
            HttpServletRequest request) {

        logger.info("Received upload request for file: {}, tenant: {}, signer: {}", fileStoreId, tenantId, signerName);

        String userInfoRowData = request.getHeader("x-user-info");
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(request.getHeader("auth-token"));
        
        
        try {
        	
        	if(!StringUtils.isEmpty(userInfoRowData)) {
            	JSONObject userInfoObj = new JSONObject(userInfoRowData);
            	User userInfo = User.builder()
            			.uuid(userInfoObj.getString("uuid"))
            			.name(userInfoObj.getString("name"))
            			.userName(userInfoObj.getString("userName"))
            			.type(userInfoObj.getString("type"))
            			.build();
            	requestInfo.setUserInfo(userInfo);
            }
            // 2. UPDATED: Passing the dynamic 'signerName' instead of null
            RequestXmlForm responseForm = eSignService.processDocumentUpload(fileStoreId, tenantId,signerName, callbackUrl, requestInfo);
            
            logger.info("Document upload processed successfully for transaction: {}", responseForm.getAspTxnID());
            return ResponseEntity.ok(responseForm);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error processing document upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Map<String, String>> completeSigning(
            @RequestParam("eSignResponse") String response,
            @RequestParam("espTxnID") String espTxnID,
            @RequestParam(value = "callbackUrl", required = false) String callbackUrl,
            HttpServletRequest request) {

        logger.info("Received complete signing request for espTxnID: {}", espTxnID);

        try {
            // Updated: Service now returns a Map
            Map<String, String> signingResult = eSignService.processDocumentCompletion(response, espTxnID, request);
            
            logger.info("Document signing completed successfully: {}", signingResult.get("fileUrl"));

            Map<String, String> body = new HashMap<>();
            body.put("status", "SUCCESS");
            body.put("fileUrl", signingResult.get("fileUrl"));
            body.put("fileStoreId", signingResult.get("fileStoreId")); // New field returned

            // Redirect to custom callback URL 
            String finalRedirectUrl = callbackUrl + "/" + signingResult.get("fileStoreId");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", finalRedirectUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (IllegalStateException e) {
            // ... [Existing error handling] ...
            logger.warn("Session expired or invalid state: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("status", "FAILED");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            // ... [Existing error handling] ...
            logger.error("Error processing signature", e);
            Map<String, String> error = new HashMap<>();
            error.put("status", "FAILED");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}