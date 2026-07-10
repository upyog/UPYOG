package org.upyog.as.core.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.upyog.as.core.ExtractionContext;
import org.upyog.as.core.IngestionResult;
import org.upyog.as.core.loader.Loader;
import org.upyog.as.core.validator.Validator;
import org.upyog.as.model.payload.ModuleData;

public abstract class AbstractModuleAdapter<T> implements ModuleAdapter<T> {

	@Autowired
	private Validator commonValidator;

	@Autowired
	private Loader commonLoader;

	@Override
	public abstract T extract(ExtractionContext ctx);

	@Override
	public abstract ModuleData transform(T rawData, ExtractionContext ctx);

	@Override
	public Boolean validateRequest(ModuleData data,String type) {
		return commonValidator.validateRequest(data,type);
	}

	@Override
	public IngestionResult load(ModuleData data) {
		return commonLoader.load(data);
	}
}