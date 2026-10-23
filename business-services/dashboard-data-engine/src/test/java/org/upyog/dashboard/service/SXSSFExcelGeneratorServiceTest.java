package org.upyog.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTDTO;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;

class SXSSFExcelGeneratorServiceTest {

    private SXSSFExcelGeneratorService service;
    private TransformerRegistry transformerRegistry;
    private OAuthTokenService oAuthTokenService;
    private ModuleTransformer<PTDTO> ptTransformer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        transformerRegistry = mock(TransformerRegistry.class);
        oAuthTokenService = mock(OAuthTokenService.class);
        ptTransformer = mock(ModuleTransformer.class);

        when(transformerRegistry.get(Module.PT)).thenReturn((ModuleTransformer) ptTransformer);
        when(oAuthTokenService.getToken()).thenReturn("mock-token-123");
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid("user-1");
        userInfo.setName("System");
        when(oAuthTokenService.getUserInfo()).thenReturn(userInfo);

        service = new SXSSFExcelGeneratorService(transformerRegistry, oAuthTokenService);
    }

    @Test
    @DisplayName("Verify legacy Excel creation: ulb renamed to Tenant, metrics excluded, payload_json present, naming legacy")
    void testLegacyExcelGeneration_ColumnsAndNaming() throws Exception {
        PTDTO dto = PTDTO.builder()
                .module("PT")
                .date("2026-09-08")
                .ulb("pg.citya")
                .state("Punjab")
                .combinedMetrics(PTAggregatedData.builder().assessments(15).build())
                .collectionMetrics(List.of())
                .build();

        DashboardData dashboardData = DashboardData.builder()
                .module("PT")
                .date("2026-09-08")
                .ulb("pg.citya")
                .state("Punjab")
                .metrics(java.util.Map.of("assessments", 15))
                .build();
        when(ptTransformer.transform(dto)).thenReturn(DashboardPayload.builder().data(List.of(dashboardData)).build());

        File excelFile = service.generateExcelFile("PT", List.of(dto), DashboardConstants.LEGACY);
        assertThat(excelFile).exists();
        assertThat(excelFile.getName()).startsWith(DashboardConstants.LEGACY + "_PT_");

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("PT_" + DashboardConstants.LEGACY);
            assertThat(sheet).isNotNull();

            Row headerRow = sheet.getRow(0);
            assertThat(headerRow).isNotNull();

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Verify clean column ordering: date, module, state, Tenant, ward, region, payload_json
            assertThat(headers).containsExactly("date", "module", "state", "Tenant", "ward", "region", "payload_json");

            Row dataRow = sheet.getRow(1);
            assertThat(dataRow).isNotNull();

            int tenantColIndex = headers.indexOf("Tenant");
            assertThat(dataRow.getCell(tenantColIndex).getStringCellValue()).isEqualTo("pg.citya");

            int payloadColIndex = headers.indexOf("payload_json");
            String payloadJson = dataRow.getCell(payloadColIndex).getStringCellValue();
            assertThat(payloadJson).contains("\"RequestInfo\"");
            assertThat(payloadJson).contains("\"authToken\":\"mock-token-123\"");
            assertThat(payloadJson).contains("\"Data\"");
            assertThat(payloadJson).contains("\"ulb\":\"pg.citya\"");
        } finally {
            excelFile.delete();
        }
    }

    @Test
    @DisplayName("Verify daily Excel creation: naming daily")
    void testDailyExcelGeneration_Naming() throws Exception {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .date("2026-09-08")
                .ulb("pg.cityb")
                .metrics(java.util.Map.of("assessments", 5))
                .build();

        File excelFile = service.generateExcelFile("PT", List.of(data), DashboardConstants.DAILY);
        assertThat(excelFile).exists();
        assertThat(excelFile.getName()).startsWith(DashboardConstants.DAILY + "_PT_");

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("PT_" + DashboardConstants.DAILY);
            assertThat(sheet).isNotNull();

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            assertThat(headers).containsExactly("date", "module", "state", "Tenant", "ward", "region", "payload_json");

            Row dataRow = sheet.getRow(1);
            int tenantColIndex = headers.indexOf("Tenant");
            assertThat(dataRow.getCell(tenantColIndex).getStringCellValue()).isEqualTo("pg.cityb");
        } finally {
            excelFile.delete();
        }
    }

    @Test
    @DisplayName("Should throw ValidationException when payload_json exceeds EXCEL_MAX_CELL_CHAR_LIMIT")
    void testGenerateExcelFile_PayloadExceedsCharacterLimit_ThrowsValidationException() {
        String hugeString = "A".repeat(33000);
        DashboardData data = DashboardData.builder()
                .module("PT")
                .date("2026-09-08")
                .ulb("pg.cityb")
                .metrics(java.util.Map.of("hugeField", hugeString))
                .build();

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                service.generateExcelFile("PT", List.of(data), DashboardConstants.DAILY)
        ).isInstanceOf(org.upyog.dashboard.exception.ValidationException.class)
         .hasMessageContaining("exceeds maximum Excel cell character limit");
    }
}
