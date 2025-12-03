package org.egov.ndc.producer;

import static org.mockito.Mockito.*;

import org.egov.ndc.producer.Producer;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class ProducerTest {

    private Producer producer;
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        kafkaTemplate = mock(CustomKafkaTemplate.class);
        producer = new Producer();
        Field field = Producer.class.getDeclaredField("kafkaTemplate");
        field.setAccessible(true);
        field.set(producer, kafkaTemplate);

    }

    @Test
    void testPushDelegatesToKafkaTemplate() {
        String topic = "ndc-test-topic";
        Object payload = "TestPayload";

        producer.push(topic, payload);

        verify(kafkaTemplate, times(1)).send(topic, payload);
    }
}