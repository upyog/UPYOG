package org.upyog.as.core.adapter;

import org.upyog.as.core.ExtractionContext;
import org.upyog.as.core.IngestionResult;
import org.upyog.as.model.payload.ModuleData;

public interface ModuleAdapter<T> {
    T extract(ExtractionContext ctx);
    ModuleData transform(T rawData, ExtractionContext ctx);
    Boolean validateRequest(ModuleData data,String type);
    IngestionResult load(ModuleData data);
}