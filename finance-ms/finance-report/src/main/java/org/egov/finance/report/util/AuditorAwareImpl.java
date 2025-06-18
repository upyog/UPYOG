/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.report.util;

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(ApplicationThreadLocals.getCurrentUserId());
    }
}

