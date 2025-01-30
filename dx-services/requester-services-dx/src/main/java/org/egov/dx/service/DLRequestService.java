package org.egov.dx.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.egov.common.contract.request.RequestInfo;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.AuthResponse;
import org.egov.dx.web.models.CreateUserRequest;
import org.egov.dx.web.models.DecReqObject;
import org.egov.dx.web.models.DigiUser;
import org.egov.dx.web.models.EncReqObject;
import org.egov.dx.web.models.EncryptionRequest;
import org.egov.dx.web.models.IssuedDocument;
import org.egov.dx.web.models.IssuedDocumentList;
import org.egov.dx.web.models.TokenReq;
import org.egov.dx.web.models.TokenRes;
import org.egov.dx.web.models.User;
import org.egov.dx.web.models.UserRequest;
import org.egov.dx.web.models.UserRes;
import org.egov.dx.web.models.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.ipc.http.HttpSender.Request;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Service
@Slf4j
public class DLRequestService {

      
    private static final User User = null;

	@Autowired
    private RestTemplate restTemplate;

    @Autowired
	private Configurations configurations;
    

   
    public URI getRedirectionURL(String module,AuthResponse authResponse) throws NoSuchAlgorithmException
    {
    	 MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	 params.add("response_type", configurations.getResponseType());
    	 params.add("state", configurations.getState());
    	 if(module.equalsIgnoreCase("SSO")) {
        	 params.add("redirect_uri", configurations.getRegisterRedirectURL());
    	 	 params.add("client_id", configurations.getRegisterClientId());}

    	 else {
    		 params.add("redirect_uri", configurations.getPtRedirectURL());
    	 	 params.add("client_id", configurations.getClientId());
    	 }
    	 params.add("code_challenge",getCodeChallenge(authResponse));
    	 params.add("code_challenge_method", "S256");
         params.add("dl_flow", configurations.getDlFlow());
         UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(configurations.getAuthorizationURL()).queryParams(params)
                .build();

    	
    	
    	return uriComponents.toUri();
    }

    public String getCodeChallenge(AuthResponse authResponse) throws NoSuchAlgorithmException   
    {     
    	String codeVerifier=getCodeVerifier();     
    	log.info("verifier is: " +codeVerifier ); 
    	//authResponse.setDlReqRef(codeVerifier);  
    	MessageDigest digest = MessageDigest.getInstance("SHA-256");     
    	byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));     
    	String encoded = Base64.getEncoder().withoutPadding().encodeToString(hash);     
    	encoded = encoded.replace("+", "-"); //Replace ’+’ with ’-’     
    	encoded= encoded.replace("/", "_");     
    	log.info("challenge is: " +encoded );        
    	EncReqObject encReqObject = EncReqObject.builder().tenantId("pg").type("Normal").value(codeVerifier).build();        
    	EncryptionRequest encryptionRequest = EncryptionRequest.builder().encryptionRequests(Collections.singletonList(encReqObject)).build();        
    	String responseBody= restTemplate.postForEntity(configurations.getEncHost() + configurations.getEncEncryptURL(), encryptionRequest, String.class).getBody();     
        try {
       	    String value = new ObjectMapper().readValue(responseBody, String[].class)[0];
       	 authResponse.setDlReqRef(value);
       	} catch (Exception e) {
       	    e.printStackTrace();
       	}
    	return encoded;              
    	}
    
    public String getCodeVerifier()
    {
    	 int leftLimit = 45; // numeral '0'
    	    int rightLimit = 126; // letter 'z'
    	    int targetStringLength = 60;
    	    Random random = new Random();

    	    String generatedString = random.ints(leftLimit, rightLimit + 1)
    	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 95) && i!=47 && i!=96 && i!=123 && i!=124 && i!=125)
    	      .limit(targetStringLength)
    	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
    	      .toString();

    	    return generatedString;
    }
    
    public TokenRes getToken(TokenReq tokenReq)
    {
    	
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         //headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
         MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
         map.add("code", tokenReq.getCode());
         map.add("grant_type", "authorization_code");
    	 if(tokenReq.getModule().equalsIgnoreCase("SSO")) {
	         map.add("client_id", configurations.getRegisterClientId());
	         map.add("redirect_uri", configurations.getRegisterRedirectURL());
	         map.add("client_secret", configurations.getRegisterClientSecret());
         }
    	 else
    	 {
    		 map.add("client_id", configurations.getClientId());
    	     map.add("redirect_uri", configurations.getPtRedirectURL());
    	     map.add("client_secret", configurations.getClientSecret());
    	 }
         //map.add("code_verifier",tokenReq.getDlReqRef());

         List<String> decReqObject = Collections.singletonList(tokenReq.getDlReqRef());
         HttpEntity<String> requestEntity = decryptReq(decReqObject);
        
         String responseBody = restTemplate.postForEntity(configurations.getEncHost() + configurations.getEncDecryptURL(), requestEntity, String.class).getBody();
         try {
        	    String value = new ObjectMapper().readValue(responseBody, String[].class)[0];
        	    map.add("code_verifier", value);
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
                 headers);
         
         TokenRes tokenRes = restTemplate.postForEntity(configurations.getApiHost() + configurations.getTokenOauthURI(), request, TokenRes.class).getBody();         
         return tokenRes;
    }
    
    
    public UserRes getUser(TokenReq tokenReq)
    {
    	
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         headers.set("Authorization", "Bearer "+tokenReq.getAuthToken());
      

         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
                 headers);
         UserRes userRes= restTemplate.postForEntity(configurations.getApiHost() + configurations.getUserOauthURI(), request, UserRes.class).getBody();
         return userRes;
    }

    public  List<IssuedDocument> getIssuedDocument(TokenReq tokenReq)
    {
    	
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         headers.set("Authorization", "Bearer "+tokenReq.getAuthToken());
         
         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
                 headers);
         IssuedDocumentList issuedDocumentList= restTemplate.postForEntity(configurations.getApiHost() + configurations.getIssuedFilesURI(), request, IssuedDocumentList.class).getBody();
         return issuedDocumentList.getItems();
    }
    public byte[] getDoc(TokenReq tokenReq,String uri)
    {
    	
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         headers.set("Authorization", "Bearer "+tokenReq.getAuthToken());
         headers.set("Authorization", "Bearer "+tokenReq.getAuthToken());
         MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

         map.add("uri",uri);
      

         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
                 headers);
         //MultipartFile doc= restTemplate.getForEntity(configurations.getApiHost() + configurations.getGetFileURI()+"/"+uri, request);
         ResponseEntity<String> entity = restTemplate.exchange(configurations.getApiHost() + configurations.getGetFileURI()+"/"+uri, HttpMethod.GET, 
                 request, String.class);
               
         
         return entity.getBody().getBytes();
    }

    public Object getOauthToken(RequestInfo requestinfo , TokenRes tokenRes)
    {
        RestTemplate restTemplate = new RestTemplate();
        UserRequest user = new UserRequest();
        user.setMobileNumber(tokenRes.getMobile());
        user.setName(tokenRes.getName());
        user.setDigilockerid(tokenRes.getDigilockerId());
        user.setTenantId("pg");
        user.setAccess_token(tokenRes.getAccessToken());
        //user.setDob(tokenRes.getDob());
        
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setRequestInfo(requestinfo);
        createUserRequest.setUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        Object userOauth= restTemplate.postForEntity(configurations.getUserHost() + configurations.getUserEndpoint(), createUserRequest, Object.class).getBody();
        return userOauth;
    }
    
    public HttpEntity<String> decryptReq(List<String> decReqObject){
        HttpHeaders decryptHeaders = new HttpHeaders();
        decryptHeaders.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = null;
        try {
            jsonPayload = objectMapper.writeValueAsString(decReqObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new HttpEntity<String>(jsonPayload, decryptHeaders);

    }
}
