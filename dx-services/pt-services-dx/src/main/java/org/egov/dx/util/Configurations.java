package org.egov.dx.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class Configurations {

	@Value("${egov.payer.validation.enable}")
	private String validationFlag;

	
	@Value("${egov.collectionservice.host}")
	private String collectionServiceHost;
	
	@Value("${egov.payment.search.endpoint}")
	private String	PaymentSearchEndpoint;

	
	@Value("${egov.filestore.host}")
	private String filestoreHost;
		
	@Value("${egov.filestore.search.endpoint}")
	private String	filstoreSearchEndpoint;

	@Value("${egov.services.hostname}")
	private String	pdfServiceHost;
	
	@Value("${egov.pdf.service.create}")
	private String	pdfServiceCreate;
	
	@Value("${user.service.host}")
	private String userHost;
	
	@Value("${user.search.endpoint}")
	private String userSearchEndPoint;

	@Value("${egov.integration.system.user.uuid}")
	private String authTokenVariable;
	
	@Value("${egov.mdms.host}")
	private String mdmsHost;
	
	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;
	
	
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
