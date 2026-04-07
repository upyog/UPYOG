package org.egov.dx.service;

import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.UserResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

      
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
	private Configurations configurations;
    
    public UserResponse getUser() {
    	 log.info("Fetch access token for register with login flow");
         try {
             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
             headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0");
             MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
             map.add("username", "EMP11");
             map.add("password", "EMP11");
             map.add("grant_type", "password");
             map.add("scope", "read");
             map.add("tenantId","pb.testing");
             map.add("isInternal", "true");
             map.add("userType", "EMPLOYEE");

             HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
                     headers);
             UserResponse userResponse= restTemplate.postForEntity(configurations.getUserHost() + configurations.getUserSearchEndPoint(), request, UserResponse.class).getBody();
            return userResponse;
             
         } catch (Exception e) {
             log.error("Error occurred while logging-in via register flow", e);
             throw new CustomException("LOGIN_ERROR", "Error occurred while logging in via register flow: " + e.getMessage());
         }

    }
    
   

}
