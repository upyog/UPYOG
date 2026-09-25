package org.upyog.dashboard.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;

/**
 * Unit tests for {@link DashboardRequest}.
 */
class DashboardRequestTest {

	@Test
	@DisplayName("Builder creates DashboardRequest with module and rawData")
	void builder_createsDashboardRequest() {
		DashboardRequest request = DashboardRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		assertThat(request.getModule()).isEqualTo(Module.PT);
	}

	@Test
	@DisplayName("Lombok generated methods work correctly")
	void lombokMethods_workCorrectly() {
		Object rawData = new Object();
		DashboardRequest request1 = DashboardRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		DashboardRequest request2 = DashboardRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		assertThat(request1).isEqualTo(request2);
		assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
		assertThat(request1.toString()).contains("PT");
	}
}