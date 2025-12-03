package org.egov.ndc.consumer;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.ndc.consumer.ReceiptConsumer;
import org.egov.ndc.service.PaymentUpdateService;
import org.egov.ndc.web.model.bill.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

class ReceiptConsumerTest {

    private ReceiptConsumer consumer;
    private PaymentUpdateService paymentUpdateService;

    @BeforeEach
    void setUp() {
        paymentUpdateService = mock(PaymentUpdateService.class);
        consumer = new ReceiptConsumer(paymentUpdateService);
    }

    @Test
    void testListenPaymentsWithValidJson() throws Exception {
        String validJson = "{"
                + "\"RequestInfo\": {"
                + "\"apiId\": \"ndc-api\","
                + "\"ver\": \"1.0\","
                + "\"ts\": 123456789,"
                + "\"msgId\": \"msg-001\""
                + "},"
                + "\"Payment\": {"
                + "\"paymentMode\": \"CASH\","
                + "\"totalAmountPaid\": 100.0"
                + "}"
                + "}";

        consumer.listenPayments(validJson);

        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentUpdateService, times(1)).process(captor.capture());

        PaymentRequest captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("CASH", captured.getPayment().getPaymentMode().toString());
        assertEquals(BigDecimal.valueOf(100.0), captured.getPayment().getTotalAmountPaid());
    }

    @Test
    void testListenPaymentsWithInvalidJson() {
        String invalidJson = "{ invalid json }";

        consumer.listenPayments(invalidJson);

        verify(paymentUpdateService, never()).process(any());
    }
}