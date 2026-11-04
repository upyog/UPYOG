package org.upyog.dashboard.transformer.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.transformer.ModuleTransformer;

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
