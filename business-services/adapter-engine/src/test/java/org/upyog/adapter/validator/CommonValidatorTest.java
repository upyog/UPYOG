package org.upyog.adapter.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.exception.ValidationException;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Unit tests for {@link CommonValidator}.
 *
 * Validates that cross-module mandatory fields are present and non-empty.
 */
class CommonValidatorTest {

    private final CommonValidator validator = new CommonValidator();

    @Test
    @DisplayName("Valid payload with non-empty string fields passes validation")
    void validPayload_passesValidation() {
        DashboardPayload payload = createValidPayload();

        assertThatCode(() -> validator.validate(payload))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Null payload throws ValidationException")
    void nullPayload_throwsException() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Payload cannot be null");
    }

    @Test
    @DisplayName("Null data list throws ValidationException")
    void nullDataList_throwsException() {
        DashboardPayload payload = DashboardPayload.builder().data(null).build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Data cannot be empty");
    }

    @Test
    @DisplayName("Empty data list throws ValidationException")
    void emptyDataList_throwsException() {
        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Data cannot be empty");
    }

    @Test
    @DisplayName("Null module field throws ValidationException")
    void nullModule_throwsException() {
        DashboardData data = DashboardData.builder()
                .module(null)
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Module is mandatory");
    }

    @Test
    @DisplayName("Empty module field throws ValidationException")
    void emptyModule_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Module is mandatory");
    }

    @Test
    @DisplayName("Null state field throws ValidationException")
    void nullState_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state(null)
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("State is mandatory");
    }

    @Test
    @DisplayName("Empty state field throws ValidationException")
    void emptyState_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("State is mandatory");
    }

    @Test
    @DisplayName("Null metrics map throws ValidationException")
    void nullMetrics_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(null)
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Metrics cannot be null");
    }

    @Test
    @DisplayName("Null ward field throws ValidationException")
    void nullWard_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward(null)
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Ward cannot be null");
    }

    @Test
    @DisplayName("Empty ward field throws ValidationException")
    void emptyWard_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Ward cannot be null");
    }

    @Test
    @DisplayName("Null region field throws ValidationException")
    void nullRegion_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region(null)
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Region cannot be null");
    }

    @Test
    @DisplayName("Empty region field throws ValidationException")
    void emptyRegion_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Region cannot be null");
    }

    @Test
    @DisplayName("Null ulb field throws ValidationException")
    void nullUlb_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb(null)
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("ULB cannot be null");
    }

    @Test
    @DisplayName("Empty ulb field throws ValidationException")
    void emptyUlb_throwsException() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(ValidationException.class)
                .hasMessage("ULB cannot be null");
    }

    @Test
    @DisplayName("Module with non-empty value passes (no longer inverted)")
    void nonEmptyModule_doesNotThrow() {
        Map<String, Object> metrics = new HashMap<>();
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(metrics)
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThatCode(() -> validator.validate(payload))
                .doesNotThrowAnyException();
    }

    private static DashboardPayload createValidPayload() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        return DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();
    }
}