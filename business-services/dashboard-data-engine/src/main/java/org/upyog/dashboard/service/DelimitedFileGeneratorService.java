package org.upyog.dashboard.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Memory-safe streaming service for generating generic delimited dataset files (e.g. {@code .psv}, {@code .csv}, {@code .txt}).
 * <p>
 * Key Capabilities:
 * <ul>
 *   <li>Dynamically extracts standard context header fields ({@code uuid}, {@code date}, {@code module}, {@code state}, {@code ulb}, {@code ward}, {@code region}).</li>
 *   <li>Recursively flattens complex nested module KPI metric objects into dot-separated column keys (e.g. {@code cess.usageCategory.RESIDENTIAL}).</li>
 *   <li>Supports state-configured delimiters (e.g. {@code |}, {@code ,}, {@code \t}) and custom file extensions (e.g. {@code .psv}, {@code .csv}).</li>
 *   <li>Works dynamically across all business modules (PT, ADV, CHB, FINANCE, PGR, etc.) without requiring module-specific file generators.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
public class DelimitedFileGeneratorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private TransformerRegistry transformerRegistry;

    @Autowired(required = false)
    private DashboardProperties dashboardProperties;

    @Autowired(required = false)
    private OAuthTokenService oAuthTokenService;

    /**
     * Default no-args constructor for Spring bean initialization.
     */
    public DelimitedFileGeneratorService() {
    }

    /**
     * Parameterized constructor for manual instantiation and unit testing.
     *
     * @param transformerRegistry registry used to resolve module transformers
     * @param dashboardProperties centralized system properties
     */
    public DelimitedFileGeneratorService(TransformerRegistry transformerRegistry, DashboardProperties dashboardProperties) {
        this(transformerRegistry, dashboardProperties, null);
    }

    public DelimitedFileGeneratorService(TransformerRegistry transformerRegistry, DashboardProperties dashboardProperties, OAuthTokenService oAuthTokenService) {
        this.transformerRegistry = transformerRegistry;
        this.dashboardProperties = dashboardProperties;
        this.oAuthTokenService = oAuthTokenService;
    }

    /**
     * Active streaming session wrapper holding an open file writer and temporary file handle.
     */
    public static class StreamingDelimitedSession implements AutoCloseable {

        private final String moduleName;
        private final String ingestionType;
        private final String delimiter;
        private final File tempFile;
        private final BufferedWriter writer;
        private final ObjectMapper objectMapper;
        private final TransformerRegistry transformerRegistry;
        private final OAuthTokenService oAuthTokenService;

        private int rowIndex = 0;
        private List<String> columnHeaders;

        /**
         * Initializes a streaming session by creating a temporary disk file with the configured extension and delimiter.
         *
         * @param moduleName          business module name (e.g. PT, ADV, CHB)
         * @param objectMapper        Jackson ObjectMapper for fallback serialization
         * @param transformerRegistry registry for transforming raw DTOs into normalized DashboardData
         * @param ingestionType       ingestion category (e.g. DAILY vs LEGACY)
         * @param delimiter           character separator (e.g. "|", ",")
         * @param fileExtension       file extension suffix (e.g. ".psv", ".csv")
         * @throws IOException if temporary file or writer creation fails
         */
        public StreamingDelimitedSession(String moduleName, ObjectMapper objectMapper,
                                        TransformerRegistry transformerRegistry,
                                        String ingestionType,
                                        String delimiter,
                                        String fileExtension) throws IOException {
            this(moduleName, objectMapper, transformerRegistry, null, ingestionType, delimiter, fileExtension);
        }

        public StreamingDelimitedSession(String moduleName, ObjectMapper objectMapper,
                                        TransformerRegistry transformerRegistry,
                                        OAuthTokenService oAuthTokenService,
                                        String ingestionType,
                                        String delimiter,
                                        String fileExtension) throws IOException {
            this.moduleName = moduleName;
            this.objectMapper = objectMapper;
            this.transformerRegistry = transformerRegistry;
            this.oAuthTokenService = oAuthTokenService;
            String type = (ingestionType != null && ingestionType.equalsIgnoreCase(DashboardConstants.DAILY))
                    ? DashboardConstants.DAILY
                    : DashboardConstants.LEGACY;
            this.ingestionType = type;
            this.delimiter = (delimiter != null && !delimiter.isEmpty()) ? delimiter : "|";
            String ext = (fileExtension != null && !fileExtension.isEmpty())
                    ? (fileExtension.startsWith(".") ? fileExtension : "." + fileExtension)
                    : ".psv";

            // Create temporary disk file for chunked row streaming
            this.tempFile = Files.createTempFile(type + "_" + moduleName + "_", ext).toFile();
            this.writer = new BufferedWriter(new FileWriter(this.tempFile));
        }

        /**
         * Extracts standard contextual fields and recursively flattens KPI metrics into an ordered map of column-value pairs.
         *
         * @param recordObj raw DTO or normalized DashboardData record
         * @return LinkedHashMap preserving insertion order of flattened headers and values
         */
        @SuppressWarnings("unchecked")
        public Map<String, Object> extractAndFlattenRecord(Object recordObj) {
            Map<String, Object> flatMap = new LinkedHashMap<>();

            // Generate row UUID and initialize context variables
            String uuid = null;
            if (oAuthTokenService != null) {
                try {
                    UserInfo userInfo = oAuthTokenService.getUserInfo();
                    if (userInfo != null && userInfo.getUuid() != null && !userInfo.getUuid().isEmpty()) {
                        uuid = userInfo.getUuid();
                    }
                } catch (Exception e) {
                    log.debug("Could not fetch user info for PSV row UUID: {}", e.getMessage());
                }
            }
            if (uuid == null || uuid.isEmpty()) {
                uuid = CommonUtils.generateUUID();
            }
            String date = "";
            String module = moduleName != null ? moduleName : "";
            String state = "";
            String ulb = "";
            String ward = "";
            String region = "";
            Map<String, Object> metrics = null;

            // Direct extraction if record is already normalized DashboardData
            if (recordObj instanceof DashboardData data) {
                if (data.getDate() != null) date = data.getDate();
                if (data.getModule() != null) module = data.getModule();
                if (data.getState() != null) state = data.getState();
                if (data.getUlb() != null) ulb = data.getUlb();
                if (data.getWard() != null) ward = data.getWard();
                if (data.getRegion() != null) region = data.getRegion();
                metrics = data.getMetrics();
            } else {
                // Attempt to transform raw DTO using TransformerRegistry
                List<DashboardData> dataList = null;
                if (transformerRegistry != null && moduleName != null) {
                    try {
                        Module mod = Module.valueOf(moduleName.toUpperCase());
                        ModuleTransformer<Object> transformer = transformerRegistry.get(mod);
                        if (transformer != null) {
                            DashboardPayload payload = transformer.transform(recordObj);
                            if (payload != null && payload.getData() != null && !payload.getData().isEmpty()) {
                                dataList = payload.getData();
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Could not transform record via TransformerRegistry for module {}: {}", moduleName, e.getMessage());
                    }
                }

                if (dataList != null && !dataList.isEmpty()) {
                    DashboardData data = dataList.get(0);
                    if (data.getDate() != null) date = data.getDate();
                    if (data.getModule() != null) module = data.getModule();
                    if (data.getState() != null) state = data.getState();
                    if (data.getUlb() != null) ulb = data.getUlb();
                    if (data.getWard() != null) ward = data.getWard();
                    if (data.getRegion() != null) region = data.getRegion();
                    metrics = data.getMetrics();
                } else {
                    // Fallback to Jackson map conversion if object is an arbitrary map or DTO
                    Map<String, Object> rawMap = (recordObj instanceof Map<?, ?> map)
                            ? (Map<String, Object>) map
                            : objectMapper.convertValue(recordObj, Map.class);

                    if (rawMap.get("date") != null) date = rawMap.get("date").toString();
                    if (rawMap.get("module") != null) module = rawMap.get("module").toString();
                    if (rawMap.get("state") != null) state = rawMap.get("state").toString();
                    Object tenantVal = rawMap.get("ulb");
                    if (tenantVal == null) tenantVal = rawMap.get("Tenant");
                    if (tenantVal == null) tenantVal = rawMap.get("tenant");
                    if (tenantVal == null) tenantVal = rawMap.get("tenantId");
                    if (tenantVal != null) ulb = tenantVal.toString();
                    if (rawMap.get("ward") != null) ward = rawMap.get("ward").toString();
                    if (rawMap.get("region") != null) region = rawMap.get("region").toString();

                    if (rawMap.get("metrics") instanceof Map<?, ?> m) {
                        metrics = (Map<String, Object>) m;
                    } else {
                        metrics = new LinkedHashMap<>(rawMap);
                        metrics.remove("date");
                        metrics.remove("module");
                        metrics.remove("state");
                        metrics.remove("ulb");
                        metrics.remove("Tenant");
                        metrics.remove("tenant");
                        metrics.remove("tenantId");
                        metrics.remove("ward");
                        metrics.remove("region");
                    }
                }
            }

            // Populate mandatory standard contextual headers first
            flatMap.put("uuid", uuid);
            flatMap.put("date", date);
            flatMap.put("module", module);
            flatMap.put("state", state);
            flatMap.put("ulb", ulb);
            flatMap.put("ward", ward);
            flatMap.put("region", region);

            // Recursively flatten module KPI metrics
            if (metrics != null) {
                for (Map.Entry<String, Object> entry : metrics.entrySet()) {
                    flattenMetricValue(entry.getKey(), entry.getValue(), flatMap);
                }
            }

            return flatMap;
        }

        /**
         * Helper method performing recursive dot-notation flattening for scalar values, nested maps, and bucket lists.
         *
         * @param prefix    current column key hierarchy prefix (e.g. "cess.usageCategory")
         * @param value     metric value object (Number, String, Map, or List of buckets)
         * @param targetMap map collecting finalized flattened key-value pairs
         */
        @SuppressWarnings("unchecked")
        private void flattenMetricValue(String prefix, Object value, Map<String, Object> targetMap) {
            if (value == null) {
                return;
            }
            if (value instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> itemMap) {
                        Object groupByObj = itemMap.get("groupBy");
                        Object bucketsObj = itemMap.get("buckets");

                        // Process standard groupBy + buckets list structure (e.g. { "groupBy": "usageCategory", "buckets": [...] })
                        if (groupByObj != null && bucketsObj instanceof List<?> bucketsList) {
                            String groupBy = groupByObj.toString();
                            for (Object bucketObj : bucketsList) {
                                if (bucketObj instanceof Map<?, ?> bucketMap) {
                                    Object nameObj = bucketMap.get("name");
                                    Object valObj = bucketMap.get("value");
                                    if (nameObj != null) {
                                        String colName = prefix + "." + groupBy + "." + nameObj.toString();
                                        targetMap.put(colName, valObj != null ? valObj : "");
                                    }
                                }
                            }
                        } else if (itemMap.containsKey("name") && itemMap.containsKey("value")) {
                            // Process simple name-value bucket pair
                            String colName = prefix + "." + itemMap.get("name");
                            targetMap.put(colName, itemMap.get("value") != null ? itemMap.get("value") : "");
                        } else {
                            // Process arbitrary map entries in list
                            for (Map.Entry<?, ?> subEntry : itemMap.entrySet()) {
                                flattenMetricValue(prefix + "." + subEntry.getKey(), subEntry.getValue(), targetMap);
                            }
                        }
                    } else {
                        targetMap.put(prefix, item);
                    }
                }
            } else if (value instanceof Map<?, ?> map) {
                // Process nested Map structure recursively
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    flattenMetricValue(prefix + "." + entry.getKey(), entry.getValue(), targetMap);
                }
            } else {
                // Scalar value (Integer, Double, String, Boolean)
                targetMap.put(prefix, value);
            }
        }

        /**
         * Appends a batch chunk of extracted records directly to the output delimited text file.
         * <p>
         * On the first batch chunk, dynamically constructs and writes the header row.
         * </p>
         *
         * @param records batch of record objects to append
         * @throws IOException on file write errors
         */
        public synchronized void appendBatchRecords(List<Object> records) throws IOException {
            if (records == null || records.isEmpty()) {
                return;
            }

            // Initialize column headers from all unique key paths present across records in the first batch chunk
            if (columnHeaders == null) {
                Map<String, Object> sampleHeadersMap = new LinkedHashMap<>();
                for (Object rec : records) {
                    sampleHeadersMap.putAll(extractAndFlattenRecord(rec));
                }
                columnHeaders = new ArrayList<>(sampleHeadersMap.keySet());
                writer.write(String.join(delimiter, columnHeaders));
                writer.newLine();
            }

            // Write data rows adhering strictly to the established columnHeaders order
            for (Object recordObj : records) {
                Map<String, Object> flatRecord = extractAndFlattenRecord(recordObj);
                List<String> rowValues = new ArrayList<>();
                for (String header : columnHeaders) {
                    Object val = flatRecord.get(header);
                    rowValues.add(val != null ? val.toString() : "");
                }
                writer.write(String.join(delimiter, rowValues));
                writer.newLine();
                rowIndex++;
            }
            writer.flush();
        }

        /**
         * Flushes buffered rows, logs metrics, and returns the finalized temporary file.
         *
         * @return generated File handle
         * @throws IOException on write/flush failure
         */
        public File finishFile() throws IOException {
            writer.flush();
            log.info("Finalized streaming delimited dataset file: {} (total rows written: {}, file size: {} bytes, delimiter: '{}')",
                    tempFile.getAbsolutePath(), rowIndex, tempFile.length(), delimiter);
            return tempFile;
        }

        /**
         * Returns the underlying temporary File handle.
         *
         * @return temporary dataset File
         */
        public File getTempFile() {
            return tempFile;
        }

        /**
         * Returns the delimiter character used by this session.
         *
         * @return delimiter string
         */
        public String getDelimiter() {
            return delimiter;
        }

        /**
         * Closes the underlying file writer safely.
         */
        @Override
        public void close() {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("Error closing delimited file writer: {}", e.getMessage());
            }
        }
    }

    /**
     * Creates a streaming delimited session defaulting to legacy batch mode.
     *
     * @param moduleName target module name
     * @return initialized StreamingDelimitedSession
     * @throws IOException on session initialization failure
     */
    public StreamingDelimitedSession createStreamingSession(String moduleName) throws IOException {
        return createStreamingSession(moduleName, DashboardConstants.LEGACY);
    }

    /**
     * Creates an active streaming session using configured delimiter and file extension properties.
     *
     * @param moduleName    target module name
     * @param ingestionType ingestion mode (DAILY or LEGACY)
     * @return initialized StreamingDelimitedSession
     * @throws IOException on session initialization failure
     */
    public StreamingDelimitedSession createStreamingSession(String moduleName, String ingestionType) throws IOException {
        String delimiter = (dashboardProperties != null && dashboardProperties.getFileDelimiter() != null)
                ? dashboardProperties.getFileDelimiter()
                : "|";
        String ext = (dashboardProperties != null && dashboardProperties.getDelimitedFileExtension() != null)
                ? dashboardProperties.getDelimitedFileExtension()
                : ".psv";
        return new StreamingDelimitedSession(moduleName, objectMapper, transformerRegistry, oAuthTokenService, ingestionType, delimiter, ext);
    }

    /**
     * Utility method to generate a delimited dataset file directly from an in-memory list of records (daily mode).
     *
     * @param moduleName target module name
     * @param records    records to serialize
     * @return generated File object
     * @throws IOException on file generation failure
     */
    public File generateDelimitedFile(String moduleName, List<Object> records) throws IOException {
        return generateDelimitedFile(moduleName, records, DashboardConstants.DAILY);
    }

    /**
     * Utility method to generate a delimited dataset file directly from an in-memory list of records.
     *
     * @param moduleName    target module name
     * @param records       records to serialize
     * @param ingestionType ingestion category (DAILY vs LEGACY)
     * @return generated File object
     * @throws IOException on file generation failure
     */
    public File generateDelimitedFile(String moduleName, List<Object> records, String ingestionType) throws IOException {
        try (StreamingDelimitedSession session = createStreamingSession(moduleName, ingestionType)) {
            session.appendBatchRecords(records);
            return session.finishFile();
        }
    }
}
