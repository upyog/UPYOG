package org.egov.dx.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class Configurations {


	@Value("${egov.mdms.host}")
	private String mdmsHost;
	
	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;
	
	

	// Collections service
	
	@Value("${egov.collectionservice.host}")
	private String collectionServiceHost;
	

	@Value("${egov.payment.search.endpoint}")
	private String	PaymentSearchEndpoint;
	

	
	@Value("${egov.pdf.host}")
	private String pdfServiceHost;
		

	@Value("${egov.pdf.search.endpoint}")
	private String	PdfSearchEndpoint;
	
	@Value("${egov.filestore.host}")
	private String filestoreHost;
		

	@Value("${egov.filestore.search.endpoint}")
	private String	filstoreSearchEndpoint;


}
