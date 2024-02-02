package org.egov.dx.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class Configurations {

	@Value("${egov.payer.validation.enable}")
	private String validationFlag;

	@Value("${egov.integration.system.user.uuid}")
	private String authTokenVariable;
		
	@Value("${authorization.url}")
	private String authorizationURL;

	@Value("${pt.redirect.url}")
	private String ptRedirectURL;

	@Value("${api.host}")
	private String apiHost;

	@Value("${token.oauth.uri}")
	private String tokenOauthURI;
	
	@Value("${user.oauth.uri}")
	private String userOauthURI;

	@Value("${issued.files.uri}")
	private String issuedFilesURI;

	@Value("${get.file.uri}")
	private String getFileURI;

	@Value("${response.type}")
	private String responseType;

	@Value("${client.id}")
	private String clientId;

	@Value("${client.secret}")
	private String clientSecret;

	@Value("${state}")
	private String state;

	@Value("${dl.flow}")
	private String dlFlow;

	
}
