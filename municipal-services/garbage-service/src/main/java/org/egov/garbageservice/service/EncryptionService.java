package org.egov.garbageservice.service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.egov.garbageservice.model.EncReqObject;
import org.egov.garbageservice.model.EncryptionRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EncryptionService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GrbgConstants grbgConfig;

	@SuppressWarnings("unchecked")
	public String encryptString(String valueToEncrypt) {
		StringBuilder url = new StringBuilder(grbgConfig.getEncServiceHostUrl());
		url.append(grbgConfig.getEncEncryptEndpoint());

		EncryptionRequest encryptionRequest = EncryptionRequest
				.builder().encryptionRequests(Arrays.asList(EncReqObject.builder()
						.tenantId(grbgConfig.getStateLevelTenantId()).type("Imp").value(valueToEncrypt).build()))
				.build();

		ArrayList<String> encryptedValueResponse = null;
		String response = null;
		try {
			encryptedValueResponse = restTemplate.postForObject(url.toString(), encryptionRequest, ArrayList.class);
		} catch (Exception e) {
			log.error("Error occured while encrypt value.", e);
			throw new CustomException("ENCRYPTION ERROR",
					"Error occured while encrypt value. Message: " + e.getMessage());
		}
		if (!CollectionUtils.isEmpty(encryptedValueResponse)) {
			response = encryptedValueResponse.get(0);
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	public String decryptString(String valueToDecrypt) {
		StringBuilder url = new StringBuilder(grbgConfig.getEncServiceHostUrl());
		url.append(grbgConfig.getEncDecrypyEndpoint());
		ResponseEntity<Object> responseEntity = null;
		String response = null;

		try {
			valueToDecrypt = URLDecoder.decode(valueToDecrypt);
			String[] body = { valueToDecrypt };
			HttpEntity<String[]> entity = new HttpEntity<>(body);

			responseEntity = restTemplate.exchange(url.toString(), HttpMethod.POST, entity, Object.class);
		} catch (Exception e) {
			log.error("Error occured while decrypt value.", e);
			throw new CustomException("DECRYPTION ERROR",
					"Error occured while decrypt value. Message: " + e.getMessage());
		}
		if (null != responseEntity && null != responseEntity.getBody()) {
			ArrayList<String> decryptedValueResponse = (ArrayList<String>) responseEntity.getBody();
			response = decryptedValueResponse.get(0);
		}

		return response;
	}

}
