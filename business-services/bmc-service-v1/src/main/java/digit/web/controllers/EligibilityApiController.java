package digit.web.controllers;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.bmc.model.UserCompleteDetails;
import digit.util.ResponseInfoFactory;
import digit.validators.SchemeApplicationValidator;
import digit.web.models.EligibilityResponse;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeValidationResponse;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-05T23:24:36.608+05:30")

@Controller
@RequestMapping("")
public class EligibilityApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final SchemeApplicationValidator schemeValidator;

    

    List<SchemeApplication> application ;
    EligibilityResponse eligibilityResponse = new EligibilityResponse();
     @Autowired
    private ResponseInfoFactory responseInfoFactory;

    public EligibilityApiController(ObjectMapper objectMapper, HttpServletRequest request,
            SchemeApplicationValidator schemeValidator) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.schemeValidator = schemeValidator;

    }

    @PostMapping("eligibility/_check")
    public ResponseEntity<SchemeValidationResponse> eligibilityCheckPost(@ApiParam(value = "Complete details of User", required = true) 
           @Valid @RequestBody SchemeApplicationRequest schemeApplicationRequest) {
        
       // for(SchemeApplication application : schemeApplicationRequest.getSchemeApplications() )  {}

        SchemeValidationResponse response;
        
        application = schemeApplicationRequest.getSchemeApplications();
        try {

            response = schemeValidator.criteriaCheck(schemeApplicationRequest);
            ResponseInfo responseInfo = responseInfoFactory
                .createResponseInfoFromRequestInfo(schemeApplicationRequest.getRequestInfo(), true);
            response.setResponseInfo(responseInfo);
            return new ResponseEntity<SchemeValidationResponse>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response = new SchemeValidationResponse();
            response.setError(new StringBuilder(e.getMessage()));
            return new ResponseEntity<SchemeValidationResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    public EligibilityResponse checkInitialEligibilityInternally(UserCompleteDetails userCompleteDetails) {
        
        try {
            eligibilityResponse = schemeValidator.isAddressFromBMCArea(userCompleteDetails);
            if (eligibilityResponse.getAddressValidated()) {
                eligibilityResponse = schemeValidator.getBeneficiaryInfo(userCompleteDetails);
            }
            return eligibilityResponse;
          
        /*     ResponseEntity<EligibilityResponse> responseEntity = eligibilityCheckPost("initial", userCompleteDetails);
            
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                throw new RuntimeException("Eligibility check failed with status: " + responseEntity.getStatusCodeValue());
            } */
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during eligibility check: " + e.getMessage());
        }
    }

}
