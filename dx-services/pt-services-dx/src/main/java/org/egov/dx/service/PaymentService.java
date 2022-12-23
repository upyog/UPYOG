package org.egov.dx.service;
import static org.egov.dx.util.CalculatorConstants.BUSINESSSERVICES_FIELD_FOR_SEARCH_URL;
import static org.egov.dx.util.CalculatorConstants.BUSINESSSERVICE_FIELD_FOR_FILESTORE_SEARCH_URL;
import static org.egov.dx.util.CalculatorConstants.CONSUMER_CODE_SEARCH_FIELD_NAME_PAYMENT;
import static org.egov.dx.util.CalculatorConstants.PROPERTY_TAX_SERVICE_CODE;
import static org.egov.dx.util.CalculatorConstants.RECEIPTNUMBER_FIELD_FOR_SEARCH_URL;
import static org.egov.dx.util.CalculatorConstants.SEPARATER;
import static org.egov.dx.util.CalculatorConstants.TENANT_ID_FIELD_FOR_SEARCH_URL;
import static org.egov.dx.util.CalculatorConstants.URL_PARAMS_SEPARATER;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.dx.repository.Repository;
import org.egov.dx.util.Configurations;
import org.egov.dx.web.models.Payment;
import org.egov.dx.web.models.PaymentResponse;
import org.egov.dx.web.models.PaymentSearchCriteria;
import org.egov.dx.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

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


	public List<Payment> getPayments(PaymentSearchCriteria criteria, RequestInfoWrapper requestInfoWrapper) {
		StringBuilder url = getPaymentSearchUrl(criteria);
		return mapper.convertValue(repository.fetchResult(url, requestInfoWrapper), PaymentResponse.class).getPayments();
	}


	public StringBuilder getPaymentSearchUrl(PaymentSearchCriteria criteria) {


		return new StringBuilder().append(configurations.getCollectionServiceHost())
				.append(configurations.getPaymentSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER).append(CONSUMER_CODE_SEARCH_FIELD_NAME_PAYMENT)
				.append(StringUtils.join(criteria.getConsumerCodes(),","))
				.append(SEPARATER)
				.append(BUSINESSSERVICES_FIELD_FOR_SEARCH_URL)
				.append(PROPERTY_TAX_SERVICE_CODE);
	}

	public Object getFilestore(RequestInfoWrapper requestInfoWrapper,
			String receiptNumber, String fileStoreId) throws IOException {
		
		StringBuilder host=new StringBuilder().append(configurations.getFilestoreHost()).append(configurations.getFilstoreSearchEndpoint())
				.append(fileStoreId);

		return restTemplate.getForObject(host.toString(),Object.class);

	}


	public StringBuilder getFilestoreSearchUrl(PaymentSearchCriteria criteria, String receiptNumber) {
		return new StringBuilder().append(configurations.getPdfServiceHost())
				.append(configurations.getPdfSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER).append(RECEIPTNUMBER_FIELD_FOR_SEARCH_URL)
				.append(receiptNumber)
				.append(SEPARATER)
				.append(BUSINESSSERVICE_FIELD_FOR_FILESTORE_SEARCH_URL)
				.append(PROPERTY_TAX_SERVICE_CODE);
	}


}
