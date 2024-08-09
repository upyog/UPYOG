package digit.web.controllers;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.validators.SchemeApplicationValidator;
import digit.web.models.EligibilityResponse;
import digit.web.models.SchemeApplication;
import jakarta.servlet.http.HttpServletRequest;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-05T23:24:36.608+05:30")

@Controller
@RequestMapping("")
public class EligibilityApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final SchemeApplicationValidator schemeValidator;

    SchemeApplication application  = new SchemeApplication();
    EligibilityResponse eligibilityResponse = new EligibilityResponse();

    public EligibilityApiController(ObjectMapper objectMapper, HttpServletRequest request,
            SchemeApplicationValidator schemeValidator) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.schemeValidator = schemeValidator;
    }

    @PostMapping("eligibility/check")
    public ResponseEntity<EligibilityResponse> eligibilityCheckPost(@RequestParam("stage") String stage,
            @RequestBody User user) {
        application.setUserId(user.getId());

        try {
            if ("initial".equalsIgnoreCase(stage)) {

                eligibilityResponse = schemeValidator.isAddressFromBMCArea(application);
                if (eligibilityResponse.getAddressValidated()) {
                    eligibilityResponse = schemeValidator.getBeneficiaryInfo(user);
                    return new ResponseEntity<EligibilityResponse>(eligibilityResponse, HttpStatus.OK);
                }

            }
            return new ResponseEntity<EligibilityResponse>(eligibilityResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            eligibilityResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<EligibilityResponse>(eligibilityResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
