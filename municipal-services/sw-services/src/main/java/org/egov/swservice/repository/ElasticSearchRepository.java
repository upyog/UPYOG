package org.egov.swservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.egov.swservice.config.SWConfiguration;
import org.egov.swservice.repository.builder.SWFuzzySearchQueryBuilder;
import org.egov.swservice.web.models.SearchCriteria;
import org.egov.tracer.model.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ElasticSearchRepository {

    private SWConfiguration config; // Updated to SW

    private SWFuzzySearchQueryBuilder queryBuilder; // Updated to SW

    private RestTemplate restTemplate;

    private ObjectMapper mapper;

    @Autowired
    public ElasticSearchRepository(SWConfiguration config, SWFuzzySearchQueryBuilder queryBuilder, 
                                   RestTemplate restTemplate, ObjectMapper mapper) {
        this.config = config;
        this.queryBuilder = queryBuilder;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /**
     * Searches records from elasticsearch based on the fuzzy search criteria for Sewerage Connections
     *
     * @param criteria SewerageConnection search criteria
     * @return Object (ElasticSearch Response Body)
     */
    public Object fuzzySearchForConnections(SearchCriteria criteria) {

        String url = getESURL();

        // Generates the JSON query string for ES using the SW Query Builder
        String searchQuery = queryBuilder.getFuzzySearchQuery(criteria);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (config.getElasticsearchUsername() != null && !config.getElasticsearchUsername().isEmpty() &&
            config.getElasticsearchPassword() != null && !config.getElasticsearchPassword().isEmpty()) {
            headers.setBasicAuth(config.getElasticsearchUsername(), config.getElasticsearchPassword());
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(searchQuery, headers);

        log.info("ES SW Search URL: {} | Request Body: {}", url, searchQuery);

        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.postForEntity(url, requestEntity, Object.class);
        } catch (Exception e) {
            log.error("Error occurred while fetching data from ElasticSearch for SW", e);
            throw new CustomException("ES_ERROR", "Failed to fetch data from ES for Sewerage Connections");
        }

        return response != null ? response.getBody() : null;
    }

    /**
     * Generates elasticsearch search url from sewerage-service application properties
     *
     * @return Full ES search URL
     */
    private String getESURL() {

        StringBuilder builder = new StringBuilder(config.getElasticsearchHost());
        
        // Ensure SWConfiguration has the property for the sewerage index name
        builder.append(config.getSwIndex()); 
        builder.append(config.getElasticsearchSearchEndpoint());

        return builder.toString();
    }
}