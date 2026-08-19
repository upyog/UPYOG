package org.upyog.dashboard.pgr.extractor;

import org.upyog.dashboard.util.TestUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.model.DashboardData;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.dashboard.extractor.impl.PgrModuleExtractor;

import org.upyog.dashboard.config.DashboardProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PgrModuleExtractor}.
 */
class PgrModuleExtractorTest {

	@Mock
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Mock
	private SchemaMappingConfig schemaMappingConfig;

	@Mock
	private org.upyog.dashboard.util.HierarchyParser hierarchyParser;

	@InjectMocks
	private PgrModuleExtractor extractor;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		// Inject ObjectMapper manually into private field
		java.lang.reflect.Field field = PgrModuleExtractor.class.getDeclaredField("objectMapper");
		field.setAccessible(true);
		field.set(extractor, objectMapper);

		// Mock and inject DashboardProperties
		DashboardProperties dashboardProperties = mock(DashboardProperties.class);
		when(dashboardProperties.getMetricUlb()).thenReturn("PG.citya.City A.Ward 1");
		when(dashboardProperties.getMetricWard()).thenReturn("Ward 1");
		when(dashboardProperties.getMetricRegion()).thenReturn("City A");
		when(dashboardProperties.getMetricState()).thenReturn("PG");
		when(dashboardProperties.getDbMaxAttempts()).thenReturn(3);
		when(dashboardProperties.getDbBaseDelayMs()).thenReturn(1L);
		when(dashboardProperties.getDbMaxDelayMs()).thenReturn(2L);

		java.lang.reflect.Field propsField = PgrModuleExtractor.class.getDeclaredField("dashboardProperties");
		propsField.setAccessible(true);
		propsField.set(extractor, dashboardProperties);

		when(hierarchyParser.parseTenantId(anyString())).thenReturn(java.util.Map.of("state", "PG", "ulb", "citya", "region", "City A", "ward", "Ward 1"));

		extractor.init();
	}

	

	@Test
	@DisplayName("getModule returns Module.PGR")
	void getModule_returnsPGR() {
		assertThat(extractor.getModule()).isEqualTo(Module.PGR);
	}

	@Test
	@DisplayName("extractData builds DashboardData with exact sample metrics structure")
	void extractData_buildsSampleMetricsStructure() {
		SchemaMappingConfig.ModuleQueries queries = new SchemaMappingConfig.ModuleQueries();
		queries.setCombinedMetricsQuery("SELECT 1");

		when(schemaMappingConfig.getQueriesForModule(Module.PGR)).thenReturn(queries);

		Map<String, Object> mockDbResult = new HashMap<>();
		mockDbResult.put("tenantid", "PG.citya.City A.Ward 1");
		mockDbResult.put("uniquecitizens", 22);
		mockDbResult.put("slaachievementjson", "[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]");
		mockDbResult.put("completionratejson", "[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]");
		mockDbResult.put("complaintsbystatusjson", "[{\"name\":\"reopened\",\"value\":15},{\"name\":\"open\",\"value\":20},{\"name\":\"assigned\",\"value\":16},{\"name\":\"rejected\",\"value\":14},{\"name\":\"reassign\",\"value\":10}]");
		mockDbResult.put("complaintsbychanneljson", "[{\"name\":\"MOBILE\",\"value\":10},{\"name\":\"WEB\",\"value\":90}]");
		mockDbResult.put("complaintsbydepartmentjson", "[{\"name\":\"DEPT1\",\"value\":20},{\"name\":\"DEPT2\",\"value\":50},{\"name\":\"DEPT3\",\"value\":30}]");
		mockDbResult.put("complaintsbycategoryjson", "[{\"name\":\"Street Lights\",\"value\":20},{\"name\":\"Road Repair\",\"value\":60},{\"name\":\"Garbage Cleaning\",\"value\":10},{\"name\":\"Drainage Issue\",\"value\":10}]");
		mockDbResult.put("todaysreopenedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":20},{\"name\":\"DEPT2\",\"value\":5},{\"name\":\"DEPT3\",\"value\":3}]");
		mockDbResult.put("todaysopencomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":7},{\"name\":\"DEPT3\",\"value\":11}]");
		mockDbResult.put("todaysassignedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":2}]");
		mockDbResult.put("averagesolutiontimejson", "[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":4},{\"name\":\"DEPT3\",\"value\":3}]");
		mockDbResult.put("todaysrejectedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]");
		mockDbResult.put("todaysreassignedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]");
		mockDbResult.put("todaysreassignrequestedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]");
		mockDbResult.put("todaysclosedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]");
		mockDbResult.put("todaysresolvedcomplaintsjson", "[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]");

		when(namedParameterJdbcTemplate.queryForList(any(), anyMap())).thenReturn(List.of(mockDbResult));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(testDate);

		assertThat(dataList).isNotNull().hasSize(1);
		DashboardData data = dataList.get(0);
		
		assertThat(data.getDate()).isEqualTo("01-06-2022");
		assertThat(data.getModule()).isEqualTo("PGR");
		assertThat(data.getWard()).isEqualTo("Ward 1");
		assertThat(data.getUlb()).isEqualTo("citya");
		assertThat(data.getRegion()).isEqualTo("City A");
		assertThat(data.getState()).isEqualTo("PG");

		Map<String, Object> metrics = data.getMetrics();
		assertThat(metrics).containsKeys(
				"slaAchievement", "completionRate", "uniqueCitizens", "todaysComplaints",
				"todaysReopenedComplaints", "todaysOpenComplaints", "todaysAssignedComplaints",
				"averageSolutionTime", "todaysRejectedComplaints", "todaysReassignedComplaints",
				"todaysReassignRequestedComplaints", "todaysClosedComplaints", "todaysResolvedComplaints"
		);

		assertThat(metrics.get("uniqueCitizens")).isEqualTo(22);
		assertThat((List<?>) metrics.get("slaAchievement")).hasSize(1);
		assertThat((List<?>) metrics.get("todaysComplaints")).hasSize(4);
	}

	@Test
	@DisplayName("extractData retries on transient DB failures and succeeds eventually")
	void extractData_retriesOnDbFailure_succeedsEventually() {
		SchemaMappingConfig.ModuleQueries queries = new SchemaMappingConfig.ModuleQueries();
		queries.setCombinedMetricsQuery("SELECT 1");

		when(schemaMappingConfig.getQueriesForModule(Module.PGR)).thenReturn(queries);

		Map<String, Object> mockDbResult = new HashMap<>();
		mockDbResult.put("tenantid", "PG.citya.City A.Ward 1");
		mockDbResult.put("uniquecitizens", 5);

		// Fail on first attempt, succeed on second
		when(namedParameterJdbcTemplate.queryForList(any(), anyMap()))
				.thenThrow(new RuntimeException("Transient lock conflict"))
				.thenReturn(List.of(mockDbResult));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(testDate);

		assertThat(dataList).isNotNull().hasSize(1);
		assertThat(dataList.get(0).getMetrics()).containsEntry("uniqueCitizens", 5);
		verify(namedParameterJdbcTemplate, times(2)).queryForList(any(), anyMap());
	}

	@Test
	@DisplayName("extractData retries up to maxAttempts and falls back on persistent DB failures")
	void extractData_allDbAttemptsFail_fallsBackToEmptyDefaults() {
		SchemaMappingConfig.ModuleQueries queries = new SchemaMappingConfig.ModuleQueries();
		queries.setCombinedMetricsQuery("SELECT 1");

		when(schemaMappingConfig.getQueriesForModule(Module.PGR)).thenReturn(queries);

		when(namedParameterJdbcTemplate.queryForList(any(), anyMap()))
				.thenThrow(new RuntimeException("Persistent DB disconnect"));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(testDate);

		assertThat(dataList).isNotNull().isEmpty(); // Empty default
		verify(namedParameterJdbcTemplate, times(3)).queryForList(any(), anyMap());
	}
}
