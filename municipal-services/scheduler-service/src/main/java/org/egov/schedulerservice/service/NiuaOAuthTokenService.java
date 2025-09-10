package org.egov.schedulerservice.service;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NiuaOAuthTokenService {

	@Value("${niua.oauth.token.url}")
	private String tokenUrl;

	@Value("${niua.oauth.authorization.header}")
	private String authorizationHeader;

	@Value("${niua.oauth.accept.header}")
	private String acceptHeader;

	@Value("${niua.oauth.username}")
	private String username;

	@Value("${niua.oauth.password}")
	private String password;

	@Value("${niua.oauth.grant_type}")
	private String grantType;

	@Value("${niua.oauth.scope}")
	private String scope;

	@Value("${niua.oauth.tenantId}")
	private String tenantId;

	@Value("${niua.oauth.userType}")
	private String userType;

	public Object requestNiuaOAuthToken() {
		RestTemplate restTemplate = new RestTemplate();

		try {
			// Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(MediaType.parseMediaTypes(acceptHeader));
			headers.set("authorization", authorizationHeader);

			// Body
			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("username", username);
			body.add("password", password);
			body.add("grant_type", grantType);
			body.add("scope", scope);
			body.add("tenantId", tenantId);
			body.add("userType", userType);
			
			log.info("Token url:{}",tokenUrl);
			log.info("Token request {}",body);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<Object> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity,
					Object.class);

			return response.getBody();

		} catch (Exception e) {
			log.error("Error while requesting NIUA OAuth token: " + e.getMessage());
			throw new CustomException("ERR_NIUA_AUTH_TOKEN_SERVICE", "Error while requesting NIUA OAuth token: ");
		}
	}

}