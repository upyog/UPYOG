package org.egov.refund.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.refund.web.contracat.Payment;
import org.egov.refund.web.contracat.PaymentWorkflow;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

@Component
public class PaymentWorkflowValidator {

    private static final String ONLINE = "ONLINE";

    public void validateForRefund(List<PaymentWorkflow> paymentWorkflows, Payment payment) {

        Map<String, String> errors = new HashMap<>();

        validatePayment(payment, errors);

        if (payment != null) {

            validateWorkflow(paymentWorkflows, payment, errors);

            validatePaymentMode(payment, errors);

            validateRefundAmount(payment, errors);
        }

        if (!errors.isEmpty()) {
            throw new CustomException(errors);
        }
    }

    private void validatePayment(Payment payment, Map<String, String> errors) {

        if (payment == null) {
            errors.put("PAYMENT_NOT_FOUND",
                    "No payment found for the given criteria.");
        }
    }

    private void validateWorkflow(List<PaymentWorkflow> workflows,
                                  Payment payment,
                                  Map<String, String> errors) {

        boolean exists = workflows != null &&
                workflows.stream()
                        .anyMatch(w ->
                                payment.getId().equalsIgnoreCase(w.getPaymentId()));

        if (!exists) {
            errors.put("WORKFLOW_NOT_FOUND",
                    "No workflow found for payment : " + payment.getId());
        }
    }

    private void validatePaymentMode(Payment payment,
                                     Map<String, String> errors) {

        if (!ONLINE.equalsIgnoreCase(String.valueOf(payment.getPaymentMode()))) {

            errors.put("INVALID_PAYMENT_MODE",
                    "Refund is supported only for ONLINE payments.");
        }
    }

    private void validateRefundAmount(Payment payment,
                                      Map<String, String> errors) {

        if (payment.getTotalAmountPaid() == null) {

            errors.put("INVALID_PAYMENT_AMOUNT",
                    "Total amount paid cannot be null.");
            return;
        }

        if (payment.getPaymentDetails() == null ||
                payment.getPaymentDetails().isEmpty()) {

            errors.put("PAYMENT_DETAILS_NOT_FOUND",
                    "Payment details are missing.");
            return;
        }
        boolean exceeds = payment.getPaymentDetails().stream()
                .filter(detail -> detail.getTotalAmountPaid() != null)
                .anyMatch(detail ->
                        detail.getTotalAmountPaid()
                                .compareTo(payment.getTotalAmountPaid()) > 0);
        if (exceeds) {

            errors.put("INVALID_REFUND_AMOUNT",
                    "Refund amount cannot exceed total paid amount.");
        }
    }
}