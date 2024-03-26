package org.egov.web.controllers;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.egov.service.ProjectMasterService;
//import org.wms.service.BirthRegistrationService;
import org.egov.service.SchemeMasterService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.Project;
import org.egov.web.models.ProjectApplicationResponse;
import org.egov.web.models.ProjectApplicationSearchCriteria;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.SORApplicationResponse;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationResponse;
import org.egov.web.models.SchemeApplicationSearchCriteria;
//import org.wms.web.models.SchemeCreationRequest;
//import org.wms.web.models.SchemeCreationResponse;
import org.egov.web.models.SchemeCreationApplication;
import org.egov.web.models.WMSProjectRequest;
//import org.wms.web.models.SchemeResponse;
import org.egov.web.models.WMSSchemeRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//import jakarta.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Controller
@RequestMapping("/wms-services")
public class ProjectApiController {
	
	private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private ProjectMasterService projectMasterService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    public ProjectApiController(ObjectMapper objectMapper, HttpServletRequest request, ProjectMasterService projectMasterService) {
    	this.objectMapper = objectMapper;
        this.request = request;
        this.projectMasterService = projectMasterService;
    }
    @ResponseBody
    @RequestMapping(value="/v1/project/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New Project for WMS")
    public ResponseEntity<ProjectApplicationResponse> v1WmsProjectCreatePost(@ApiParam(value = "Details for the new Project Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSProjectRequest projectRequest) {
        
    	List<Project> applications = projectMasterService.createProjectRequest(projectRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(projectRequest.getRequestInfo(), true);
        //sorRequest.builder().
        ProjectApplicationResponse response = ProjectApplicationResponse.builder().projectApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    	
    	
    	
    }

    
    
    @RequestMapping(value="/v1/project/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Project for WMS")
    public ResponseEntity<ProjectApplicationResponse> v1ProjectSearchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute ProjectApplicationSearchCriteria projectApplicationSearchCriteria) {
        List<Project> applications = projectMasterService.searchProjectApplications(requestInfoWrapper.getRequestInfo(), projectApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        ProjectApplicationResponse response = ProjectApplicationResponse.builder().projectApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RequestMapping(value="/v1/project/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Upadate Project for WMS")
    //public ResponseEntity<String> v1SchemeUpdatePost(@RequestBody Scheme scheme) {
        //List<SchemeCreationApplication> applications = schemeMasterService.updateBtApplication(birthRegistrationRequest);
    public ResponseEntity<ProjectApplicationResponse> v1ProjectUpdatePost(@ApiParam(value = "Details for the new Project(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSProjectRequest projectRequest) {
        List<Project> applications = projectMasterService.updateProjectMaster(projectRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(projectRequest.getRequestInfo(), true);
        ProjectApplicationResponse response = ProjectApplicationResponse.builder().projectApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    	

	
    
    @RequestMapping(value="/v1/project/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch Project for WMS")
    public ResponseEntity<ProjectApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute ProjectApplicationSearchCriteria projectApplicationSearchCriteria) {
        List<Project> applications = projectMasterService.fetchProjectApplications(requestInfoWrapper.getRequestInfo(), projectApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        ProjectApplicationResponse response = ProjectApplicationResponse.builder().projectApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
	
	
	

}
