package org.egov.finance.master.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MdmsCriteria {
	private String tenantId;
    private List<ModuleDetail> moduleDetails;
}
