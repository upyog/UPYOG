package org.upyog.Automation.Utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelDataReader {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDataReader.class);

    private static final String EXCEL_FILE =
            AutomationConstants.DEFAULT_EXCEL_FILE;

    /*
     * Uploaded Excel is the PRIMARY source.
     * Bundled Excel is used only when no Excel has been uploaded.
     */
    private static File uploadedExcelFile;


    // =========================================================
    // UPLOADED EXCEL MANAGEMENT
    // =========================================================

    public static void setUploadedExcelFile(File file) {
        uploadedExcelFile = file;
    }

    public static void clearUploadedExcelFile() {
        uploadedExcelFile = null;
    }

    public static boolean hasUploadedExcelFile() {
        return uploadedExcelFile != null
                && uploadedExcelFile.exists();
    }

    public static File getUploadedExcelFile() {
        return uploadedExcelFile;
    }


    // =========================================================
    // READ EXCEL SHEET
    // =========================================================

    public static List<Map<String, String>> readSheet(
            String sheetName) {

        List<Map<String, String>> testData =
                new ArrayList<>();

        /*
         * IMPORTANT:
         *
         * Uploaded Excel → PRIMARY
         * Bundled Excel   → FALLBACK
         */
        try (InputStream inputStream =
                     hasUploadedExcelFile()
                             ? new FileInputStream(
                             uploadedExcelFile)
                             : ExcelDataReader.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     EXCEL_FILE)) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "Excel file not found: "
                                + EXCEL_FILE
                );
            }

            Workbook workbook =
                    WorkbookFactory.create(inputStream);

            Sheet sheet = getSheetSafely(workbook, sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "Sheet not found in Excel: "
                                + sheetName
                );
            }

            // First row contains column names
            Row headerRow =
                    sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException(
                        "Excel sheet is empty: "
                                + sheetName
                );
            }

            List<String> headers =
                    new ArrayList<>();

            DataFormatter formatter =
                    new DataFormatter();

            for (Cell cell : headerRow) {

                headers.add(
                        formatter
                                .formatCellValue(cell)
                                .trim()
                );
            }

            // Read each data row
            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row =
                        sheet.getRow(rowIndex);

                if (row == null) {
                    continue;
                }

                Map<String, String> rowData =
                        new LinkedHashMap<>();

                for (int columnIndex = 0;
                     columnIndex < headers.size();
                     columnIndex++) {

                    Cell cell =
                            row.getCell(
                                    columnIndex,
                                    Row.MissingCellPolicy
                                            .CREATE_NULL_AS_BLANK
                            );

                    rowData.put(
                            headers.get(columnIndex),
                            getCellValue(cell)
                    );
                }

                // Only execute rows where Execute = YES
                String execute =
                        rowData.get(AutomationConstants.EXCEL_COLUMN_EXECUTE);

                if (AutomationConstants.EXCEL_EXECUTE_YES.equalsIgnoreCase(execute)) {
                    testData.add(rowData);
                }
            }

            workbook.close();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read Excel sheet: "
                            + sheetName,
                    e
            );
        }

        return testData;
    }


    // =========================================================
    // CHECK SHEET
    // =========================================================

    public static boolean hasSheet(
            String sheetName) {

        /*
         * Uploaded Excel → PRIMARY
         * Bundled Excel   → FALLBACK
         */
        try (InputStream inputStream =
                     hasUploadedExcelFile()
                             ? new FileInputStream(
                             uploadedExcelFile)
                             : ExcelDataReader.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     EXCEL_FILE)) {

            if (inputStream == null) {
                return false;
            }

            Workbook workbook =
                    WorkbookFactory.create(inputStream);

            boolean exists =
                    getSheetSafely(workbook, sheetName) != null;

            workbook.close();

            return exists;

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Resiliently finds a sheet by exact name, case-insensitively, or via standard aliases.
     *
     * @param workbook the active Excel workbook
     * @param sheetName the sheet name to search for
     * @return the resolved {@link Sheet}, or {@code null} if not found
     */
    public static Sheet getSheetSafely(Workbook workbook, String sheetName) {
        if (workbook == null || sheetName == null) {
            return null;
        }

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet != null) {
            return sheet;
        }

        // 1. Direct case-insensitive match
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet s = workbook.getSheetAt(i);
            if (s.getSheetName().equalsIgnoreCase(sheetName)) {
                return s;
            }
        }

        // 2. Normalized matching (stripping non-alphanumeric chars)
        String normTarget = sheetName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet s = workbook.getSheetAt(i);
            String normSheet = s.getSheetName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (normSheet.equals(normTarget)) {
                return s;
            }
        }

        // 3. Known module alias mappings (Full name <-> Abbreviation)
        Map<String, List<String>> aliases = Map.ofEntries(
                Map.entry("pet", List.of("pet", "petregistration", "ptr")),
                Map.entry("tradelicense", List.of("tradelicense", "tl", "tlmodule")),
                Map.entry("streetvending", List.of("streetvending", "sv")),
                Map.entry("waterandsewerage", List.of("waterandsewerage", "ws", "watersewerage", "water", "sewerage")),
                Map.entry("propertytax", List.of("propertytax", "pt")),
                Map.entry("publicgrievanceredressal", List.of("publicgrievanceredressal", "publicgrievance", "pgr")),
                Map.entry("advertisement", List.of("advertisement", "adv", "ads")),
                Map.entry("ewaste", List.of("ewaste", "ew")),
                Map.entry("communityhallbooking", List.of("communityhallbooking", "communityhall", "chb")),
                Map.entry("constructionanddemolition", List.of("constructionanddemolition", "cnd")),
                Map.entry("desludging", List.of("desludging", "fsm")),
                Map.entry("garbagecollection", List.of("garbagecollection", "gc")),
                Map.entry("estatemanagement", List.of("estatemanagement", "estate")),
                Map.entry("noduecertificate", List.of("noduecertificate", "ndc")),
                Map.entry("assetmanagement", List.of("assetmanagement", "asset")),
                Map.entry("challangeneration", List.of("challangeneration", "challan", "cg")),
                Map.entry("treepruning", List.of("treepruning", "tp")),
                Map.entry("watertanker", List.of("watertanker", "wt")),
                Map.entry("mobiletoilet", List.of("mobiletoilet", "mt")),
                Map.entry("obpas", List.of("obpas", "bpa"))
        );

        String cleanTarget = normTarget.replace("testdata", "").replace("test", "").replace("data", "");

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet s = workbook.getSheetAt(i);
            String cleanSheet = s.getSheetName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase()
                    .replace("testdata", "").replace("test", "").replace("data", "");

            if (cleanSheet.equals(cleanTarget)) {
                return s;
            }

            for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
                List<String> list = entry.getValue();
                boolean targetMatches = list.contains(cleanTarget) || cleanTarget.contains(entry.getKey());
                boolean sheetMatches = list.contains(cleanSheet) || cleanSheet.contains(entry.getKey());
                if (targetMatches && sheetMatches) {
                    return s;
                }
            }
        }

        return null;
    }


    // =========================================================
    // CELL VALUE
    // =========================================================

    private static String getCellValue(
            Cell cell) {

        DataFormatter formatter =
                new DataFormatter();

        return formatter
                .formatCellValue(cell)
                .trim();
    }


    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {
        logger.info("ExcelDataReader initialized. Default excel: {}", EXCEL_FILE);
        for (String sheet : new String[]{
                AutomationConstants.SHEET_PET,
                AutomationConstants.SHEET_PGR,
                AutomationConstants.SHEET_PT,
                AutomationConstants.SHEET_NDC
        }) {
            if (hasSheet(sheet)) {
                List<Map<String, String>> data = readSheet(sheet);
                logger.info("Sheet '{}' has {} executable rows.", sheet, data.size());
            }
        }
    }
}