package digit.web.controllers;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import digit.bmc.model.VerificationDetails;
import digit.service.VerifierService;
import digit.util.ResponseInfoFactory;

import digit.web.models.VerifierRequest;
import digit.web.models.VerifierResponse;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class VerifierApiController {


    @Autowired
    private VerifierResponse response;
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private VerifierService service;

    @PostMapping("/verifier/_getapplications")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<VerifierResponse> getApplicationsForVerification(@ApiParam(value = "Details for Schemes", required = true) 
     @Valid @RequestBody VerifierRequest request ) {
      
      try{
        List<VerificationDetails> details = service.getApplicationDetails(request);
        response.setVerificationDetails(details);
         ResponseInfo responseInfo = responseInfoFactory
         .createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
         response.setResponseInfo(responseInfo);
        return new ResponseEntity<VerifierResponse>(response, HttpStatus.OK);

      }
      catch(Exception e){

        e.printStackTrace();
        response = new VerifierResponse();
        response.setErrorMessage(e.getMessage());
        return new ResponseEntity<VerifierResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
 
    }

}
