package org.upyog.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.extractor.service.DailyIngestionService;

@SpringBootTest
class DataExtractorApplicationTests {

	@Autowired
	private DailyIngestionService dailyIngestionService;

	@Test
	void contextLoads() {
	}

	@Test
	void testFetchPTDataFromDatabase() {
		LocalDate testDate = LocalDate.of(2026, 7, 15);
		DashboardData data = dailyIngestionService.fetchPTDataFromDatabase(testDate);
		
		assertNotNull(data);
		assertEquals("15-07-2026", data.getDate());
		assertEquals("PT", data.getModule());
		assertEquals("Block 4", data.getWard());
		assertEquals("pg.citya", data.getUlb());
		assertEquals("Test", data.getRegion());
		assertEquals("PG", data.getState());
		
		assertNotNull(data.getMetrics());
		System.out.println("Fetched Metrics: " + data.getMetrics());
	}

}

