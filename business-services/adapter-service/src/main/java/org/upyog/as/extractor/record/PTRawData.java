package org.upyog.as.extractor.record;

import java.util.List;
import java.util.Map;

import org.upyog.as.model.config.PTSchemaMapping;

public record PTRawData(
	    List<Map<String, Object>> properties,
	    List<Map<String, Object>> assessments,
	    List<Map<String, Object>> units,
	    long startMs, long endMs,
	    PTSchemaMapping.Rules rules
	) {}