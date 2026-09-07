package org.upyog.dashboard.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for streaming large datasets into memory-safe Apache POI SXSSF Excel workbooks.
 */
@Slf4j
@Service
public class SXSSFExcelGeneratorService {

    private static final int MEMORY_ROW_WINDOW_SIZE = 100;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Session wrapper holding open SXSSFWorkbook and output destination.
     */
    public static class StreamingExcelSession implements AutoCloseable {
        private final String moduleName;
        private final SXSSFWorkbook workbook;
        private final Sheet sheet;
        private final CellStyle headerStyle;
        private final File tempFile;
        private final ObjectMapper objectMapper;

        private int rowIndex = 0;
        private java.util.Set<String> columnHeaders;

        /**
         * Initializes a streaming SXSSF Excel session with a temporary disk file.
         *
         * @param moduleName   module short code used in file and sheet naming
         * @param objectMapper ObjectMapper for serializing nested JSON column data
         * @throws IOException on temporary file creation failure
         */
        public StreamingExcelSession(String moduleName, ObjectMapper objectMapper) throws IOException {
            this.moduleName = moduleName;
            this.objectMapper = objectMapper;
            this.workbook = new SXSSFWorkbook(MEMORY_ROW_WINDOW_SIZE);
            this.workbook.setCompressTempFiles(true);
            this.sheet = workbook.createSheet(moduleName + "_LegacyData");

            this.headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            this.headerStyle.setFont(headerFont);

            this.tempFile = Files.createTempFile("legacy_ingest_" + moduleName + "_", ".xlsx").toFile();
        }

        @SuppressWarnings("unchecked")
        /**
         * Appends a chunk of extracted records directly to the streaming Excel worksheet.
         *
         * @param records batch of record objects to serialize into Excel rows
         */
        public synchronized void appendBatchRecords(List<Object> records) {
            if (records == null || records.isEmpty()) {
                return;
            }

            if (columnHeaders == null) {
                Map<String, Object> sampleMap = objectMapper.convertValue(records.get(0), Map.class);
                columnHeaders = sampleMap.keySet();

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
                Map<String, Object> recordMap = objectMapper.convertValue(recordObj, Map.class);
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
                                if (jsonStr.length() > 32765) {
                                    jsonStr = jsonStr.substring(0, 32765);
                                }
                                cell.setCellValue(jsonStr);
                            } catch (Exception exception) {
                                log.error("Failed to serialize complex cell object for column {}: {}", header, exception.getMessage());
                                cell.setCellValue(val.toString());
                            }
                        } else {
                            String strVal = val.toString();
                            if (strVal.length() > 32765) {
                                strVal = strVal.substring(0, 32765);
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
         * Writes buffered rows to the temporary file on disk and closes the stream.
         *
         * @return the generated File object
         * @throws IOException on file write error
         */
        public File finishWorkbook() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }
            log.info("Finalized streaming Excel file: {} (total rows written: {}, file size: {} bytes)",
                    tempFile.getAbsolutePath(), rowIndex, tempFile.length());
            return tempFile;
        }

        @Override
        public void close() {
            workbook.dispose();
        }
    }

    /**
     * Creates an active streaming Excel session for memory-safe chunked record generation.
     *
     * @param moduleName module name used in sheet and temp file naming
     * @return initialized StreamingExcelSession instance
     * @throws IOException on session creation failure
     */
    public StreamingExcelSession createStreamingSession(String moduleName) throws IOException {
        return new StreamingExcelSession(moduleName, objectMapper);
    }

    /**
     * Helper method to generate an Excel file directly from a list of records.
     *
     * @param moduleName a {@link java.lang.String} object
     * @param records a {@link java.util.List} object
     * @return a {@link java.io.File} object
     * @throws java.io.IOException if any.
     */
    public File generateExcelFile(String moduleName, List<Object> records) throws IOException {
        try (StreamingExcelSession session = createStreamingSession(moduleName)) {
            session.appendBatchRecords(records);
            return session.finishWorkbook();
        }
    }
}
