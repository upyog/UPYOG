package org.egov.web.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.service.WMSTenderEntryService;
import org.egov.util.ResponseInfoFactory;
import org.egov.web.models.RequestInfoWrapper;
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSTenderEntryApplicationResponse;
import org.egov.web.models.WMSTenderEntryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderEntryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-12T17:07:08.384+05:30")
@Slf4j
@ToString
@RestController
@RequestMapping("/wms-services")
@Api(tags = "Tender Entry")
public class WMSTenderEntryApiController{

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private WMSTenderEntryService tenderEntryService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
    
    @Value("${upload.directory}")
    private String uploadDirectory;
 

    @Autowired
    public WMSTenderEntryApiController(ObjectMapper objectMapper, HttpServletRequest request, WMSTenderEntryService tenderEntryService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.tenderEntryService = tenderEntryService;
    }
    
    @ResponseBody
    @RequestMapping(value="/v1/tenderentry/_create", method = RequestMethod.POST)
    @ApiOperation(value = "Create New TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationCreateTenderEntry(@ApiParam(value = "Details for the new Tender Entry Application(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderEntryRequest wmsTenderEntryRequest) {
    	List<WMSTenderEntryApplication> applications = tenderEntryService.registerWMSTenderEntryRequest(wmsTenderEntryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(wmsTenderEntryRequest.getRequestInfo(), true);
        
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
       
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_view", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationFetchPost(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderEntryApplicationSearchCriteria tenderEntryApplicationSearchCriteria) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.fetchTenderEntryApplications(requestInfoWrapper.getRequestInfo(), tenderEntryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_update", method = RequestMethod.POST)
    @ApiOperation(value = "Update TenderEntry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1TenderEntryUpdatePost(@ApiParam(value = "Details for the new TenderEntry(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody WMSTenderEntryRequest tenderEntryRequest) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.updateTenderEntryMaster(tenderEntryRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(tenderEntryRequest.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @RequestMapping(value="/v1/tenderentry/_search", method = RequestMethod.POST)
    @ApiOperation(value = "Search Tender Entry for WMS")
    public ResponseEntity<WMSTenderEntryApplicationResponse> v1RegistrationSearchTenderEntry(@RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute WMSTenderEntryApplicationSearchCriteria wmsTenderEntryApplicationSearchCriteria) {
        List<WMSTenderEntryApplication> applications = tenderEntryService.searchWMSTenderEntryApplications(requestInfoWrapper.getRequestInfo(), wmsTenderEntryApplicationSearchCriteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        WMSTenderEntryApplicationResponse response = WMSTenderEntryApplicationResponse.builder().wmsTenderEntryApplications(applications).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @CrossOrigin(
    	    origins = {
    	        "http://localhost:3000", 
    	        "https://staging.example.com", 
    	        "https://app.example.com"
    	        },
    	    methods = {
    	                RequestMethod.OPTIONS,
    	                RequestMethod.GET,
    	                RequestMethod.PUT,
    	                RequestMethod.DELETE,
    	                RequestMethod.POST
    	})
    @RequestMapping(value="/v1/tenderentry/_upload", headers = ("content-type=multipart/*"),method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Search Tender Entry for WMS")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("tenantId") String tenantId,@RequestParam("module") String module) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
            }

            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the specified directory
            Path filePath = Paths.get(uploadDirectory, file.getOriginalFilename());
            file.transferTo(filePath.toFile());
            
            
			/*
			 * HttpHeaders headers = new HttpHeaders(); headers.add("Custom-Header", "foo");
			 */

            return new ResponseEntity<String>("C:/uploaded/"+file.getOriginalFilename(),HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
}
