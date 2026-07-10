package org.upyog.as.core.adapter.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.as.core.ExtractionContext;
import org.upyog.as.core.adapter.AbstractModuleAdapter;
import org.upyog.as.core.extractor.impl.UttarakhandPTExtractor;
import org.upyog.as.core.transformer.impl.UttarakhandPTTransformer;
import org.upyog.as.extractor.record.PTRawData;
import org.upyog.as.model.payload.ModuleData;

@Component
public class UttarakhandPropertyTaxAdapter extends AbstractModuleAdapter<PTRawData> {

	@Autowired
	private UttarakhandPTExtractor extractor;
	@Autowired
	private UttarakhandPTTransformer transformer;
	@Autowired
	private DataSource dataSource;

	@Override
	public PTRawData extract(ExtractionContext ctx) {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		return extractor.extract(jdbc, ctx.getTenantId(), ctx.getDate());
	}

	@Override
	public ModuleData transform(PTRawData rawData, ExtractionContext ctx) {
		return transformer.transform(rawData, ctx);
	}
}