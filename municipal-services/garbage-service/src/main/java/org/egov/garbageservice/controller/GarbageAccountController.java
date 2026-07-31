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