/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.util;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;

import org.egov.finance.master.model.SchemeModel;

import org.egov.finance.master.model.SubSchemeModel;

import org.egov.finance.master.service.CacheEvictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

	private CacheEvictionService cacheEvictionService;

	@Autowired
	public CacheConfig(CacheEvictionService cacheEvictionService) {
		this.cacheEvictionService = cacheEvictionService;
	}

	@Bean(MasterConstants.FUND_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator fundSearchKeyGenerator() {

		return (target, method, params) -> {
			FundModel criteria = (FundModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "identifier", criteria.getIdentifier());
			addIfNotNull(parts, "llevel", criteria.getLlevel());
			addIfNotNull(parts, "parentId", criteria.getParentId());
			addIfNotNull(parts, "isnotleaf", criteria.getIsnotleaf());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.SCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator schemeSearchKeyGenerator() {
		return (target, method, params) -> {
			SchemeModel criteria = (SchemeModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "fundId", criteria.getFundId());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "validfrom",
					criteria.getValidfrom() != null ? criteria.getValidfrom().getTime() : null);
			addIfNotNull(parts, "validto", criteria.getValidto() != null ? criteria.getValidto().getTime() : null);
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator subschemeSearchKeyGenerator() {
		return (target, method, params) -> {
			SubSchemeModel criteria = (SubSchemeModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "schemeid", criteria.getScheme());
			addIfNotNull(parts, "validfrom", criteria.getValidfrom());
			addIfNotNull(parts, "validto", criteria.getValidto());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};

	}

	@Bean(MasterConstants.FUNCTION_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator functionSearchKeyGenerator() {
		return (target, method, params) -> {
			FunctionModel criteria = (FunctionModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "llevel", criteria.getLlevel());
			addIfNotNull(parts, "parentId", criteria.getParentId());
			addIfNotNull(parts, "isNotLeaf", criteria.getIsNotLeaf());
			addIfNotNull(parts, "isaAtive", criteria.getIsActive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	private void addIfNotNull(List<String> parts, String key, Object value) {
		if (value != null) {
			parts.add(key + "=" + value);
		}
	}

}
