package org.egov.pqm.web.controllers;

import java.util.List;
import javax.validation.Valid;
import org.egov.pqm.service.PlantUserService;
import org.egov.pqm.util.ResponseInfoFactory;
import org.egov.pqm.web.model.plant.user.PlantUser;
import org.egov.pqm.web.model.plant.user.PlantUserRequest;
import org.egov.pqm.web.model.plant.user.PlantUserResponse;
import org.egov.pqm.web.model.plant.user.PlantUserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plant/user/v1")
public class PlantUserController {


  @Autowired
  private ResponseInfoFactory responseInfoFactory;

  @Autowired
  private PlantUserService plantUserService;

  @PostMapping(
      value = "/_create",
      consumes = {"application/json"})
  ResponseEntity<PlantUserResponse> create(@Valid @RequestBody PlantUserRequest plantUserRequest) {
    return new ResponseEntity<>(PlantUserResponse.builder()
        .plantUsers(plantUserService.create(plantUserRequest))
        .responseInfo(
            responseInfoFactory.createResponseInfoFromRequestInfo(plantUserRequest.getRequestInfo(),
                true))
        .build(), HttpStatus.OK);
  }

  @PostMapping(
      value = "/_update",
      consumes = {"application/json"})
  ResponseEntity<PlantUserResponse> update(@Valid @RequestBody PlantUserRequest plantUserRequest) {
    return new ResponseEntity<>(PlantUserResponse.builder()
        .plantUsers(plantUserService.update(plantUserRequest))
        .responseInfo(
            responseInfoFactory.createResponseInfoFromRequestInfo(plantUserRequest.getRequestInfo(),
                true))
        .build(), HttpStatus.ACCEPTED);
  }

  @PostMapping(
      value = "/_search",
      consumes = {"application/json"})
  ResponseEntity<PlantUserResponse> search(
      @Valid @RequestBody PlantUserSearchRequest plantUserSearchRequest) {
    PlantUserResponse plantUserResponse = plantUserService.search(plantUserSearchRequest);
    plantUserResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
        plantUserSearchRequest.getRequestInfo(), true));
    return new ResponseEntity<>(plantUserResponse, HttpStatus.OK);
  }
}
