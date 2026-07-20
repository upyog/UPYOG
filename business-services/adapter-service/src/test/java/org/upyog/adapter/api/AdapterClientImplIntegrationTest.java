package org.upyog.adapter.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.loader.impl.HttpLoader;
import org.upyog.adapter.model.*;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.registry.TransformerRegistry;
import org.upyog.adapter.service.OAuthTokenService;
import org.upyog.adapter.transformer.ModuleTransformer;
import org.upyog.adapter.validator.CommonValidator;

import java.util.*;

class AdapterClientSimpleTest {

	private AdapterClientImpl adapterClient;

	@BeforeEach
	void setUp() throws Exception {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		AdapterProducer producer = Mockito.mock(AdapterProducer.class);
		ModuleTransformer<Object> transformer = Mockito.mock(ModuleTransformer.class);

		// 1. Build a dummy payload so downstream components have configuration context
		DashboardData dataForPayload = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test")
				.state("PG").metrics(new LinkedHashMap<>()).build();

		DashboardPayload dummyPayload = DashboardPayload.builder().data(Collections.singletonList(dataForPayload))
				.build();

		// 2. Stub the mock transformer to return this valid payload structure
		Mockito.when(transformer.getModule()).thenReturn(Module.PT);
		Mockito.when(transformer.transform(Mockito.any())).thenReturn(dummyPayload);

		OAuthTokenService oAuthTokenService = new OAuthTokenService();
		setField(oAuthTokenService, "restTemplate", restTemplate);

		HttpLoader loader = new HttpLoader();
		setField(loader, "restTemplate", restTemplate);
		setField(loader, "oAuthTokenService", oAuthTokenService);
		setField(loader, "producer", producer);

		TransformerRegistry registry = new TransformerRegistry(Collections.singletonList(transformer));
		CommonValidator commonValidator = new CommonValidator();

		adapterClient = new AdapterClientImpl(registry, loader, commonValidator);
	}

	// Method 1: Makes the dummy data
	private AdapterRequest createDummyRequest() {
		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("assessments", 48);
		metrics.put("todaysTotalApplications", 145);

		DashboardData data = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4").ulb("pg.citya").region("Test").state("PG")
				.metrics(metrics).build();

		return AdapterRequest.builder().module(Module.PT).rawData(List.of(data)).build();
	}

	// Method 2: Simply calls the execute method
	@Test
	void testExecuteWithDummyData() {
		AdapterRequest request = createDummyRequest();

		// Execution flow handles payload metadata matching properly now
		IngestionResult result = adapterClient.execute(request);

		System.out.println("Execution finished. Status: " + result.getIngestionStatus());
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception {
		java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}