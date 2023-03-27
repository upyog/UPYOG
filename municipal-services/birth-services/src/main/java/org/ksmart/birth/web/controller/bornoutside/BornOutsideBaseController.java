package org.ksmart.birth.web.controller.bornoutside;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.egov.tracer.model.ErrorRes;
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.bornoutside.BornOutsideResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Tag(name = "Born outside Application")
@Validated
interface BornOutsideBaseController {
    @Operation(summary = "Create birth application along with parent details, address details, initiator and informer Details.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = BornOutsideDetailRequest.class)),
                                          required = true),
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Birth application created successfully",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = NewBirthResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad birth application service request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<BornOutsideResponse> create(@Valid BornOutsideDetailRequest request);

    @Operation(summary = "Update birth application along with parent details, address details",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = BornOutsideDetailRequest.class)),
                                          required = true),
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Birth application details updated successfully",
                                    content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = NewBirthResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<BornOutsideResponse> update(@Valid BornOutsideDetailRequest request);

    @Operation(summary = "Search applicant service details with the given query parameters.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = RequestInfoWrapper.class)),
                                          required = true),
               parameters = {
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "tenantId",
                                  required = true,
                                  allowEmptyValue = false,
                                  description = "Tenant identification number",
                                  schema = @Schema(type = "string",
                                                   pattern = BirthConstants.PATTERN_TENANT,
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "applicantId",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Applicant personal id",
                                  schema = @Schema(type = "string",
                                                   format = "uuid",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "fileCode",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "File code",
                                  schema = @Schema(type = "string",
                                                   example = "KL-FM-2022-10-25-000001",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "fromDate",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "File arising date, search from",
                                  schema = @Schema(type = "integer",
                                                   format = "int64",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "toDate",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "File arising date, search to",
                                  schema = @Schema(type = "integer",
                                                   format = "int64",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "aadhaarNumber",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Aadhaar number",
                                  schema = @Schema(type = "string",
                                                   pattern = BirthConstants.PATTERN_AADHAAR,
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "offset",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Search offset",
                                  schema = @Schema(type = "integer",
                                                   format = "int32",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "limit",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Search limit",
                                  schema = @Schema(type = "integer",
                                                   format = "int32",
                                                   accessMode = Schema.AccessMode.READ_ONLY)) },
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant service details retrieved successfully",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = NewBirthSearchResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad applicant service detail search request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<NewBirthSearchResponse> searchServices(@Valid RequestInfoWrapper request,
                                                                  @Valid SearchCriteria searchCriteria);

    @Operation(summary = "Search applicant details with the given query parameters.",
               description = "",
               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                             schema = @Schema(implementation = RequestInfoWrapper.class)),
                                          required = true),
               parameters = {
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "tenantId",
                                  required = true,
                                  allowEmptyValue = false,
                                  description = "Tenant identification number",
                                  schema = @Schema(type = "string",
                                                   pattern = BirthConstants.PATTERN_TENANT,
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "id",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Applicant personal id",
                                  schema = @Schema(type = "string",
                                                   format = "uuid",
                                                   accessMode = Schema.AccessMode.READ_ONLY)),
                       @Parameter(in = ParameterIn.QUERY,
                                  name = "aadhaarNumber",
                                  required = false,
                                  allowEmptyValue = true,
                                  description = "Aadhaar number",
                                  schema = @Schema(type = "string",
                                                   pattern = BirthConstants.PATTERN_AADHAAR,
                                                   accessMode = Schema.AccessMode.READ_ONLY)) },
               responses = {
                       @ApiResponse(responseCode = "200",
                                    description = "Applicant personals retrieved successfully",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = NewBirthSearchResponse.class))),
                       @ApiResponse(responseCode = "400",
                                    description = "Bad applicant personal search request",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ErrorRes.class))) })
    ResponseEntity<NewBirthSearchResponse> searchApplicants(@Valid RequestInfoWrapper request,
                                                             @Valid SearchCriteria searchCriteria);

//    @Operation(summary = "Download applicant service certificate with the given query parameters.",
//               description = "",
//               requestBody = @RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                                                             schema = @Schema(implementation = RequestInfoWrapper.class)),
//                                          required = true),
//               parameters = {
//                       @Parameter(in = ParameterIn.QUERY,
//                                  name = "tenantId",
//                                  required = true,
//                                  allowEmptyValue = false,
//                                  description = "Tenant identification number",
//                                  schema = @Schema(type = "string",
//                                                   pattern = BirthConstants.PATTERN_TENANT,
//                                                   accessMode = Schema.AccessMode.READ_ONLY)),
//                       @Parameter(in = ParameterIn.QUERY,
//                                  name = "serviceDetailId",
//                                  required = true,
//                                  allowEmptyValue = false,
//                                  description = "Applicant service detail id",
//                                  schema = @Schema(type = "string",
//                                                   format = "uuid",
//                                                   accessMode = Schema.AccessMode.READ_ONLY)),
//                       @Parameter(in = ParameterIn.QUERY,
//                                  name = "applicantId",
//                                  required = false,
//                                  allowEmptyValue = true,
//                                  description = "Applicant personal id",
//                                  schema = @Schema(type = "string",
//                                                   format = "uuid",
//                                                   accessMode = Schema.AccessMode.READ_ONLY)), })
//    ResponseEntity<CertificateResponse> downloadCertificate(@Valid RequestInfoWrapper request,
//                                                            @Valid SearchCriteria searchCriteria);
}