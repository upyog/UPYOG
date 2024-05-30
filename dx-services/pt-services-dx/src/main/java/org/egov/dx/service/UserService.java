package org.egov.dx.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.AuthResponse;
import org.egov.dx.web.models.IssuedDocument;
import org.egov.dx.web.models.IssuedDocumentList;
import org.egov.dx.web.models.TokenReq;
import org.egov.dx.web.models.TokenRes;
import org.egov.dx.web.models.UserRes;
import org.egov.dx.web.models.UserResponse;
import org.egov.tracer.model.CustomException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
             headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
             MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
             map.add("username", "PTUATCE3");
             map.add("password", "eGov@123");
             map.add("grant_type", "password");
             map.add("scope", "read");
             map.add("tenantId","pg.citya");
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
    
   
    public URI getRedirectionURL(String module,AuthResponse authResponse) throws NoSuchAlgorithmException
    {
    	 MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	 params.add("response_type", configurations.getResponseType());
    	 params.add("client_id", configurations.getClientId());
    	 params.add("state", configurations.getState());
    	 params.add("redirect_uri", configurations.getPtRedirectURL());
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
    	authResponse.setCodeverifier(codeVerifier);
    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
    	byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
    	String encoded = Base64.getEncoder().withoutPadding().encodeToString(hash);
    	encoded = encoded.replace("+", "-"); //Replace ’+’ with ’-’
    	encoded= encoded.replace("/", "_");
    	log.info("challenge is: " +encoded );
    	
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
         map.add("client_id", configurations.getClientId());
         map.add("redirect_uri", configurations.getPtRedirectURL());
         map.add("code_verifier",tokenReq.getCodeVerifier());
         map.add("client_secret", configurations.getClientSecret());

         HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
                 headers);
         TokenRes tokenRes= restTemplate.postForEntity(configurations.getApiHost() + configurations.getTokenOauthURI(), request, TokenRes.class).getBody();
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
}
