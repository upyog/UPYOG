package digit.web.controllers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.bmc.model.UserSchemeApplication;
import digit.service.BmcApplicationService;
import digit.util.ResponseInfoFactory;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationResponse;
import digit.web.models.SchemeApplicationSearchRequest;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/schemeApplication")
public class SchemeApplicationController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final BmcApplicationService schemeApplicationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    public SchemeApplicationController(ObjectMapper objectMapper, HttpServletRequest request,
            BmcApplicationService schemeApplicationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.schemeApplicationService = schemeApplicationService;
    }

    @PostMapping(value = "/v1/_create")
    public ResponseEntity<SchemeApplicationResponse> SchemeApplicationCreatePost(
            @ApiParam(value = "Details for the new Scheme Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody SchemeApplicationRequest schemeApplicationRequest) {
        List<SchemeApplication> applications = schemeApplicationService
                .registerSchemeApplication(schemeApplicationRequest);
        ResponseInfo responseInfo = responseInfoFactory
                .createResponseInfoFromRequestInfo(schemeApplicationRequest.getRequestInfo(), true);
        SchemeApplicationResponse response = SchemeApplicationResponse.builder().schemeApplications(applications)
                .responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/v1/_search")
    public ResponseEntity<SchemeApplicationResponse> SchemeApplicationSearchPost(
            @ApiParam(value = "Details for searching Scheme Applications + RequestInfo meta data.", required = true) @Valid @RequestBody SchemeApplicationSearchRequest schemeApplicationSearchRequest) {
        List<SchemeApplication> applications = schemeApplicationService.searchSchemeApplications(
                schemeApplicationSearchRequest.getRequestInfo(),
                schemeApplicationSearchRequest.getSchemeApplicationSearchCriteria());
        ResponseInfo responseInfo = responseInfoFactory
                .createResponseInfoFromRequestInfo(schemeApplicationSearchRequest.getRequestInfo(), true);
        SchemeApplicationResponse response = SchemeApplicationResponse.builder().schemeApplications(applications)
                .responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/v1/_update")
    public ResponseEntity<SchemeApplicationResponse> SchemeApplicationUpdatePost(
            @ApiParam(value = "Details for updating Scheme Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody SchemeApplicationRequest schemeApplicationRequest) {
        SchemeApplication application = schemeApplicationService.updateSchemeApplication(schemeApplicationRequest);
        ResponseInfo responseInfo = responseInfoFactory
                .createResponseInfoFromRequestInfo(schemeApplicationRequest.getRequestInfo(), true);
        SchemeApplicationResponse response = SchemeApplicationResponse.builder()
                .schemeApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/v1/_rendomize")
    public ResponseEntity<SchemeApplicationResponse> schemeApplicationRandomizePost(
        @ApiParam(value = "Details for Randomize citizen for Scheme Application(s) + RequestInfo meta data.", required = true)
        @Valid @RequestBody SchemeApplicationRequest schemeApplicationRequest) {

        List<UserSchemeApplication> randomizedCitizens = schemeApplicationService.rendomizeCitizens(schemeApplicationRequest);
        List<SchemeApplication> schemeApplications = randomizedCitizens.stream()
            .map(this::convertToSchemeApplication)
            .collect(Collectors.toList());

        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(schemeApplicationRequest.getRequestInfo(), true);
        SchemeApplicationResponse response = SchemeApplicationResponse.builder()
            .schemeApplications(schemeApplications)
            .randomizedCitizens(randomizedCitizens) 
            .responseInfo(responseInfo)
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private SchemeApplication convertToSchemeApplication(UserSchemeApplication userSchemeApplication) {
        SchemeApplication schemeApplication = new SchemeApplication();
        return schemeApplication;
    }
    
    
    @PostMapping(value = "/v1/_bmc")
    public ResponseEntity<SchemeApplicationResponse> SchemeApplicationPresnalDetails(@ApiParam(value = "Details for the new Scheme Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody SchemeApplicationRequest schemeApplicationRequest) {
        List<SchemeApplication> applications = schemeApplicationService.savePersnaldetails(schemeApplicationRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(schemeApplicationRequest.getRequestInfo(), true);
        SchemeApplicationResponse response = SchemeApplicationResponse.builder().schemeApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
