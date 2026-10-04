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

	@Value("${register.redirect.url}")
	private String registerRedirectURL;
	
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
	
	@Value("${register.client.id}")
	private String registerClientId;

	@Value("${register.client.secret}")
	private String registerClientSecret;

	@Value("${state}")
	private String state;

	@Value("${dl.flow}")
	private String dlFlow;

	
	@Value("${esign.responseurl}")
	private String responseUrl;
	
	@Value("${esign.redirecturl}")
	private String redirectUrl;
	
	@Value("${esign.licencefile}" )
	private String licenceFile;
	
	@Value("${esign.pfxpath}")
	private String pfxPath;
	
	@Value("${esign.pfxpassword}")
	private String pfxPassword;
	
	@Value("${esign.pfxallias}")
	private String pfxAllias;
	
	@Value("${esign.pdf}")
	private String pdf;
	
	@Value("${esign.tempfolder}")
	private String tempFolder;
	
	@Value("${esign.outputfolder}")
	private String outputFolder;
	
	@Value("${esign.UIURL}")
	private String uIURL;
		
	@Value("${persister.save.tl.esign.txns}")
	private String saveTLEsignTxnTopic;
	
	@Value("${persister.update.tl.esign.txns}")
	private String updateTLEsignTxnTopic;
	
	
	@Value("${egov.idgen.host}")
	private String IdGenHost;
	
	@Value("${egov.idgen.path}")
	private String IdGenPath;
	
	@Value("${egov.idgen.ack.format}")
	private String IdGenFormat;
	
	@Value("${egov.idgen.ack.name}")
	private String IdGenName;

	@Value("${egov.enc.host}")
	private String encHost;

	@Value("${egov.enc.encrypt.endpoint}")
	private String encEncryptURL;
	
	@Value("${egov.enc.decrypt.endpoint}")
	private String encDecryptURL;
	
	@Value("${egov.user.host}")
	private String UserHost;
	
	@Value("${egov.user.endpoint}")
	private String userEndpoint;
	
	
	@Value("${esign.aspId}")
	private String AspId;
	
	@Value("${esign.v1.url}")
	private String EsignV1Url;
	
	@Value("${esign.v2.url}")
	private String EsignV2Url;
	
	@Value("${esign.signature.content.size}")
	private int SignatureContentSize;

	@Value("${esign.gateway.url}")
	private String gatewayUrl;
	
	
	@Value("${filestore.host}")
	private String fileStoreUrl;
	
	@Value("${filestore.post.endpoint}")
	private String fileStorePostUrl;
	
	
}
