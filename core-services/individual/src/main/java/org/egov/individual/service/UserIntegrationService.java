package org.egov.individual.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.user.CreateUserRequest;
import org.egov.common.models.user.UserRequest;
import org.egov.common.service.UserService;
import org.egov.individual.config.IndividualProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserIntegrationService {

    private final UserService userService;

    private final IndividualProperties individualProperties;

    @Autowired
    public UserIntegrationService(UserService userService, IndividualProperties individualProperties) {
        this.userService = userService;
        this.individualProperties = individualProperties;
    }

    public List<UserRequest> createUser(List<Individual> validIndividuals,
                                            RequestInfo requestInfo) {
        log.info("integrating with user service");
        List<UserRequest> userRequests = validIndividuals.stream()
                .filter(Individual::getIsSystemUser).map(toUserRequest())
                .collect(Collectors.toList());
        return userRequests.stream().flatMap(userRequest -> userService.create(
                new CreateUserRequest(requestInfo,
                        userRequest)).stream()).collect(Collectors.toList());
    }


    public List<UserRequest> updateUser(List<Individual> validIndividuals,
                                            RequestInfo requestInfo) {
        log.info("updating the user in user service");
        List<UserRequest> userRequests = validIndividuals.stream()
                .filter(Individual::getIsSystemUser).map(toUserRequest())
                .collect(Collectors.toList());
        return userRequests.stream().flatMap(userRequest -> userService.update(
                new CreateUserRequest(requestInfo,
                        userRequest)).stream()).collect(Collectors.toList());
    }

    public List<UserRequest> deleteUser(List<Individual> validIndividuals,
                                            RequestInfo requestInfo) {
        log.info("deleting the user in user service");
        List<UserRequest> userRequests = validIndividuals.stream()
                .filter(Individual::getIsSystemUser).map(toUserRequest())
                .peek(userRequest -> userRequest.setActive(Boolean.FALSE))
                .collect(Collectors.toList());
        return userRequests.stream().flatMap(userRequest -> userService.update(
                new CreateUserRequest(requestInfo,
                        userRequest)).stream()).collect(Collectors.toList());
    }

    private Function<Individual, UserRequest> toUserRequest() {
        return individual -> IndividualMapper
                .toUserRequest(individual,
                        individualProperties);
    }
}
