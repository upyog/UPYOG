package org.upyog.dashboard.pgr.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.transformer.impl.PGRTransformer;

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
