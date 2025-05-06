package org.egov.swservice.service;

import org.egov.swservice.web.models.SewerageConnection;
import org.egov.swservice.web.models.SewerageConnectionRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@Service
public class EODBredirect {
	@Value("${eodb.token.url}")
	private String tokenUrl;
	
	@Value("${eodb.update.url}")
	private String updateUrl;
	
	@Value("${eodb.integration.key}")
	private String integrationKey;
	
	
	
    public boolean runEodbFlow(SewerageConnectionRequest connection) {
        String token = fetchToken();
        if (token != null && !token.isEmpty()) {
            return updateStatusWithToken(token, connection);
        } else {
            System.out.println("Token fetch failed.");
            return false;
        }
    }

    private String fetchToken() {
        String apiUrl = tokenUrl;
        String integrationKeyJson = "{\"IntegrationKey\":\"" + integrationKey + "\"}";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(integrationKeyJson.getBytes("utf-8"));
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner sc = new Scanner(con.getInputStream(), "utf-8");
                StringBuilder response = new StringBuilder();
                while (sc.hasNext()) {
                    response.append(sc.nextLine());
                }
                sc.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.optString("token"); // Adjust key if token is nested
            } else {
                System.out.println("Token API failed. HTTP code: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean updateStatusWithToken(String token, SewerageConnectionRequest connection) {
        String apiUrl = updateUrl;

        // Extract additionalDetails from the connection (assuming it is a Map)
        Object additionalDetailsObject = connection.getSewerageConnection().getAdditionalDetails();  // This should be a Map<String, Object>

        // Null check for additionalDetailsObject
        if (additionalDetailsObject == null) {
            log.error("Additional details are null.");
            return false;
        }

        // Ensure that the additionalDetailsObject is a Map
        Map<String, Object> additionalDetailsMap = null;
        if (additionalDetailsObject instanceof Map) {
            additionalDetailsMap = (Map<String, Object>) additionalDetailsObject;
        } else {
            log.error("Expected additionalDetails to be a Map, but found: " + additionalDetailsObject.getClass().getName());
            return false;
        }

        // Null check for keys (iPin and AppId)
        String iPin = (String) additionalDetailsMap.get("iPin");
        String appId = (String) additionalDetailsMap.get("appid");  // appid should be correct key

        if (iPin == null || appId == null) {
            log.error("Missing iPin or appId in additionalDetails.");
            return false;
        }

        Map<String, Integer> statusIdMap = new HashMap<>();
        Map<String, String> statusDescMap = new HashMap<>();

        // Populate the maps based on the original mseva status
        statusIdMap.put("PENDING_FOR_DOCUMENT_VERIFICATION", 1);  // Form Submitted (Created)
        statusIdMap.put("PENDING_FOR_PAYMENT", 2);  // Payment Raised (Estimation Notice Generated)
        statusIdMap.put("REJECTED", 3);  // Rejected
        statusIdMap.put("PENDING_FOR_CONNECTION_ACTIVATION", 4);  // Clearance Issued
        statusIdMap.put("PENDING_FOR_CITIZEN_ACTION", 5);  // Objection Raised
        statusIdMap.put("PENDING_FOR_FIELD_INSPECTION", 6);  // Pending for on-site inspection
        statusIdMap.put("PENDING_APPROVAL_FOR_CONNECTION", 7);  // Inspection done, pending for final approval

        statusDescMap.put("PENDING_FOR_DOCUMENT_VERIFICATION", "Form Submitted (Created)");
        statusDescMap.put("PENDING_FOR_PAYMENT", "Payment Raised (Estimation Notice Generated)");
        statusDescMap.put("REJECTED", "Rejected");
        statusDescMap.put("PENDING_FOR_CONNECTION_ACTIVATION", "Clearance Issued");
        statusDescMap.put("PENDING_FOR_CITIZEN_ACTION", "Objection Raised");
        statusDescMap.put("PENDING_FOR_FIELD_INSPECTION", "Pending for on-site inspection");
        statusDescMap.put("PENDING_APPROVAL_FOR_CONNECTION", "inspection done, pending for final approval");

        
        // Get the status from the connection and find the corresponding StatusId and StatusDesc
        String applicationStatus = connection.getSewerageConnection().getApplicationStatus();

        // Check if a valid status exists in the map
        Integer statusId = statusIdMap.get(applicationStatus);
        String statusDesc = statusDescMap.get(applicationStatus);

        // If the statusId or statusDesc is not found, return false
        if (statusId == null || statusDesc == null) {
            log.error("Invalid applicationStatus: " + applicationStatus);
            return false;
        }
        
        
        String senderName = "LG";
        String receiverName = "LG";
        String designation = "NA";

        if ("PENDING_FOR_CITIZEN_ACTION".equals(applicationStatus)) {
            receiverName = connection.getRequestInfo().getUserInfo().getName();
        }

        switch (applicationStatus) {
            case "PENDING_FOR_DOCUMENT_VERIFICATION":
            case "PENDING_FOR_FIELD_INSPECTION":
            case "REJECTED":
                designation = "Junior Engineer";
                break;
            case "PENDING_APPROVAL_FOR_CONNECTION":
                designation = "SDO/SDE/EO";
                break;
            case "PENDING_FOR_PAYMENT":
                designation = "CITIZEN/CLERK";
                break;
            case "PENDING_FOR_CONNECTION_ACTIVATION":
                designation = "CLERK";
                break;
        }

        String clearanceIssuedOn = "PENDING_FOR_PAYMENT".equals(applicationStatus) ?
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                                    "NA";

        String statusDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("iPin", iPin);
            jsonInput.put("AppId", appId);
            jsonInput.put("statusId", statusId);
            jsonInput.put("statusDesc", statusDesc);
            jsonInput.put("comments", "NA");
            jsonInput.put("senderName", connection.getRequestInfo().getUserInfo().getName());
            jsonInput.put("senderDesignation", "NA");
            jsonInput.put("receiverName", receiverName);
            jsonInput.put("receiverDesignation", designation);
            jsonInput.put("clearanceIssuedOn", clearanceIssuedOn);
            jsonInput.put("clearanceExpiredOn", "NA");
            jsonInput.put("licenseNo", "NA");
            jsonInput.put("clearanceFile", "NA");
            jsonInput.put("statusDate", statusDate);
            jsonInput.put("integrationSource", "LG");
            jsonInput.put("deemedApproval", "false");

            // Call the API with the constructed JSON payload
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", token);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonInput.toString().getBytes("utf-8"));
            }

            int responseCode = con.getResponseCode();
            
            InputStream inputStream = responseCode >= 200 && responseCode < 300 ? con.getInputStream() : con.getErrorStream();

            if (inputStream != null) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    log.info("EODB API Response: " + response.toString());
                }
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.info("EODB status update success.");
                return true;
            } else {
                log.error("EODB update failed. HTTP code: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            log.error("Exception while updating EODB status", e);
            return false;
        }
    }
}
