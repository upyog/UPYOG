package org.egov.pt.models;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PropertyExternal {

	@NotNull
	private String upin;
	@NotNull
	private String tenantId;
	private List<OwnerInfoExternal> ownerInfoExternals;
	private BigDecimal pendingAmount;
}
