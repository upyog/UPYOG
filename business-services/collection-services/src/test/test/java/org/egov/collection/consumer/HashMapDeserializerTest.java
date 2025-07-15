package org.egov.collection.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.converter.DefaultJackson2JavaRecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

public class HashMapDeserializer implements Deserializer<Map<String, Object>> {

    private final DefaultJackson2JavaRecordMessageConverter converter;

    public HashMapDeserializer() {
        this.converter = new DefaultJackson2JavaRecordMessageConverter();
    }

    @Override
    public Map<String, Object> deserialize(String topic, byte[] data) {
        Message<byte[]> message = MessageBuilder.withPayload(data).build();
        Object result = converter.fromMessage(message, Map.class);
        return (Map<String, Object>) result;
    }

    public DefaultJackson2JavaRecordMessageConverter getConverter() {
        return this.converter;
    }
}
