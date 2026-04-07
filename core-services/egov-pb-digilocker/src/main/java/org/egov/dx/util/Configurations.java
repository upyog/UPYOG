package org.egov.dx.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class Configurations {

@Value("${egov.payer.validation.enable}")
	private String validationFlag;

	@Value("${egov.digilocker.origin}")
	private String digilockerOrigin;

	@Value("${egov.digilocker.issuer.id}")
	private String digilockerIssuerId;

	@Value("${egov.digilocker.doctype.prtax:PRTAX}")
	private String prtaxDocType;

	@Value("${egov.pt.language.code:99}")
	private String languageCode;

	@Value("${egov.pt.issuer.org.name}")
	private String issuerOrgName;

	@Value("${egov.pt.issuer.pin}")
	private String issuerPin;

	@Value("${egov.pt.person.state:Punjab}")
	private String personState;

	// Existing props...

	@Value("${egov.searchservice.host}")
	private String searchServiceHost;
	
	@Value("${egov.searchservice.ws.endpoint}")
	private String searchWsEndpoint;
	
	@Value("${egov.searchservice.sw.endpoint}")
	private String searchSwEndpoint;
	
	
	@Value("${egov.ws.search.endpoint}")
	private String searchWSConnEndpoint;
	
	@Value("${egov.sw.search.endpoint}")
	private String searchSwConnEndpoint;
	
	@Value("${egov.property.search.host}")
	private String serachPropertyHost;
	
	@Value("${egov.property.search.endpoint}")
	private String searchPropertyEndpoint;
	
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
	
	@Value("${egov.ws.host}")
    private String wsHost;
	
	@Value("${egov.sw.host}")
    private String swHost;
}
