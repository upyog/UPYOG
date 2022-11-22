package org.egov.filemgmnt.web.controllers;

import javax.validation.Valid;

import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalResponse;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.egov.filemgmnt.web.models.RequestInfoWrapper;
import org.egov.tracer.model.ErrorRes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "File Management")
@Validated
interface FileManagementResource {

    @Operation(summary = "Create applicant personal along with details.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = ApplicantPersonalRequest.class)),
                                          required = true),
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant personal created successfully",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = ApplicantPersonalResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })

    ResponseEntity<ApplicantPersonalResponse> create(@Valid ApplicantPersonalRequest request);

    @Operation(summary = "Update applicant personal along with details.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = ApplicantPersonalRequest.class)),
                                          required = true),
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant personal updated successfully",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = ApplicantPersonalResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<ApplicantPersonalResponse> update(@Valid ApplicantPersonalRequest request);

    @Operation(summary = "Search applicant personal along with details.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = RequestInfoWrapper.class)),
                                          required = true),
               parameters = { @Parameter(in = ParameterIn.QUERY,
                                         name = "id",
                                         required = false,
                                         allowEmptyValue = true,
                                         description = "Applicant personal id",
//                                  array = @ArraySchema(schema = @Schema(type = "string",
//                                                                        format = "uuid",
//                                                                        accessMode = Schema.AccessMode.READ_ONLY)),
                                         schema = @Schema(type = "string",
                                                          format = "uuid",
                                                          accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "fileCodes",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "File code",
                                  schema = @Schema(type = "string",
                                                   example = "KL-FM-2022-10-25-000001",
                                                   accessMode = Schema.AccessMode.READ_ONLY)) },
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant personals retrieved successfully",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ApplicantPersonalResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<ApplicantPersonalResponse> search(@Valid RequestInfoWrapper request,
                                                     @Valid ApplicantPersonalSearchCriteria criteria);

}