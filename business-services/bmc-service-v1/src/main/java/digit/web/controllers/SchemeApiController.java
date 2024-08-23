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

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.service.SchemeService;
import digit.util.ResponseInfoFactory;
import digit.web.models.scheme.EventDetails;
import digit.web.models.scheme.SchemeSearchRequest;
import digit.web.models.scheme.SchemeSearchResponse;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-05T23:24:36.608+05:30")

@Controller
@RequestMapping("")
public class SchemeApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private SchemeService schemesService;
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    public SchemeApiController(ObjectMapper objectMapper, HttpServletRequest request, SchemeService schemesService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.schemesService = schemesService;
    }

    @PostMapping("/scheme/_search")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<SchemeSearchResponse> v1RegistrationSearchPost(
            @ApiParam(value = "Details for Schemes", required = true) 
            @Valid @RequestBody SchemeSearchRequest schemeSearchRequest) {
        List<EventDetails> schemes = schemesService.getSchemes(
            schemeSearchRequest.getRequestInfo(),
            schemeSearchRequest.getSchemeSearchCriteria());
        ResponseInfo responseInfo = responseInfoFactory
                .createResponseInfoFromRequestInfo(schemeSearchRequest.getRequestInfo(), true);
                SchemeSearchResponse res = SchemeSearchResponse.builder()
                .schemeDetails(schemes).responseInfo(responseInfo).build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
