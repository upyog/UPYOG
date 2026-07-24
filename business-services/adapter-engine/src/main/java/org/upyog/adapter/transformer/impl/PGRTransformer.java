package org.upyog.adapter.transformer.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.transformer.ModuleTransformer;

/**
 * Public Grievance Redressal (PGR) implementation of {@link ModuleTransformer}.
 *
 * <p>Responsible for converting PGR-specific raw data into a normalized {@link DashboardPayload}.
 */
@Component
public class PGRTransformer implements ModuleTransformer<List<DashboardData>> {

	@Override
	public Module getModule() {
		return Module.PGR;
	}

	@Override
	public DashboardPayload transform(List<DashboardData> rawData) {
		return DashboardPayload.builder().data(rawData).build();
	}
}
