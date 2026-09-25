package org.upyog.dashboard.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.IngestionResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link DashboardClient} interface contract.
 */
class DashboardClientTest {

    @Test
    @DisplayName("DashboardClient interface has execute method")
    void interface_hasExecuteMethod() throws Exception {
        assertThat(DashboardClient.class.getMethod("execute", DashboardRequest.class)).isNotNull();
    }

    @Test
    @DisplayName("DashboardClient execute returns IngestionResult")
    void execute_returnsIngestionResult() throws Exception {
        assertThat(DashboardClient.class.getMethod("execute", DashboardRequest.class)
                .getReturnType()).isEqualTo(IngestionResult.class);
    }
}