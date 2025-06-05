package org.egov.finance.master.util;

import java.util.ArrayList;
import java.util.List;
import org.egov.finance.master.model.FundModel;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

	@Bean("fundSearchKeyGenerator")
	public KeyGenerator fundSearchKeyGenerator() {
	    return (target, method, params) -> {
	        FundModel criteria = (FundModel) params[0];
	        List<String> parts = new ArrayList<>();
	        if (criteria.getName() != null) parts.add(criteria.getName());
	        if (criteria.getCode() != null) parts.add(criteria.getCode());
	        if (criteria.getIdentifier() != null) parts.add(criteria.getIdentifier().toString());
	        if (criteria.getLlevel() != null) parts.add(criteria.getLlevel().toString());
	        return String.join("::", parts);

	    };
	}

}
