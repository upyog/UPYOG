package org.egov.egovnationaldashboardingest;

import org.egov.nationaldashboardingest.NationalDashboardIngestApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requires Kafka, Postgres and a complete local environment")
@SpringBootTest(classes = NationalDashboardIngestApplication.class)
class EgovNationalDashboardIngestApplicationTests {

	@Test
	void contextLoads() {
	}

}
