package org.upyog.as.core.transformer;

import org.upyog.as.core.ExtractionContext;
import org.upyog.as.extractor.record.PTRawData;
import org.upyog.as.model.payload.ModuleData;

public interface Transformer {
	ModuleData transform(PTRawData rawData, ExtractionContext ctx);
}
