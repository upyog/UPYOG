package org.upyog.adapter.pt.transformer;

import org.upyog.adapter.transformer.impl.PTTransformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.pt.dto.PTCollectionDTO;
import org.upyog.adapter.pt.dto.PTAggregatedData;
import org.upyog.adapter.pt.dto.PTDTO;

import org.upyog.adapter.config.AdapterProperties;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link PTTransformer}.
 */
class PTTransformerTest {

	private final PTTransformer transformer = new PTTransformer();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() throws Exception {
		Field field = PTTransformer.class.getDeclaredField("objectMapper");
		field.setAccessible(true);
		field.set(transformer, objectMapper);

		AdapterProperties adapterProperties = Mockito.mock(AdapterProperties.class);
		Mockito.when(adapterProperties.getPtUsageCategories())
				.thenReturn(List.of("RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"));
		Mockito.when(adapterProperties.getPtTaxHeads()).thenReturn(List.of("PT_TAX"));
		Mockito.when(adapterProperties.getPtCessHeads()).thenReturn(List.of("PT_FIRE_CESS", "PT_CANCER_CESS"));
		Mockito.when(adapterProperties.getPtRebateHeads()).thenReturn(List.of("PT_TIME_REBATE", "PT_ADHOC_REBATE"));
		Mockito.when(adapterProperties.getPtPenaltyHeads()).thenReturn(List.of("PT_TIME_PENALTY", "PT_ADHOC_PENALTY"));
		Mockito.when(adapterProperties.getPtInterestHeads()).thenReturn(List.of("PT_TIME_INTEREST"));
		Mockito.when(adapterProperties.getPtDigitalPaymentModes()).thenReturn(List.of("ONLINE", "CARD"));

		Field propsField = PTTransformer.class.getDeclaredField("adapterProperties");
		propsField.setAccessible(true);
		propsField.set(transformer, adapterProperties);
	}

	@Test
	@DisplayName("getModule returns Module.PT")
	void getModule_returnsPT() {
		assertThat(transformer.getModule()).isEqualTo(Module.PT);
	}

	@Test
	@DisplayName("transform returns valid DashboardPayload")
	void transform_returnsPayload() {
		PTDTO rawData = PTDTO.builder()
				.date("15-07-2026")
				.module("PT")
				.ulb("pg.citya")
				.combinedMetrics(PTAggregatedData.builder()
						.assessments(50)
						.todaysTotalApplications(120)
						.propertiesRegisteredJson("[{\"name\":\"2025-26\",\"value\":1500}]")
						.assessedPropertiesJson("[{\"name\":\"RESIDENTIAL\",\"value\":1000}]")
						.build())
				.collectionMetrics(List.of(
						PTCollectionDTO.builder()
								.usageCategory("RESIDENTIAL")
								.paymentMode("ONLINE")
								.paymentId("PAY-101")
								.taxHeadCode("PT_TAX")
								.taxHeadAmount(15000.0)
								.build()
				))
				.build();

		DashboardPayload payload = transformer.transform(rawData);
		assertThat(payload).isNotNull();
		assertThat(payload.getData()).hasSize(1);
		
		DashboardData data = payload.getData().get(0);
		assertThat(data.getModule()).isEqualTo("PT");
		assertThat(data.getMetrics().get("assessments")).isEqualTo(50);
	}
}