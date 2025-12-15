package org.egov.tobacco.controller;


import org.egov.tobacco.web.models.TobaccoLicenseRequest;
import org.egov.tobacco.web.models.TobaccoLicenseResponse;
import org.egov.tobacco.service.TobaccoLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tobacco")
public class TobaccoLicenseController {

    @Autowired
    private TobaccoLicenseService tobaccoLicenseService;

    @PostMapping("/_create")
    public ResponseEntity<TobaccoLicenseResponse> createLicense(
            @Valid @RequestBody TobaccoLicenseRequest request) {

        TobaccoLicenseResponse response = tobaccoLicenseService.createLicense(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

