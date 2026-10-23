package org.egov.garbageservice.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.web.models.*;
import org.egov.garbageservice.service.GarbageAccountService;
import org.egov.garbageservice.service.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        log.info("REST request to create garbage account: {}", createGarbageRequest);
        GarbageAccountResponse response = service.create(createGarbageRequest);
        log.info("REST response for create garbage account: {}", response);
        return ResponseEntity.ok(response);
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
        log.info("REST request to update garbage account: {}", createGarbageRequest);
        GarbageAccountResponse response = service.update(createGarbageRequest);
        log.info("REST response for update garbage account: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches for garbage accounts based on various criteria.
     *
     * <p>Supports searching by mobile number, property ID, application number, or old garbage IDs.
     *
     * @param searchCriteriaGarbageAccountRequest the request containing search parameters
     * @return a {@link ResponseEntity} containing a list of matched garbage accounts
     */

    @Operation(summary = "Search garbage accounts")
    @PostMapping("/_search")
    public ResponseEntity<GarbageAccountResponse> search(
            @RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest,
            @ModelAttribute SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        log.info("REST request to search garbage accounts. Request body: {}, query params: {}",
                searchCriteriaGarbageAccountRequest, searchCriteriaGarbageAccount);

        if (searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount() == null) {
            searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(searchCriteriaGarbageAccount);
        } else if (service.isCriteriaEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())
                && !service.isCriteriaEmpty(searchCriteriaGarbageAccount)) {
            searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(searchCriteriaGarbageAccount);
        }

        GarbageAccountResponse response = service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest);
        log.info("REST response for search garbage accounts. Total found: {}",
                response != null && response.getGarbageAccounts() != null ? response.getGarbageAccounts().size() : 0);
        return ResponseEntity.ok(response);
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