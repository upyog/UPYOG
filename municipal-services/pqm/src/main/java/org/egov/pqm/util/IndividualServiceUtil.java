package org.egov.pqm.util;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkResponse;
import org.egov.common.models.individual.IndividualSearch;
import org.egov.common.models.individual.IndividualSearchRequest;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IndividualServiceUtil {

    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MultiStateInstanceUtil multiStateInstanceUtil;

    public List<String> fetchIndividualIds(List<String> individualIds, RequestInfo requestInfo, String tenantId) {

        List<Individual> individualList = getIndividualDetails(individualIds, requestInfo, tenantId);

        List<String> ids = null;
        try {
            ids = individualList.stream().map(Individual::getId).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse Individual service response");
        }
        log.info("Individual search fetched successfully");
        return ids;
    }

    public List<Individual> getIndividualDetails(List<String> individualIds, RequestInfo requestInfo, String tenantId) {

        String uri = getSearchURLWithParams(tenantId).toUriString();

        IndividualSearch individualSearch = IndividualSearch.builder().id(individualIds).build();
        IndividualSearchRequest individualSearchRequest = IndividualSearchRequest.builder()
                .requestInfo(requestInfo).individual(individualSearch).build();

        IndividualBulkResponse response = null;
        log.info("call individual search with tenantId::" + tenantId + "::individual ids::" + individualIds);

        try {
            response = restTemplate.postForObject(uri, individualSearchRequest, IndividualBulkResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
            log.error("Error thrown from individual search service::" + httpClientOrServerExc.getStatusCode());
            throw new CustomException("INDIVIDUAL_SEARCH_SERVICE_EXCEPTION", "Error thrown from individual search service::" + httpClientOrServerExc.getStatusCode());
        }
        if (response == null || CollectionUtils.isEmpty(response.getIndividual())) {
            throw new CustomException("INDIVIDUAL_SEARCH_RESPONSE_IS_EMPTY", "Individuals not found");
        }

        return response.getIndividual();
    }

    private UriComponentsBuilder getSearchURLWithParams(String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getIndividualHost()).append(config.getIndividualSearchEndpoint());
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri.toString())
                .queryParam("limit", 100)
                .queryParam("offset", 0)
                .queryParam("tenantId", tenantId);

        return uriBuilder;
    }

    public List<Individual> getIndividualDetailsFromUserId(Long userId, RequestInfo requestInfo, String tenantId) {
        String uri = getSearchURLWithParams(multiStateInstanceUtil.getStateLevelTenant(tenantId)).toUriString();
        IndividualSearch individualSearch = IndividualSearch.builder().userId(userId).build();
        IndividualSearchRequest individualSearchRequest = IndividualSearchRequest.builder()
                .requestInfo(requestInfo).individual(individualSearch).build();

        IndividualBulkResponse response = null;
        log.info("call individual search with tenantId::" + tenantId + "::user id::" + userId);

        try {
            response = restTemplate.postForObject(uri, individualSearchRequest, IndividualBulkResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
            log.error("Error thrown from individual search service::" + httpClientOrServerExc.getStatusCode());
            throw new CustomException("INDIVIDUAL_SEARCH_SERVICE_EXCEPTION", "Error thrown from individual search service::" + httpClientOrServerExc.getStatusCode());
        }
        if (response == null || CollectionUtils.isEmpty(response.getIndividual())) {
            throw new CustomException("INDIVIDUAL_SEARCH_RESPONSE_IS_EMPTY", "Individuals not found");
        }

        return response.getIndividual();
    }
}
