package org.upyog.as.core.loader;

import org.upyog.as.core.IngestionResult;
import org.upyog.as.model.payload.ModuleData;

public interface Loader {

	public default IngestionResult load(ModuleData data) {
		return IngestionResult.builder().ingestionStatus("SUCCESS").responseData("")
				.ingestedAt(System.currentTimeMillis()).build();
	}
}
