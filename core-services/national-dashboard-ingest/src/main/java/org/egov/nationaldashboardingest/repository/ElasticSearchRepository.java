package org.egov.nationaldashboardingest.repository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.utils.IngestConstants;
import org.egov.nationaldashboardingest.web.models.ProducerPOJO;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ElasticSearchRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private Producer producer;

    public void indexFlattenedDataToES(Map<String, List<String>> indexNameVsDocumentsToBeIndexed) {
        StringBuilder bulkRequestBody = new StringBuilder();

        // Conversion of multi-index request to a single request to avoid repetitive REST calls to ES.
        indexNameVsDocumentsToBeIndexed.keySet().forEach(indexName -> {
            String actionMetaData;
        	if(applicationProperties.getIsLegacyVersionES())
        		actionMetaData = String.format("{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"_doc\" } }%n", indexName);
        	else
        		actionMetaData = String.format("{ \"index\" : { \"_index\" : \"%s\" } }%n", indexName);
            for (String document : indexNameVsDocumentsToBeIndexed.get(indexName)) {
                bulkRequestBody.append(actionMetaData);
                bulkRequestBody.append(document);
                bulkRequestBody.append("\n");
            }
        });

        // Persisting flattened data to ES.
        try {
            HttpHeaders headers = getHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(bulkRequestBody.toString(), headers);
            StringBuilder uri = new StringBuilder(applicationProperties.getElasticSearchHost() + IngestConstants.BULK_ENDPOINT);
            Object response = restTemplate.postForEntity(uri.toString(), httpEntity, Map.class);
            String res = objectMapper.writeValueAsString(response);
            JsonNode responseNode = objectMapper.readValue(res, JsonNode.class);
            log.info("RESPONSE FROM ES: " + responseNode.toString());
            Boolean errorWhileIndexingData = responseNode.get(IngestConstants.BODY).get(IngestConstants.ERRORS).asBoolean();
            if(errorWhileIndexingData)
                throw new CustomException("EG_ES_IDX_ERR", "Error occurred while indexing data onto ES. Please ensure input data fields are in accordance with index mapping.");
        }catch (ResourceAccessException e){
            log.error("ES is down");
            throw new CustomException("EG_ES_ERR", "Elastic search is down");
        }catch (Exception e){
            log.error("Exception while indexing data onto ES.");
            throw new CustomException("EG_ES_IDX_ERR", e.getMessage());
        }
    }

    public Integer findIfRecordAlreadyExists(StringBuilder uri) {
        Integer recordsFound = 0;
        try {
            Object response = restTemplate.getForEntity(uri.toString(), Map.class);
            String res = objectMapper.writeValueAsString(response);
            JsonNode responseNode = objectMapper.readValue(res, JsonNode.class);
            log.info(responseNode.get(IngestConstants.BODY).toString());
            recordsFound = responseNode.get(IngestConstants.BODY).get(IngestConstants.HITS).get(IngestConstants.TOTAL).asInt();
        }catch (ResourceAccessException e){
            log.error("ES is down");
            throw new CustomException("EG_ES_ERR", "Elastic search is down");
        }catch (Exception e){
            log.error("Exception while fetching data from ES.");
            throw new CustomException("EG_ES_IDX_ERR", e.getMessage());
        }
        return recordsFound;
    }

    public void pushDataToKafkaConnector(Map<String, List<JsonNode>> indexNameVsDocumentsToBeIndexed) {
//        indexNameVsDocumentsToBeIndexed.keySet().forEach(indexName -> {
//            for(JsonNode record : indexNameVsDocumentsToBeIndexed.get(indexName)) {
//                producer.push("persist-national-records", record);
//            }
//        });
        
    	List<List<JsonNode>> chunkList = new ArrayList<>();

        indexNameVsDocumentsToBeIndexed.keySet().forEach(indexName -> {
        	List<JsonNode> records=indexNameVsDocumentsToBeIndexed.get(indexName);

        	if(records.size()>=applicationProperties.getMaxDataSizeKafka())
        	{
        	int chunkSize=applicationProperties.getMaxDataSizeKafka();
            for (int i = 0; i < records.size(); i += chunkSize) {
                int end = Math.min(i + chunkSize, records.size());
                chunkList.add(records.subList(i, end));
            }
            for(List<JsonNode> partitions:chunkList)
            	producer.push("persist-national-records", ProducerPOJO.builder().requestInfo(new RequestInfo()).records(partitions).build());

        	}
        	else
            	producer.push("persist-national-records", ProducerPOJO.builder().requestInfo(new RequestInfo()).records(indexNameVsDocumentsToBeIndexed.get(indexName)).build());

        });

//        indexNameVsDocumentsToBeIndexed.keySet().forEach(indexName -> {
//            producer.push("persist-national-records", ProducerPOJO.builder().requestInfo(new RequestInfo()).records(indexNameVsDocumentsToBeIndexed.get(indexName)).build());
//        });
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getESEncodedCredentials());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        return headers;
    }

    public String getESEncodedCredentials() {
        String credentials = applicationProperties.getUserName() + ":" + applicationProperties.getPassword();
        byte[] credentialsBytes = credentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(credentialsBytes);
        return "Basic " + new String(base64CredentialsBytes);
    }
}
