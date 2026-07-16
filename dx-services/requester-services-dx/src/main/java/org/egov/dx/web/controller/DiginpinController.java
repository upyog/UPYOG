package org.egov.dx.web.controller;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.dx.service.DiginpinService;
import org.egov.dx.web.models.DiginpinRequest;
import org.egov.dx.web.models.DiginpinResponse;
import org.egov.dx.web.models.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for DIGIPIN related operations.
 * Exposes API endpoints to generate DIGIPIN codes based on geographical coordinates.
 */
@RestController
@Slf4j
@RequestMapping("/digipin")
public class DiginpinController {

    @Autowired
    private DiginpinService diginpinService;

    /**
     * Endpoint to generate a DIGIPIN code.
     * 
     * @param request The request payload containing latitude, longitude, and standard request info.
     * @return A response entity containing the generated DIGIPIN and standard response info.
     */
    @RequestMapping(value = "/v1/_generate", method = RequestMethod.POST)
    public ResponseEntity<DiginpinResponse> generateV1(@Valid @RequestBody DiginpinRequest request) {
        log.info("Generating DIGIPIN for lat={}, lon={}", request.getLatitude(), request.getLongitude());

        // Generate the DIGIPIN code using the underlying service
        String digipin = diginpinService.generate(request.getLatitude(), request.getLongitude());

        // Create a standard eGov response info block based on the incoming request info
        ResponseInfo responseInfo = ResponseInfoFactory
                .createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

        // Build the final response object with the generated DIGIPIN
        DiginpinResponse response = DiginpinResponse.builder()
                .responseInfo(responseInfo)
                .digipin(digipin)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
