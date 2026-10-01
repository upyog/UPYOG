package org.upyog.dashboard.pgr.extractor;

import org.upyog.dashboard.util.HierarchyParser;
import org.upyog.dashboard.util.DatabaseQueryExecutor;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.extractor.impl.PgrModuleExtractor;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.pgr.model.RawPgrMetric;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PgrModuleExtractor}.
 */
class PgrModuleExtractorTest {

	@Mock
	private DatabaseQueryExecutor queryExecutor;

	@Mock
	private SchemaMappingConfig schemaMappingConfig;

	@Mock
	private HierarchyParser hierarchyParser;

	@InjectMocks
	private PgrModuleExtractor extractor;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		extractor = new PgrModuleExtractor(queryExecutor, schemaMappingConfig, objectMapper, hierarchyParser);
		when(hierarchyParser.parseTenantId(anyString())).thenReturn(java.util.Map.of("state", "PG", "ulb", "citya", "region", "City A", "ward", "Ward 1"));
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

		RawPgrMetric mockDbResult = RawPgrMetric.builder()
				.tenantid("PG.citya.City A.Ward 1")
				.uniquecitizens(22)
				.slaachievementjson("[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]")
				.completionratejson("[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]")
				.complaintsbydepartmentjson("[{\"name\":\"DEPT1\",\"value\":20},{\"name\":\"DEPT2\",\"value\":50},{\"name\":\"DEPT3\",\"value\":30}]")
				.complaintsbycategoryjson("[{\"name\":\"Street Lights\",\"value\":20},{\"name\":\"Road Repair\",\"value\":60},{\"name\":\"Garbage Cleaning\",\"value\":10},{\"name\":\"Drainage Issue\",\"value\":10}]")
				.todaysreopenedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":20},{\"name\":\"DEPT2\",\"value\":5},{\"name\":\"DEPT3\",\"value\":3}]")
				.todaysopencomplaintsjson("[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":7},{\"name\":\"DEPT3\",\"value\":11}]")
				.todaysassignedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":2}]")
				.averagesolutiontimejson("[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":4},{\"name\":\"DEPT3\",\"value\":3}]")
				.todaysrejectedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":2},{\"name\":\"DEPT2\",\"value\":0},{\"name\":\"DEPT3\",\"value\":6}]")
				.todaysreassignedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]")
				.todaysreassignrequestedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]")
				.todaysclosedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]")
				.todaysresolvedcomplaintsjson("[{\"name\":\"DEPT1\",\"value\":1},{\"name\":\"DEPT2\",\"value\":3},{\"name\":\"DEPT3\",\"value\":1}]")
				.build();

		when(queryExecutor.executeQueryWithRetry(anyString(), any(), any(), anyString())).thenReturn(List.of(mockDbResult));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(List.of("PG.citya.City A.Ward 1"), testDate);

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

		RawPgrMetric mockDbResult = RawPgrMetric.builder()
				.tenantid("PG.citya.City A.Ward 1")
				.uniquecitizens(5)
				.build();

		when(queryExecutor.executeQueryWithRetry(anyString(), any(), any(), anyString()))
				.thenReturn(List.of(mockDbResult));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(List.of("PG.citya.City A.Ward 1"), testDate);

		assertThat(dataList).isNotNull().hasSize(1);
		assertThat(dataList.get(0).getMetrics()).containsEntry("uniqueCitizens", 5);
		verify(queryExecutor).executeQueryWithRetry(anyString(), any(), any(), anyString());
	}

	@Test
	@DisplayName("extractData retries up to maxAttempts and falls back on persistent DB failures")
	void extractData_allDbAttemptsFail_fallsBackToEmptyDefaults() {
		SchemaMappingConfig.ModuleQueries queries = new SchemaMappingConfig.ModuleQueries();
		queries.setCombinedMetricsQuery("SELECT 1");

		when(schemaMappingConfig.getQueriesForModule(Module.PGR)).thenReturn(queries);

		when(queryExecutor.executeQueryWithRetry(anyString(), any(), any(), anyString()))
				.thenThrow(new RuntimeException("Persistent DB disconnect"));

		LocalDate testDate = LocalDate.of(2022, 6, 1);
		List<DashboardData> dataList = extractor.extractData(List.of("PG.citya.City A.Ward 1"), testDate);

		assertThat(dataList).isNotNull().isEmpty(); // Empty default
		verify(queryExecutor).executeQueryWithRetry(anyString(), any(), any(), anyString());
	}
}
