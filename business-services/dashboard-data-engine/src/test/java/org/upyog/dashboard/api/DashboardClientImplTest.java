package org.upyog.dashboard.api;

import org.upyog.dashboard.util.CommonUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.loader.DashboardDataLoaderFactory;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.validator.CommonValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DashboardClientImpl}.
 */
@ExtendWith(MockitoExtension.class)
class DashboardClientImplTest {

	@Mock
	private TransformerRegistry registry;

	@Mock
	private DashboardDataLoaderFactory dataLoaderFactory;

	@Mock
	private DashboardDataLoader loader;

	@Mock
	private CommonValidator commonValidator;

	@Mock
	private ModuleTransformer<Object> transformer;

	@InjectMocks
	private DashboardClientImpl dashboardClient;

	private static IngestionResult createSuccessResult() {
		return IngestionResult.builder().ingestionStatus("SUCCESS").responseData("{\"response\": \"ok\"}")
				.ingestedAt(CommonUtils.getCurrentEpochMillis()).build();
	}

	private static IngestionResult createFailureResult() {
		return IngestionResult.builder().ingestionStatus("FAILURE").failureReason("Connection refused")
				.ingestedAt(CommonUtils.getCurrentEpochMillis()).build();
	}

	@Test
	@DisplayName("Execute pipeline succeeds end-to-end")
	void execute_successfulPipeline() {
		// Arrange
		Map<String, Object> metrics = new HashMap<>();
		metrics.put("assessments", 100);
		DashboardData data = DashboardData.builder().module("PT").state("Punjab").ward("ward-1").region("pb")
				.ulb("pb.amritsar").metrics(metrics).build();

		DashboardPayload payload = DashboardPayload.builder().data(Collections.singletonList(data)).build();

		IngestionResult expectedResult = createSuccessResult();

		DashboardRequest request = DashboardRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(any())).thenReturn(payload);
		doNothing().when(commonValidator).validate(payload);
		when(dataLoaderFactory.getDailyDataLoader()).thenReturn(loader);
		when(loader.load(payload)).thenReturn(expectedResult);

		// Act
		IngestionResult result = dashboardClient.execute(request);

		// Assert
		assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
		assertThat(result.getResponseData()).isEqualTo("{\"response\": \"ok\"}");

		verify(registry).get(Module.PT);
		verify(transformer).transform(any());
		verify(commonValidator).validate(payload);
		verify(dataLoaderFactory).getDailyDataLoader();
		verify(loader).load(payload);
	}

	@Test
	@DisplayName("Execute throws when transformer not found for module")
	void execute_transformerNotFound_throwsException() {
		DashboardRequest request = DashboardRequest.builder().module(Module.PT).rawData(null).build();

		when(registry.get(Module.PT)).thenThrow(new IllegalArgumentException("No transformer found for module : PT"));

		assertThatThrownBy(() -> dashboardClient.execute(request)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("No transformer found for module");

		verify(registry).get(Module.PT);
		verify(transformer, never()).transform(any());
		verify(dataLoaderFactory, never()).getDailyDataLoader();
		verify(loader, never()).load(any());
	}

	@Test
	@DisplayName("Execute propagates ValidationException from CommonValidator")
	void execute_validationFails_throwsValidationException() {
		Map<String, Object> metrics = new HashMap<>();
		DashboardData data = DashboardData.builder().module("PT").ulb("pb.amritsar").metrics(metrics).build();

		DashboardPayload payload = DashboardPayload.builder().data(Collections.singletonList(data)).build();

		DashboardRequest request = DashboardRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(any())).thenReturn(payload);
		doThrow(new ValidationException("Module is mandatory")).when(commonValidator).validate(payload);

		assertThatThrownBy(() -> dashboardClient.execute(request)).isInstanceOf(ValidationException.class)
				.hasMessage("Module is mandatory");

		verify(dataLoaderFactory, never()).getDailyDataLoader();
		verify(loader, never()).load(any());
	}

	@Test
	@DisplayName("Execute returns failure result when loader fails")
	void execute_loaderFails_returnsFailure() {
		Map<String, Object> metrics = new HashMap<>();
		DashboardData data = DashboardData.builder().module("PT").state("Punjab").ward("ward-1").region("pb")
				.ulb("pb.amritsar").metrics(metrics).build();

		DashboardPayload payload = DashboardPayload.builder().data(Collections.singletonList(data)).build();

		IngestionResult failureResult = IngestionResult.builder().ingestionStatus("FAILURE")
				.failureReason("Connection timeout").ingestedAt(CommonUtils.getCurrentEpochMillis()).build();

		DashboardRequest request = DashboardRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(any())).thenReturn(payload);
		doNothing().when(commonValidator).validate(payload);
		when(dataLoaderFactory.getDailyDataLoader()).thenReturn(loader);
		when(loader.load(payload)).thenReturn(failureResult);

		// Act
		IngestionResult result = dashboardClient.execute(request);

		// Assert
		assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
		assertThat(result.getFailureReason()).isEqualTo("Connection timeout");
	}
}
