package org.upyog.dashboard.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.NationalDashboardIngestRequest;
import org.upyog.dashboard.model.RequestInfo;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;

/**
 * Service for streaming large datasets into memory-safe Apache POI SXSSF Excel
 * workbooks.
 */
@Slf4j
@Service
public class SXSSFExcelGeneratorService {

    private static final int MEMORY_ROW_WINDOW_SIZE = 100;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private TransformerRegistry transformerRegistry;

    @Autowired(required = false)
    private OAuthTokenService oAuthTokenService;

    @Autowired(required = false)
    private DelimitedFileGeneratorService delimitedFileGeneratorService;

    @Autowired(required = false)
    private DashboardProperties dashboardProperties;

    /**
     * Default no-argument constructor for Spring instantiation.
     */
    public SXSSFExcelGeneratorService() {
    }

    /**
     * Constructor initializing transformer registry and OAuth token service dependencies.
     *
     * @param transformerRegistry registry for looking up module-specific DTO transformers
     * @param oAuthTokenService   service for providing system user OAuth access tokens
     */
    public SXSSFExcelGeneratorService(TransformerRegistry transformerRegistry, OAuthTokenService oAuthTokenService) {
        this(transformerRegistry, oAuthTokenService, null, null);
    }

    /**
     * Fully parameterized constructor initializing transformer registry, OAuth token service, delimited file generator, and system properties.
     *
     * @param transformerRegistry           registry for looking up module-specific DTO transformers
     * @param oAuthTokenService             service for providing system user OAuth access tokens
     * @param delimitedFileGeneratorService service for parallel delimited file generation
     * @param dashboardProperties           centralized configuration properties
     */
    public SXSSFExcelGeneratorService(TransformerRegistry transformerRegistry, OAuthTokenService oAuthTokenService,
            DelimitedFileGeneratorService delimitedFileGeneratorService, DashboardProperties dashboardProperties) {
        this.transformerRegistry = transformerRegistry;
        this.oAuthTokenService = oAuthTokenService;
        this.delimitedFileGeneratorService = delimitedFileGeneratorService;
        this.dashboardProperties = dashboardProperties;
    }

    /**
     * Session wrapper holding open SXSSFWorkbook and output destination.
     */
    public static class StreamingExcelSession implements AutoCloseable {

        private final String moduleName;
        private final String ingestionType;
        private final SXSSFWorkbook workbook;
        private final Sheet sheet;
        private final CellStyle headerStyle;
        private final File tempFile;
        private final ObjectMapper objectMapper;
        private final TransformerRegistry transformerRegistry;
        private final OAuthTokenService oAuthTokenService;
        private final DelimitedFileGeneratorService.StreamingDelimitedSession delimitedSession;

        private int rowIndex = 0;
        private List<String> columnHeaders;

        /**
         * Initializes a streaming SXSSF Excel session with a temporary disk
         * file (defaults to legacy ingestion type).
         *
         * @param moduleName module short code used in file and sheet naming
         * @param objectMapper ObjectMapper for serializing nested JSON column
         * data
         * @throws IOException on temporary file creation failure
         */
        public StreamingExcelSession(String moduleName, ObjectMapper objectMapper) throws IOException {
            this(moduleName, objectMapper, null, null, null, null, DashboardConstants.LEGACY);
        }

        /**
         * Initializes a streaming SXSSF Excel session with transformer registry and OAuth token service dependencies.
         *
         * @param moduleName          module short code
         * @param objectMapper        Jackson ObjectMapper
         * @param transformerRegistry registry of module transformers
         * @param oAuthTokenService   OAuth token provider
         * @param ingestionType       DAILY or LEGACY
         * @throws IOException on file creation error
         */
        public StreamingExcelSession(String moduleName, ObjectMapper objectMapper,
                TransformerRegistry transformerRegistry,
                OAuthTokenService oAuthTokenService,
                String ingestionType) throws IOException {
            this(moduleName, objectMapper, transformerRegistry, oAuthTokenService, null, null, ingestionType);
        }

        /**
         * Fully parameterized constructor initializing both Excel streaming and optional delimited session streaming.
         *
         * @param moduleName                    module short code
         * @param objectMapper                  Jackson ObjectMapper
         * @param transformerRegistry           registry of module transformers
         * @param oAuthTokenService             OAuth token provider
         * @param delimitedFileGeneratorService service for generating flat delimited files
         * @param dashboardProperties           centralized system configuration properties
         * @param ingestionType                 DAILY or LEGACY
         * @throws IOException on file creation error
         */
        public StreamingExcelSession(String moduleName, ObjectMapper objectMapper,
                TransformerRegistry transformerRegistry,
                OAuthTokenService oAuthTokenService,
                DelimitedFileGeneratorService delimitedFileGeneratorService,
                DashboardProperties dashboardProperties,
                String ingestionType) throws IOException {
            this.moduleName = moduleName;
            this.objectMapper = objectMapper;
            this.transformerRegistry = transformerRegistry;
            this.oAuthTokenService = oAuthTokenService;
            String type = (ingestionType != null && ingestionType.equalsIgnoreCase(DashboardConstants.DAILY)) ? DashboardConstants.DAILY : DashboardConstants.LEGACY;
            this.ingestionType = type;
            boolean isDelimitedEnabled = (dashboardProperties != null) ? (dashboardProperties.isDelimitedFileEnabled() || dashboardProperties.isPipeFileEnabled()) : (delimitedFileGeneratorService != null);

            if (isDelimitedEnabled) {
                DelimitedFileGeneratorService fileService = (delimitedFileGeneratorService != null)
                        ? delimitedFileGeneratorService
                        : new DelimitedFileGeneratorService(transformerRegistry, dashboardProperties, oAuthTokenService);
                this.delimitedSession = fileService.createStreamingSession(moduleName, ingestionType);
                this.workbook = null;
                this.sheet = null;
                this.headerStyle = null;
                this.tempFile = null;
            } else {
                this.delimitedSession = null;
                this.workbook = new SXSSFWorkbook(MEMORY_ROW_WINDOW_SIZE);
                this.workbook.setCompressTempFiles(true);
                this.sheet = workbook.createSheet(moduleName + "_" + type);

                this.headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                this.headerStyle.setFont(headerFont);

                this.tempFile = Files.createTempFile(type + "_" + moduleName + "_", ".xlsx").toFile();
            }
        }

        /**
         * Returns the active streaming session for parallel flat delimited file generation, or {@code null} if disabled.
         *
         * @return StreamingDelimitedSession handle or null
         */
        public DelimitedFileGeneratorService.StreamingDelimitedSession getDelimitedSession() {
            return delimitedSession;
        }

        /**
         * Prepares an ordered Map representation of the record for Excel column writing.
         * Extracts tenant context and formats columns in standard order: date, module, state, Tenant, ward, region, payload_json.
         *
         * @param recordObj raw DTO or map record
         * @return LinkedHashMap of ordered header-value pairs
         */
        @SuppressWarnings("unchecked")
        private Map<String, Object> prepareRecordMap(Object recordObj) {
            Map<String, Object> rawMap = (recordObj instanceof Map<?, ?> map)
                    ? (Map<String, Object>) map
                    : objectMapper.convertValue(recordObj, Map.class);
            Map<String, Object> formattedMap = new LinkedHashMap<>();

            // Extract Tenant value from Tenant, ulb, tenant, or tenantId
            Object tenantVal = rawMap.get("Tenant");
            if (tenantVal == null) {
                tenantVal = rawMap.get("ulb");
            }
            if (tenantVal == null) {
                tenantVal = rawMap.get("tenant");
            }
            if (tenantVal == null) {
                tenantVal = rawMap.get("tenantId");
            }

            String payloadJson = generatePayloadJson(recordObj);

            // Cleanly order standard columns: date, module, state, Tenant, ward, region, payload_json
            formattedMap.put("date", rawMap.get("date"));
            formattedMap.put("module", rawMap.get("module"));
            formattedMap.put("state", rawMap.get("state"));
            formattedMap.put("Tenant", tenantVal);
            formattedMap.put("ward", rawMap.get("ward"));
            formattedMap.put("region", rawMap.get("region"));
            formattedMap.put("payload_json", payloadJson);

            // Append any other non-metric, non-ulb fields if present
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                String key = entry.getKey();
                if ("combinedMetrics".equalsIgnoreCase(key)
                        || "collectionMetrics".equalsIgnoreCase(key)
                        || "metrics".equalsIgnoreCase(key)
                        || "ulb".equalsIgnoreCase(key)
                        || "tenant".equalsIgnoreCase(key)
                        || "tenantId".equalsIgnoreCase(key)
                        || formattedMap.containsKey(key)) {
                    continue;
                }
                formattedMap.put(key, entry.getValue());
            }

            return formattedMap;
        }

        /**
         * Generates the serialized payload_json string containing RequestInfo and transformed DashboardData.
         * Validates that payload_json does not exceed the Excel cell character limit.
         *
         * @param recordObj record object to transform into NationalDashboardIngestRequest JSON
         * @return JSON string or empty string if transformation yields no data
         */
        @SuppressWarnings("unchecked")
        private String generatePayloadJson(Object recordObj) {
            try {
                List<DashboardData> dataList = null;

                if (recordObj instanceof DashboardData dashboardData) {
                    dataList = Collections.singletonList(dashboardData);
                } else if (recordObj instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof DashboardData) {
                    dataList = (List<DashboardData>) list;
                } else if (transformerRegistry != null && moduleName != null) {
                    try {
                        Module module = Module.valueOf(moduleName.toUpperCase());
                        ModuleTransformer<Object> transformer = transformerRegistry.get(module);
                        if (transformer != null) {
                            DashboardPayload payload = transformer.transform(recordObj);
                            if (payload != null) {
                                dataList = payload.getData();
                            }
                        }
                    } catch (Exception te) {
                        log.debug("Failed to transform record via TransformerRegistry for module {}: {}", moduleName, te.getMessage());
                    }
                }

                if (dataList != null) {
                    String oauthToken = null;
                    UserInfo userInfo = null;
                    if (oAuthTokenService != null) {
                        try {
                            oauthToken = oAuthTokenService.getToken();
                            userInfo = oAuthTokenService.getUserInfo();
                        } catch (Exception oe) {
                            log.debug("Could not fetch OAuth token for payload_json: {}", oe.getMessage());
                        }
                    }

                    RequestInfo requestInfo = RequestInfo.builder()
                            .apiId("Rainmaker")
                            .authToken(oauthToken)
                            .userInfo(userInfo)
                            .msgId(System.currentTimeMillis() + "|en_IN")
                            .build();

                    NationalDashboardIngestRequest ingestRequest = NationalDashboardIngestRequest.builder()
                            .requestInfo(requestInfo)
                            .data(dataList)
                            .build();

                    String payloadJsonString = objectMapper.writeValueAsString(ingestRequest);
                    if (payloadJsonString.length() > DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT) {
                        log.error("Failed to generate payload_json: Payload length ({}) exceeds maximum allowed Excel cell character limit ({}) for module {}",
                                payloadJsonString.length(), DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT, moduleName);
                        throw new ValidationException("Generated payload_json exceeds maximum Excel cell character limit of "
                                + DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT + " characters (actual length: " + payloadJsonString.length() + ")");
                    }
                    return payloadJsonString;
                }
                return "";
            } catch (ValidationException validationException) {
                throw validationException;
            } catch (Exception exception) {
                log.warn("Failed to generate payload_json for record in module {}: {}", moduleName, exception.getMessage());
                return "";
            }
        }

        /**
         * Appends a chunk of extracted records directly to the streaming Excel
         * worksheet.
         *
         * @param records batch of record objects to serialize into Excel rows
         */
        public synchronized void appendBatchRecords(List<Object> records) {
            if (records == null || records.isEmpty()) {
                return;
            }

            if (delimitedSession != null) {
                try {
                    delimitedSession.appendBatchRecords(records);
                } catch (IOException e) {
                    log.error("Failed to append batch records to delimited session for module {}: {}", moduleName, e.getMessage());
                }
                return;
            }

            if (columnHeaders == null) {
                Map<String, Object> sampleMap = prepareRecordMap(records.get(0));
                columnHeaders = new ArrayList<>(sampleMap.keySet());

                Row headerRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (String header : columnHeaders) {
                    Cell cell = headerRow.createCell(colIndex++);
                    cell.setCellValue(header);
                    cell.setCellStyle(headerStyle);
                }
            }

            for (Object recordObj : records) {
                Row dataRow = sheet.createRow(rowIndex++);
                Map<String, Object> recordMap = prepareRecordMap(recordObj);
                int colIndex = 0;

                for (String header : columnHeaders) {
                    Cell cell = dataRow.createCell(colIndex++);
                    Object val = recordMap.get(header);
                    if (val != null) {
                        if (val instanceof Number num) {
                            cell.setCellValue(num.doubleValue());
                        } else if (val instanceof Boolean boolVal) {
                            cell.setCellValue(boolVal);
                        } else if (val instanceof Map || val instanceof List) {
                            try {
                                String jsonStr = objectMapper.writeValueAsString(val);
                                if (jsonStr.length() > DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT) {
                                    jsonStr = jsonStr.substring(0, DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT);
                                }
                                cell.setCellValue(jsonStr);
                            } catch (Exception exception) {
                                log.error("Failed to serialize complex cell object for column {}: {}", header, exception.getMessage());
                                cell.setCellValue(val.toString());
                            }
                        } else {
                            String strVal = val.toString();
                            if (strVal.length() > DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT) {
                                strVal = strVal.substring(0, DashboardConstants.EXCEL_MAX_CELL_CHAR_LIMIT);
                            }
                            cell.setCellValue(strVal);
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
        }

        /**
         * Writes buffered rows to the temporary file on disk and closes the
         * stream.
         *
         * @return the generated File object
         * @throws IOException on file write error
         */
        public File finishWorkbook() throws IOException {
            if (delimitedSession != null) {
                return delimitedSession.finishFile();
            }
            if (workbook != null && tempFile != null) {
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    workbook.write(fos);
                }
                log.info("Finalized streaming Excel file: {} (total rows written: {}, file size: {} bytes)",
                        tempFile.getAbsolutePath(), rowIndex, tempFile.length());
                return tempFile;
            }
            return null;
        }

        @Override
        public void close() {
            if (workbook != null) {
                workbook.dispose();
            }
            if (delimitedSession != null) {
                delimitedSession.close();
            }
        }
    }

    /**
     * Creates an active streaming Excel session for memory-safe chunked record
     * generation.
     *
     * @param moduleName module name used in sheet and temp file naming
     * @return initialized StreamingExcelSession instance
     * @throws IOException on session creation failure
     */
    public StreamingExcelSession createStreamingSession(String moduleName) throws IOException {
        return createStreamingSession(moduleName, DashboardConstants.LEGACY);
    }

    /**
     * Creates an active streaming Excel session with an explicit ingestion type.
     *
     * @param moduleName    module name used in sheet and temp file naming
     * @param ingestionType DAILY or LEGACY ingestion mode
     * @return initialized StreamingExcelSession instance
     * @throws IOException on session creation failure
     */
    public StreamingExcelSession createStreamingSession(String moduleName, String ingestionType) throws IOException {
        return new StreamingExcelSession(moduleName, objectMapper, transformerRegistry, oAuthTokenService, delimitedFileGeneratorService, dashboardProperties, ingestionType);
    }

    /**
     * Helper method to generate an Excel file directly from a list of records.
     *
     * @param moduleName target module name
     * @param records    records to serialize
     * @return generated File handle
     * @throws IOException on file writing failure
     */
    public File generateExcelFile(String moduleName, List<Object> records) throws IOException {
        return generateExcelFile(moduleName, records, DashboardConstants.DAILY);
    }

    /**
     * Helper method to generate an Excel file directly from a list of records with specified ingestion type.
     *
     * @param moduleName    target module name
     * @param records       records to serialize
     * @param ingestionType DAILY or LEGACY ingestion mode
     * @return generated File handle
     * @throws IOException on file writing failure
     */
    public File generateExcelFile(String moduleName, List<Object> records, String ingestionType) throws IOException {
        try (StreamingExcelSession session = createStreamingSession(moduleName, ingestionType)) {
            session.appendBatchRecords(records);
            return session.finishWorkbook();
        }
    }
}
