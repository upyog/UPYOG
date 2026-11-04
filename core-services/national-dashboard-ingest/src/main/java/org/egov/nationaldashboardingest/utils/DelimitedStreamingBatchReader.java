package org.egov.nationaldashboardingest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Memory-efficient streaming reader component for processing large delimited
 * text files (such as CSV, Pipe-separated PSV, or Tab-separated TSV files)
 * line-by-line.
 * <p>
 * Bypasses spreadsheet row count limits (1,048,576 rows) and cell character
 * caps (32,767 chars). Supports automatic un-flattening of dot-notation column
 * headers (e.g., {@code "cess.usageCategory.RESIDENTIAL"}) into nested UPYOG
 * {@link Data} DTOs.
 * </p>
 */
@Slf4j
@Component
public class DelimitedStreamingBatchReader {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Reads a delimited text file line-by-line in memory-efficient streaming
     * batches, triggering the supplied consumer callback for each batch.
     *
     * @param textFile local delimited text file to read
     * @param customDelimiter optional caller-specified delimiter (e.g. "|",
     * ","). Auto-detected if blank.
     * @param batchProcessor callback consumer receiving batches of parsed
     * {@link IngestRowData}
     * @return total number of data rows parsed and dispatched
     */
    public int processDelimitedFileInBatches(File textFile, String customDelimiter, Consumer<List<IngestRowData>> batchProcessor) {
        int totalRowsProcessed = 0;
        List<IngestRowData> currentBatch = new ArrayList<>();
        int batchSize = (applicationProperties != null && applicationProperties.getBatchSize() > 0)
                ? applicationProperties.getBatchSize() : 100;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (StringUtils.isBlank(headerLine)) {
                log.warn("Delimited text file {} is empty.", textFile.getName());
                return 0;
            }

            // Step 1: Detect delimiter
            String delimiter = resolveDelimiter(headerLine, customDelimiter);
            String regexDelimiter = java.util.regex.Pattern.quote(delimiter);

            // Step 2: Parse headers
            String[] headers = headerLine.split(regexDelimiter, -1);
            List<ColumnHeaderMeta> headerMetaList = parseHeaders(headers);

            log.info("Processing delimited file [{}] with delimiter [{}] and {} headers",
                    textFile.getName(), delimiter, headers.length);

            // Step 3: Stream data rows
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                IngestRowData rowData = parseDataRow(line, regexDelimiter, headerMetaList, lineNumber);
                if (rowData != null && rowData.getData() != null) {
                    currentBatch.add(rowData);
                    totalRowsProcessed++;

                    if (currentBatch.size() == batchSize) {
                        batchProcessor.accept(new ArrayList<>(currentBatch));
                        currentBatch.clear();
                    }
                }
            }

            // Dispatch final remaining batch
            if (!currentBatch.isEmpty()) {
                batchProcessor.accept(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }

            log.info("Finished streaming delimited file [{}]. Total data rows processed: {}", textFile.getName(), totalRowsProcessed);

        } catch (Exception e) {
            log.error("Error reading delimited text file in batches: {}", textFile.getName(), e);
            throw new RuntimeException("Failed to read delimited file rows: " + e.getMessage(), e);
        }

        return totalRowsProcessed;
    }

    /**
     * Resolves effective delimiter character.
     */
    private String resolveDelimiter(String headerLine, String customDelimiter) {
        if (StringUtils.isNotBlank(customDelimiter)) {
            return customDelimiter;
        }
        if (headerLine.contains("|")) {
            return "|";
        }
        if (headerLine.contains("\t")) {
            return "\t";
        }
        if (headerLine.contains(";")) {
            return ";";
        }
        return ",";
    }

    /**
     * Header metadata structure for tracking scalar vs bucketed dot-notation
     * metric headers.
     */
    private static class ColumnHeaderMeta {

        int index;
        String rawHeader;
        HeaderType type;
        String metricName;
        String groupByField;
        String bucketName;

        enum HeaderType {
            DATE, MODULE, STATE, ULB, WARD, REGION, METRICS_JSON, SCALAR_METRIC, BUCKET_METRIC, UUID
        }
    }

    /**
     * Analyzes line 1 column headers to classify standard demographics vs
     * dynamic metric columns.
     */
    private List<ColumnHeaderMeta> parseHeaders(String[] headers) {
        List<ColumnHeaderMeta> metaList = new ArrayList<>();
        for (int i = 0; i < headers.length; i++) {
            ColumnHeaderMeta meta = new ColumnHeaderMeta();
            meta.index = i;
            meta.rawHeader = headers[i].trim();
            String lower = meta.rawHeader.toLowerCase();

            if ("date".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.DATE;
            } else if ("module".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.MODULE;
            } else if ("state".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.STATE;
            } else if ("ulb".equals(lower) || "tenantid".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.ULB;
            } else if ("ward".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.WARD;
            } else if ("region".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.REGION;
            } else if ("uuid".equals(lower) || "useruuid".equals(lower) || "user_uuid".equals(lower) || "user.uuid".equals(lower) || "userinfo.uuid".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.UUID;
            } else if ("metrics".equals(lower) || "payload_json".equals(lower) || "metrics_json".equals(lower)) {
                meta.type = ColumnHeaderMeta.HeaderType.METRICS_JSON;
            } else {
                // Check if column follows dot notation: metricName.groupByField.bucketName
                String[] parts = meta.rawHeader.split("\\.");
                if (parts.length >= 3) {
                    meta.type = ColumnHeaderMeta.HeaderType.BUCKET_METRIC;
                    meta.metricName = parts[0].trim();
                    meta.groupByField = parts[1].trim();
                    meta.bucketName = parts[2].trim();
                } else if (parts.length == 2) {
                    meta.type = ColumnHeaderMeta.HeaderType.BUCKET_METRIC;
                    meta.metricName = parts[0].trim();
                    meta.groupByField = "usageCategory";
                    meta.bucketName = parts[1].trim();
                } else {
                    meta.type = ColumnHeaderMeta.HeaderType.SCALAR_METRIC;
                    meta.metricName = meta.rawHeader;
                }
            }
            metaList.add(meta);
        }
        return metaList;
    }

    /**
     * Parses a single data line into an {@link IngestRowData} object and
     * un-flattens metric fields.
     */
    private IngestRowData parseDataRow(String line, String regexDelimiter, List<ColumnHeaderMeta> headerMetaList, int lineNumber) {
        try {
            String[] tokens = line.split(regexDelimiter, -1);

            String date = "";
            String module = "";
            String state = "";
            String ulb = "";
            String ward = "";
            String region = "";
            String uuid = "";
            HashMap<String, Object> metricsMap = new HashMap<>();

            // Intermediate map for bucket metrics: metricName -> (groupByField -> List of bucket maps)
            Map<String, Map<String, List<Map<String, Object>>>> groupedBucketsMap = new LinkedHashMap<>();

            for (ColumnHeaderMeta meta : headerMetaList) {
                if (meta.index >= tokens.length) {
                    continue;
                }
                String val = tokens[meta.index].trim();

                switch (meta.type) {
                    case DATE:
                        date = val;
                        break;
                    case MODULE:
                        module = val;
                        break;
                    case STATE:
                        state = val;
                        break;
                    case ULB:
                        ulb = val;
                        break;
                    case WARD:
                        ward = val;
                        break;
                    case REGION:
                        region = val;
                        break;
                    case UUID:
                        uuid = val;
                        break;
                    case METRICS_JSON:
                        if (StringUtils.isNotBlank(val) && val.startsWith("{")) {
                            Map<String, Object> parsedJson = objectMapper.readValue(val, HashMap.class);
                            if (parsedJson != null) {
                                metricsMap.putAll(parsedJson);
                            }
                        }
                        break;
                    case SCALAR_METRIC:
                        if (StringUtils.isNotBlank(val)) {
                            metricsMap.put(meta.metricName, parseValue(val));
                        }
                        break;
                    case BUCKET_METRIC:
                        if (StringUtils.isNotBlank(val)) {
                            Object parsedVal = parseValue(val);
                            Map<String, Object> bucketItem = new LinkedHashMap<>();
                            bucketItem.put("name", meta.bucketName);
                            bucketItem.put("value", parsedVal);

                            groupedBucketsMap
                                    .computeIfAbsent(meta.metricName, k -> new LinkedHashMap<>())
                                    .computeIfAbsent(meta.groupByField, k -> new ArrayList<>())
                                    .add(bucketItem);
                        }
                        break;
                }
            }

            // Un-flatten collected bucket metric groups into metricsMap
            for (Map.Entry<String, Map<String, List<Map<String, Object>>>> metricEntry : groupedBucketsMap.entrySet()) {
                String metricName = metricEntry.getKey();
                List<Map<String, Object>> groupList = new ArrayList<>();

                for (Map.Entry<String, List<Map<String, Object>>> groupEntry : metricEntry.getValue().entrySet()) {
                    String groupByField = groupEntry.getKey();
                    List<Map<String, Object>> bucketList = groupEntry.getValue();

                    Map<String, Object> groupObj = new LinkedHashMap<>();
                    groupObj.put("groupBy", groupByField);
                    groupObj.put("buckets", bucketList);
                    groupList.add(groupObj);
                }
                metricsMap.put(metricName, groupList);
            }

            // Skip row if core date or module is missing
            if (StringUtils.isBlank(date) || StringUtils.isBlank(module)) {
                return null;
            }

            Data data = Data.builder()
                    .date(date)
                    .module(module)
                    .state(state)
                    .ulb(ulb)
                    .ward(ward)
                    .region(region)
                    .metrics(metricsMap)
                    .build();

            org.egov.common.contract.request.RequestInfo rowRequestInfo = null;
            if (StringUtils.isNotBlank(uuid)) {
                org.egov.common.contract.request.User user = org.egov.common.contract.request.User.builder()
                        .uuid(uuid)
                        .tenantId(StringUtils.isNotBlank(ulb) ? ulb : state)
                        .build();
                rowRequestInfo = org.egov.common.contract.request.RequestInfo.builder()
                        .apiId("org.egov.nationaldashboardingest")
                        .ver("1.0")
                        .ts(System.currentTimeMillis())
                        .msgId(java.util.UUID.randomUUID().toString())
                        .userInfo(user)
                        .build();
            }

            return IngestRowData.builder()
                    .requestInfo(rowRequestInfo)
                    .data(data)
                    .build();

        } catch (Exception e) {
            log.warn("Skipping line {}: unable to parse delimited record - {}", lineNumber, e.getMessage());
            return null;
        }
    }

    /**
     * Converts string token to integer, double, boolean, or string.
     */
    private Object parseValue(String val) {
        if (NumberUtils.isCreatable(val)) {
            try {
                if (val.contains(".")) {
                    return Double.parseDouble(val);
                } else {
                    long l = Long.parseLong(val);
                    if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                        return (int) l;
                    }
                    return l;
                }
            } catch (Exception e) {
                return val;
            }
        }
        if ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
            return Boolean.parseBoolean(val);
        }
        return val;
    }
}
