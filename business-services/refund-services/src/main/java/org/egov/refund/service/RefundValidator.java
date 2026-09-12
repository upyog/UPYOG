package org.egov.refund.service;

import java.math.BigDecimal;

import org.egov.refund.model.Refund;
import org.egov.refund.model.WorkflowTransition;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.springframework.stereotype.Service;

@Service
public class RefundValidator {

    // ============================================================
    // REQUEST INFO VALIDATION
    // ============================================================

    public void validateRequestInfo(RefundRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Refund request cannot be null");
        }

        if (request.getRequestInfo() == null) {
            throw new IllegalArgumentException("RequestInfo is mandatory");
        }

        if (request.getRequestInfo().getUserInfo() == null) {
            throw new IllegalArgumentException("UserInfo is mandatory");
        }

        if (isBlank(request.getRequestInfo().getUserInfo().getUuid())) {
            throw new IllegalArgumentException("User UUID is mandatory");
        }
    }

    // ============================================================
    // CREATE VALIDATION
    // ============================================================

    public void validateCreateRequest(Refund refund) {

        if (refund == null) {
            throw new IllegalArgumentException("Refund request cannot be null");
        }

        if (isBlank(refund.getTenantId())) {
            throw new IllegalArgumentException("tenantId is mandatory");
        }

        if (isBlank(refund.getModuleName())) {
            throw new IllegalArgumentException("moduleName is mandatory");
        }

        if (isBlank(refund.getBusinessService())) {
            throw new IllegalArgumentException(
                    "businessService is mandatory");
        }

        if (isBlank(refund.getConsumerCode())) {
            throw new IllegalArgumentException(
                    "consumerCode is mandatory");
        }

        if (isBlank(refund.getPaymentId())) {
            throw new IllegalArgumentException(
                    "paymentId is mandatory");
        }

        if (isBlank(refund.getRefundCategory())) {
            throw new IllegalArgumentException(
                    "Refund category is mandatory");
        }

        if (isBlank(refund.getRefundReason())) {
            throw new IllegalArgumentException(
                    "Refund reason is mandatory");
        }

        validateAmounts(
                refund.getAmountPaid(),
                refund.getRefundAmount());
    }

    // ============================================================
    // UPDATE VALIDATION
    // ============================================================

    public void validateUpdateRequest(Refund refund) {

        if (refund == null) {
            throw new IllegalArgumentException(
                    "Refund is mandatory");
        }

        if (refund.getId() == null) {
            throw new IllegalArgumentException(
                    "Refund id is mandatory for update");
        }

        if (refund.getRefundAmount() == null) {
            throw new IllegalArgumentException(
                    "Refund amount is mandatory");
        }

        if (refund.getRefundAmount()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Refund amount cannot be negative");
        }

        if (isBlank(refund.getRefundCategory())) {
            throw new IllegalArgumentException(
                    "Refund category is mandatory");
        }

        if (isBlank(refund.getRefundReason())) {
            throw new IllegalArgumentException(
                    "Refund reason is mandatory");
        }

        /*
         * amountPaid is optional during update because the existing
         * database value can be retained.
         */
        if (refund.getAmountPaid() != null
                && refund.getAmountPaid()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "amountPaid cannot be negative");
        }

        if (refund.getAmountPaid() != null
                && refund.getRefundAmount()
                        .compareTo(refund.getAmountPaid()) > 0) {

            throw new IllegalArgumentException(
                    "refundAmount cannot be greater than amountPaid");
        }
    }

    // ============================================================
    // AMOUNT VALIDATION
    // ============================================================

    public void validateAmounts(
            BigDecimal amountPaid,
            BigDecimal refundAmount) {

        if (amountPaid == null
                || amountPaid.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "amountPaid must be greater than or equal to zero");
        }

        if (refundAmount == null
                || refundAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "refundAmount must be greater than or equal to zero");
        }

        if (refundAmount.compareTo(amountPaid) > 0) {

            throw new IllegalArgumentException(
                    "refundAmount cannot be greater than amountPaid");
        }
    }

    // ============================================================
    // ACTION VALIDATION
    // ============================================================

    public void validateActionRequest(
            RefundActionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund action request cannot be null");
        }

        if (request.getId() == null) {
            throw new IllegalArgumentException(
                    "Refund id is mandatory");
        }

        if (isBlank(request.getAction())) {
            throw new IllegalArgumentException(
                    "Action is mandatory");
        }

        if (isBlank(request.getUserId())) {
            throw new IllegalArgumentException(
                    "userId is mandatory");
        }

        if (request.getRequestInfo() == null) {
            throw new IllegalArgumentException(
                    "RequestInfo is mandatory");
        }

        if (request.getRequestInfo().getUserInfo() == null) {
            throw new IllegalArgumentException(
                    "UserInfo is mandatory");
        }
    }

    // ============================================================
    // GET VALIDATION
    // ============================================================

    public void validateGetRequest(
            RefundGetRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund get request cannot be null");
        }

        if (request.getRequestInfo() == null) {
            throw new IllegalArgumentException(
                    "RequestInfo is mandatory");
        }

        if (isBlank(request.getId())
                && isBlank(request.getRefundNo())) {

            throw new IllegalArgumentException(
                    "Either id or refundNo is mandatory");
        }
    }

    // ============================================================
    // SEARCH VALIDATION
    // ============================================================

    public void validateSearchRequest(
            RefundSearchRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund search request cannot be null");
        }

        if (request.getRequestInfo() == null) {
            throw new IllegalArgumentException(
                    "RequestInfo is mandatory");
        }

        if (request.getRequestInfo().getUserInfo() == null) {
            throw new IllegalArgumentException(
                    "UserInfo is mandatory");
        }
    }

    

    // ============================================================
    // WORKFLOW TRANSITION VALIDATION
    // ============================================================

    public void validateWorkflowTransition(
            RefundActionRequest request,
            WorkflowTransition transition) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund action request cannot be null");
        }

        if (transition == null) {
            throw new IllegalArgumentException(
                    "Workflow transition cannot be null");
        }

        if (isBlank(transition.getAction())) {
            throw new IllegalArgumentException(
                    "Workflow transition action is mandatory");
        }

        if (isBlank(transition.getApplicationStatus())) {
            throw new IllegalArgumentException(
                    "Workflow application status is mandatory");
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}