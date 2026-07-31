package org.egov.garbageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.model.*;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.service.Scheduler;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@Slf4j
@Tag(name = "Garbage Account", description = "APIs for managing garbage accounts")
@RestController
@RequestMapping("/garbage-accounts")
/**
 * REST controller exposing garbage account lifecycle and citizen-facing APIs.
 *
 * Behavior:
 * - POST /_create, /_update, /_update_status — create, modify, and workflow-update garbage accounts.
 * - POST /_search — search accounts (optional IsIndex for index-backed search).
 * - POST /open/_search — citizen open search/pay preview; requires at least one search key; builds synthetic RequestInfo if missing.
 * - POST /fetch/{value} — CALCULATEFEE (application details) or ACTIONS (workflow actions) via path segment.
 * - POST /_payNow — initiate pay-now flow for a garbage bill.
 * - POST /_createUserForGarbage, /_counts, /_generateGrbgTaxBillReceipt, /_createArear — user provisioning, dashboards, receipt PDF, arrear generation.
 * - Delegates all business logic to {@link org.egov.garbageservice.service.GarbageAccountService}.
 *
 * Notes:
 * - Base path: /garbage-accounts; documented in Swagger under tag "Garbage Account".
 * - open/_search throws INVALID_SEARCH when no mobile, application, property, oldGarbageId, or name is provided.
 * - /fetch without a valid path value returns HTTP 400.
 */
public class GarbageAccountController {

    @Autowired
    private GarbageAccountService service;

    @Autowired
    private Scheduler scheduler;


    /**
     * Creates a new garbage account and initiates the underlying billing workflow.
     *
     * <p>This endpoint processes the incoming request by delegating to the
     * {@link org.egov.garbageservice.service.GarbageAccountService}. It captures
     * property and applicant details to register the account in the system.
     *
     * @param createGarbageRequest the request payload containing garbage account details
     * @return a {@link ResponseEntity} containing the created {@link GarbageAccountResponse}
     */

    @Operation(summary = "Create garbage account")
    @PostMapping("/_create")
    public ResponseEntity<GarbageAccountResponse> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.create(createGarbageRequest));
    }

    /**
     * Updates an existing garbage account with modified details.
     *
     * <p>This endpoint allows for modifications to account details such as applicant info,
     * collection units, or commercial details, depending on the workflow state.
     *
     * @param createGarbageRequest the request payload containing updated garbage account details
     * @return a {@link ResponseEntity} containing the updated {@link GarbageAccountResponse}
     */

    @Operation(summary = "Update garbage account")
    @PostMapping("/_update")
    public ResponseEntity<GarbageAccountResponse> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.update(createGarbageRequest));
    }

    /**
     * Searches for garbage accounts based on various criteria.
     *
     * <p>Supports searching by mobile number, property ID, application number, or old garbage IDs.
     * Results can optionally be fetched from an index if {@code IsIndex} is true.
     *
     * @param searchCriteriaGarbageAccountRequest the request containing search parameters
     * @param IsIndex                             boolean flag indicating if the search should use the indexed data
     * @return a {@link ResponseEntity} containing a list of matched garbage accounts
     */

    @Operation(summary = "Search garbage accounts")
    @PostMapping("/_search")
    public ResponseEntity<GarbageAccountResponse> search(
            @RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest, @RequestParam(name = "IsIndex", required = false, defaultValue = "false") Boolean IsIndex) {

        return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest, IsIndex));
    }

    /**
     * Performs a citizen-facing open search and payment preview for garbage accounts.
     *
     * <p>This endpoint is designed for unauthenticated or citizen users trying to view
     * their garbage bill. It performs the following steps:
     * <ol>
     *   <li>Validates that at least one search key (mobile, application, property, name, old garbage ID) is provided.</li>
     *   <li>Constructs a synthetic {@link RequestInfo} if one is not provided, assigning the user as a citizen.</li>
     *   <li>Delegates the search and pay-preview logic to the underlying service.</li>
     * </ol>
     *
     * @param request the request containing search parameters
     * @param isIndex boolean flag indicating if the search should use indexed data
     * @return a {@link ResponseEntity} containing the search results and payment preview
     * @throws CustomException if no valid search criteria are provided
     */

    @Operation(summary = "Open search for garbage accounts")
    @PostMapping("/open/_search")
    public ResponseEntity<?> openSearch(
            @RequestBody SearchCriteriaGarbageAccountRequest request,
            @RequestParam(name = "IsIndex", required = false, defaultValue = "false") Boolean isIndex) {

        if (request.getRequestInfo() == null) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setApiId("open-search");
            requestInfo.setVer("1.0");
            requestInfo.setTs(System.currentTimeMillis());

            User user = new User();
            user.setType(GrbgConstants.USER_TYPE_CITIZEN);
            user.setUuid("OPEN-SEARCH");
            user.setRoles(Collections.emptyList());

            requestInfo.setUserInfo(user);
            request.setRequestInfo(requestInfo);
        }

        if (request.getSearchCriteriaGarbageAccount() == null) {
            request.setSearchCriteriaGarbageAccount(new SearchCriteriaGarbageAccount());
        }


        SearchCriteriaGarbageAccount sc =
                request.getSearchCriteriaGarbageAccount();


        if ((sc.getMobileNumber() == null || sc.getMobileNumber().isEmpty())
                && (sc.getApplicationNumber() == null || sc.getApplicationNumber().isEmpty())
                && (sc.getPropertyId() == null || sc.getPropertyId().isEmpty())
                && (sc.getOldGarbageIds() == null || sc.getOldGarbageIds().isEmpty())
                && (sc.getName() == null || sc.getName().isEmpty())) {

            throw new CustomException(
                    "INVALID_SEARCH",
                    "Provide at least one of mobileNumber, applicationNumber, propertyId, oldGarbageIds or owner name"
            );
        }
        return ResponseEntity.ok(
                service.openSearchPayPreview(request, isIndex)
        );
    }


    /**
     * Fetches application details or workflow actions based on the provided path variable.
     *
     * <p>Depending on the path variable {@code value}:
     * <ol>
     *   <li>{@code CALCULATEFEE}: Retrieves fee calculation details for the application.</li>
     *   <li>{@code ACTIONS}: Retrieves the available workflow actions for the application's current state.</li>
     * </ol>
     *
     * @param garbageAccountActionRequest the request payload containing application details
     * @param value                       the path variable determining the fetch operation (CALCULATEFEE or ACTIONS)
     * @return a {@link ResponseEntity} containing the requested {@link GarbageAccountActionResponse}
     */

    @Operation(summary = "Fetch application details or actions")
    @PostMapping({"/fetch", "/fetch/{value}"})
    public ResponseEntity<?> calculateTLFee(@RequestBody GarbageAccountActionRequest garbageAccountActionRequest,
                                            @PathVariable String value) {

        GarbageAccountActionResponse response = null;

        if (StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) {
            response = service.getApplicationDetails(garbageAccountActionRequest);
        } else if (StringUtils.equalsIgnoreCase(value, "ACTIONS")) {
            response = service.getActionsOnApplication(garbageAccountActionRequest);
        } else {
            return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Initiates the pay-now flow for a specific garbage bill.
     *
     * <p>This endpoint triggers the payment generation and workflow update
     * required before a citizen can complete the transaction.
     *
     * @param payNowRequest the request payload containing billing details for payment
     * @return a {@link ResponseEntity} containing the {@link GarbageAccountActionResponse} with payment initiation status
     */

    @Operation(summary = "Pay garbage bill now")
    @PostMapping("/_payNow")
    public ResponseEntity<?> payNowGrbgBill(@RequestBody PayNowRequest payNowRequest) {

        GarbageAccountActionResponse response = null;
        response = service.payNowGrbgBill(payNowRequest, true);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Provisions a user account for a citizen associated with a garbage account.
     *
     * <p>This endpoint reads the garbage account details and ensures a corresponding
     * user record exists in the user service, creating one if necessary.
     *
     * @param searchCriteriaGarbageAccountRequest the request containing the garbage account details
     * @return a {@link ResponseEntity} indicating the success of user creation
     */

    @Operation(summary = "Create user for garbage account")
    @PostMapping("/_createUserForGarbage")
    public ResponseEntity<?> createUserForGarbage(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {

        log.info("createGarbageUser {}", searchCriteriaGarbageAccountRequest);
        service.createUserForGarbage(searchCriteriaGarbageAccountRequest);

        return new ResponseEntity("User created for garbage account", HttpStatus.OK);
    }

    /**
     * Retrieves aggregated counts for garbage accounts based on the provided criteria.
     *
     * <p>This endpoint is typically used for dashboard metrics, returning a map of counts
     * (e.g., total applications, approved, rejected, etc.).
     *
     * @param totalCountRequest the request containing criteria for aggregating counts
     * @return a {@link ResponseEntity} containing a map of count metrics
     */

    @Operation(summary = "Get total counts")
    @PostMapping("/_counts")

    public ResponseEntity<?> counts(@RequestBody TotalCountRequest totalCountRequest) {


        Map<String, Object> result = service.totalCount(totalCountRequest);

        return new ResponseEntity(result, HttpStatus.OK);

    }

    /**
     * Generates a PDF receipt for a garbage tax bill.
     *
     * <p>This endpoint interacts with the PDF service to generate and return a downloadable
     * receipt resource for a specified garbage account and bill ID.
     *
     * @param requestInfoWrapper the wrapper containing request context information
     * @param grbgId             the garbage account ID
     * @param billid             the specific bill ID
     * @param status             the status of the bill or payment
     * @return a {@link ResponseEntity} containing the PDF {@link Resource}
     */

    @Operation(summary = "Generate garbage tax bill receipt")
    @PostMapping("/_generateGrbgTaxBillReceipt")
    public ResponseEntity<?> generateGrbgTaxBillReceipt(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @RequestParam String grbgId, @RequestParam String billid, @RequestParam String status) {
        ResponseEntity<Resource> response = service.generateGrbgTaxBillReceipt(requestInfoWrapper, grbgId, billid, status);

        return response;

    }

    /**
     * Updates the workflow status of a garbage account.
     *
     * <p>This endpoint handles state transitions (e.g., APPROVE, REJECT) in the
     * garbage account lifecycle without modifying the core account details.
     *
     * @param createGarbageRequest the request payload containing the status update action
     * @return a {@link ResponseEntity} containing the updated {@link GarbageAccountResponse}
     */

    @Operation(summary = "Update garbage account status")
    @PostMapping("/_update_status")
    public ResponseEntity<GarbageAccountResponse> updateStatus(
            @RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.updateStatus(createGarbageRequest));
    }

    /**
     * Generates an arrear entry for a garbage account.
     *
     * <p>This endpoint calculates and creates past due arrear records based on
     * previous billing cycles and unpaid dues.
     *
     * @param genrateArrearRequest the request payload containing details for arrear generation
     * @return a {@link ResponseEntity} containing a map with the arrear generation result
     */

    @Operation(summary = "Create arrear for garbage account")
    @PostMapping("/_createArear")
    public ResponseEntity<Map<String, Object>> createArear(
            @Valid @RequestBody GenrateArrearRequest genrateArrearRequest) {
        return ResponseEntity.ok(service.generateArrear(genrateArrearRequest));
    }

    /**
     * Manually triggers the billing scheduler for a specific date.
     *
     * <p>This endpoint allows administrators to manually invoke the cron job logic
     * for generating bills, bypassing the automated schedule.
     *
     * @param request the request payload containing the target billing date and request info
     * @return a {@link ResponseEntity} with a string message indicating the trigger status
     */

    @PostMapping("/scheduler/v1/_trigger")
    public ResponseEntity<String> triggerScheduler(@RequestBody SchedulerRequest request) {
        log.info("Manual scheduler trigger requested for billingDate: {}", request.getBillingDate());
        String result = scheduler.triggerManually(request.getRequestInfo(), request.getBillingDate());
        return ResponseEntity.ok(result);
    }
}