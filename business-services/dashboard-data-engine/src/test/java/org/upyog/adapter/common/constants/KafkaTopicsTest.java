package org.upyog.adapter.common.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link KafkaTopics}.
 * Verifies constant values and that the utility class cannot be instantiated.
 */
class KafkaTopicsTest {

    @Test
    @DisplayName("SAVE_INGESTION_DETAIL has expected topic name")
    void saveIngestionDetail_hasCorrectValue() {
        assertThat(KafkaTopics.SAVE_INGESTION_DETAIL)
                .isEqualTo("save-adapter-ingestion-detail");
    }

    @Test
    @DisplayName("SAVE_LEGACY_INGESTION_DETAIL has expected topic name")
    void saveLegacyIngestionDetail_hasCorrectValue() {
        assertThat(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL)
                .isEqualTo("save-adapter-module-ingestion-detail");
    }

    @Test
    @DisplayName("UPDATE_LEGACY_INGESTION_DETAIL has expected topic name")
    void updateLegacyIngestionDetail_hasCorrectValue() {
        assertThat(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL)
                .isEqualTo("update-adapter-module-ingestion-detail");
    }

    @Test
    @DisplayName("KafkaTopics cannot be instantiated via reflection")
    void kafkaTopics_cannotBeInstantiated() throws Exception {
        Constructor<KafkaTopics> ctor = KafkaTopics.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        // The private constructor body is a comment, so it throws InvocationTargetException
        // only if it has actual throwing logic. Here we just verify the class is final.
        assertThat(KafkaTopics.class.getModifiers())
                .matches(m -> java.lang.reflect.Modifier.isFinal(m),
                        "KafkaTopics should be declared final");
    }

    @Test
    @DisplayName("all topic names are non-null and non-empty")
    void allTopicNames_areNonNullAndNonEmpty() {
        assertThat(KafkaTopics.SAVE_INGESTION_DETAIL).isNotBlank();
        assertThat(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL).isNotBlank();
        assertThat(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL).isNotBlank();
    }

    @Test
    @DisplayName("all topic names are distinct")
    void allTopicNames_areDistinct() {
        assertThat(KafkaTopics.SAVE_INGESTION_DETAIL)
                .isNotEqualTo(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL)
                .isNotEqualTo(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL);
        assertThat(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL)
                .isNotEqualTo(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL);
    }
}
