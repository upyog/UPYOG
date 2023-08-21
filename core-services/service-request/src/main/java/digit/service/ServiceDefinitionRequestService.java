package digit.service;

import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.ServiceDefinitionRequestRepository;
import digit.validators.ServiceDefinitionRequestValidator;
import digit.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import digit.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ServiceDefinitionRequestService {

    @Autowired
    private ServiceDefinitionRequestValidator serviceDefinitionRequestValidator;

    @Autowired
    private ServiceRequestEnrichmentService enrichmentService;

    @Autowired
    private ServiceDefinitionRequestRepository serviceDefinitionRequestRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;

    @Autowired
	private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public ServiceDefinition createServiceDefinition(ServiceDefinitionRequest serviceDefinitionRequest) {

        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();

        // Validate incoming service definition request
        serviceDefinitionRequestValidator.validateServiceDefinitionRequest(serviceDefinitionRequest);

        // Enrich incoming service definition request
        enrichmentService.enrichServiceDefinitionRequest(serviceDefinitionRequest);

        // Producer statement to emit service definition to kafka for persisting
        producer.push(config.getServiceDefinitionCreateTopic(), serviceDefinitionRequest);

        // Restore attribute values to the type in which it was sent in service definition request
        enrichmentService.setAttributeDefinitionValuesBackToNativeState(serviceDefinition);

        return serviceDefinition;
    }

    public List<ServiceDefinition> searchServiceDefinition(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest){

        List<ServiceDefinition> listOfServiceDefinitions = serviceDefinitionRequestRepository.getServiceDefinitions(serviceDefinitionSearchRequest);

        if(CollectionUtils.isEmpty(listOfServiceDefinitions))
            return new ArrayList<>();

        listOfServiceDefinitions.forEach(serviceDefinition -> {
            // Restore attribute values to native state
            enrichmentService.setAttributeDefinitionValuesBackToNativeState(serviceDefinition);
        });

        // serch user from client id and enrich the posted by variable.
        Set<String> clientIds = new HashSet<>();

        // prepare a set of uuids from servicedefinitionrequest to send to user search request
        listOfServiceDefinitions.forEach(serviceDefinition -> {
            if (serviceDefinition.getClientId() != null)
			    clientIds.add(serviceDefinition.getClientId());
        });

        UserSearchRequest userSearchRequest = null;
        String userUri = config.getUserServiceHostName()
				.concat(config.getUserServiceSearchPath());

        userSearchRequest = UserSearchRequest.builder().requestInfo(serviceDefinitionSearchRequest.getRequestInfo())
					.uuid(clientIds).build();

        List<User> users = mapper.convertValue(serviceRequestRepository.fetchResult(userUri, userSearchRequest), UserResponse.class).getUser();
        
        System.out.println("user ::");	
        System.out.println(users);
         listOfServiceDefinitions.forEach(serviceDefinition -> {
             String id = serviceDefinition.getClientId();
             users.forEach(user ->{
             if(user.getUuid().equals(id)){
            	 serviceDefinition.setPostedBy(user.getName());
             }
         });
         });

        
        Collections.sort(listOfServiceDefinitions);
        System.out.println(listOfServiceDefinitions);
        return listOfServiceDefinitions;
    }

    public ServiceDefinition updateServiceDefinition(ServiceDefinitionRequest serviceDefinitionRequest) {

        // TO DO
        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();
    	
    	enrichmentService.updateServiceDefinitionRequest(serviceDefinitionRequest);

        System.out.println("updated service definition::");
    	System.out.println(serviceDefinitionRequest.getServiceDefinition());
    	
    	producer.push(config.getServiceDefinitionUpdateTopic(), serviceDefinitionRequest);

        return serviceDefinition;
    }
    
    /**
     * Fetches total count of surveys in the system based on the search criteria
     * @param criteria Survey search criteria
     */
    public Integer countTotalSurveys(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        return serviceDefinitionRequestRepository.fetchTotalSurveyCount(serviceDefinitionSearchRequest);
    }

}

