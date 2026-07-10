package org.upyog.as.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.as.core.adapter.ModuleAdapter;
import org.upyog.as.core.adapter.impl.UttarakhandPropertyTaxAdapter;

@Component
public class AdapterFactory {

	@Autowired
	private UttarakhandPropertyTaxAdapter uttarakhandPropertyTaxAdapter;

	public ModuleAdapter getAdapter(String tenantId, String module) {
		if ("PT".equals(module)) {
			return uttarakhandPropertyTaxAdapter;
		}
		throw new IllegalStateException("No adapter registered for module=" + module);
	}
}