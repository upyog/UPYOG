package org.egov.pg.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.BankAccountResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankAccountService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AppProperties appProperties;

	public BankAccountResponse searchBankAccount(Set<String> tenantIds, RequestInfo requestInfo) {
		StringBuilder url = new StringBuilder(appProperties.getEgfMasterHost());
		url.append(appProperties.getEgfMasterBankaccountSearchEndpoint());

		url.append("?tenantIds=").append(tenantIds.stream().collect(Collectors.joining(", ")));

		BankAccountResponse bankAccountResponse = null;
		try {
			bankAccountResponse = restTemplate.postForObject(url.toString(), requestInfo, BankAccountResponse.class);
		} catch (Exception e) {
			log.error("Error occured while bank account search.", e);
			throw new CustomException("BANK_ACCOUNT_SEARCH_ERROR",
					"Error occured while bank account search. Message: " + e.getMessage());
		}

		return bankAccountResponse;
	}

}
