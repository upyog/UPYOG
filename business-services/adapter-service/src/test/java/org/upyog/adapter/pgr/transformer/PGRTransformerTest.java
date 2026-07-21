package org.upyog.adapter.pgr.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/**
 * Unit tests for {@link PGRTransformer}.
 */
class PGRTransformerTest {

	private final PGRTransformer transformer = new PGRTransformer();

	@Test
	@DisplayName("getModule returns Module.PGR")
	void getModule_returnsPGR() {
		assertThat(transformer.getModule()).isEqualTo(Module.PGR);
	}

	@Test
	@DisplayName("transform returns valid DashboardPayload")
	void transform_returnsPayload() {
		DashboardData data = DashboardData.builder().module("PGR").ulb("pg.citya").build();

		DashboardPayload payload = transformer.transform(List.of(data));
		assertThat(payload).isNotNull();
		assertThat(payload.getData()).containsExactly(data);
	}
}
