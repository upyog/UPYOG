package org.egov.ewst.consumer;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

/**
 * Custom deserializer for HashMap.
 * This class extends the JsonDeserializer to handle deserialization of HashMap objects.
 */
public class HashMapDeserializer extends JsonDeserializer<HashMap> {

    /**
     * Constructor to initialize the deserializer with HashMap class type.
     */
    public HashMapDeserializer() {
        super(HashMap.class);
    }

}