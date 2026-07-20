package org.upyog.adapter.pt.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/**
 * Unit tests for {@link PTTransformer}.
 */
class PTTransformerTest {

	private final PTTransformer transformer = new PTTransformer();

	@Test
	@DisplayName("getModule returns Module.PT")
	void getModule_returnsPT() {
		assertThat(transformer.getModule()).isEqualTo(Module.PT);
	}

	@Test
	@DisplayName("transform returns valid DashboardPayload")
	void transform_returnsPayload() {
		DashboardData data = DashboardData.builder().module("PT").ulb("pb.amritsar").build();

		assertThat(transformer.transform(List.of(data))).isNotNull();
	}
}