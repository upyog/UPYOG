package org.egov.finance.voucher.service;

import org.egov.finance.voucher.util.ApplicationThreadLocals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCacheManager {

	@Autowired
	private CacheManager cacheManager;

	public void put(Object key, Object value) {
		cacheManager.getCache(ApplicationThreadLocals.getTenantID()).put(key, value);
	}

	public Object get(Object key) {
		return cacheManager.getCache(ApplicationThreadLocals.getTenantID()).get(key) != null
				? cacheManager.getCache(ApplicationThreadLocals.getTenantID()).get(key).get()
				: null;
	}

	public <T> T get(Object key, Class<T> returnType) {
		return cacheManager.getCache(ApplicationThreadLocals.getTenantID()).get(key, returnType);
	}

	public void remove(Object key) {
		cacheManager.getCache(ApplicationThreadLocals.getTenantID()).evict(key);
	}
}
