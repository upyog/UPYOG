/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.voucher.config.multitenant;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import org.egov.finance.voucher.util.ApplicationThreadLocals;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DomainBasedSchemaTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

	@Value("${default.schema.name}")
	private String defaultSchema;

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenantId = ApplicationThreadLocals.getTenantID();
		log.debug(defaultIfBlank(tenantId, defaultSchema));
		return defaultIfBlank(tenantId, defaultSchema);
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
