package org.upyog.adapter.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DashboardData}.
 */
class DashboardDataTest {

    @Test
    @DisplayName("Builder creates DashboardData with all fields")
    void builder_createsFullDashboardData() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("assessments", 100);

        DashboardData data = DashboardData.builder()
                .date("2024-01-15")
                .module("PT")
                .ward("ward-1")
                .ulb("pb.amritsar")
                .region("pb")
                .state("Punjab")
                .metrics(metrics)
                .build();

        assertThat(data.getDate()).isEqualTo("2024-01-15");
        assertThat(data.getModule()).isEqualTo("PT");
        assertThat(data.getWard()).isEqualTo("ward-1");
        assertThat(data.getUlb()).isEqualTo("pb.amritsar");
        assertThat(data.getRegion()).isEqualTo("pb");
        assertThat(data.getState()).isEqualTo("Punjab");
        assertThat(data.getMetrics()).containsEntry("assessments", 100);
    }

    @Test
    @DisplayName("No-args constructor and setters work")
    void noArgsConstructorAndSetters_work() {
        DashboardData data = new DashboardData();
        data.setDate("2024-06-01");
        data.setModule("TL");
        data.setUlb("mh.pune");

        assertThat(data.getDate()).isEqualTo("2024-06-01");
        assertThat(data.getModule()).isEqualTo("TL");
        assertThat(data.getUlb()).isEqualTo("mh.pune");
    }

    @Test
    @DisplayName("Lombok equals and hashCode work correctly")
    void lombokEqualsAndHashCode_work() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("key", "value");

        DashboardData data1 = DashboardData.builder()
                .date("2024-01-15")
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(metrics)
                .build();

        DashboardData data2 = DashboardData.builder()
                .date("2024-01-15")
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(metrics)
                .build();

        assertThat(data1).isEqualTo(data2);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
    }

    @Test
    @DisplayName("Metrics map can be modified after construction")
    void metricsMap_isMutable() {
        DashboardData data = new DashboardData();
        data.setMetrics(new HashMap<>());
        data.getMetrics().put("newKey", 42);

        assertThat(data.getMetrics()).containsEntry("newKey", 42);
    }
}