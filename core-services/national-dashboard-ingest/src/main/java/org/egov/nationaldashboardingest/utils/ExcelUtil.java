package org.egov.nationaldashboardingest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.service.StateListDB;
import org.egov.nationaldashboardingest.web.models.Attachments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExcelUtil {

    @Autowired
    private StateListDB stateListDB;

    @Autowired
    private ApplicationProperties appProp;

    public List<Attachments> generateExcelFiles(Map<String, Map<String, Object>> stateList) throws IOException {
        List<Attachments> attachmentsList = new ArrayList<>();


        Map<String, Map<String, List<String>>> ulbModules = stateListDB.findIfRecordExists();

        for (Map.Entry<String, Map<String, Object>> entry : stateList.entrySet()) {
            String state = entry.getKey();
            Map<String, Object> officerInfo = entry.getValue();
            List<String> ulbList = (List<String>) officerInfo.get("ULBs");


            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("ULB-Module Data for " + state);
                createHeader(sheet, state);
                fillULBData(sheet, ulbList, state, ulbModules);


                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    workbook.write(baos);
                    baos.flush();


                    Attachments attachment = new Attachments();
                    attachment.setFileName(state + ".xlsx");
                    attachment.setContent(baos.toByteArray());
                    attachment.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


                    attachmentsList.add(attachment);
                }
            }
        }


        return attachmentsList;
    }

    private List<String> getModules() {

        Map<String,String> moduleIndexMapping = appProp.getModuleIndexMapping();
        List<String> allModules = new ArrayList<>(moduleIndexMapping.keySet());
        List<String> requiredModules =  allModules.stream().limit(10).collect(Collectors.toList());

        return requiredModules;
    }

    private void createHeader(XSSFSheet sheet, String state) {
        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:A2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B1:M1"));

        createCell(row, 0, "ULB - " + state, style, sheet);
        createCell(row, 1, "Modules/Services", style, sheet);
        int column = 1;
        for (String module : getModules()) {
            createCell(row1, column++, module, style, sheet);
        }
        createCell(row1, column++, "(blank)", style, sheet);
        createCell(row1, column, "Total", style, sheet);
    }

    private void fillULBData(XSSFSheet sheet, List<String> ulbList, String state, Map<String, Map<String, List<String>>> ulbModules) {
        int rowCount = 2;

        CellStyle style = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle grandTotalStyle = sheet.getWorkbook().createCellStyle();
        XSSFFont grandTotalFont = sheet.getWorkbook().createFont();
        grandTotalFont.setBold(true);
        grandTotalFont.setFontHeight(16);
        grandTotalStyle.setFont(grandTotalFont);
        grandTotalStyle.setAlignment(HorizontalAlignment.CENTER);
        grandTotalStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Map<String, List<String>> stateModules = ulbModules.get(state);


        int[] moduleYCounts = new int[getModules().size()];
        int totalYmodule = 0;


        for (String ulb : ulbList) {
            Row row = sheet.createRow(rowCount++);
            createCell(row, 0, ulb, style, sheet);

            int moduleCount = 0;
            int column = 1;


            for (int i = 0; i < getModules().size(); i++) {
                String module = getModules().get(i);
                boolean moduleDataAvailable = checkModuleAvailability(ulb, module, stateModules);
                createCell(row, column++, moduleDataAvailable ? "Y" : "N", style, sheet);
                if (moduleDataAvailable) {
                    moduleCount++;
                    moduleYCounts[i]++;
                }
            }

            createCell(row, column++, "", style, sheet);
            createCell(row, column, moduleCount, style, sheet);
            totalYmodule += moduleCount;
        }


        Row grandTotalRow = sheet.createRow(rowCount);
        createCell(grandTotalRow, 0, "Grand Total", style, sheet);

        int column = 1;
        for (int i = 0; i < moduleYCounts.length; i++) {
            createCell(grandTotalRow, column++, moduleYCounts[i], grandTotalStyle, sheet);
        }

        column++;
        createCell(grandTotalRow, column, totalYmodule, grandTotalStyle, sheet);
    }

    private boolean checkModuleAvailability(String ulb, String module, Map<String, List<String>> stateModules) {
        if (stateModules == null) {
            return false;
        }
        List<String> availableModules = stateModules.get(ulb);
        if (availableModules == null) {
            return false;
        }
        return availableModules.contains(module);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style, XSSFSheet sheet) {
        
        Cell cell = row.createCell(columnCount);

        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }

        cell.setCellStyle(style);
    }
}
