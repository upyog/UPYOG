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
}
