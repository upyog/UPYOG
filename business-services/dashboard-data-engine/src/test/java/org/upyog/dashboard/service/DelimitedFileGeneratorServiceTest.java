package org.upyog.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.pt.model.PTMetric;
import org.upyog.dashboard.registry.TransformerRegistry;

class DelimitedFileGeneratorServiceTest {

    private DelimitedFileGeneratorService service;
    private TransformerRegistry transformerRegistry;
    private DashboardProperties properties;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        transformerRegistry = mock(TransformerRegistry.class);
        properties = mock(DashboardProperties.class);
        when(properties.getFileDelimiter()).thenReturn("|");
        when(properties.isDelimitedFileEnabled()).thenReturn(true);

        service = new DelimitedFileGeneratorService(transformerRegistry, properties);
    }

    @Test
    @DisplayName("Verify delimited dataset file generation for Property Tax (PT) module with pipe delimiter")
    void testDelimitedFileGeneration_PTModule() throws Exception {
        PTMetric ptMetric = PTMetric.builder()
                .assessments(0)
                .noOfPropertiesPaidToday(0)
                .todaysTotalApplications(1)
                .todaysClosedApplications(0)
                .todaysApprovedApplications(0)
                .avgDaysForApplicationApproval(0)
                .todaysApprovedApplicationsWithinSLA(0)
                .cess(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .rebate(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .penalty(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .interest(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .propertyTax(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .transactions(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0.0),
                        Map.of("name", "COMMERCIAL", "value", 0.0),
                        Map.of("name", "INDUSTRIAL", "value", 0.0)
                ))))
                .todaysCollection(List.of(
                        Map.of("groupBy", "usageCategory", "buckets", List.of(
                                Map.of("name", "RESIDENTIAL", "value", 0.0),
                                Map.of("name", "COMMERCIAL", "value", 0.0),
                                Map.of("name", "INDUSTRIAL", "value", 0.0)
                        )),
                        Map.of("groupBy", "paymentChannelType", "buckets", List.of(
                                Map.of("name", "Digital", "value", 0.0),
                                Map.of("name", "Non Digital", "value", 0.0)
                        ))
                ))
                .assessedProperties(List.of(Map.of("groupBy", "usageCategory", "buckets", List.of(
                        Map.of("name", "RESIDENTIAL", "value", 0),
                        Map.of("name", "COMMERCIAL", "value", 0),
                        Map.of("name", "INDUSTRIAL", "value", 0)
                ))))
                .propertiesRegistered(List.of(Map.of("groupBy", "financialYear", "buckets", List.of(
                        Map.of("name", "2024-25", "value", 7),
                        Map.of("name", "2025-26", "value", 287),
                        Map.of("name", "2026-27", "value", 35423)
                ))))
                .build();

        DashboardData ptData = DashboardData.builder()
                .date("15-09-2024")
                .module("PT")
                .state("Punjab")
                .ulb("pg.citya")
                .ward("Block 4")
                .region("Test")
                .metrics(ptMetric.toMap())
                .build();

        File file = service.generateDelimitedFile("PT", List.of(ptData), DashboardConstants.DAILY);
        assertThat(file).exists();

        List<String> lines = Files.readAllLines(file.toPath());
        assertThat(lines).hasSize(2);

        String header = lines.get(0);
        assertThat(header).contains("uuid|date|module|state|ulb|ward|region");
        assertThat(header).contains("propertiesRegistered.financialYear.2024-25|propertiesRegistered.financialYear.2025-26|propertiesRegistered.financialYear.2026-27");
        assertThat(header).contains("cess.usageCategory.RESIDENTIAL|cess.usageCategory.COMMERCIAL|cess.usageCategory.INDUSTRIAL");

        String dataRow = lines.get(1);
        String[] tokens = dataRow.split("\\|", -1);
        assertThat(tokens[1]).isEqualTo("15-09-2024");
        assertThat(tokens[2]).isEqualTo("PT");
        assertThat(tokens[3]).isEqualTo("Punjab");
        assertThat(tokens[4]).isEqualTo("pg.citya");
        assertThat(tokens[5]).isEqualTo("Block 4");
        assertThat(tokens[6]).isEqualTo("Test");

        file.delete();
    }

    @Test
    @DisplayName("Verify integration with SXSSFExcelGeneratorService when delimitedFileEnabled flag is true")
    void testSXSSFExcelGeneratorService_DelimitedIntegration() throws Exception {
        DashboardData data = DashboardData.builder()
                .date("15-09-2024")
                .module("ADV")
                .state("Punjab")
                .ulb("pg.citya")
                .metrics(Map.of("previousYearRevenue", 1000, "currentFYCollection", 5000))
                .build();

        DelimitedFileGeneratorService fileService = new DelimitedFileGeneratorService(transformerRegistry, properties);
        SXSSFExcelGeneratorService excelService = new SXSSFExcelGeneratorService(transformerRegistry, null, fileService, properties);

        try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelService.createStreamingSession("ADV", DashboardConstants.DAILY)) {
            session.appendBatchRecords(List.of(data));
            File excelFile = session.finishWorkbook();
            assertThat(excelFile).exists();
            assertThat(session.getDelimitedSession()).isNotNull();

            File delimitedFile = session.getDelimitedSession().getTempFile();
            assertThat(delimitedFile).exists();
            List<String> lines = Files.readAllLines(delimitedFile.toPath());
            assertThat(lines).hasSize(2);
            assertThat(lines.get(0)).contains("uuid|date|module|state|ulb|ward|region");
            assertThat(lines.get(0)).contains("previousYearRevenue");
            assertThat(lines.get(0)).contains("currentFYCollection");
            assertThat(lines.get(1)).contains("1000");
            assertThat(lines.get(1)).contains("5000");

            excelFile.delete();
            delimitedFile.delete();
        }
    }

    @Test
    @DisplayName("Verify PSV row UUID uses system user UUID from OAuthTokenService when available")
    void testDelimitedFileGeneration_SystemUserUuid() throws Exception {
        OAuthTokenService oAuthTokenService = mock(OAuthTokenService.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid("b3177c56-a9f2-4b59-a00c-9e9c38b5f28f");
        when(oAuthTokenService.getUserInfo()).thenReturn(userInfo);

        DelimitedFileGeneratorService customService = new DelimitedFileGeneratorService(transformerRegistry, properties, oAuthTokenService);

        DashboardData data = DashboardData.builder()
                .date("27-09-2026")
                .module("PT")
                .state("Punjab")
                .ulb("pg.citya")
                .metrics(Map.of("totalCollections", 100))
                .build();

        File file = customService.generateDelimitedFile("PT", List.of(data), DashboardConstants.DAILY);
        assertThat(file).exists();

        List<String> lines = Files.readAllLines(file.toPath());
        assertThat(lines).hasSize(2);

        String dataRow = lines.get(1);
        String[] tokens = dataRow.split("\\|", -1);
        assertThat(tokens[0]).isEqualTo("b3177c56-a9f2-4b59-a00c-9e9c38b5f28f");

        file.delete();
    }
}
