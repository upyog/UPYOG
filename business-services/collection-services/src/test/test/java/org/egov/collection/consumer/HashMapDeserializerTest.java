package org.egov.collection.consumer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

class HashMapDeserializerTest {

    /**
     * Test the default constructor and basic functionality of HashMapDeserializer.
     */
    @Test
    void testConstructor() {
        HashMapDeserializer deserializer = new HashMapDeserializer();
        assertNotNull(deserializer.getConverter());
    }

    /**
     * Test deserialization of a valid JSON string.
     */
    @Test
    void testDeserializeJson() {
        HashMapDeserializer deserializer = new HashMapDeserializer();

        String json = "{\"key\":\"value\",\"count\":5}";
        byte[] data = json.getBytes();

        Map<String, Object> result = deserializer.deserialize("some-topic", data);

        assertNotNull(result);
        assertEquals("value", result.get("key"));
        assertEquals(5, ((Number) result.get("count")).intValue());
    }
}
