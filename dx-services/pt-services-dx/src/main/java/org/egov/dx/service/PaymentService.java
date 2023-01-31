package org.egov.dx.service;
import static org.egov.dx.util.PTServiceDXConstants.BUSINESSSERVICES_FIELD_FOR_SEARCH_URL;
import static org.egov.dx.util.PTServiceDXConstants.CONSUMER_CODE_SEARCH_FIELD_NAME_PAYMENT;
import static org.egov.dx.util.PTServiceDXConstants.KEY;
import static org.egov.dx.util.PTServiceDXConstants.PDF_KEY_PT;
import static org.egov.dx.util.PTServiceDXConstants.SEPARATER;
import static org.egov.dx.util.PTServiceDXConstants.STATE_TENANT;
import static org.egov.dx.util.PTServiceDXConstants.TENANT_ID_FIELD_FOR_SEARCH_URL;
import static org.egov.dx.util.PTServiceDXConstants.URL_PARAMS_SEPARATER;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.dx.repository.Repository;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.FileStoreResponse;
import org.egov.dx.web.models.Payment;
import org.egov.dx.web.models.PaymentRequest;
import org.egov.dx.web.models.PaymentResponse;
import org.egov.dx.web.models.PaymentSearchCriteria;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	@Autowired
	private Repository repository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Configurations configurations;


	public List<Payment> getPayments(PaymentSearchCriteria criteria, String docType,RequestInfoWrapper requestInfoWrapper) {
		StringBuilder url = getPaymentSearchUrl(criteria,docType);
		return mapper.convertValue(repository.fetchResult(url, requestInfoWrapper), PaymentResponse.class).getPayments();
	}


	public StringBuilder getPaymentSearchUrl(PaymentSearchCriteria criteria, String docType) {
		String moduleName=getModule(docType);

		return new StringBuilder().append(configurations.getCollectionServiceHost())
				.append(configurations.getPaymentSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER).append(CONSUMER_CODE_SEARCH_FIELD_NAME_PAYMENT)
				.append(StringUtils.join(criteria.getConsumerCodes(),","))
				.append(SEPARATER)
				.append(BUSINESSSERVICES_FIELD_FOR_SEARCH_URL)
				.append(moduleName);
	}
	
	public String getModule(String docType)
	{
		if(docType.equals("PRTAX"))
				return "PT";
		
		return "PT";
				
	}

	public Object getFilestore(String fileStoreId) throws IOException {
		log.info("fetching document from filestore id::::");
		StringBuilder host=new StringBuilder().append(configurations.getFilestoreHost()).append(configurations.getFilstoreSearchEndpoint())
				.append(fileStoreId);

		return restTemplate.getForObject(host.toString(),Object.class);

	}

	public String createPDF(PaymentRequest paymentRequest) {
		StringBuilder url = new StringBuilder().append(configurations.getPdfServiceHost())
				.append(configurations.getPdfServiceCreate())
				.append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(STATE_TENANT)
				.append(SEPARATER).append(KEY)
				.append(PDF_KEY_PT);
		return mapper.convertValue(repository.fetchResult(url, paymentRequest), FileStoreResponse.class).getFilestoreIds().get(0);
	}


//	public StringBuilder getFilestoreSearchUrl(PaymentSearchCriteria criteria,String docType, String receiptNumber) {
//		String moduleName=getModule(docType);
//
//		return new StringBuilder().append(configurations.getPdfServiceHost())
//				.append(configurations.getPdfSearchEndpoint()).append(URL_PARAMS_SEPARATER)
//				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
//				.append(SEPARATER).append(RECEIPTNUMBER_FIELD_FOR_SEARCH_URL)
//				.append(receiptNumber)
//				.append(SEPARATER)
//				.append(BUSINESSSERVICE_FIELD_FOR_FILESTORE_SEARCH_URL)
//				.append(moduleName);
//	}


}
