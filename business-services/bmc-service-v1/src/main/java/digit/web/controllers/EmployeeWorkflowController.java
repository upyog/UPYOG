package digit.web.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.bmc.model.UserSchemeApplication;
import digit.service.BmcApplicationService;
import digit.service.EmployeeWorkflowService;
import digit.util.ResponseInfoFactory;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationResponse;
import digit.web.models.SchemeApplicationSearchRequest;
import digit.web.models.UserSchemeApplicationRequest;
import digit.web.models.employee.ApplicationStatusRequest;
import digit.web.models.employee.ApplicationStatusResponse;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/Employee")
public class EmployeeWorkflowController {

    @Autowired
    EmployeeWorkflowService employeeWorkflowService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;


    @PostMapping("/workflow/_transit")
    public ResponseEntity<ApplicationStatusResponse> transitSchemeApplicationWorkflow(
        @ApiParam(value = "update the workflow of scheme application + RequestInfo meta data.", required = true) @Valid @RequestBody ApplicationStatusRequest request) throws Exception  {
        
        String result = employeeWorkflowService.updateApplicationsStatus(request);
        
        
         ResponseInfo responseInfo = responseInfoFactory
         .createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
         ApplicationStatusResponse response = ApplicationStatusResponse.builder()
         .info(result)
         .responseInfo(responseInfo).build();
         return new ResponseEntity<>(response, HttpStatus.OK);

        }

   
   
}
