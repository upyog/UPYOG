package org.egov.pg.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.CollectionPaymentRequest;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class RefundNotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Consumes refund-related records and sends SMS notifications.
     *
     * @param record incoming kafka payload
     * @param topic  kafka topic name
     */
    @KafkaListener(topics = {"${persister.update.pg.refund}", "${egov.collectionservice.payment.refund}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            if (appProperties.getUpdateRefundTxnsTopic().equals(topic)) {
                RefundRequest refundRequest = mapper.convertValue(record, RefundRequest.class);
                notificationService.refundInitiateSmsNotification(refundRequest, topic);
                return;
            }  

            CollectionPaymentRequest paymentRequest = mapper.convertValue(record, CollectionPaymentRequest.class);
            notificationService.refundSuccessSmsNotification(paymentRequest, topic);
        } catch (Exception ex) {
            StringBuilder builder = new StringBuilder("Error while listening to value: ").append(record)
                    .append(" on topic: ").append(topic);
            log.error(builder.toString(), ex);
        }
    }
}