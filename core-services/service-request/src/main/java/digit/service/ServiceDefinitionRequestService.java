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
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

        // Restore attribute values to the type in which it was sent in service
        // definition request
        enrichmentService.setAttributeDefinitionValuesBackToNativeState(serviceDefinition);

        return serviceDefinition;
    }

    public List<ServiceDefinition> searchServiceDefinition(
            ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {

        System.out.println("search service definition outside");
        System.out.println(serviceDefinitionSearchRequest.getServiceDefinitionCriteria());
        if (serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getStatus() != null
                && (!serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getStatus().isEmpty())
                && (!serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getStatus()
                        .equalsIgnoreCase("all"))) {

            LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
            long todayStartEpochMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
       
            ServiceDefinitionCriteria ServiceDefinitionCriteria = serviceDefinitionSearchRequest.getServiceDefinitionCriteria();
            ServiceDefinitionCriteria.setTodaysDate(todayStartEpochMillis);
            serviceDefinitionSearchRequest.setServiceDefinitionCriteria(ServiceDefinitionCriteria);
            System.out.println("search service definition inside");
            System.out.println(serviceDefinitionSearchRequest.getServiceDefinitionCriteria());
            List<ServiceDefinition> ListOfActiveInactiveServiceDefinitions = serviceDefinitionRequestRepository
                .getServiceDefinitions(serviceDefinitionSearchRequest);
            
            Collections.sort(ListOfActiveInactiveServiceDefinitions);
            return ListOfActiveInactiveServiceDefinitions;

        }

        List<ServiceDefinition> listOfServiceDefinitions = serviceDefinitionRequestRepository
                .getServiceDefinitions(serviceDefinitionSearchRequest);

        if (CollectionUtils.isEmpty(listOfServiceDefinitions))
            return new ArrayList<>();

        listOfServiceDefinitions.forEach(serviceDefinition -> {
            // Restore attribute values to native state
            enrichmentService.setAttributeDefinitionValuesBackToNativeState(serviceDefinition);
        });

        // serch user by uuid which is clientid in service definition and enrich the
        // posted by variable.
        Set<String> clientIds = new HashSet<>();
        // prepare a set of clientIds from servicedefinitionrequest to send to user
        // search request
        listOfServiceDefinitions.forEach(serviceDefinition -> {
            if (serviceDefinition.getClientId() != null)
                clientIds.add(serviceDefinition.getClientId());
        });

        UserSearchRequest userSearchRequestByUuid = null;
        String Url = config.getUserServiceHostName()
                .concat(config.getUserServiceSearchPath());

        userSearchRequestByUuid = UserSearchRequest.builder()
                .requestInfo(serviceDefinitionSearchRequest.getRequestInfo())
                .uuid(clientIds).build();

        List<User> usersresponse = mapper
                .convertValue(serviceRequestRepository.fetchResult(Url, userSearchRequestByUuid), UserResponse.class)
                .getUser();

        listOfServiceDefinitions.forEach(serviceDefinition -> {
            String id = serviceDefinition.getClientId();
            usersresponse.forEach(user -> {
                if (user.getUuid().equals(id)) {
                    serviceDefinition.setPostedBy(user.getName());
                }
            });
        });

        if (serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getPostedBy() != null
                && (!serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getPostedBy().isEmpty())
                && (!serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getPostedBy()
                        .equalsIgnoreCase("All"))) {
            // UserSearchRequest userSearchRequest = null;
            // String userUri = config.getUserServiceHostName()
            //         .concat(config.getUserServiceSearchPath());

            // userSearchRequest = UserSearchRequest.builder().requestInfo(serviceDefinitionSearchRequest.getRequestInfo())
            //         .tenantId(serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getTenantId())
            //         .name(serviceDefinitionSearchRequest.getServiceDefinitionCriteria().getPostedBy()).build();

            // List<User> users = mapper
            //         .convertValue(serviceRequestRepository.fetchResult(userUri, userSearchRequest), UserResponse.class)
            //         .getUser();

            // List<ServiceDefinition> finalListOfServiceDefinitions = new ArrayList<>();

            // listOfServiceDefinitions.forEach(serviceDefinition -> {
            //     String id = serviceDefinition.getClientId();
            //     users.forEach(user -> {
            //         if (user.getUuid().equals(id)) {
            //             finalListOfServiceDefinitions.add(serviceDefinition);
            //         }
            //     });
            // });

            // listOfServiceDefinitions = finalListOfServiceDefinitions;
            List<ServiceDefinition> ListOfPostedByServiceDefinitions = serviceDefinitionRequestRepository
                .getServiceDefinitions(serviceDefinitionSearchRequest);
            listOfServiceDefinitions = ListOfPostedByServiceDefinitions;
        }
        Collections.sort(listOfServiceDefinitions);
        return listOfServiceDefinitions;
    }


    public ServiceDefinition updateServiceDefinition(ServiceDefinitionRequest serviceDefinitionRequest) {

        ServiceDefinition serviceDefinition = serviceDefinitionRequest.getServiceDefinition();

        enrichmentService.updateServiceDefinitionRequest(serviceDefinitionRequest);

        producer.push(config.getServiceDefinitionUpdateTopic(), serviceDefinitionRequest);

        return serviceDefinition;
    }

    /**
     * Fetches total count of surveys in the system based on the search criteria
     * 
     * @param criteria Survey search criteria
     */
    public Integer countTotalSurveys(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        return serviceDefinitionRequestRepository.fetchTotalSurveyCount(serviceDefinitionSearchRequest);
    }

}
