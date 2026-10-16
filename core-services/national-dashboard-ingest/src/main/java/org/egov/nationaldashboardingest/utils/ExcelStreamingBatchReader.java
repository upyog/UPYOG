package org.egov.nationaldashboardingest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRequest;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Streaming reader component for memory-safe processing of large Excel spreadsheet datasets.
 * <p>
 * Key capabilities:
 * <ul>
 *   <li>Sequential row iteration via Apache POI {@link WorkbookFactory} in fixed streaming batches (default 100).</li>
 *   <li>Intelligent JSON detection: scans cells for full {@link IngestRequest} or {@link Data} JSON strings
 *       (such as those generated in the {@code payload_json} column).</li>
 *   <li>Common {@link org.egov.common.contract.request.RequestInfo} extraction: dynamically extracts and caches the authentication
 *       and caller context from the first valid row, assigning it to subsequent rows lacking explicit headers.</li>
 *   <li>Column-based fallback: parses traditional tabular columns (date, module, state, ULB, ward, region, metrics)
 *       when raw JSON strings are not present.</li>
 *   <li>Row empty check and error-resilient cell string conversion across numeric, string, date, and formula types.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class ExcelStreamingBatchReader {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Reads an Excel spreadsheet file row-by-row in memory-efficient batches of 100 rows,
     * triggering the supplied consumer callback for each batch.
     * <p>
     * Guarantees that any partial final batch is dispatched before returning.
     * Catches and encapsulates I/O and workbook parsing errors in a {@link RuntimeException}.
     * </p>
     *
     * @param excelFile      local Excel spreadsheet file to read
     * @param batchProcessor callback consumer that receives and processes each batch of parsed {@link IngestRowData}
     * @return total number of valid data rows parsed and successfully passed to the batch processor
     * @throws RuntimeException if an unrecoverable I/O or POI workbook error occurs
     */
    public int processExcelFileInBatches(File excelFile, Consumer<List<IngestRowData>> batchProcessor) {
        int totalRowsProcessed = 0;
        List<IngestRowData> currentBatch = new ArrayList<>();
        java.util.concurrent.atomic.AtomicReference<org.egov.common.contract.request.RequestInfo> commonRequestInfoHolder = new java.util.concurrent.atomic.AtomicReference<>();

        try (InputStream inputStream = new FileInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) {
                    continue;
                }

                List<IngestRowData> dataRows = parseDataRowsFromRow(row, commonRequestInfoHolder);
                if (dataRows != null && !dataRows.isEmpty()) {
                    for (IngestRowData rowData : dataRows) {
                        if (rowData.getRequestInfo() == null && commonRequestInfoHolder.get() != null) {
                            rowData.setRequestInfo(commonRequestInfoHolder.get());
                        }
                        currentBatch.add(rowData);
                        totalRowsProcessed++;

                        if (currentBatch.size() == applicationProperties.getBatchSize()) {
                            batchProcessor.accept(new ArrayList<>(currentBatch));
                            currentBatch.clear();
                        }
                    }
                }
            }

            // Process final remaining batch
            if (!currentBatch.isEmpty()) {
                if (commonRequestInfoHolder.get() != null) {
                    for (IngestRowData r : currentBatch) {
                        if (r.getRequestInfo() == null) {
                            r.setRequestInfo(commonRequestInfoHolder.get());
                        }
                    }
                }
                batchProcessor.accept(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }

            log.info("Finished streaming Excel file. Total data rows processed: {}", totalRowsProcessed);

        } catch (Exception e) {
            log.error("Error while reading Excel file in batches", e);
            throw new RuntimeException("Failed to read Excel file rows: " + e.getMessage(), e);
        }

        return totalRowsProcessed;
    }

    /**
     * Parses a single Excel row into one or more {@link IngestRowData} objects.
     * <p>
     * Strategy:
     * <ol>
     *   <li>First scans cells for JSON payloads matching {@link IngestRequest} or {@link Data}.
     *       If an {@link IngestRequest} is found on row 1, sets the common {@link org.egov.common.contract.request.RequestInfo} reference.</li>
     *   <li>If no JSON payload is found, parses individual cell columns by ordinal index:
     *       0: date, 1: module, 2: state, 3: ulb, 4: ward, 5: region, 6: metrics JSON.</li>
     *   <li>Skips header rows and unparseable rows safely.</li>
     * </ol>
     * </p>
     *
     * @param row                    the Apache POI row instance
     * @param commonRequestInfoHolder atomic reference holding the common RequestInfo extracted from the first row
     * @return list of parsed {@link IngestRowData} objects, or an empty list if header/unparseable
     */
    private List<IngestRowData> parseDataRowsFromRow(Row row, java.util.concurrent.atomic.AtomicReference<org.egov.common.contract.request.RequestInfo> commonRequestInfoHolder) {
        try {
            // STEP 1: Scan row cells for raw JSON payload string (e.g. payload_json in Column G or Column A)
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) continue;

                String cellValue = getCellValueAsString(cell).trim();
                if (cellValue.startsWith("{") && cellValue.endsWith("}")) {
                    if (cellValue.contains("\"Data\"") || cellValue.contains("\"data\"") || cellValue.contains("\"RequestInfo\"")) {
                        try {
                            IngestRequest ingestRequest = objectMapper.readValue(cellValue, IngestRequest.class);
                            if (ingestRequest != null && ingestRequest.getIngestData() != null && !ingestRequest.getIngestData().isEmpty()) {

                                // Pick common RequestInfo from the 1st row object of the Excel file
                                if (ingestRequest.getRequestInfo() != null && commonRequestInfoHolder.get() == null) {
                                    commonRequestInfoHolder.set(ingestRequest.getRequestInfo());
                                    log.info("Extracted common RequestInfo from 1st row object of Excel: apiId={}, msgId={}",
                                            ingestRequest.getRequestInfo().getApiId(), ingestRequest.getRequestInfo().getMsgId());
                                }

                                org.egov.common.contract.request.RequestInfo rowReqInfo = ingestRequest.getRequestInfo() != null
                                        ? ingestRequest.getRequestInfo()
                                        : commonRequestInfoHolder.get();

                                List<IngestRowData> rowDataList = new ArrayList<>();
                                for (Data d : ingestRequest.getIngestData()) {
                                    rowDataList.add(IngestRowData.builder()
                                            .requestInfo(rowReqInfo)
                                            .data(d)
                                            .build());
                                }
                                return rowDataList;
                            }
                        } catch (Exception e) {
                            log.debug("Cell {} is not a valid IngestRequest JSON: {}", i, e.getMessage());
                        }
                    }

                    try {
                        Data dataObj = objectMapper.readValue(cellValue, Data.class);
                        if (dataObj != null && (dataObj.getDate() != null || dataObj.getUlb() != null)) {
                            return Collections.singletonList(IngestRowData.builder()
                                    .requestInfo(commonRequestInfoHolder.get())
                                    .data(dataObj)
                                    .build());
                        }
                    } catch (Exception e) {
                        log.debug("Cell {} is not a valid Data JSON: {}", i, e.getMessage());
                    }
                }
            }

            // STEP 2: Fallback to individual column parsing (date, module, state, Tenant/ulb, ward, region, metrics)
            String date = getCellValueAsString(row.getCell(0)).trim();
            if (StringUtils.isBlank(date) || "date".equalsIgnoreCase(date) || "payload_json".equalsIgnoreCase(date)) {
                // Header row or blank
                return Collections.emptyList();
            }

            String module = getCellValueAsString(row.getCell(1)).trim();
            String state = getCellValueAsString(row.getCell(2)).trim();
            String ulb = getCellValueAsString(row.getCell(3)).trim();
            String ward = getCellValueAsString(row.getCell(4)).trim();
            String region = getCellValueAsString(row.getCell(5)).trim();
            String metricsJson = getCellValueAsString(row.getCell(6)).trim();

            HashMap<String, Object> metricsMap = new HashMap<>();
            if (StringUtils.isNotBlank(metricsJson) && metricsJson.startsWith("{")) {
                metricsMap = objectMapper.readValue(metricsJson, HashMap.class);
            }

            Data dataRow = Data.builder()
                    .date(date)
                    .module(module)
                    .state(state)
                    .ulb(ulb)
                    .ward(ward)
                    .region(region)
                    .metrics(metricsMap)
                    .build();

            return Collections.singletonList(IngestRowData.builder()
                    .requestInfo(commonRequestInfoHolder.get())
                    .data(dataRow)
                    .build());

        } catch (Exception e) {
            log.warn("Skipping row {}: unable to parse data - {}", row.getRowNum(), e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Extracts cell text value cleanly regardless of underlying Apache POI {@link CellType}.
     *
     * @param cell the POI {@link Cell} to inspect
     * @return normalized string representation of the cell content, or empty string if null
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Checks whether an Excel row is empty or contains only whitespace/blank cells.
     *
     * @param row the Apache POI {@link Row} to check
     * @return {@code true} if row is null or contains only blank cells; {@code false} if non-blank data exists
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }
}
