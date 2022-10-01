package org.egov.filemgmnt.web.controllers;

import javax.validation.Valid;

import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalResponse;
import org.egov.tracer.model.ErrorRes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Applicant Personals")
@Validated
interface ApplicantPersonalsResource {

    @Operation(summary = "Create applicant personal",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = "application/json",
                                                             schema = @Schema(implementation = ApplicantPersonalRequest.class)),
                                          required = true),
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant personal created successfully",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = ApplicantPersonalResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad request",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<ApplicantPersonalResponse> create(@Valid ApplicantPersonalRequest request);

    ResponseEntity<ApplicantPersonalResponse> update(@Valid ApplicantPersonalRequest request);

}