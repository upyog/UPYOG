package org.upyog.chb.kafka.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ProducerTest {

    @Mock
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private Producer producer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPushSuccess() {
        // Arrange
        String topic = "test-topic";
        Object value = "test-message";

        // Act
        producer.push(topic, value);

        // Assert
        Mockito.verify(kafkaTemplate).send(topic, value);
    }

    @Test
    void testPushWithException() {
        // Arrange
        String topic = "test-topic";
        Object value = "test-message";

        Mockito.doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(topic, value);

        // Act
        producer.push(topic, value);

        // Assert
        Mockito.verify(kafkaTemplate).send(topic, value);
    }
}