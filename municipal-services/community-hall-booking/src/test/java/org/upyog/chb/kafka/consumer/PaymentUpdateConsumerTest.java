package org.upyog.chb.kafka.consumer;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.upyog.chb.service.PaymentNotificationService;

class PaymentUpdateConsumerTest {

    @Mock
    private PaymentNotificationService paymentNotificationService;

    @InjectMocks
    private PaymentUpdateConsumer paymentUpdateConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPaymentSuccess() throws Exception {
        // Arrange
        HashMap<String, Object> paymentRecord = new HashMap<>();
        paymentRecord.put("key", "value");
        String topic = "receipt-create-topic";

        // Act
        paymentUpdateConsumer.paymentSuccess(paymentRecord, topic);

        // Assert
        verify(paymentNotificationService).process(paymentRecord, topic);
    }

    @Test
    void testPaymentUpdate() {
        // Arrange
        HashMap<String, Object> paymentRecord = new HashMap<>();
        paymentRecord.put("key", "value");
        String topic = "update-pg-txns-topic";

        // Act
        paymentUpdateConsumer.paymentUpdate(paymentRecord, topic);

        // Assert
        verify(paymentNotificationService).processTransaction(paymentRecord, topic, null);
    }

    @Test
    void testPaymentSuccessWithException() throws Exception {
        // Arrange
        HashMap<String, Object> paymentRecord = new HashMap<>();
        paymentRecord.put("key", "value");
        String topic = "receipt-create-topic";

        doThrow(new RuntimeException("Processing error")).when(paymentNotificationService).process(paymentRecord, topic);


        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentUpdateConsumer.paymentSuccess(paymentRecord, topic));

        // Assert
        verify(paymentNotificationService).process(paymentRecord, topic);
    }
}