package org.upyog.adapter.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.adapter.common.constants.Module;

/**
 * Unit tests for {@link AdapterRequest}.
 */
class AdapterRequestTest {

	@Test
	@DisplayName("Builder creates AdapterRequest with module and rawData")
	void builder_createsAdapterRequest() {
		AdapterRequest request = AdapterRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		assertThat(request.getModule()).isEqualTo(Module.PT);
	}

	@Test
	@DisplayName("Lombok generated methods work correctly")
	void lombokMethods_workCorrectly() {
		Object rawData = new Object();
		AdapterRequest request1 = AdapterRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		AdapterRequest request2 = AdapterRequest.builder().module(Module.PT).rawData(new ArrayList<>()).build();

		assertThat(request1).isEqualTo(request2);
		assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
		assertThat(request1.toString()).contains("PT");
	}
}