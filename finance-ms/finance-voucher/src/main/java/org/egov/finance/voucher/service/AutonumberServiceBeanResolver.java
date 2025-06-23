package org.egov.finance.voucher.service;

import java.util.Map;

import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.repository.OverrideImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class AutonumberServiceBeanResolver {

	@Autowired
	private ApplicationContext context;

	public <T> T getAutoNumberServiceFor(Class<T> autoNumberInterface) {

		Map<String, T> autoNumberImpls = context.getBeansOfType(autoNumberInterface);
		if (autoNumberImpls.isEmpty()) {
			throw new ApplicationRuntimeException(
					"Could not find any implementation bean for interface " + autoNumberInterface.getSimpleName());
		}
		if (autoNumberImpls.size() > 1) {
			for (T autoNumberImpl : autoNumberImpls.values()) {
				if (autoNumberImpl.getClass().isAnnotationPresent(OverrideImpl.class)) {
					return autoNumberImpl;
				}
			}
		}
		return autoNumberImpls.values().iterator().next();
	}

}
