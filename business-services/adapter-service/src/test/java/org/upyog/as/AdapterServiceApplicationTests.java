package org.upyog.as;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.upyog.as.adapter.AdapterFactory;
import org.upyog.as.common.CommonLoader;
import org.upyog.as.common.CommonValidator;
import org.upyog.as.core.ExtractionContext;
import org.upyog.as.core.IngestionResult;
import org.upyog.as.core.adapter.ModuleAdapter;
import org.upyog.as.extractor.record.PTRawData;
import org.upyog.as.model.payload.ModuleData;

@SpringBootTest
class AdapterServiceApplicationTests {

	@Autowired
	private AdapterFactory adapterFactory;
	@Autowired
	private CommonValidator validator;
	@Autowired
	private CommonLoader loader;

	@Test
	void testFullPipeline() {
		ExtractionContext ctx = ExtractionContext.builder().tenantId("pg.citya").moduleName("PT")
				.date(LocalDate.now().minusDays(1)).build();

		ModuleAdapter adapter = adapterFactory.getAdapter("uk.kedarnath", "PT");
		PTRawData raw = (PTRawData) adapter.extract(ctx);
		ModuleData data = adapter.transform(raw, ctx);

		System.out.println("Metrics: " + data.getMetrics());

		Boolean valid = validator.validatePropertyTaxRequest(data);
		System.out.println("Valid: " + valid);

		if (Boolean.TRUE.equals(valid)) {
			try {
				IngestionResult result = loader.load(data);
				System.out.println("Status: " + result.getIngestionStatus());
				System.out.println("Response: " + result.getResponseData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Skipped load — validation failed");
		}
	}
}