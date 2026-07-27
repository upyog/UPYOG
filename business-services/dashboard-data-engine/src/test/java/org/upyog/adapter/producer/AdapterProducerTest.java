package org.upyog.adapter.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AdapterProducer}.
 */
@ExtendWith(MockitoExtension.class)
class AdapterProducerTest {

    @Mock
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    private AdapterProducer producer;

    @BeforeEach
    void setUp() throws Exception {
        producer = new AdapterProducer();
        java.lang.reflect.Field field = AdapterProducer.class.getDeclaredField("kafkaTemplate");
        field.setAccessible(true);
        field.set(producer, kafkaTemplate);
    }

    @Test
    @DisplayName("Push sends message to kafka template")
    void push_sendsMessageToKafka() {
        Map<String, Object> message = new HashMap<>();
        message.put("key", "value");

        producer.push("test-topic", message);

        verify(kafkaTemplate).send("test-topic", message);
    }

    @Test
    @DisplayName("Push works with different topic and object types")
    void push_worksWithDifferentTypes() {
        String message = "simple string message";

        producer.push("another-topic", message);

        verify(kafkaTemplate).send("another-topic", message);
    }
}