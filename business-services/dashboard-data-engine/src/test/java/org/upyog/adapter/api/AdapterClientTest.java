package org.upyog.adapter.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.IngestionResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link AdapterClient} interface contract.
 */
class AdapterClientTest {

    @Test
    @DisplayName("AdapterClient interface has execute method")
    void interface_hasExecuteMethod() throws Exception {
        assertThat(AdapterClient.class.getMethod("execute", AdapterRequest.class)).isNotNull();
    }

    @Test
    @DisplayName("AdapterClient execute returns IngestionResult")
    void execute_returnsIngestionResult() throws Exception {
        assertThat(AdapterClient.class.getMethod("execute", AdapterRequest.class)
                .getReturnType()).isEqualTo(IngestionResult.class);
    }
}