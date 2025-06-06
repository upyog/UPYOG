package org.egov.pt.repository.rowmapper;

import org.egov.pt.config.PropertyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Service
public class CurlWrapperService {
	
	@Autowired
    private PropertyConfiguration config;
	
	
    public String fetchData(String ulb, String uidNo) {
        
    	StringBuilder urlString = new StringBuilder(config.getThirdPartyhost());
    	urlString.append(config.getThirdPartysubUrl())
    	         .append("?ULB=")
    	         .append(ulb)
    	         .append("&UIDNo=")
    	         .append(uidNo);


        String authHeader = "Basic "+config.getThirdpartykey(); 

        try {
            URL url = new URL(urlString.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json, text/plain, */*");
            connection.setRequestProperty("Authorization", authHeader); 


            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); 

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                return "GET request failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
