package org.egov.waterquality.web.models.collection;

public enum ApplicationType {
	WATER_SAMPLE, WATER_QUALITY_TEST;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
