package org.egov.service;

import java.util.LinkedHashMap;

import org.egov.utils.JsonPathConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SignOutService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${egov.coexistence.hostname}")
	private String coexistencehost;

	@Value("${egov.coexistence.singout.uri}")
	private String coexistencelogoutUri;

	public void callFinanceForSignOut(DocumentContext documentContext) {
		log.info("Calling the funcation callFinanceForSignOut");
		ResponseEntity<?> response = null;
		String accessToken = documentContext.read(JsonPathConstant.signOutAccessToken);
		log.info("accessToken:::"+accessToken);
		documentContext = documentContext.delete(JsonPathConstant.userInfo);
		documentContext = documentContext.put(JsonPathConstant.requestInfo, "authToken", accessToken);
		LinkedHashMap<String, Object> jsonRequest = documentContext.read(JsonPathConstant.request);
		log.info("Before Finance logout API is calling"+ coexistencehost + coexistencelogoutUri);
		response = restTemplate.exchange(coexistencehost + coexistencelogoutUri, HttpMethod.POST,
				new HttpEntity<>(jsonRequest), ResponseEntity.class);
		log.info("After Finance logout API is calling"+ coexistencehost + coexistencelogoutUri);
		log.info("SignOutService response :" + response.getStatusCode());
	}

}
