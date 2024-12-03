package org.egov.nationaldashboardingest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
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
import org.egov.nationaldashboardingest.service.StateListDB;
import org.egov.nationaldashboardingest.web.models.Attachments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExcelUtil {

    @Autowired
    private StateListDB stateListDB;

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
        sheet.addMergedRegion(CellRangeAddress.valueOf("B1:I1"));

        createCell(row, 0, "ULB - " + state, style, sheet);
        createCell(row, 1, "Modules/Services", style, sheet);

        createCell(row1, 1, "FIRENOC", style, sheet);
        createCell(row1, 2, "MCOLLECT", style, sheet);
        createCell(row1, 3, "PGR", style, sheet);
        createCell(row1, 4, "PT", style, sheet);
        createCell(row1, 5, "TL", style, sheet);
        createCell(row1, 6, "WS", style, sheet);
        createCell(row1, 7, "(blank)", style, sheet);
        createCell(row1, 8, "Total", style, sheet);
    }

    private void fillULBData(XSSFSheet sheet, List<String> ulbList, String state, Map<String, Map<String, List<String>>> ulbModules) {
        int rowCount = 2;
        String[] modules = {"FIRENOC", "MCOLLECT", "PGR", "PT", "TL", "WS", "OBPS"};

        CellStyle style = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Map<String, List<String>> stateModules = ulbModules.get(state);

        if (stateModules == null) {
            log.warn("No module data found for state: {}", state);
            return;
        }

        for (String ulb : ulbList) {
            Row row = sheet.createRow(rowCount++);
            createCell(row, 0, ulb, style, sheet);

            int moduleCount = 0;

            for (int i = 0; i < modules.length; i++) {
                String module = modules[i];
                boolean isAvailable = checkModuleAvailability(ulb, module, stateModules);

                createCell(row, i + 1, isAvailable ? "Y" : "N", style, sheet);
                if (isAvailable) {
                    moduleCount++;
                }
            }

            createCell(row, 7, "", style, sheet);
            createCell(row, 8, moduleCount, style, sheet);
        }
    }

    private boolean checkModuleAvailability(String ulb, String module, Map<String, List<String>> stateModules) {
        List<String> availableModules = stateModules.get(ulb);
        if (availableModules != null) {
            return availableModules.contains(module);
        }
        return false;
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
