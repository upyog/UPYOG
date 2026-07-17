package org.upyog.adapter.common.constants;

/**
 * Compile-time constants for every Kafka topic name used by the adapter-service.
 *
 * <p>Topic names defined here must stay in sync with three other artefacts:
 * <ol>
 *   <li><strong>application.properties</strong> — each constant corresponds to a
 *       {@code kafka.topics.*} property key so that the value can also be
 *       injected via {@code @Value} when needed.</li>
 *   <li><strong>adapter-service-persister.yml</strong> — each topic has a matching
 *       {@code fromTopic} entry that binds it to an INSERT or UPDATE SQL statement
 *       executed by the DIGIT persister service.</li>
 *   <li><strong>Call sites</strong> — every {@link org.upyog.adapter.producer.AdapterProducer#push}
 *       invocation should reference one of these constants instead of a raw string
 *       to prevent silent mismatches.</li>
 * </ol>
 *
 * <h3>Topic overview</h3>
 * <pre>
 * Constant                        Topic name                               Persister action
 * ──────────────────────────────  ───────────────────────────────────────  ──────────────────────────────────────────────
 * SAVE_INGESTION_DETAIL           save-adapter-ingestion-detail            INSERT into ingestion_detail
 * SAVE_LEGACY_INGESTION_DETAIL    save-adapter-module-ingestion-detail     INSERT into legacy_data_ingestion_detail
 * UPDATE_LEGACY_INGESTION_DETAIL  update-adapter-module-ingestion-detail   UPDATE legacy_data_ingestion_detail by PK
 * </pre>
 *
 * <p>This class is a non-instantiable utility class; the private constructor
 * prevents accidental instantiation.
 *
 * @see org.upyog.adapter.producer.AdapterProducer
 */
public final class KafkaTopics {

    /**
     * Private constructor — prevents instantiation of this utility class.
     */
    private KafkaTopics() {
        // utility class — no instantiation
    }

    /**
     * Topic consumed by the DIGIT persister to execute an <strong>INSERT</strong>
     * into the {@code ingestion_detail} table.
     *
     * <p>Payload structure expected by the persister:
     * <pre>{@code
     * {
     *   "dailyIngestionData": [ { ...DailyIngestionData fields... } ]
     * }
     * }</pre>
     *
     * <p>Corresponding {@code application.properties} key:
     * {@code kafka.topics.save.ingestion.detail}
     *
     * @see org.upyog.adapter.entity.DailyIngestionData
     */
    public static final String SAVE_INGESTION_DETAIL = "save-adapter-ingestion-detail";

    /**
     * Topic consumed by the DIGIT persister to execute an <strong>INSERT</strong>
     * into the {@code legacy_data_ingestion_detail} table.
     *
     * <p>Payload structure expected by the persister:
     * <pre>{@code
     * {
     *   "legacyIngestionData": [ { ...LegacyIngestionData fields... } ]
     * }
     * }</pre>
     *
     * <p>Corresponding {@code application.properties} key:
     * {@code kafka.topics.save.module.ingestion.detail}
     *
     * @see org.upyog.adapter.entity.LegacyIngestionData
     */
    public static final String SAVE_LEGACY_INGESTION_DETAIL = "save-adapter-module-ingestion-detail";

    /**
     * Topic consumed by the DIGIT persister to execute an <strong>UPDATE</strong>
     * on an existing row in the {@code legacy_data_ingestion_detail} table.
     *
     * <p>The UPDATE sets {@code response_data}, {@code ingestion_status},
     * {@code last_modified_by}, and {@code last_modified_time} identified by
     * {@code module_ingestion_id}.  Use this topic when a previously-recorded
     * legacy ingestion attempt is retried and its outcome changes.
     *
     * <p>Payload structure expected by the persister:
     * <pre>{@code
     * {
     *   "legacyIngestionData": [ { ...LegacyIngestionData fields... } ]
     * }
     * }</pre>
     *
     * <p>Corresponding {@code application.properties} key:
     * {@code kafka.topics.update.module.ingestion.detail}
     *
     * @see org.upyog.adapter.entity.LegacyIngestionData
     */
    public static final String UPDATE_LEGACY_INGESTION_DETAIL = "update-adapter-module-ingestion-detail";
}
