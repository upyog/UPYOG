package org.egov.wscalculation.web.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingSlab {
	private String id;
	private String buildingType = null;
	private String connectionType = null;
	private String waterSubUsageType=null;
	private String calculationAttribute = null;
	private double minimumCharge;
	private List<Slab> slabs = new ArrayList<>();
	@Override
	public String toString() {
		return "BillingSlab [id=" + id + ", buildingType=" + buildingType + ", connectionType=" + connectionType
				+ ", waterSubUsageType=" + waterSubUsageType + ", calculationAttribute=" + calculationAttribute
				+ ", minimumCharge=" + minimumCharge + ", slabs=" + slabs.toString() + "]";
	}
}