package org.upyog.Automation.Utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.upyog.Automation.model.ModuleExecutionResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelResultGenerator {

    private static final String STATUS_COLUMN = AutomationConstants.EXCEL_COLUMN_STATUS;
    private static final String REMARK_COLUMN = AutomationConstants.EXCEL_COLUMN_REMARK;
    private static final String FAILED_STEP_COLUMN = AutomationConstants.EXCEL_COLUMN_FAILED_STEPS;

    /**
     * Generates a result Excel from the source Excel.
     *
     * The original Excel is NOT modified.
     *
     * New columns added:
     * - Status
     * - Remark
     * - Failed Steps
     */
    public static File generateResultExcel(
            File sourceExcel,
            String sheetName,
            List<ModuleExecutionResult> results,
            File outputFile) {

        try (
                FileInputStream inputStream =
                        new FileInputStream(sourceExcel);

                Workbook workbook =
                        WorkbookFactory.create(inputStream)
        ) {

            Sheet sheet =
                    ExcelDataReader.getSheetSafely(workbook, sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "Sheet not found in Excel: "
                                + sheetName
                );
            }

            Row headerRow =
                    sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException(
                        "Excel sheet is empty: "
                                + sheetName
                );
            }

            // Find existing result columns
            int statusColumn =
                    findColumn(headerRow, STATUS_COLUMN);

            int remarkColumn =
                    findColumn(headerRow, REMARK_COLUMN);

            int failedStepColumn =
                    findColumn(headerRow, FAILED_STEP_COLUMN);


            // Add columns if they do not already exist
            int nextColumn =
                    headerRow.getLastCellNum();

            if (nextColumn < 0) {
                nextColumn = 0;
            }

            if (statusColumn == -1) {

                statusColumn = nextColumn++;

                headerRow.createCell(statusColumn)
                        .setCellValue(STATUS_COLUMN);
            }

            if (remarkColumn == -1) {

                remarkColumn = nextColumn++;

                headerRow.createCell(remarkColumn)
                        .setCellValue(REMARK_COLUMN);
            }

            if (failedStepColumn == -1) {

                failedStepColumn = nextColumn++;

                headerRow.createCell(failedStepColumn)
                        .setCellValue(FAILED_STEP_COLUMN);
            }


            // Update test-case rows
            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row =
                        sheet.getRow(rowIndex);

                if (row == null) {
                    continue;
                }

                String testCase =
                        getCellValue(
                                row,
                                findColumn(
                                        headerRow,
                                        AutomationConstants.EXCEL_COLUMN_TEST_CASE
                                )
                        );

                String execute =
                        getCellValue(
                                row,
                                findColumn(
                                        headerRow,
                                        AutomationConstants.EXCEL_COLUMN_EXECUTE
                                )
                        );


                // Find result belonging to this test case
                ModuleExecutionResult result =
                        findResult(
                                results,
                                testCase
                        );


                if (result != null) {

                    row.createCell(statusColumn)
                            .setCellValue(
                                    safeValue(
                                            result.getStatus()
                                    )
                            );

                    row.createCell(remarkColumn)
                            .setCellValue(
                                    safeValue(
                                            result.getMessage()
                                    )
                            );

                    row.createCell(failedStepColumn)
                            .setCellValue(
                                    safeValue(
                                            result.getFailedStep()
                                    )
                            );

                } else if (
                        "NO".equalsIgnoreCase(execute)
                ) {

                    row.createCell(statusColumn)
                            .setCellValue(
                                    "NOT EXECUTED"
                            );

                    row.createCell(remarkColumn)
                            .setCellValue(
                                    "Execute is set to NO"
                            );

                    row.createCell(failedStepColumn)
                            .setCellValue("");
                }
            }


            // Make result columns readable
            try {
                sheet.autoSizeColumn(statusColumn);
                sheet.autoSizeColumn(remarkColumn);
                sheet.autoSizeColumn(failedStepColumn);
            } catch (Exception ignored) {}

            // Synchronize Excel Table (XSSFTable) dimensions if present to prevent Excel recovery warning
            if (sheet instanceof org.apache.poi.xssf.usermodel.XSSFSheet) {
                try {
                    org.apache.poi.xssf.usermodel.XSSFSheet xssfSheet = (org.apache.poi.xssf.usermodel.XSSFSheet) sheet;
                    List<org.apache.poi.xssf.usermodel.XSSFTable> tables = xssfSheet.getTables();
                    if (tables != null && !tables.isEmpty()) {
                        for (org.apache.poi.xssf.usermodel.XSSFTable table : tables) {
                            org.apache.poi.ss.util.AreaReference currentArea = table.getArea();
                            if (currentArea != null) {
                                org.apache.poi.ss.util.CellReference firstCell = currentArea.getFirstCell();
                                org.apache.poi.ss.util.CellReference lastCell = currentArea.getLastCell();
                                int maxCol = Math.max((int) lastCell.getCol(), headerRow.getLastCellNum() - 1);
                                int maxRow = Math.max((int) lastCell.getRow(), sheet.getLastRowNum());
                                org.apache.poi.ss.util.AreaReference newArea = new org.apache.poi.ss.util.AreaReference(
                                        firstCell,
                                        new org.apache.poi.ss.util.CellReference(maxRow, maxCol),
                                        org.apache.poi.ss.SpreadsheetVersion.EXCEL2007
                                );
                                table.setArea(newArea);
                                table.updateHeaders();
                            }
                        }
                    }
                } catch (Throwable t) {
                    // Ignore table sync failure if not supported
                }
            }

            // Write result workbook
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                workbook.write(outputStream);
                outputStream.flush();
            }

            return outputFile;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate result Excel",
                    e
            );
        }
    }


    /**
     * Finds a column index from the header row.
     */
    private static int findColumn(
            Row headerRow,
            String columnName) {

        for (Cell cell : headerRow) {

            String value =
                    cell.getStringCellValue()
                            .trim();

            if (columnName.equalsIgnoreCase(value)) {
                return cell.getColumnIndex();
            }
        }

        return -1;
    }


    /**
     * Gets a cell value safely.
     */
    private static String getCellValue(
            Row row,
            int columnIndex) {

        if (columnIndex < 0) {
            return "";
        }

        Cell cell =
                row.getCell(
                        columnIndex,
                        Row.MissingCellPolicy
                                .CREATE_NULL_AS_BLANK
                );

        return cell.getStringCellValue()
                .trim();
    }


    /**
     * Finds a test-case result.
     */
    private static ModuleExecutionResult findResult(
            List<ModuleExecutionResult> results,
            String testCase) {

        if (results == null || testCase == null) {
            return null;
        }

        for (ModuleExecutionResult result : results) {

            if (testCase.equalsIgnoreCase(
                    safeValue(result.getTestCase())
            )) {
                return result;
            }
        }

        return null;
    }


    /**
     * Prevents null values from reaching Excel.
     */
    private static String safeValue(String value) {

        return value == null
                ? ""
                : value;
    }
}