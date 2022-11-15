package org.bel.birthdeath.crdeath.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.bel.birthdeath.crdeath.service.CrDeathService;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathRequest;
import org.bel.birthdeath.crdeath.web.models.CrDeathResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/v1")
@Validated
public class CrDeathController {

	private final CrDeathService deathService;

    @Autowired
    public CrDeathController(CrDeathService deathService) {

        this.deathService = deathService;
    }

// 	@PostMapping("/crdeathdtls/_create")
//    public ResponseEntity<CrDeathResponse> create(@Valid @RequestBody CrDeathRequest request) {

//       // List<CrDeathDtl> deathdetails = deathService.create(request);

//     //    CrDeathResponse response = CrDeathResponse.builder().responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
//     //                                                                                                                                      Boolean.TRUE))
//     //                                                                  .applicantPersonals(personals)
//     //                                                                  .build();
//        //return ResponseEntity.ok(response);

//    }
}
