package org.upyog.adapter.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.exception.ValidationException;
import org.upyog.adapter.loader.Loader;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.registry.TransformerRegistry;
import org.upyog.adapter.transformer.ModuleTransformer;
import org.upyog.adapter.validator.CommonValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AdapterClientImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AdapterClientImplTest {

	@Mock
	private TransformerRegistry registry;

	@Mock
	private Loader loader;

	@Mock
	private CommonValidator commonValidator;

	@Mock
	private ModuleTransformer<Object> transformer;

	@InjectMocks
	private AdapterClientImpl adapterClient;

	private static IngestionResult createSuccessResult() {
		return IngestionResult.builder().ingestionStatus("SUCCESS").responseData("{\"response\": \"ok\"}")
				.ingestedAt(System.currentTimeMillis()).build();
	}

	private static IngestionResult createFailureResult() {
		return IngestionResult.builder().ingestionStatus("FAILURE").failureReason("Connection refused")
				.ingestedAt(System.currentTimeMillis()).build();
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

		AdapterRequest request = AdapterRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(data)).thenReturn(payload);
		doNothing().when(commonValidator).validate(payload);
		when(loader.load(payload)).thenReturn(expectedResult);

		// Act
		IngestionResult result = adapterClient.execute(request);

		// Assert
		assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
		assertThat(result.getResponseData()).isEqualTo("{\"response\": \"ok\"}");

		verify(registry).get(Module.PT);
		verify(transformer).transform(data);
		verify(commonValidator).validate(payload);
		verify(loader).load(payload);
	}

	@Test
	@DisplayName("Execute throws when transformer not found for module")
	void execute_transformerNotFound_throwsException() {
		AdapterRequest request = AdapterRequest.builder().module(Module.PT).rawData(null).build();

		when(registry.get(Module.PT)).thenThrow(new IllegalArgumentException("No transformer found for module : PT"));

		assertThatThrownBy(() -> adapterClient.execute(request)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("No transformer found for module");

		verify(registry).get(Module.PT);
		verify(transformer, never()).transform(any());
		verify(loader, never()).load(any());
	}

	@Test
	@DisplayName("Execute propagates ValidationException from CommonValidator")
	void execute_validationFails_throwsValidationException() {
		Map<String, Object> metrics = new HashMap<>();
		DashboardData data = DashboardData.builder().module("PT").ulb("pb.amritsar").metrics(metrics).build();

		DashboardPayload payload = DashboardPayload.builder().data(Collections.singletonList(data)).build();

		AdapterRequest request = AdapterRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(data)).thenReturn(payload);
		doThrow(new ValidationException("Module is mandatory")).when(commonValidator).validate(payload);

		assertThatThrownBy(() -> adapterClient.execute(request)).isInstanceOf(ValidationException.class)
				.hasMessage("Module is mandatory");

		verify(loader, never()).load(any());
	}

	@Test
	@DisplayName("Execute returns failure result when loader fails")
	void execute_loaderFails_returnsFailure() {
		Map<String, Object> metrics = new HashMap<>();
		DashboardData data = DashboardData.builder().module("PT").state("Punjab").ward("ward-1").region("pb")
				.ulb("pb.amritsar").metrics(metrics).build();

		DashboardPayload payload = DashboardPayload.builder().data(Collections.singletonList(data)).build();

		IngestionResult failureResult = createFailureResult();

		AdapterRequest request = AdapterRequest.builder().module(Module.PT).rawData(List.of(data)).build();

		when(registry.get(Module.PT)).thenReturn(transformer);
		when(transformer.transform(data)).thenReturn(payload);
		doNothing().when(commonValidator).validate(payload);
		when(loader.load(payload)).thenReturn(failureResult);

		IngestionResult result = adapterClient.execute(request);

		assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
		assertThat(result.getFailureReason()).isEqualTo("Connection refused");
	}
}