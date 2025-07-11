package org.egov.finance.voucher.util;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.voucher.entity.Department;
import org.egov.finance.voucher.model.ApplicationConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MicroserviceUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroserviceUtils.class);

	private static final String CLIENT_ID = "client.id";
	private static final int DEFAULT_PAGE_SIZE = 100;

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ApplicationConfigManager appConfigManager;

	/*---- SI user details-----*/
	@Value("${token.authorization.key}")
	private String tokenAuthorizationKey;

	@Value("${si.microservice.user}")
	private String siUser;

	@Value("${si.microservice.password}")
	private String siPassword;

	@Value("${si.microservice.usertype}")
	private String siUserType;

	@Value("${si.microservice.scope}")
	private String siScope;

	@Value("${si.microservice.granttype}")
	private String siGrantType;

	@Value("${egov.services.user.token.url}")
	private String tokenGenUrl;

	public String getTenentId() {
		environment.getProperty(CLIENT_ID);
		String userTenantId = ApplicationThreadLocals.getUserTenantId();
		String tenantId = ApplicationThreadLocals.getTenantID();
		// if (isNotBlank(clientId)) {
		// final StringBuilder stringBuilder = new StringBuilder();
		// stringBuilder.append(clientId).append('.').append(tenantId);
		// tenantId = stringBuilder.toString();
		// }

		// If tenantId already includes ".", assume it's in full format and return it
		// directly
		if (StringUtils.isNotBlank(tenantId) && tenantId.contains(".")) {
			return tenantId;
		}

		// If userTenantId is blank, default to "mn"
		if (StringUtils.isBlank(userTenantId)) {
			userTenantId = "mn";
		}

		// If tenantId is not blank, combine with userTenantId
		if (StringUtils.isNotBlank(tenantId)) {
			return userTenantId + "." + tenantId;
		}

		// Fallback: only userTenantId is available
		return userTenantId;
	}

	public String generateAdminToken(String tenantId) {
		final RestTemplate restTemplate = createRestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		header.add("Authorization", this.tokenAuthorizationKey);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", this.siUser);
		map.add("scope", this.siScope);
		map.add("password", this.siPassword);
		map.add("grant_type", this.siGrantType);
		map.add("tenantId", tenantId);
		map.add("userType", this.siUserType);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, header);
		try {
			StringBuilder url = new StringBuilder(appConfigManager.getEgovUserSerHost()).append(tokenGenUrl);
			LOGGER.info("call:" + url);
			Object response = restTemplate.postForObject(url.toString(), request, Object.class);
			if (response != null)
				return String.valueOf(((HashMap) response).get("access_token"));
		} catch (RestClientException e) {
			LOGGER.info("Eror while getting admin authtoken", e);
			return null;
		}
		return null;
	}

	public RestTemplate createRestTemplate() {

		return restTemplate;
	}

	

}
