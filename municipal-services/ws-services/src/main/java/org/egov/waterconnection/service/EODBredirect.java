package org.egov.waterconnection.service;

import org.egov.waterconnection.web.models.WaterConnectionRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
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
	
	
	
    public boolean runEodbFlow(WaterConnectionRequest connection) {
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


    private boolean updateStatusWithToken(String token, WaterConnectionRequest connection) {
        String apiUrl = updateUrl;

        // Extract additionalDetails from the connection (assuming it is a Map)
        Object additionalDetailsObject = connection.getWaterConnection().getAdditionalDetails();  // This should be a Map<String, Object>

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
        statusIdMap.put("INITIATED", 1);  // Form Submitted (Created)
        statusIdMap.put("PENDING_FOR_CONNECTION_ACTIVATION", 2);  // Payment Raised (Estimation Notice Generated)
        statusIdMap.put("REJECTED", 3);  // Rejected
        statusIdMap.put("CONNECTION_ACTIVATED", 4);  // Clearance Issued
        //statusIdMap.put("PENDING_FOR_CONNECTION_ACTIVATION", 5);  // Fees Paid (Estimation amount paid)
        statusIdMap.put("PENDING_FOR_CITIZEN_ACTION", 6);  // Objection Raised
        statusIdMap.put("PENDING_FOR_DOCUMENT_VERIFICATION", 7);  // Form Filled and Fee Pending
        statusIdMap.put("PENDING_FOR_PAYMENT", 8);  // Form Filled and Fee Pending
        statusIdMap.put("PENDING_FOR_FIELD_INSPECTION", 9);  // Pending for on-site inspection
        statusIdMap.put("PENDING_APPROVAL_FOR_CONNECTION", 10);  // Inspection done, pending for final approval

        statusDescMap.put("INITIATED", "Form Submitted (Created)");
        statusDescMap.put("PENDING_FOR_CONNECTION_ACTIVATION", "Payment Raised (Estimation Notice Generated)");
        statusDescMap.put("REJECTED", "Rejected");
        statusDescMap.put("CONNECTION_ACTIVATED", "Clearance Issued");
      //  statusDescMap.put("PENDING_FOR_CONNECTION_ACTIVATION", "Fees Paid (Estimation amount paid)");
        statusDescMap.put("PENDING_FOR_CITIZEN_ACTION", "Objection Raised");
        statusDescMap.put("PENDING_FOR_DOCUMENT_VERIFICATION", "Form Filled and Fee Pending");
        statusDescMap.put("PENDING_FOR_PAYMENT", "Form Filled and Fee Pending");
        statusDescMap.put("PENDING_FOR_FIELD_INSPECTION", "Pending for on-site inspection");
        statusDescMap.put("PENDING_APPROVAL_FOR_CONNECTION", "Inspection done, pending for final approval");

        
        
        // Get the status from the connection and find the corresponding StatusId and StatusDesc
        String applicationStatus = connection.getWaterConnection().getApplicationStatus();

        // Check if a valid status exists in the map
        Integer statusId = statusIdMap.get(applicationStatus);
        String statusDesc = statusDescMap.get(applicationStatus);

        // If the statusId or statusDesc is not found, return false
        if (statusId == null || statusDesc == null) {
            log.error("Invalid applicationStatus: " + applicationStatus);
            return false;
        }
        
        try {
            // Create the JSON payload for the API request
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("iPin", iPin);  
            jsonInput.put("AppId", appId);  
            jsonInput.put("statusId", statusId);
            jsonInput.put("statusDesc", statusDesc);
            jsonInput.put("comments", "NA");
            jsonInput.put("senderName", connection.getRequestInfo().getUserInfo().getName());
            jsonInput.put("senderDesignation", "NA");
            jsonInput.put("receiverName", "NA");
            jsonInput.put("receiverDesignation", "NA");
            jsonInput.put("clearanceIssuedOn", "NA");
            jsonInput.put("clearanceExpiredOn", "NA");
            jsonInput.put("licenseNo", "NA");
            jsonInput.put("clearanceFile", "NA");
            jsonInput.put("statusDate", LocalDate.now().toString());
            jsonInput.put("integrationSource", "LG");
            jsonInput.put("deemedApproval", "false");

            // Call the API with the constructed JSON payload
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonInput.toString().getBytes("utf-8"));
            }

            int responseCode = con.getResponseCode();
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
